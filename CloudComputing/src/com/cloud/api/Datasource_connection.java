package com.cloud.api;

import java.sql.*;
import javax.sql.DataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Datasource_connection{


	private String DataSourceName = "jdbc/MySQLDataSource";

	private DataSource ds;
	
	public Connection conn;
		

/*-------------------------------------------------------------------------------------------------*/
	

	public void Open_link(){
		try{		
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			conn = ds.getConnection();	
			//System.out.println("Datasource connection link established ("+DataSourceName+").");
			
		}catch (SQLException e) {
			System.out.println("Error connecting to Datasource ("+DataSourceName+").");
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("Error: " + e.getMessage() );
			stopProgramExecution(methodName, className, message);
			return;
		}catch (NamingException e) {
			System.out.println("Error finding Datasource ("+DataSourceName+").");
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("Error: " + e.getMessage() );
			stopProgramExecution(methodName, className, message);
			return;
		}
	}
	

	public void Close_link(){
		try{
			if(conn != null && !conn.isClosed()) conn.close();
			//System.out.println("Datasource connection link closed ("+DataSourceName+").");
		}
		catch(SQLException e){
			System.out.println("Error closing Datasource link ("+DataSourceName+").");
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("There was an error closing the database connection");
			stopProgramExecution(methodName, className, message);
		}
	}
		
	private static void stopProgramExecution(String methodName, String className, String message) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		String programName = main.getClassName();
		System.out.println("Error in program ---> " + programName);
		System.out.println("Error in class   ---> " + className);
		System.out.println("Error in method  ---> " + methodName);
		System.out.println("Error message    ---> " + message);
	}
}
