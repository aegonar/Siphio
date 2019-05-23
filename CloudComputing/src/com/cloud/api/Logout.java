package com.cloud.api;


import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/logout")

public class Logout{

	@POST
    public Response delteToken(@Context HttpHeaders headers) {

		System.out.println("POST] /logout");
		
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

          String token = authorizationHeader.substring("Bearer".length()).trim();

    	  String DataSourceName = "jdbc/MySQLDataSource";
      	  DataSource ds;
      	  Connection con = null;
      	      	
        	 try {  
     			InitialContext ctx = new InitialContext();
     			ds = (DataSource)ctx.lookup(DataSourceName);			
     			con = ds.getConnection();			
        	
        	  	PreparedStatement ps_query_deleteToken=null;
        	  	try{
        			String query_deleteToken= "Delete FROM Session where `Token` = ?;";
        			ps_query_deleteToken = con.prepareStatement(query_deleteToken);
        			ps_query_deleteToken.setString(1, token);
        			
          			int rs_query_deleteToken = ps_query_deleteToken.executeUpdate();

          			if(rs_query_deleteToken==0){          				
         			    System.out.println("query_validateToken no data");
         			    return Response.status(Response.Status.UNAUTHORIZED).build();       			 
          			}else{       	  		
	        	  		System.out.println("logout [" + token +"]");
	        	  		return Response.status(Response.Status.OK).build();    	  	   
          			}
	
        	  }catch(Exception e){
      			System.out.println("Error at query_deleteToken: " + e.getMessage());
      			return Response.status(Response.Status.UNAUTHORIZED).build();
        	  }finally {
    		      try {
    		          if (ps_query_deleteToken != null) {
    		        	  ps_query_deleteToken.close();
    		          }
    		      } catch (SQLException sqle) {
    			      System.out.println(sqle);
    		      }	    
        	  }
    		      
    		      
        	  } catch (Exception e) {
        	      System.out.println(e);
        	      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        	  } finally {
          	      try {
          	          if (con != null) {
          	              con.close();
          	          }
          	      } catch (SQLException sqle) {
          	    	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
          	      }
          	  }    
    		      

    }
   
}