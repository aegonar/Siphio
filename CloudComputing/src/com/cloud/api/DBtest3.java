package com.cloud.api;

import java.sql.*;
import javax.sql.DataSource;

import javax.naming.InitialContext;


import java.text.SimpleDateFormat;


import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/test3")
public class DBtest3 {

	
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response test() {

	  String DataSourceName = "jdbc/MySQLDataSource";

	  DataSource ds;
	  Connection con = null;
	  //Statement st = null;
	  PreparedStatement ps=null;
	  ResultSet rs = null;
	  Post post = new Post();
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
	     // st = con.createStatement();
	      
			String query_getPosts = "SELECT * FROM Post where `PostID` = ?";
			ps = con.prepareStatement(query_getPosts);
			ps.setInt(1, 1);
			rs = ps.executeQuery();

	      while (rs.next()) {
	    	  	post.setPostID(rs.getInt("PostID"));
				post.setUserID(rs.getInt("UserID"));

				post.setPostMessage(rs.getString("PostMessage"));
				post.setPostLink(rs.getString("PostLink"));
				
				post.setFavoriteCount(rs.getInt("FavoriteCount"));
				post.setReplyCount(rs.getInt("ReplyCount"));
				
				Timestamp myTimestamp = rs.getTimestamp("DateTime");
				String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
				post.setDateTime(S);
	      }
	  } catch (Exception e) {
	      System.out.println(e);
	  } finally {
	      //closing the resources in this transaction
	      //similar logic than the used in the last close block code
	      try {
	          if (rs != null) {
	              rs.close();
	          }
	          if (ps != null) {
	        	  ps.close();
	          }
	          //at the last of all the operations, close the connection
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
			//System.out.println("json mapping");
			jsonString = mapper.writeValueAsString(post);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}
		
		//System.out.println("mapped");
		 return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();

	  
  }
  
} 