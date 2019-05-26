package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import java.io.ByteArrayInputStream;
//import java.io.ObjectInputStream;
//import java.math.BigInteger;
//import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/find")
public class FindResource {
	

	@Logged
	@GET
	@Path("{Query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getThread(@PathParam("Query") String Query, @Context HttpHeaders headers) {
	
		Session session = new Session(headers);
		User currentUser = session.getUser(); 
//		int UserID = currentUser.getUserID();
//		int SessionID = session.getSessionID();
						
 		System.out.println("["+currentUser.getUserName()+"] [GET] /find/"+Query);
 		
 		ArrayList<Integer> results = new ArrayList<Integer>();
 		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  String[] query = Query.split("\\s+");

	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
	 		//-----------------------------------------
	 		


 			PreparedStatement ps_query_getResults=null;
 			ResultSet rs_query_getResults=null;
			
				try{
					String query_getResults = "SELECT * FROM User WHERE Name LIKE ? AND Lastname LIKE ?";
	
					ps_query_getResults = con.prepareStatement(query_getResults);	
					
//					ps_query_getResults.setString(1, "\'%"+query[0]+"%\'");
//					ps_query_getResults.setString(2, "\'%"+query[1]+"%\'");		
					
					try{
						ps_query_getResults.setString(1, "%"+query[0]+"%");
						System.out.println("Query "+ query[0]);
					}catch (Exception e){
						ps_query_getResults.setString(1, "%%");
					}
					try{
						ps_query_getResults.setString(2, "%"+query[1]+"%");	
						System.out.println("Query "+ query[1]);
					}catch (Exception e){
						ps_query_getResults.setString(2, "%%");
					}
											
					rs_query_getResults= ps_query_getResults.executeQuery();
					System.out.println(ps_query_getResults.toString());
					
						while(rs_query_getResults.next()){																					
							System.out.println(rs_query_getResults.getInt("UserID"));
							results.add(rs_query_getResults.getInt("UserID"));
						}
						
				}catch(Exception e){
					System.out.println("Error at rs_query_getResults: " + e.getMessage());
				      try {
				          if (con != null) {
				              con.close();
				          }
				      } catch (SQLException sqle) {
					      System.out.println(sqle);
				      }
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();
			  } finally {
			      try {
			          if (ps_query_getResults != null) {
			        	  ps_query_getResults.close();
			          }
			          if (rs_query_getResults != null) {
			        	  rs_query_getResults.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
			  }	

			
		//-----------------------------------------
			
//			postThread.setReplies(replies);
//			postThread.setPost(post);
			
		
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
			jsonString = mapper.writeValueAsString(results);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}

	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  }
	
	
} 