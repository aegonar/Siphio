package com.cloud.api;

import java.sql.*;


public class Database_connection{

	/** The driver namespace being in use	 */
	private String driverName = "";
	/** The server address, note to be encapsulated	 */
	private String serverName = "";
	/** The database name	 */
	private String database = "";
	/** The connection string url  */
	private String url = "";
	/** The user name: 	 */
	private String username = "";
	/** The password	 */
	private String password = "";
	/** Connection arguments	 */
	private String arguments = "";	
	/** The connection object to be used 	 */
	public Connection linea;
		
	public Database_connection(){
		//this.init();					
		url = "jdbc:mysql://localhost/cloud";
		username="root";
		password="root";
		driverName="com.mysql.jdbc.Driver";
	}

/*-------------------------------------------------------------------------------------------------*/
	
	/** This method opens a database connection with the set connection string	 */
	public void Open_link(){
		//System.out.println("Opening Database connection link (//" + serverName + "/" + database + arguments + ").");
		try{
			Class.forName(driverName);
			linea =  DriverManager.getConnection(url, username, password);
			//System.out.println("Database connection link established.");
		}
		catch (SQLException e) {
			System.out.println("Error connecting to Database (//" + serverName + "/" + database + arguments + ").");
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("Error: " + e.getMessage() );
			stopProgramExecution(methodName, className, message);
			return;
		}
		catch (ClassNotFoundException f) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("SQL jar missing or not found, check classpath.");
				stopProgramExecution(methodName, className, message);
		}
	}
	
	
	/** This method closes the database link */
	public void Close_link(){
		//System.out.println("Closing Database connection link (//" + serverName + "/" + database + ").");
		try{
			if(linea != null && !linea.isClosed()) linea.close();
			//System.out.println("Database connection link closed.");
		}
		catch(SQLException e){
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("There was an error closing the database connection");
			stopProgramExecution(methodName, className, message);
		}
	}
	
	
	/** @param value is the String to set up the mysql driver name */
	public void Set_driver_name(String value){
		driverName = value;
	}
	/** @param value is the String to set up the server name */
	public void Set_server_name(String value){
		serverName = value;
	}
	/** @param value is the String to set up the database name/schema */
	public void Set_dbname(String value){
		database = value;
	}
	/** @param value is the String used to set up the url for the connection */
	public void Set_url(String value){
		url = value;
	}
	/** @param value is the String to set up the user name */
	public void Set_username(String value){
		username = value;
	}
	/** @param value is the string to set up the password */
	public void Set_password(String value){
		password = value;
	}
	
	/** @param value is the string to set up the password */
	public void Set_arguments(String value){		/*AG*/
		arguments = value;
	}
	
	
	/** @return The driver name set up */
	public String Get_driver_name(){
		return driverName;
	}
	/** @return The server name set up */
	public String Get_server_name(){
		return serverName;
	}
		/** @return The set up database */
	public String Get_dbname(){
		return database;
	}	
	/** @return The setup url */
	public String Get_url(){
		return url;
	}	
	/** @return The set up user name */
	public String Get_username(){
		return username;		
	}
	/** @return The set up password */
	public String Get_password(){
		return password;
	}	
	/** @return The set up DB arguments */
	public String Get_arguments(){				
		return arguments;
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
