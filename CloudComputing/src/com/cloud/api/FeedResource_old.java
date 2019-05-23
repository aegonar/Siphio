//package com.cloud.api;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import java.math.BigInteger;
//import java.security.SecureRandom;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Timestamp;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Random;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Path("/feed")
//public class FeedResource_old {
//	
//	private static Database_connection link = new Database_connection();
//	private static PreparedStatement prep_sql;
//	
//	@Logged
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getAlerts(@QueryParam("id") String id, @Context HttpHeaders headers) {
//	
//		Session session = new Session(headers);
//		User currentUser = session.getUser(); 
//		int UserID = currentUser.getUserID();
//		int SessionID = session.getSessionID();
//						
// 		link.Open_link();
// 		
// 		//System.out.println("id=" + id); 
// 		
// 		LocalDateTime now = LocalDateTime.now();	 		
// 		String dateTime = now+"";
// 		
// 		if(id != null && !id.equals("null") && !id.equals("")){
// 		
// 			System.out.println("["+currentUser.getUserName()+"] [GET] /feed?id="+id); 
// 			
//			try{
//				String query_getPrev = "SELECT Datetime from Scroll where SessionID =? AND Token=?";
//	
//				prep_sql = link.linea.prepareStatement(query_getPrev);
//				
//				prep_sql.setInt(1, SessionID);
//				prep_sql.setString(2, id);
//				
//				ResultSet rs_query_getPrevs= prep_sql.executeQuery();
//				
//					if(rs_query_getPrevs.next()){
//						Timestamp myTimestamp = rs_query_getPrevs.getTimestamp("DateTime");
//						dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
//					}else{
//						//System.out.println("Invalid token");
//						return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Invalid token\"}").build();
//						
//					}
//			}catch(Exception e){
//				System.out.println("Error at query_getPrev: " + e.getMessage());	
//				link.Close_link();	
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//				
//			}		
//// 		}
//			link.Close_link();
//// 			System.out.println("["+currentUser.getUserName()+"] [GET] /feed"); 
// 		}else{
// 			System.out.println("["+currentUser.getUserName()+"] [GET] /feed"); 
// 		}
// 			
//		
//		ArrayList<Post> posts = new ArrayList<Post>();
//		link.Open_link();
//			try{
//				String query_getPosts = "SELECT PostID, PostMessage, PostLink, "
//						+ "Post.UserId, FavoriteCount, ReplyCount, "
//						+ "Datetime, UserName , Name, Lastname "
//						+ "from Following, Post USE INDEX(Index_UserID_Datetime), User "
//						+ "where Following.userid=? and "
//						+ "Post.UserID = Following.FollowingUserID "
//						+ "And User.UserID=Following.FollowingUserID "
//						+ "AND Datetime < ? order by Datetime DESC LIMIT 10;";
//
//				prep_sql = link.linea.prepareStatement(query_getPosts);
//				
//									
//				prep_sql.setInt(1, UserID);
//				prep_sql.setString(2, dateTime);
//				
//				//System.out.println(prep_sql);
//							
//				ResultSet rs_query_getPosts= prep_sql.executeQuery();
//				
//					while(rs_query_getPosts.next()){
//						
//						Post post = new Post();
//								
//						post.setPostID(rs_query_getPosts.getInt("PostID"));
//						post.setUserID(rs_query_getPosts.getInt("UserID"));
//
//						post.setPostMessage(rs_query_getPosts.getString("PostMessage"));
//						post.setPostLink(rs_query_getPosts.getString("PostLink"));
//						
//						post.setFavoriteCount(rs_query_getPosts.getInt("FavoriteCount"));
//						post.setReplyCount(rs_query_getPosts.getInt("ReplyCount"));
//						
//						Timestamp myTimestamp = rs_query_getPosts.getTimestamp("DateTime");
//						String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
//						post.setDateTime(S);
//						
//						post.setUserName(rs_query_getPosts.getString("UserName"));
//						post.setName(rs_query_getPosts.getString("Name"));
//						post.setLastname(rs_query_getPosts.getString("Lastname"));
//						
//						posts.add(post);
//						
//						//System.out.println(post);
//					}
//			}catch(Exception e){
//				System.out.println("Error at rs_query_getPosts: " + e.getMessage());
//				link.Close_link();
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();
//			}
//
//		link.Close_link();
//
//		String newId=null;
//			
//		if(posts.size() == 0){
//			
//			System.out.println("\n\n\t\tEOF\n\n");
//			
//			link.Open_link();
//			try{
//				String query_deletePost = "DELETE FROM Scroll where `SessionID` = ? AND `Token` = ?";
//				prep_sql = link.linea.prepareStatement(query_deletePost);
//				
//				prep_sql.setInt(1, SessionID);
//				prep_sql.setString(2, id);
//				
//				int rs_query_deletePost=prep_sql.executeUpdate();
//
//				if (rs_query_deletePost == 0){
//					System.out.println("rs_query_deletePost no data");
//					link.Close_link();
//					return Response.status(Response.Status.NOT_FOUND).entity("Token not found").build();
//				}	
//
//			}catch(Exception e){
//
//				System.out.println("Error at query_deletePost: " + e.getMessage());
//				
//				link.Close_link();
//				
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting token").build();
//					
//			}
//			link.Close_link();
//		}else{
//			String lastPost = posts.get(posts.size()-1).getDateTime();
//			//System.out.println("lastPost: " + lastPost);
//			
//			
//			if(id == null || id.equals("null") || id.equals("")){
//			
//		    	Random random = new SecureRandom();
//		        newId = new BigInteger(130, random).toString(32).substring(0,16);
//		    	
//		        boolean unique=false;
//		        
//		        int retries=3;
//		        while(!unique){
//		        	if(retries<=0){
//		        		return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No Token Creation\"}").build();
//		        	}else{
//			        	link.Open_link();
//					    try{    
//					    	String query_setToken = "INSERT INTO `Scroll` (`SessionID`,`Token`, `Datetime`) values (?,?,?);";
//								prep_sql = link.linea.prepareStatement(query_setToken);
//								prep_sql.setInt(1, SessionID);
//								prep_sql.setString(2, newId);
//								prep_sql.setString(3, lastPost);
//								prep_sql.executeUpdate();
//								
//								//System.out.println(prep_sql);
//								
//								unique=true;
//								
//						}catch(Exception e){
//							newId = new BigInteger(130, random).toString(32).substring(0,16);
//							System.out.println("Error at query_setToken: " + e.getMessage());
//							System.out.println("Token not unique, regenerating");
//				//			link.Close_link();
//				
//						} 
//					    link.Close_link();
//					    retries--;
//		        	}
//				}
//			}else{
//				newId=id;
//				link.Open_link();
//				try{
//					String query_setNewDate = "UPDATE Scroll SET Datetime=? WHERE SessionID=? AND Token=?;";
//					prep_sql = link.linea.prepareStatement(query_setNewDate);
//					
//					prep_sql.setTimestamp(1, parseDate(lastPost));
//					prep_sql.setInt(2, SessionID);
//					prep_sql.setString(3, id);				
//					prep_sql.executeUpdate();
//					
//				}catch(Exception e){
//					System.out.println("Error at query_setNewDate: " + e.getMessage());			
//					link.Close_link();
//					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//				}
//				link.Close_link();
//				
//			}
//		}
//		
//		
//		ArrayList<Feed> FeedList = new ArrayList<Feed>();
//		
//		link.Open_link();
//		
//		for(Post post : posts){
//			int postID = post.getPostID();
//			
//			Feed feed = new Feed();
//			
//			feed.setPost(post);
//			
//			ArrayList<Reply> replies = new ArrayList<Reply>();
//			
//			try{
//				String query_getReplies = "SELECT ReplyID, PostID, ReplyMessage, Reply.UserId, "
//						+ "VoteCount, Datetime, UserName, Name, Lastname "
//						+ "from Reply USE INDEX(Index_PostID_Datetime), User "
//						+ "where PostID=? AND User.UserID=Reply.UserID order by Datetime DESC LIMIT 3;";
//
//				prep_sql = link.linea.prepareStatement(query_getReplies);
//				
//				prep_sql.setInt(1, postID);
//				
//				ResultSet rs_query_getReplies= prep_sql.executeQuery();
//				
//					while(rs_query_getReplies.next()){
//						
//						Reply reply = new Reply();
//							
//						reply.setReplyID(rs_query_getReplies.getInt("ReplyID"));
//						reply.setPostID(rs_query_getReplies.getInt("PostID"));
//						reply.setUserID(rs_query_getReplies.getInt("UserID"));
//
//						reply.setReplyMessage(rs_query_getReplies.getString("ReplyMessage"));
//						
//						reply.setVoteCount(rs_query_getReplies.getInt("VoteCount"));
//						
//						Timestamp myTimestamp = rs_query_getReplies.getTimestamp("DateTime");
//						String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
//						reply.setDateTime(S);
//						
//						reply.setUserName(rs_query_getReplies.getString("UserName"));
//						reply.setName(rs_query_getReplies.getString("Name"));
//						reply.setLastname(rs_query_getReplies.getString("Lastname"));
//						
//						replies.add(reply);
//						
//						//System.out.println(reply);
//
//					}
//					
//			}catch(Exception e){
//
//				System.out.println("Error at rs_query_getReplies: " + e.getMessage());
//				
//				link.Close_link();
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//				
//			
//			}			
//			
//			feed.setReplies(replies);
//			
//			FeedList.add(feed);
//			//System.out.print(feed);
//			
//		}
//		
//		link.Close_link();
//		
//		FeedWrapper wrapper = new FeedWrapper();
//		wrapper.setFeed(FeedList);
//		wrapper.setId(newId);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		String jsonString = null;
//		
//		try {
//			jsonString = mapper.writeValueAsString(wrapper);
//			
//		} catch (JsonProcessingException e) {
//			
//			System.out.println("Error mapping to json: " + e.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
//		}
//
//	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
//  }
//	
//	
//	class FeedWrapper{
//		
//		ArrayList<Feed> feed = new ArrayList<Feed>();
//		String id;
//		
//		public String getId() {
//			return id;
//		}
//
//		public void setId(String id) {
//			this.id = id;
//		}
//
//		public ArrayList<Feed> getFeed() {
//			return feed;
//		}
//
//		public void setFeed(ArrayList<Feed> feed) {
//			this.feed = feed;
//		}
//		
//	}
//	
//	private static java.sql.Timestamp parseDate(String s) {
//		
//		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = null;
//
//		try {
//			date = formatter.parse(s);	
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	
//		return new java.sql.Timestamp(date.getTime());
//	
//	}
//  
//} 