package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/follow")
public class FollowResource {

	@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postVote(@QueryParam("profileID") int profileID, @QueryParam("follow") int follow,
								@Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /follow?profileID="+profileID+"&follow="+follow);
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  	  
  	  if(follow > 1 || follow <0){
  		return Response.status(Response.Status.BAD_REQUEST).entity("Invalid follow").build();
  	  }
  	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
		PreparedStatement ps_query_getFollow=null;
		ResultSet rs_query_getFollow=null;
		PreparedStatement ps_query_setFollow=null;	
		PreparedStatement ps_query_deleteFollow=null;
 		
	    try{
	    	boolean found=false;
	    	
	    	ps_query_getFollow = con.prepareStatement("SELECT * from Following where UserID=? AND FollowingUserID=?;");
	    	ps_query_getFollow.setInt(1, userID);
	    	ps_query_getFollow.setInt(2, profileID);
	    	rs_query_getFollow = ps_query_getFollow.executeQuery();
	    	
	    	rs_query_getFollow.next();
	    	int followingID=0;
	    	try{
	    		followingID = rs_query_getFollow.getInt("FollowingID");
	    		System.out.println("Follow found");
	    		found=true;
	    	}catch (Exception e){
	    		System.out.println("Follow not found");
	    	}

	    	if(follow==1 && !found){
	    		ps_query_setFollow = con.prepareStatement("INSERT INTO Following (`UserID`,`FollowingUserID`) VALUES (?,?);");
	    		ps_query_setFollow.setInt(1, userID);
	    		ps_query_setFollow.setInt(2, profileID);
	    		ps_query_setFollow.executeUpdate();
		    	  		
	    		System.out.println("Follow set");
	    	}else if(follow==0 && found){
	    		ps_query_deleteFollow = con.prepareStatement("DELETE FROM Following WHERE FollowingID=?;");
	    		ps_query_deleteFollow.setInt(1, followingID);
	    		ps_query_deleteFollow.executeUpdate();
	    		
	    		System.out.println("Follow unset");
	    	}else{
		    	return Response.status(Response.Status.OK).entity("Already set").build();
	    	}
	    	
   
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error voting").build();
		} finally {
	
		    try {
		        if (ps_query_getFollow != null) {
		        	ps_query_getFollow.close();
		        }
		        if (rs_query_getFollow != null) {
		        	rs_query_getFollow.close();
		        }
		        if (ps_query_setFollow != null) {
		        	ps_query_setFollow.close();
			    }
		        if (ps_query_deleteFollow != null) {
		        	ps_query_deleteFollow.close();
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
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving follow").build();
	      }
	  }

	return Response.status(Response.Status.OK).build();
  }

} 