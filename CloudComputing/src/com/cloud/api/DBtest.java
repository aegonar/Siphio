package com.cloud.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/test")
public class DBtest {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response test() {

	  System.out.println("DB test called");
	  
		PreparedStatement prep_sql;
		javax.sql.DataSource ds;
		java.sql.Connection conn = null;
		
		try{
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			ds = (javax.sql.DataSource)ctx.lookup("jdbc/MySQLDataSource");
			conn = ds.getConnection();
			
			 System.out.println("connected");
		}catch(Exception e){
			 System.out.println("error connecting");
			System.out.println("Error: " + e.getMessage());
		}
		
		//ArrayList<Post> list = new ArrayList<Post>();
		Post post = new Post();
			
			try{
				 System.out.println("Select statement");
				
				String query_getPosts = "SELECT * FROM Post where `PostID` = ?";
				prep_sql = conn.prepareStatement(query_getPosts);
				
				prep_sql.setInt(1, 1);
				
				ResultSet rs_query_getPosts= prep_sql.executeQuery();
				
				 System.out.println("ResultSet found");
				
					while(rs_query_getPosts.next()){
						
						
								
						post.setPostID(rs_query_getPosts.getInt("PostID"));
						post.setUserID(rs_query_getPosts.getInt("UserID"));

						post.setPostMessage(rs_query_getPosts.getString("PostMessage"));
						post.setPostLink(rs_query_getPosts.getString("PostLink"));
						
						post.setFavoriteCount(rs_query_getPosts.getInt("FavoriteCount"));
						post.setReplyCount(rs_query_getPosts.getInt("ReplyCount"));
						
						Timestamp myTimestamp = rs_query_getPosts.getTimestamp("DateTime");
						String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
						post.setDateTime(S);
						
						//list.add(post);

					}
			}catch(Exception e){
				 System.out.println("error on select");
				System.out.println("Error: " + e.getMessage());
				
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();
				
			}

		try {
			conn.close();
			 System.out.println("conection closed");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			 System.out.println("error closing");
			e1.printStackTrace();
		}
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			System.out.println("json mapping");
			jsonString = mapper.writeValueAsString(post);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}
		
		System.out.println("mapped");
		 return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();

	  
  }
  
} 