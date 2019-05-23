package com.cloud.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Priority;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

//import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.Priorities;
//import javax.ws.rs.WebApplicationException;


@Logged
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            validateToken(token);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        
    }

    private void validateToken(String token) throws Exception {

  	  String DataSourceName = "jdbc/MySQLDataSource";
  	  DataSource ds;
  	  Connection con = null;
  	      	
    	 try {  
 			InitialContext ctx = new InitialContext();
 			ds = (DataSource)ctx.lookup(DataSourceName);			
 			con = ds.getConnection();			
    	
      	PreparedStatement ps_query_validateToken=null;
      	ResultSet rs_query_validateToken=null;
  		
     	  	try{
     			String query_validateToken= "Select `UserID` from `Session` where `Token` = ?;";
     			ps_query_validateToken = con.prepareStatement(query_validateToken);
     			ps_query_validateToken.setString(1, token);
     			
     			rs_query_validateToken = ps_query_validateToken.executeQuery();
    			
     			if (!rs_query_validateToken.next() ) {
     			  System.out.println("query_validateToken no data");
     			  throw new NotAuthorizedException("Invalid session token"); 
     			}
     			
     	  }catch(Exception e){
   			System.out.println("Error at query_validateToken: " + e.getMessage());
   			throw new NotAuthorizedException("Invalid session token");
     	  }finally {
		      try {
		          if (ps_query_validateToken != null) {
		        	  ps_query_validateToken.close();
		          }
		          if (rs_query_validateToken != null) {
		        	  rs_query_validateToken.close();
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
	
    	
    }
       
}
