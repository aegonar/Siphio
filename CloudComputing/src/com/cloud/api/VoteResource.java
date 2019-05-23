package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
//import java.util.Map.Entry;

@Path("/vote")
public class VoteResource {
	
	@SuppressWarnings("unchecked")
	@Logged
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postVote(@QueryParam("reply") int reply, @QueryParam("vote") int vote,
								@Context HttpHeaders headers) {
	  	  
	  Session session = new Session(headers);
      User currentUser = session.getUser();
      int userID = currentUser.getUserID(); 
        	  
      System.out.println("["+currentUser.getUserName()+"] [POST] /vote?reply="+reply+"&vote="+vote);
		
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
  	  HashMap<Integer, Integer> voteMap = null;
  	  int voteCount=0;
	  
  	  if(vote > 1 || vote <-1){
  		return Response.status(Response.Status.BAD_REQUEST).entity("Invalid vote").build();
  	  }
  	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			
		PreparedStatement ps_query_getLock=null;
		ResultSet rs_query_getLock=null;
		PreparedStatement ps_query_setLock=null;	
		PreparedStatement ps_query_getBlob=null;
		ResultSet rs_query_getBlob=null;		
		PreparedStatement ps_query_setBlob=null;
 		
	    try{
	    	boolean locked=true;
	    	
	    	while(locked){
		    	ps_query_getLock = con.prepareStatement("SELECT `Lock` FROM Reply WHERE ReplyID=?;");
		    	ps_query_getLock.setInt(1, reply);
		    	rs_query_getLock = ps_query_getLock.executeQuery();
		    	
		    	rs_query_getLock.next();
		    	locked = rs_query_getLock.getBoolean("Lock");
		    	if(locked){
		    		System.out.println("Reply Locked!");
		    	}
//		    	
//				try {
//					Thread.sleep(5);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
	    	}
	    	ps_query_setLock = con.prepareStatement("UPDATE Reply SET `Lock`=true, `Datetime`=`Datetime` where ReplyID=?;");
	    	ps_query_setLock.setInt(1, reply);
	    	ps_query_setLock.executeUpdate();
	    		    	
			ps_query_getBlob = con.prepareStatement("SELECT VoteCount, Votes FROM Reply WHERE ReplyID=?;");
			ps_query_getBlob.setInt(1, reply);
		    rs_query_getBlob = ps_query_getBlob.executeQuery();
		    
		    if(rs_query_getBlob==null){
		    	return Response.status(Response.Status.NOT_FOUND).entity("Reply not found").build();
		    }
		    
		    while (rs_query_getBlob.next()) {
		    	voteCount=rs_query_getBlob.getInt("VoteCount");
			    try{				    	
			      byte[] st = (byte[]) rs_query_getBlob.getObject("Votes");
			      ByteArrayInputStream baip = new ByteArrayInputStream(st);
			      ObjectInputStream ois = new ObjectInputStream(baip);
			      voteMap = (HashMap<Integer, Integer>) ois.readObject();
			    }catch (Exception e){
			    	//System.out.println("no voteMap!");
			    }
		    }
			
		    //System.out.println("current voteCount: "+voteCount);
		    
		    boolean newVotes=false;
		    if(voteMap != null){
//				for (Entry<Integer, Integer> entry : voteMap.entrySet()){
//					System.out.println("current hashmap: "+entry.getKey()+" : "+entry.getValue());
//				}
		    }else{
		    	voteMap = new HashMap<Integer, Integer>();
		    	newVotes=true;
		    }

		    int oldVote=0;
		    if(!newVotes){
		    	try{
			    	oldVote=voteMap.get(userID);
			    	voteMap.remove(userID);
		    	}catch (Exception e){
		    		//System.out.println("existing blob, new vote");
		    	}
		    }
		    
		    //System.out.println("old vote: "+oldVote);
		    
		    if(vote == oldVote){
		    	System.out.println("already voted");
		    	
			    ps_query_setBlob = con.prepareStatement("UPDATE Reply SET `Lock`=False,`Datetime`=`Datetime` where ReplyID=?;");
		    
				ps_query_setBlob.setInt(1, reply);
				ps_query_setBlob.executeUpdate();
		    	
		    	return Response.status(Response.Status.OK).build();
		    }else{
		    
			    if(vote != 0){
					voteMap.put(userID, vote);
			    }
			    
			    if(oldVote == -1 && vote == 0){
			    	voteCount+=1;
			    }else if(oldVote == 1 && vote == 0){
			    	voteCount+=-1;
			    }else if(oldVote == -1 && vote == 1){
			    	voteCount+=2;
			    }else if(oldVote == 1 && vote == -1){
			    	voteCount+=-2;
			    }else{
			    	voteCount+=vote;
			    }
			    
			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    ObjectOutputStream oos = new ObjectOutputStream(baos);
			    oos.writeObject(voteMap);
			    byte[] hashMapAsBytes = baos.toByteArray();
			    
			    ps_query_setBlob = con.prepareStatement("UPDATE Reply SET VoteCount=?, Votes=?, `Lock`=False, "
			    										+ "`Datetime`=`Datetime` where ReplyID=?;");
			    
			    ByteArrayInputStream bais = new ByteArrayInputStream(hashMapAsBytes);
			    ps_query_setBlob.setInt(1, voteCount);
			    ps_query_setBlob.setBinaryStream(2, bais, hashMapAsBytes.length);		    
			    ps_query_setBlob.setInt(3, reply);
			    ps_query_setBlob.executeUpdate();
				   		    
			    //System.out.println("new voteCount: "+voteCount);
			    
//				for (Entry<Integer, Integer> entry : voteMap.entrySet()) {
//					System.out.println("new hashmap: "+entry.getKey()+" : "+entry.getValue());
//				}
		    }    
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error voting").build();
		} finally {
	
		    try {
		        if (ps_query_setLock != null) {
		        	ps_query_setLock.close();
		        }
		        if (ps_query_getBlob != null) {
		        	ps_query_getBlob.close();
		        }
		        if (rs_query_getBlob != null) {
		        	rs_query_getBlob.close();
			    }
		        if (ps_query_setBlob != null) {
		        	ps_query_setBlob.close();
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