package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
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


@Path("/favorite")
public class FavoriteResource {
	
	@Logged
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postVote(@QueryParam("postID") int postID, @QueryParam("favorite") int favorite,
								@Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /favorite?postID="+postID+"&favorite="+favorite);
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;

//  	  int favoriteCount=0;
	  
  	  if(favorite > 1 || favorite <0){
  		return Response.status(Response.Status.BAD_REQUEST).entity("Invalid favorite").build();
  	  }
  	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
		PreparedStatement ps_query_getFavoriteCount=null;
		ResultSet rs_query_getFavoriteCount=null;
		PreparedStatement ps_query_setFavoriteCount=null;			
		PreparedStatement ps_query_setFavorite=null;
 		
	    try{
	    		    	
	    	ps_query_getFavoriteCount = con.prepareStatement("SELECT * FROM Favorite WHERE UserID=? AND PostID=?;");
	    	ps_query_getFavoriteCount.setInt(1, userID);
	    	ps_query_getFavoriteCount.setInt(2, postID);
	    	rs_query_getFavoriteCount = ps_query_getFavoriteCount.executeQuery();

		    rs_query_getFavoriteCount.next();
		    try{
		    	rs_query_getFavoriteCount.getInt("FavoriteID");
		    	if(favorite==1){
		    		return Response.status(Response.Status.FORBIDDEN).entity("{\"message\":\"Already Favorite\"}").build();
		    	}
		    }catch (Exception e){
		    	if(favorite==0){
		    		return Response.status(Response.Status.FORBIDDEN).entity("{\"message\":\"Not a Favorite\"}").build();
		    	}
		    }
    
		    ps_query_setFavoriteCount = con.prepareStatement("UPDATE Post SET FavoriteCount=FavoriteCount+?, "
		    										+ "`Datetime`=`Datetime` where PostID=?;");
		    if(favorite==1){
		    	ps_query_setFavoriteCount.setInt(1, 1);
		    }else{
		    	ps_query_setFavoriteCount.setInt(1, -1);
		    }
		    ps_query_setFavoriteCount.setInt(2, postID);
		    ps_query_setFavoriteCount.executeUpdate();
			   	
		    if(favorite==1){
			    ps_query_setFavorite = con.prepareStatement("INSERT INTO Favorite (UserID, PostID) Values (?,?);");
			    ps_query_setFavorite.setInt(1, userID);	    
			    ps_query_setFavorite.setInt(2, postID);
			    ps_query_setFavorite.executeUpdate();
		    }else{
			    ps_query_setFavorite = con.prepareStatement("DELETE FROM Favorite where UserID=? AND PostID=?");
			    ps_query_setFavorite.setInt(1, userID);	    
			    ps_query_setFavorite.setInt(2, postID);
			    ps_query_setFavorite.executeUpdate();
		    }
 
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error voting").build();
		} finally {
	
		    try {
		        if (ps_query_getFavoriteCount != null) {
		        	ps_query_getFavoriteCount.close();
		        }
		        if (rs_query_getFavoriteCount != null) {
		        	rs_query_getFavoriteCount.close();
		        }
		        if (ps_query_setFavoriteCount != null) {
		        	ps_query_setFavoriteCount.close();
			    }
		        if (ps_query_setFavorite != null) {
		        	ps_query_setFavorite.close();
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