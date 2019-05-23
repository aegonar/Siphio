package com.cloud.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
;

public class Session {
	
	private User user = new User();
	
	public User getUser(){
		return this.user;
	}	
	private int sessionID;	
	
	public int getSessionID() {
		return sessionID;
	}

	public Session(HttpHeaders headers){
	
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
		
		PreparedStatement ps_query_session=null;
		ResultSet rs_query_session=null;
		try{
			String query_session = "SELECT `User`.`UserID`, `UserName`, `Name`, "
					+ "`LastName`, `Email`, `About`, `Session`.`SessionID` "
					+ "FROM `User`, `Session` where `Token` = ? "
					+ "and `User`.`UserID`=`Session`.`UserID`;";
			
			ps_query_session = con.prepareStatement(query_session);
			ps_query_session.setString(1, token);
			
			rs_query_session = ps_query_session.executeQuery();

			if (!rs_query_session.next() ) {
				System.out.println("rs_query_session no data");
				throw new NotAuthorizedException("Invalid session token");
			} else {
				this.user.setUserID(rs_query_session.getInt("UserID"));  
				this.user.setUserName(rs_query_session.getString("UserName"));
				this.user.setName(rs_query_session.getString("Name"));
				this.user.setLastname(rs_query_session.getString("LastName"));
				this.user.setEmail(rs_query_session.getString("Email"));
				this.user.setAbout(rs_query_session.getString("About"));
				this.sessionID = (rs_query_session.getInt("SessionID"));
			}
		}catch(Exception e){
			System.out.println("Error at rs_query_session: " + e.getMessage());		
			throw new NotAuthorizedException("Invalid session token");
		} finally {
		      try {
		          if (ps_query_session != null) {
		        	  ps_query_session.close();
		          }
		          if (rs_query_session != null) {
		        	  rs_query_session.close();
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
