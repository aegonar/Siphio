package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;

//import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/post")
public class PostResource {
	
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

	@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(String post, @Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /post");
      
      System.out.println(post);
      
      	ObjectMapper mapper = new ObjectMapper();
		Post postFromJSON = null;
			
		try {
			postFromJSON = mapper.readValue(post, Post.class);
		} catch (Exception e1) {
			System.out.println("Error mapping from json: " + e1.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid input").build();
		}
      
		int postID=0;
		
		  String DataSourceName = "jdbc/MySQLDataSource";
		  DataSource ds;
		  Connection con = null;
		  
		  try {  
				InitialContext ctx = new InitialContext();
				ds = (DataSource)ctx.lookup(DataSourceName);			
				con = ds.getConnection();
							
			PreparedStatement ps_query_setPost=null;
			PreparedStatement ps_query_getCallback=null;
			ResultSet rs_query_getCallback=null;
			PreparedStatement ps_query_clearCallback=null;
	 		
		    try{    
				String query_setPost = "INSERT INTO Post (`UserID`,`PostMessage`,`PostLink`, `Callback`) VALUES (?,?,?,?);";				
				ps_query_setPost = con.prepareStatement(query_setPost);
				ps_query_setPost.setInt(1, userID);
				ps_query_setPost.setString(2, postFromJSON.getPostMessage());
				ps_query_setPost.setString(3, postFromJSON.getPostLink());
				
			    Random random = new SecureRandom();
			    String callback = new BigInteger(130, random).toString(32).substring(0,16);
			    ps_query_setPost.setString(4, callback);
				
				ps_query_setPost.executeUpdate();
				
	  		    //-------------
    	    	
		    	String query_getCallback = "Select PostID from Post where UserID=? AND Callback=?";	    	
		    	ps_query_getCallback = con.prepareStatement(query_getCallback);
		    	ps_query_getCallback.setInt(1, userID);	
		    	ps_query_getCallback.setString(2, callback);
		    	rs_query_getCallback = ps_query_getCallback.executeQuery();
			    
			    rs_query_getCallback.next();
			    
			    try{		    	
			    	postID = rs_query_getCallback.getInt("PostID");
			    }catch (Exception e){
			    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Saving Post: Callback").build();
			    }
			    
			    //-------------
			    
		    	String query_clearCallback = "UPDATE Post SET `Callback`=null, `Datetime`=`Datetime` where UserID=? AND PostID=?;";	    	
		    	ps_query_clearCallback = con.prepareStatement(query_clearCallback);
		    	ps_query_clearCallback.setInt(1, userID);
		    	ps_query_clearCallback.setInt(2, postID);
		    	ps_query_clearCallback.executeUpdate();
					
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
				
//	return Response.status(Response.Status.OK).build();
		  return Response.ok("{\"postID\":"+postID+"}", MediaType.APPLICATION_JSON).build();
  
  }
} 