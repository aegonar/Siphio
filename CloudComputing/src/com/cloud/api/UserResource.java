package com.cloud.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/user")
public class UserResource {
		

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		
	  System.out.println("["+user.getUserName()+"] [POST] /user");
              
      String token = null;
		
  	  String DataSourceName = "jdbc/MySQLDataSource";
  	  DataSource ds;
  	  Connection con = null;
  	  
  	  try {  
  			InitialContext ctx = new InitialContext();
  			ds = (DataSource)ctx.lookup(DataSourceName);			
  			con = ds.getConnection();
  	
        
        PreparedStatement ps_query_register=null;
		try{
			String query_register = "INSERT INTO User (`UserName`,`Password`,`Name`,`LastName`,`Email`,`About`) VALUES (?,?,?,?,?,?);";
			ps_query_register = con.prepareStatement(query_register);
							
			ps_query_register.setString(1, user.getUserName());
			ps_query_register.setString(2, user.getPassword());
			ps_query_register.setString(3, user.getName());
			ps_query_register.setString(4, user.getLastname());
			ps_query_register.setString(5, user.getEmail());
			ps_query_register.setString(6, user.getAbout());
	
			int  rs_query_register = ps_query_register.executeUpdate();
			
			if (rs_query_register == 0){
				return Response.status(Response.Status.FORBIDDEN).entity("Username or password error").build();
			}
				
			Login log = new Login();
	        token = log.issueToken(user.getUserName());               
	        
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());						
			return Response.status(Response.Status.CONFLICT).entity("Username or password error").build();		
		} finally {
		      try {
		          if (ps_query_register != null) {
		        	  ps_query_register.close();
		          }
		      } catch (SQLException sqle) {
			      System.out.println(sqle);
		      }
		}	
		
		
		} catch (Exception e) {
	      System.out.println(e);
		} finally {
	      try {
	          if (con != null) {
	              con.close();
	          }
	      } catch (SQLException sqle) {
		      System.out.println(sqle);
	      }
		}

		return Response.ok("{\"token\":\""+ token + "\"}", MediaType.APPLICATION_JSON).build();
	}
	
	/*
	@Logged
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(User user, @Context HttpHeaders headers) {
		
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /user");
      
        link.Open_link();
      		
		try{
			String query_updateUser = "UPDATE User SET `UserName`=?,`Name`=?,`LastName`=?,`Email`=?,`Phone`=? WHERE `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_updateUser);
							
			prep_sql.setString(1, user.getUserName());
			prep_sql.setString(2, user.getName());
			prep_sql.setString(3, user.getLastName());
			prep_sql.setString(4, user.getEmail());
			prep_sql.setString(5, user.getAbout());
			prep_sql.setInt(6, currentUser.getUserID());
	
			prep_sql.executeUpdate();
//			int  rs_query_updateUser = prep_sql.executeUpdate();
//			
//			if (rs_query_updateUser == 0){
//				return Response.status(Response.Status.CONFLICT).entity("Username or email already in use").build();			
//			}               
//	        
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			//return Response.status(Response.Status.CONFLICT).entity("Error updating user").build();
			return Response.status(Response.Status.CONFLICT).entity("Username or email already in use").build();
				
		}

	link.Close_link();
	
	return Response.status(Response.Status.OK).build();
	}
	*/
	
	@Logged
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers) {
			
		Session session = new Session(headers);
        User currentUser = session.getUser();

		System.out.println("["+currentUser.getUserName()+"] [GET] /user");
        
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(currentUser);			
		} catch (JsonProcessingException e) {			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}

	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();	  
	}
	
	
	@Logged
	@GET
	@Path("{profileID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@PathParam("profileID") int profileID, @Context HttpHeaders headers) {
			
		Session session = new Session(headers);
        User currentUser = session.getUser();
        int userID = currentUser.getUserID(); 

		System.out.println("["+currentUser.getUserName()+"] [GET] /user");
        
		User profile = new User();
		
		String DataSourceName = "jdbc/MySQLDataSource";
		  DataSource ds;
		  Connection con = null;
		  
		  try {   
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
			PreparedStatement ps_query_session=null;
			ResultSet rs_query_session=null;
			PreparedStatement ps_query_getFollow=null;
			ResultSet rs_query_getFollow=null;
			PreparedStatement ps_query_getFollowing=null;
			ResultSet rs_query_getFollowing=null;
			PreparedStatement ps_query_getFollower=null;
			ResultSet rs_query_getFollower=null;
			try{
				String query_session = "SELECT * "
						+ "FROM `User` where`UserID`=?;";
				
				ps_query_session = con.prepareStatement(query_session);
				ps_query_session.setInt(1, profileID);
				
				rs_query_session = ps_query_session.executeQuery();

				if (!rs_query_session.next() ) {
					System.out.println("rs_query_session no data");
			  		return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
				} else {
					profile.setUserID(rs_query_session.getInt("UserID"));  
					profile.setUserName(rs_query_session.getString("UserName"));
					profile.setName(rs_query_session.getString("Name"));
					profile.setLastname(rs_query_session.getString("LastName"));
					profile.setEmail(rs_query_session.getString("Email"));
					profile.setAbout(rs_query_session.getString("About"));
//					this.sessionID = (rs_query_session.getInt("SessionID"));
				}
				
				
				ps_query_getFollow = con.prepareStatement("SELECT * from Following where UserID=? AND FollowingUserID=?;");
		    	ps_query_getFollow.setInt(1, userID);
		    	ps_query_getFollow.setInt(2, profileID);
		    	rs_query_getFollow = ps_query_getFollow.executeQuery();
		    	
		    	rs_query_getFollow.next();
		    	try{
		    		rs_query_getFollow.getInt("FollowingID");
		    		System.out.println("Follow found");
					profile.setFollow(1);
		    	}catch (Exception e){
		    		System.out.println("Follow not found");
					profile.setFollow(0);
		    	}
		    	
				ps_query_getFollowing = con.prepareStatement("SELECT count(*) as total from Following where UserID=?;");
				ps_query_getFollowing.setInt(1, profileID);
		    	rs_query_getFollowing = ps_query_getFollowing.executeQuery();
		    	
		    	rs_query_getFollowing.next();
				profile.setFollowing(rs_query_getFollowing.getInt("total"));
				
				ps_query_getFollower = con.prepareStatement("SELECT count(*) as total from Following where FollowingUserID=?;");
				ps_query_getFollower.setInt(1, profileID);;
				rs_query_getFollower = ps_query_getFollower.executeQuery();
		    	
				rs_query_getFollower.next();
				profile.setFollower(rs_query_getFollower.getInt("total"));
				
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());		
		  		return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
			} finally {
			      try {
			          if (ps_query_session != null) {
			        	  ps_query_session.close();
			          }
			          if (rs_query_session != null) {
			        	  rs_query_session.close();
			          }
			          if (ps_query_getFollow != null) {
			        	  ps_query_getFollow.close();
			          }
			          if (rs_query_getFollow != null) {
			        	  rs_query_getFollow.close();
			          }
			          if (ps_query_getFollowing != null) {
			        	  ps_query_getFollowing.close();
			          }
			          if (rs_query_getFollowing != null) {
			        	  rs_query_getFollowing.close();
			          }
			          if (rs_query_getFollower != null) {
			        	  rs_query_getFollower.close();
			          }
			          if (rs_query_getFollower != null) {
			        	  rs_query_getFollower.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
			  }	
			
			
		  } catch (Exception e) {
		      System.out.println(e);
		  } finally {

		      try {
		          if (con != null) {
		              con.close();
		          }
		      } catch (SQLException sqle) {
			      System.out.println(sqle);
		      }
		  }
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(profile);			
		} catch (JsonProcessingException e) {			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}

	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();	  
	}
	
} 