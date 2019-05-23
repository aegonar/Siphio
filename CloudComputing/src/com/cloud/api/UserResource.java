package com.cloud.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
} 