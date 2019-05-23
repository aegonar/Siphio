package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
//import javax.ws.rs.NotAuthorizedException;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
//import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/test/post")
public class Test_PostResource {
	/*
	@GET
	@Path("{PostID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(@PathParam("PostID") int PostID, @Context HttpHeaders headers) {

	try{	
		Session session = new Session(headers);
		User currentUser = session.getUser();       
 		System.out.println("["+currentUser.getUserName()+"] [GET] /post"); 
	}catch (NotAuthorizedException e){
		System.out.println("[GET] /post");
	}

	Post post = new Post();
	
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
					
		PreparedStatement ps_query_getPost=null;
		ResultSet rs_query_getPost=null;
		
		try{
			String query_getPost = "SELECT PostID, Post.UserID, PostMessage, PostLink, "
					+ "FavoriteCount, ReplyCount, DateTime, UserName, Name, Lastname "
					+ "FROM Post, User where User.UserID=Post.UserID AND PostID=?;";
			
			ps_query_getPost = con.prepareStatement(query_getPost);			
			ps_query_getPost.setInt(1, PostID);			
			rs_query_getPost= ps_query_getPost.executeQuery();			
			rs_query_getPost.next();
								
				post.setPostID(rs_query_getPost.getInt("PostID"));
				post.setUserID(rs_query_getPost.getInt("UserID"));

				post.setPostMessage(rs_query_getPost.getString("PostMessage"));
				post.setPostLink(rs_query_getPost.getString("PostLink"));
				
				post.setFavoriteCount(rs_query_getPost.getInt("FavoriteCount"));
				post.setReplyCount(rs_query_getPost.getInt("ReplyCount"));
				
				Timestamp myTimestamp = rs_query_getPost.getTimestamp("DateTime");
				String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
				post.setDateTime(S);
				
				post.setUserName(rs_query_getPost.getString("UserName"));
				post.setName(rs_query_getPost.getString("Name"));
				post.setLastname(rs_query_getPost.getString("Lastname"));

		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());	
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();			
		} finally {			
		      try {
		          if (ps_query_getPost != null) {
		        	  ps_query_getPost.close();
		          }
		          if (rs_query_getPost != null) {
		        	  rs_query_getPost.close();
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
				System.out.println("Error: " + sqle.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving reply").build();
	      }
	  }
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(post);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
	
	
//	@Logged
	/*
	@GET
	@Path("{UserID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(@PathParam("UserID") int UserID, @Context HttpHeaders headers) {

	try{	
		Session session = new Session(headers);
		User currentUser = session.getUser();       
 		System.out.println("["+currentUser.getUserName()+"] [GET] /post/"); 
	}catch (NotAuthorizedException e){
		System.out.println("[GET] /post/");
	}

	link.Open_link();
		
	ArrayList<Post> list = new ArrayList<Post>();
		
		try{
			String query_getPosts = "SELECT * FROM Post where `UserID` = ?";
			prep_sql = link.linea.prepareStatement(query_getPosts);
			
			prep_sql.setInt(1, UserID);
			
			ResultSet rs_query_getPosts= prep_sql.executeQuery();
			
				while(rs_query_getPosts.next()){
					
					Post post = new Post();
							
					post.setPostID(rs_query_getPosts.getInt("PostID"));
					post.setUserID(rs_query_getPosts.getInt("UserID"));

					post.setPostMessage(rs_query_getPosts.getString("PostMessage"));
					post.setPostLink(rs_query_getPosts.getString("PostLink"));
					
					post.setFavoriteCount(rs_query_getPosts.getInt("FavoriteCount"));
					post.setReplyCount(rs_query_getPosts.getInt("ReplyCount"));
					
					Timestamp myTimestamp = rs_query_getPosts.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					post.setDateTime(S);
					
					list.add(post);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();
			
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(list);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
*/
	@Logged
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(@QueryParam("postMessage") String postMessage, @QueryParam("postLink") String postLink, @Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [GET] /test/post");
      
//      	ObjectMapper mapper = new ObjectMapper();
//		Post postFromJSON = null;
//			
//		try {
//			postFromJSON = mapper.readValue(post, Post.class);
//		} catch (Exception e1) {
//			System.out.println("Error mapping from json: " + e1.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid input").build();
//		}
      
		  String DataSourceName = "jdbc/MySQLDataSource";
		  DataSource ds;
		  Connection con = null;
		  
		  try {  
				InitialContext ctx = new InitialContext();
				ds = (DataSource)ctx.lookup(DataSourceName);			
				con = ds.getConnection();
							
			PreparedStatement ps_query_setPost=null;
	 		
		    try{    
				String query_setPost = "INSERT INTO Post (`UserID`,`PostMessage`,`PostLink`) VALUES (?,?,?);";				
				ps_query_setPost = con.prepareStatement(query_setPost);
				ps_query_setPost.setInt(1, userID);
				ps_query_setPost.setString(2, postMessage);
				ps_query_setPost.setString(3, postLink);
				ps_query_setPost.executeUpdate();
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving post").build();
			  } finally {
		
			      try {
			          if (ps_query_setPost != null) {
			        	  ps_query_setPost.close();
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
					System.out.println("Error: " + sqle.getMessage());
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving post").build();
		      }
		  }
				
	return Response.status(Response.Status.OK).build();
  
  }

	/*
	//@Logged
	@GET
	@Path("{PostID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@PathParam("PostID") int PostID, @Context HttpHeaders headers) {
	  
	try{	
	  Session session = new Session(headers);
      User currentUser = session.getUser();       
      System.out.println("["+currentUser.getUserName()+"] [GET] /post/"+PostID); 
	}catch (NotAuthorizedException e){
      System.out.println("[GET] /post/"+PostID);
	}
	
	  link.Open_link();
			  
	  Post post = new Post();
		
		try{

			String query_getPost = "SELECT * FROM Post where `PostID` = ?";
			prep_sql = link.linea.prepareStatement(query_getPost);
			
			prep_sql.setInt(1, PostID);
			
			ResultSet rs_query_getPosts= prep_sql.executeQuery();
			
			if (!rs_query_getPosts.next() ) {
				System.out.println("rs_query_getPosts no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
				
			} else {
				post.setPostID(rs_query_getPosts.getInt("PostID"));
				post.setUserID(rs_query_getPosts.getInt("UserID"));

				post.setPostMessage(rs_query_getPosts.getString("PostMessage"));
				post.setPostLink(rs_query_getPosts.getString("PostLink"));
				
				post.setFavoriteCount(rs_query_getPosts.getInt("FavoriteCount"));
				post.setReplyCount(rs_query_getPosts.getInt("ReplyCount"));
				
				Timestamp myTimestamp = rs_query_getPosts.getTimestamp("DateTime");
				String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
				post.setDateTime(S);
				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profile").build();
				
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(post);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }
	*/
	/*
	@Logged
	@DELETE
	@Path("{PostID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProfile(@PathParam("PostID") int PostID, @Context HttpHeaders headers) {

	  Session session = new Session(headers);
      User currentUser = session.getUser();       
      int userID=currentUser.getUserID();
	  
      System.out.println("["+currentUser.getUserName()+"] [DELETE] /post/"+PostID);
      
	  link.Open_link();
		
		try{
			String query_deletePost = "DELETE FROM Post where `UserID` = ? AND `PostID` = ?";
			prep_sql = link.linea.prepareStatement(query_deletePost);
			
			prep_sql.setInt(1, userID);
			prep_sql.setInt(2, PostID);
			
			int rs_query_deletePost=prep_sql.executeUpdate();

			if (rs_query_deletePost == 0){
				System.out.println("rs_query_deletePost no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Post not found").build();
			}	

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting post").build();
				
		}

	link.Close_link();
	
	return Response.status(Response.Status.OK).build();
  
  }
/*	
	@Logged
	@PUT
	@Path("{PetProfileID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(@PathParam("PetProfileID") String PetProfileID, PetProfile profile, @Context HttpHeaders headers) {
	
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
	  int userID = currentUser.getUserID();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /profiles/"+PetProfileID);
	  
	  link.Open_link();
			
		try{
			String query_putProfile = "UPDATE PetProfiles SET `PetProfileID`=?,`Day_Temperature_SP`=?,`Day_Humidity_SP`=?,`Night_Temperature_SP`=?,`Night_Humidity_SP`=?,`Temperature_TH`=?,`Humidity_TH`=?,`DayTime`=?,`NightTime`=? WHERE `PetProfileID`=? AND `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_putProfile);
			
			prep_sql.setString(1, profile.getPetProfileID());
			prep_sql.setFloat(2, profile.getDay_Temperature_SP());
			prep_sql.setFloat(3, profile.getDay_Humidity_SP());
			prep_sql.setFloat(4, profile.getNight_Temperature_SP());
			prep_sql.setFloat(5, profile.getNight_Humidity_SP());
			prep_sql.setFloat(6, profile.getTemperature_TH());
			prep_sql.setFloat(7, profile.getHumidity_TH());
			prep_sql.setString(8, profile.getDayTime());
			prep_sql.setString(9, profile.getNightTime());
			prep_sql.setString(10, PetProfileID);
			prep_sql.setInt(11, currentUser.getUserID());
					
			int rs_query_putProfile=prep_sql.executeUpdate();

			if (rs_query_putProfile == 0){
				System.out.println("rs_query_putProfile no data");
				link.Close_link();
				return Response.status(Response.Status.NOT_FOUND).entity("Profile not found").build();
			}

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Profile exists").build();
			
		}

	link.Close_link();
	
	SendToCentralNode sendToCentralNode = new SendToCentralNode(profile, "PUT", "IoT/profiles/"+PetProfileID);
	sendToCentralNode.sendToUser(userID);
	
	return Response.status(Response.Status.OK).build();
  
  }	
*/
} 