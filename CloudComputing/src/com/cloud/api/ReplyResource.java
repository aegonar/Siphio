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
//import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
//import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

//import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/reply")
public class ReplyResource {
	
	@GET
	@Path("{ReplyID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(@PathParam("ReplyID") int ReplyID, @Context HttpHeaders headers) {

		try{	
			Session session = new Session(headers);
			User currentUser = session.getUser();       
	 		System.out.println("["+currentUser.getUserName()+"] [GET] /reply"); 
		}catch (NotAuthorizedException e){
			System.out.println("[GET] /reply");
		}


	  Reply reply = new Reply();
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
	
		PreparedStatement ps_query_getReplies=null;
		ResultSet rs_query_getReplies=null;
		try{
			String query_getReplies = "SELECT ReplyID, PostID, ReplyMessage, Reply.UserId, "
					+ "VoteCount, Datetime, UserName, Name, Lastname "
					+ "from Reply, User "
					+ "where User.UserID=Reply.UserID AND ReplyID=?;";
			
			ps_query_getReplies = con.prepareStatement(query_getReplies);	
			ps_query_getReplies.setInt(1, ReplyID);			
			rs_query_getReplies= ps_query_getReplies.executeQuery();
			
				while(rs_query_getReplies.next()){
							
					reply.setReplyID(rs_query_getReplies.getInt("ReplyID"));
					reply.setPostID(rs_query_getReplies.getInt("PostID"));
					reply.setUserID(rs_query_getReplies.getInt("UserID"));

					reply.setReplyMessage(rs_query_getReplies.getString("ReplyMessage"));
					
					reply.setVoteCount(rs_query_getReplies.getInt("VoteCount"));
					
					Timestamp myTimestamp = rs_query_getReplies.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					reply.setDateTime(S);
					
					reply.setUserName(rs_query_getReplies.getString("UserName"));
					reply.setName(rs_query_getReplies.getString("Name"));
					reply.setLastname(rs_query_getReplies.getString("Lastname"));					

				}
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();			
		} finally {
		      try {
		          if (ps_query_getReplies != null) {
		        	  ps_query_getReplies.close();
		          }
		          if (rs_query_getReplies != null) {
		        	  rs_query_getReplies.close();
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
		jsonString = mapper.writeValueAsString(reply);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }

	@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(String reply, @Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /reply");

      System.out.println(reply);
      
      	ObjectMapper mapper = new ObjectMapper();
		Reply replyFromJSON = null;
			
		try {
			replyFromJSON = mapper.readValue(reply, Reply.class);
		} catch (Exception e1) {
			System.out.println("Error mapping from json: " + e1.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid input").build();
		}

		if(replyFromJSON.getReplyMessage()==null){
			return Response.status(Response.Status.FORBIDDEN).entity("Invalid input").build();
		}
		
		int replyID=0;
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
		
		PreparedStatement ps_query_setReply=null;
		PreparedStatement ps_query_setReplyCount=null;
		PreparedStatement ps_query_getCallback=null;
		ResultSet rs_query_getCallback=null;
		PreparedStatement ps_query_clearCallback=null;
 		
	    try{    
	    	String query_setToken = "INSERT INTO `Reply` (`PostID`,`UserID`,`ReplyMessage`,`VoteCount`,`Votes`, `Callback`) "
	    							+ "values (?,?,?,?,?,?);";
	    	
	    	ps_query_setReply = con.prepareStatement(query_setToken);
	    	ps_query_setReply.setInt(1, replyFromJSON.getPostID());
	    	ps_query_setReply.setInt(2, userID);
	    	ps_query_setReply.setString(3, replyFromJSON.getReplyMessage());
	    	ps_query_setReply.setInt(4, 1);
	    	
		     HashMap<Integer, Integer> voteMap = new HashMap<Integer, Integer>();
		     voteMap.put(userID,1);
		     
		     	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    ObjectOutputStream oos = new ObjectOutputStream(baos);
			    oos.writeObject(voteMap);
			    byte[] hashMapAsBytes = baos.toByteArray();
			    ByteArrayInputStream bais = new ByteArrayInputStream(hashMapAsBytes);
			    ps_query_setReply.setBinaryStream(5, bais, hashMapAsBytes.length);

		    Random random = new SecureRandom();
		    String callback = new BigInteger(130, random).toString(32).substring(0,16);
		    ps_query_setReply.setString(6, callback);
			    
	    	ps_query_setReply.executeUpdate();
	    	
	    	//-------------
		    
	    	String query_setReplyCount = "UPDATE Post SET ReplyCount=ReplyCount+1, `Datetime`=`Datetime` where PostID=?;";	    	
	    	ps_query_setReplyCount = con.prepareStatement(query_setReplyCount);
	    	ps_query_setReplyCount.setInt(1, replyFromJSON.getPostID());			
	    	ps_query_setReplyCount.executeUpdate();
	    	
  		    //-------------
	    	    	
	    	String query_getCallback = "Select ReplyID from Reply where UserID=? AND PostID=? AND Callback=?";	    	
	    	ps_query_getCallback = con.prepareStatement(query_getCallback);
	    	ps_query_getCallback.setInt(1, userID);	
	    	ps_query_getCallback.setInt(2, replyFromJSON.getPostID());
	    	ps_query_getCallback.setString(3, callback);
	    	rs_query_getCallback = ps_query_getCallback.executeQuery();
		    
		    rs_query_getCallback.next();
		    
		    try{		    	
		    	replyID = rs_query_getCallback.getInt("ReplyID");
		    }catch (Exception e){
		    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error Saving Reply: Callback").build();
		    }
		    
		    //-------------
		    
	    	String query_clearCallback = "UPDATE Reply SET `Callback`=null, `Datetime`=`Datetime` where PostID=? AND UserID=? AND ReplyID=?;";	    	
	    	ps_query_clearCallback = con.prepareStatement(query_clearCallback);
	    	ps_query_clearCallback.setInt(1, replyFromJSON.getPostID());
	    	ps_query_clearCallback.setInt(2, userID);
	    	ps_query_clearCallback.setInt(3, replyID);
	    	ps_query_clearCallback.executeUpdate();
	    	
	    	    				
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving reply").build();
		  } finally {
	
		      try {
		          if (ps_query_setReply != null) {
		        	  ps_query_setReply.close();
		          }
		          if (ps_query_setReplyCount != null) {
		        	  ps_query_setReplyCount.close();
		          }
		          if (ps_query_getCallback != null) {
		        	  ps_query_getCallback.close();
		          }
		          if (rs_query_getCallback != null) {
		        	  rs_query_getCallback.close();
		          }
		          if (ps_query_clearCallback != null) {
		        	  ps_query_clearCallback.close();
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

//	return Response.status(Response.Status.OK).build();
    return Response.ok("{\"replyID\":"+replyID+"}", MediaType.APPLICATION_JSON).build();
  }

} 