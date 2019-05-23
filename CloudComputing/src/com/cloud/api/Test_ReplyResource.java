package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.NotAuthorizedException;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
//import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import java.util.HashMap;

//import java.util.ArrayList;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/test/reply")
public class Test_ReplyResource {

	@Logged
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProfile(@QueryParam("postID") int postID, @QueryParam("replyMessage") String replyMessage, @Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [GET] /test/reply");

//      System.out.println(reply);
//      
//      	ObjectMapper mapper = new ObjectMapper();
//		Reply replyFromJSON = null;
//			
//		try {
//			replyFromJSON = mapper.readValue(reply, Reply.class);
//		} catch (Exception e1) {
//			System.out.println("Error mapping from json: " + e1.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid input").build();
//		}
//
//		if(replyFromJSON.getReplyMessage()==null){
//			return Response.status(Response.Status.FORBIDDEN).entity("Invalid input").build();
//		}
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
		
		PreparedStatement ps_query_setReply=null;
		PreparedStatement ps_query_setReplyCount=null;
 		
	    try{    
	    	String query_setToken = "INSERT INTO `Reply` (`PostID`,`UserID`,`ReplyMessage`,`VoteCount`,`Votes`) "
	    							+ "values (?,?,?,?,?);";
	    	
	    	ps_query_setReply = con.prepareStatement(query_setToken);
	    	ps_query_setReply.setInt(1, postID);
	    	ps_query_setReply.setInt(2, userID);
	    	ps_query_setReply.setString(3, replyMessage);
	    	ps_query_setReply.setInt(4, 1);
	    	
		     HashMap<Integer, Integer> voteMap = new HashMap<Integer, Integer>();
		     voteMap.put(userID,1);
		     
		     	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    ObjectOutputStream oos = new ObjectOutputStream(baos);
			    oos.writeObject(voteMap);
			    byte[] hashMapAsBytes = baos.toByteArray();
			    ByteArrayInputStream bais = new ByteArrayInputStream(hashMapAsBytes);
			    ps_query_setReply.setBinaryStream(5, bais, hashMapAsBytes.length);

	    	ps_query_setReply.executeUpdate();
	    	
  		    //-------------
	    	
	    	String query_setReplyCount = "UPDATE Post SET ReplyCount=ReplyCount+1, `Datetime`=`Datetime` where PostID=?;";	    	
	    	ps_query_setReplyCount = con.prepareStatement(query_setReplyCount);
	    	ps_query_setReplyCount.setInt(1, postID);			
	    	ps_query_setReplyCount.executeUpdate();
				
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

	return Response.status(Response.Status.OK).build();
  }

} 