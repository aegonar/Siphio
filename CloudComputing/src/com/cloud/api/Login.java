package com.cloud.api;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.Random;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
//import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;



@Path("/login")
public class Login {

	@POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials, @Context HttpHeaders headers) {

		System.out.println("[POST] /login");
		
		System.out.println(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
		
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        
        System.out.println("credentials "+ credentials);

        try {
            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            //return Response.ok("{\"token\":\""+ token + "\"}", MediaType.APPLICATION_JSON).cookie(new NewCookie("token", token)).build();
            return Response.ok("{\"success\":{\"token\":\""+ token + "\"}}", MediaType.APPLICATION_JSON).cookie(new NewCookie("token", token)).build();
//            { success: username === 'test' && password === 'test' };
        } catch (Exception e) {
        	System.out.println("Error authenticating user");
            return Response.status(Response.Status.UNAUTHORIZED).build();
            
        }    
   
    }

    private void authenticate(String username, String password) throws Exception {

	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	      	
	 try {  
		InitialContext ctx = new InitialContext();
		ds = (DataSource)ctx.lookup(DataSourceName);			
		con = ds.getConnection();
  		
      	PreparedStatement ps_query_authenticate=null;
      	ResultSet rs_query_authenticate=null;
      	
      	  	try{
      			String query_authenticate= "Select `UserID` from `User` where `UserName` = ? and `Password` = ?;";
      			ps_query_authenticate = con.prepareStatement(query_authenticate);
      			ps_query_authenticate.setString(1, username);
      			ps_query_authenticate.setString(2, password);
      			
      			rs_query_authenticate = ps_query_authenticate.executeQuery();

      			if (!rs_query_authenticate.next() ) {
      			    System.out.println("rs_query_authenticate no data");
      			    throw new NotAuthorizedException("Invalid username or password");
      			}
     			
      	  }catch(Exception e){
    			System.out.println("Error at query_authenticate: " + e.getMessage());  			
    			throw new NotAuthorizedException("Invalid username or password");		
      	  }finally {
		      try {
		          if (ps_query_authenticate != null) {
		        	  ps_query_authenticate.close();
		          }
		          if (rs_query_authenticate != null) {
		        	  rs_query_authenticate.close();
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

    public String issueToken(String username) throws SQLException {

  	  String UserID=null;
  	  String token=null;
  	
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	      	
		 try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
  	  	
  	  	
	  	  PreparedStatement ps_query_getUserID=null;
	  	  ResultSet rs_query_getUserID=null;
	  	  	try{
	  			String query_getUserID = "Select `UserID` from `User` where `UserName` = ?;";
	  			ps_query_getUserID = con.prepareStatement(query_getUserID);
	  			ps_query_getUserID.setString(1, username);
	  			
	  			rs_query_getUserID = ps_query_getUserID.executeQuery();
	
	  			rs_query_getUserID.next();
	  				UserID = rs_query_getUserID.getString("UserID");
	  			
	  	  }catch(Exception e){
				System.out.println("Error at query_getUserID: " + e.getMessage());			
				throw new SQLException();
	  	  }finally {
		      try {
		          if (ps_query_getUserID != null) {
		        	  ps_query_getUserID.close();
		          }
		          if (rs_query_getUserID != null) {
		        	  rs_query_getUserID.close();
		          }
		      } catch (SQLException sqle) {
			      System.out.println(sqle);
		      }	    
	  	  }		
				
	  	  	
		    	Random random = new SecureRandom();
		        token = new BigInteger(130, random).toString(32);
		    	
		  PreparedStatement ps_query_setToken=null;
		    try{    
		    	String query_setToken = "Insert into `Session` (`UserID`,`Token`) values (?,?);";
		    	ps_query_setToken = con.prepareStatement(query_setToken);
		    	ps_query_setToken.setString(1, UserID);
		    	ps_query_setToken.setString(2, token);
		    	ps_query_setToken.executeUpdate();
	
		    }catch(Exception e){	
				System.out.println("Error at query_setToken: " + e.getMessage());			
				throw new SQLException();
		    }finally {
		      try {
		          if (ps_query_setToken != null) {
		        	  ps_query_setToken.close();
		          }
		      } catch (SQLException sqle) {
			      System.out.println(sqle);
		      }	 
		    }
		      
	    }catch (Exception e) {
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
		    
  	return token;
  	
    }
    
}