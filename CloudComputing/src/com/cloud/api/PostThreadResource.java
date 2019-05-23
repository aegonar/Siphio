package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingEjacksonxception;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/thread")
public class PostThreadResource {
	

	@Logged
	@GET
	@Path("{PostID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getThread(@PathParam("PostID") int PostID, @QueryParam("id") String id, @Context HttpHeaders headers) {
	
		Session session = new Session(headers);
		User currentUser = session.getUser(); 
		int UserID = currentUser.getUserID();
		int SessionID = session.getSessionID();
						
 		LocalDateTime now = LocalDateTime.now();	 		
 		String dateTime = now+"";
 		
 		PostThread postThread = new PostThread();
 		String newId=null;
 	
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			

	 		if(id != null && !id.equals("null") && !id.equals("")){
	 		
	 			System.out.println("["+currentUser.getUserName()+"] [GET] /thread/"+PostID+"?id="+id); 
	 			
	 			PreparedStatement ps_query_getPrevs=null;
	 			ResultSet rs_query_getPrevs=null;
	 			
				try{
					String query_getPrev = "SELECT Datetime from Scroll where SessionID =? AND Token=?";
		
					ps_query_getPrevs = con.prepareStatement(query_getPrev);
					
					ps_query_getPrevs.setInt(1, SessionID);
					ps_query_getPrevs.setString(2, id);
					
					rs_query_getPrevs= ps_query_getPrevs.executeQuery();
					
						if(rs_query_getPrevs.next()){
							Timestamp myTimestamp = rs_query_getPrevs.getTimestamp("DateTime");
							dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
						}else{
							//System.out.println("Invalid token");
							return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Invalid token\"}").build();
							
						}
					}catch(Exception e){
						System.out.println("Error at query_getPrev: " + e.getMessage());
					      try {
					          if (con != null) {
					              con.close();
					          }
					      } catch (SQLException sqle) {
						      System.out.println(sqle);
					      }
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						
				  } finally {
				      try {
				          if (ps_query_getPrevs != null) {
				        	  ps_query_getPrevs.close();
				          }
				          if (rs_query_getPrevs != null) {
				        	  rs_query_getPrevs.close();
				          }
				      } catch (SQLException sqle) {
					      System.out.println(sqle);
				      }
				  }		
 
	 		}else{
	 			System.out.println("["+currentUser.getUserName()+"] [GET] /thread/"+PostID); 
	 		}
	 			
			
	 		//-----------------------------------------
	 		
	 		ArrayList<Reply> replies = new ArrayList<Reply>();

 			PreparedStatement ps_query_getReplies=null;
 			ResultSet rs_query_getReplies=null;
			
				try{
					String query_getReplies = "SELECT ReplyID, PostID, ReplyMessage, Reply.UserId, "
									+ "VoteCount, Votes, Datetime, UserName, Name, Lastname "
									+ "from Reply USE INDEX(Index_PostID), User "
									+ "where User.UserID=Reply.UserID AND PostID=? "
									+ "AND Datetime < ? order by Datetime DESC LIMIT 10;";
	
					ps_query_getReplies = con.prepareStatement(query_getReplies);					
										
					ps_query_getReplies.setInt(1, PostID);
					ps_query_getReplies.setString(2, dateTime);
					
					System.out.println("dateTime " + dateTime);
								
					rs_query_getReplies= ps_query_getReplies.executeQuery();
					
						while(rs_query_getReplies.next()){
																					
							Reply reply = new Reply();
									
							reply.setReplyID(rs_query_getReplies.getInt("ReplyID"));
							//System.out.println("ReplyID "+ rs_query_getReplies.getInt("ReplyID"));
							
							reply.setPostID(rs_query_getReplies.getInt("PostID"));
							reply.setUserID(rs_query_getReplies.getInt("UserID"));

							reply.setReplyMessage(rs_query_getReplies.getString("ReplyMessage"));
							
							reply.setVoteCount(rs_query_getReplies.getInt("VoteCount"));
							
							Timestamp myTimestamp = rs_query_getReplies.getTimestamp("DateTime");
							String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
							reply.setDateTime(S);
							
							reply.setUserName(rs_query_getReplies.getString("UserName"));
							reply.setName(rs_query_getReplies.getString("Name"));
							reply.setLastname(rs_query_getReplies.getString("Lastname"));
							
							reply.setVote(getVote(rs_query_getReplies, UserID));
							
							replies.add(reply);
							
							System.out.println(reply);
						}
				}catch(Exception e){
					System.out.println("Error at rs_query_getReplies: " + e.getMessage());
				      try {
				          if (con != null) {
				              con.close();
				          }
				      } catch (SQLException sqle) {
					      System.out.println(sqle);
				      }
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading posts").build();
			  } finally {
			      try {
			          if (rs_query_getReplies != null) {
			        	  rs_query_getReplies.close();
			          }
			          if (ps_query_getReplies != null) {
			        	  ps_query_getReplies.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
			  }	

			newId=null;
				
			if(replies.size() == 0 && !(id == null || id.equals("null") || id.equals(""))){
				
				//System.out.println("\n\n\t\tEOF\n\n");
				
	 			PreparedStatement ps_query_deleteToken=null;
	 			//ResultSet rs_query_deletePost;
				try{
					String query_deleteToken = "DELETE FROM Scroll where `SessionID` = ? AND `Token` = ?";
					ps_query_deleteToken = con.prepareStatement(query_deleteToken);
					
					ps_query_deleteToken.setInt(1, SessionID);
					ps_query_deleteToken.setString(2, id);
					
					int rs_query_deleteToken=ps_query_deleteToken.executeUpdate();
	
					if (rs_query_deleteToken == 0){
						System.out.println("rs_query_deletePost no data");
						return Response.status(Response.Status.NOT_FOUND).entity("Token not found").build();
					}
					System.out.println("token deleted");
	
				}catch(Exception e){
	
					System.out.println("Error at rs_query_deleteToken: " + e.getMessage());
				      try {
				          if (con != null) {
				              con.close();
				          }
				      } catch (SQLException sqle) {
					      System.out.println(sqle);
				      }					
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting token").build();
						
				  } finally {
				      try {

				          if (ps_query_deleteToken != null) {
				        	  ps_query_deleteToken.close();
				          }
				      } catch (SQLException sqle) {
					      System.out.println(sqle);
				      }
				  }
				
//				return Response.status(Response.Status.NOT_FOUND).entity("No more replies").build();			
				
			}else{
				
				if(replies.size() != 0){
				
					String lastPost = replies.get(replies.size()-1).getDateTime();
		
					if(id == null || id.equals("null") || id.equals("")){
					
				    	Random random = new SecureRandom();
				        newId = new BigInteger(130, random).toString(32).substring(0,16);
				    	
				        boolean unique=false;
				        
				        int retries=3;
				        while(!unique){
				        	if(retries<=0){
				        		return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No Token Created\"}").build();
				        	}else{
				        		
				     			PreparedStatement ps_query_setToken=null;
						     		
							    try{    
							    	String query_setToken = "INSERT INTO `Scroll` (`SessionID`,`Token`, `Datetime`) values (?,?,?);";
							    	ps_query_setToken = con.prepareStatement(query_setToken);
							    	ps_query_setToken.setInt(1, SessionID);
							    	ps_query_setToken.setString(2, newId);
							    	ps_query_setToken.setString(3, lastPost);
							    	ps_query_setToken.executeUpdate();								
							    	
										//System.out.println(prep_sql);
										
										unique=true;
										
								}catch(Exception e){
									newId = new BigInteger(130, random).toString(32).substring(0,16);
									System.out.println("Error at query_setToken: " + e.getMessage());
									System.out.println("Token not unique, regenerating");
						
								  } finally {
	
								      try {
								          if (ps_query_setToken != null) {
								        	  ps_query_setToken.close();
								          }
								      } catch (SQLException sqle) {
									      System.out.println(sqle);
								      }
								  }
							    retries--;
				        	}
						}
					}else{
						newId=id;
						
			 			PreparedStatement ps_query_setNewDate=null;
						
						try{
							String query_setNewDate = "UPDATE Scroll SET Datetime=? WHERE SessionID=? AND Token=?;";
							ps_query_setNewDate = con.prepareStatement(query_setNewDate);
							
							ps_query_setNewDate.setString(1, lastPost);
							ps_query_setNewDate.setInt(2, SessionID);
							ps_query_setNewDate.setString(3, id);				
							ps_query_setNewDate.executeUpdate();
							
						}catch(Exception e){
							System.out.println("Error at query_setNewDate: " + e.getMessage());	
						      try {
						          if (con != null) {
						              con.close();
						          }
						      } catch (SQLException sqle) {
							      System.out.println(sqle);
						      }
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						 } finally {
	
					      try {
	
					          if (ps_query_setNewDate != null) {
					        	  ps_query_setNewDate.close();
					          }
	
					      } catch (SQLException sqle) {
						      System.out.println(sqle);
					      }
					}
				}
			}
		}
			
		//-----------------------------------------
			

 			PreparedStatement ps_query_getPost=null;
 			ResultSet rs_query_getPost=null;
 			Post post = new Post();
			
			try{
				String query_getPost = "SELECT PostID, PostMessage, PostLink, Post.UserId, "
						+ "FavoriteCount, ReplyCount, Datetime, UserName, Name, Lastname "
						+ "from Post USE INDEX(Index_UserID), User "
						+ "where User.UserID=Post.UserID AND PostID=?;";

				ps_query_getPost = con.prepareStatement(query_getPost);
				
				ps_query_getPost.setInt(1, PostID);
				
				rs_query_getPost= ps_query_getPost.executeQuery();
				
						rs_query_getPost.next();
						
						post.setPostID(rs_query_getPost.getInt("PostID"));
						post.setUserID(rs_query_getPost.getInt("UserID"));

						post.setPostMessage(rs_query_getPost.getString("PostMessage"));
						post.setPostLink(rs_query_getPost.getString("PostLink"));
						
						post.setFavoriteCount(rs_query_getPost.getInt("FavoriteCount"));
						post.setReplyCount(rs_query_getPost.getInt("ReplyCount"));
						
						Timestamp myTimestamp = rs_query_getPost.getTimestamp("DateTime");
						String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
						post.setDateTime(S);
						
						post.setUserName(rs_query_getPost.getString("UserName"));
						post.setName(rs_query_getPost.getString("Name"));
						post.setLastname(rs_query_getPost.getString("Lastname"));
						
					
			}catch(Exception e){

				System.out.println("Error at rs_query_getPost: " + e.getMessage());

			      try {
			          if (con != null) {
			              con.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				
			
			} finally {

			      try {
			          if (ps_query_getReplies != null) {
			        	  ps_query_getReplies.close();
			          }
			          if (rs_query_getReplies != null) {
			        	  rs_query_getReplies.close();
			          }

			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }	
			      
			}
			
			//------------
			
			PreparedStatement ps_query_getFavorite=null;
 			ResultSet rs_query_getFavorite=null;;
			
			try{
				String query_getFavorite = "SELECT * from Favorite where UserID=? AND PostID=?;";

				ps_query_getFavorite = con.prepareStatement(query_getFavorite);				
				ps_query_getFavorite.setInt(1, UserID);
				ps_query_getFavorite.setInt(2, PostID);										
				rs_query_getFavorite= ps_query_getFavorite.executeQuery();			
				
				rs_query_getFavorite.next();

					try{					
						rs_query_getFavorite.getInt("FavoriteID");
						post.setFavorite(1);
//						System.out.println("rs_query_getFavorite favorite");
					}catch (Exception e){
						post.setFavorite(0);
//						System.out.println("rs_query_getFavorite not favorite");
					}
																
			}catch(Exception e){

				System.out.println("Error at rs_query_getPost: " + e.getMessage());

			      try {
			          if (con != null) {
			              con.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				
			
			} finally {

			      try {
			          if (ps_query_getFavorite != null) {
			        	  ps_query_getFavorite.close();
			          }
			          if (rs_query_getFavorite != null) {
			        	  rs_query_getFavorite.close();
			          }

			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }	
			      
			}
			
			//------------
			
			postThread.setReplies(replies);
			postThread.setPost(post);
			
		
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
		
	
		FeedWrapper wrapper = new FeedWrapper();
		wrapper.setPostThread(postThread);
		wrapper.setId(newId);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(wrapper);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}

	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  }
	
	
	@SuppressWarnings("unchecked")
	private int getVote(ResultSet rs_query_getReplies, int userID){
		
		HashMap<Integer, Integer> voteMap = new HashMap<Integer, Integer>();
	    try{				    	
	      byte[] st = (byte[]) rs_query_getReplies.getObject("Votes");
	      ByteArrayInputStream baip = new ByteArrayInputStream(st);
	      ObjectInputStream ois = new ObjectInputStream(baip);
	      voteMap = (HashMap<Integer, Integer>) ois.readObject();
	    }catch (Exception e){
	    	//e.printStackTrace();
	    	//System.out.println("no voteMap");
	    }
	    
	    int vote=0;
	    
	    try{
	    	vote=voteMap.get(userID);
	    	//System.out.println("vote: " + vote);
	    }catch (Exception e){
	    	//System.out.println("no votes");
	    }
	    return vote;
	}
	
	
	class FeedWrapper{
		
		PostThread postThread = new PostThread();
		String id;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public PostThread getPostThread() {
			return postThread;
		}

		public void setPostThread(PostThread postThread) {
			this.postThread = postThread;
		}
		
	}
	
} 