package com.cloud.api;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/profile")
public class ProfileResource {
	

	@Logged
	@GET
	@Path("{ProfileID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getThread(@PathParam("ProfileID") int ProfileID, @QueryParam("id") String id, @Context HttpHeaders headers) {
	
		Session session = new Session(headers);
		User currentUser = session.getUser(); 
		int UserID = currentUser.getUserID();
		int SessionID = session.getSessionID();
						
 		LocalDateTime now = LocalDateTime.now();	 		
 		String dateTime = now+"";
 		
 		Profile profile = new Profile();
 		String newId=null;
 	
	  String DataSourceName = "jdbc/MySQLDataSource";
	  DataSource ds;
	  Connection con = null;
	  
	  try {  
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(DataSourceName);			
			con = ds.getConnection();
			

	 		if(id != null && !id.equals("null") && !id.equals("")){
	 		
	 			System.out.println("["+currentUser.getUserName()+"] [GET] /profile/"+ProfileID+"?id="+id); 
	 			
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
	 			System.out.println("["+currentUser.getUserName()+"] [GET] /thread/"+ProfileID); 
	 		}
	 			
			
	 		//-----------------------------------------
	 		
			ArrayList<Post> posts = new ArrayList<Post>();

 			PreparedStatement ps_query_getPosts=null;
 			ResultSet rs_query_getPosts=null;
			
				try{
					String query_getPosts = "SELECT PostID, PostMessage, PostLink, "
							+ "Post.UserId, FavoriteCount, ReplyCount, "
							+ "Datetime, UserName , Name, Lastname "
							+ "from Post USE INDEX(Index_UserID_Datetime), User "
							+ "where Post.UserID=? and "
							+ "Post.UserID=User.UserID "
							+ "AND Datetime < ? order by Datetime DESC LIMIT 10;";
	
					ps_query_getPosts = con.prepareStatement(query_getPosts);
					
										
					ps_query_getPosts.setInt(1, ProfileID);
					ps_query_getPosts.setString(2, dateTime);
					
					//System.out.println(prep_sql);
								
					rs_query_getPosts= ps_query_getPosts.executeQuery();
					
						while(rs_query_getPosts.next()){
							
							Post post = new Post();
									
							post.setPostID(rs_query_getPosts.getInt("PostID"));
							post.setUserID(rs_query_getPosts.getInt("UserID"));
	
							post.setPostMessage(rs_query_getPosts.getString("PostMessage"));
							post.setPostLink(rs_query_getPosts.getString("PostLink"));
							
							post.setFavoriteCount(rs_query_getPosts.getInt("FavoriteCount"));
							post.setReplyCount(rs_query_getPosts.getInt("ReplyCount"));
							
							Timestamp myTimestamp = rs_query_getPosts.getTimestamp("DateTime");
							String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
							post.setDateTime(S);
							
							post.setUserName(rs_query_getPosts.getString("UserName"));
							post.setName(rs_query_getPosts.getString("Name"));
							post.setLastname(rs_query_getPosts.getString("Lastname"));
																																						
								//------------
								
								PreparedStatement ps_query_getFavorite=null;
					 			ResultSet rs_query_getFavorite=null;;
								
								try{
									String query_getFavorite = "SELECT * from Favorite where UserID=? AND PostID=?;";
	
									ps_query_getFavorite = con.prepareStatement(query_getFavorite);				
									ps_query_getFavorite.setInt(1, UserID);
									ps_query_getFavorite.setInt(2, rs_query_getPosts.getInt("PostID"));										
									rs_query_getFavorite= ps_query_getFavorite.executeQuery();			
									
									rs_query_getFavorite.next();
	
										try{					
											rs_query_getFavorite.getInt("FavoriteID");
											post.setFavorite(1);
//											System.out.println("rs_query_getFavorite favorite");
										}catch (Exception e){
											post.setFavorite(0);
//											System.out.println("rs_query_getFavorite not favorite");
										}
																					
								}catch(Exception e){
									System.out.println("Error at rs_query_getPost: " + e.getMessage());		
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
																																		
							posts.add(post);
							
							//System.out.println(post);
						}
				}catch(Exception e){
					System.out.println("Error at rs_query_getPosts: " + e.getMessage());
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
			          if (rs_query_getPosts != null) {
			        	  rs_query_getPosts.close();
			          }
			          if (ps_query_getPosts != null) {
			        	  ps_query_getPosts.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
			  }	

			newId=null;
				
			if(posts.size() == 0 && !(id == null || id.equals("null") || id.equals(""))){
				
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
				
				if(posts.size() != 0){
				
					String lastPost = posts.get(posts.size()-1).getDateTime();
		
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
			
			User user = new User();
			
			PreparedStatement ps_query_session=null;
			ResultSet rs_query_session=null;
			PreparedStatement ps_query_getFollow=null;
			ResultSet rs_query_getFollow=null;
			PreparedStatement ps_query_getFollowing=null;
			ResultSet rs_query_getFollowing=null;
			PreparedStatement ps_query_getFollower=null;
			ResultSet rs_query_getFollower=null;
			try{
				String query_session = "SELECT `User`.`UserID`, `UserName`, `Name`, "
						+ "`LastName`, `Email`, `About` "
						+ "FROM `User` where `UserID`=?;";
				
				ps_query_session = con.prepareStatement(query_session);
				ps_query_session.setInt(1, ProfileID);
				
				rs_query_session = ps_query_session.executeQuery();

				if (!rs_query_session.next() ) {
					System.out.println("rs_query_session no data");
					throw new NotAuthorizedException("Invalid session token");
				} else {
					user.setUserID(rs_query_session.getInt("UserID"));  
					user.setUserName(rs_query_session.getString("UserName"));
					user.setName(rs_query_session.getString("Name"));
					user.setLastname(rs_query_session.getString("LastName"));
					user.setEmail(rs_query_session.getString("Email"));
					user.setAbout(rs_query_session.getString("About"));
				}
				
				ps_query_getFollow = con.prepareStatement("SELECT * from Following where UserID=? AND FollowingUserID=?;");
		    	ps_query_getFollow.setInt(1, UserID);
		    	ps_query_getFollow.setInt(2, ProfileID);
		    	rs_query_getFollow = ps_query_getFollow.executeQuery();
		    	
		    	rs_query_getFollow.next();
		    	try{
		    		rs_query_getFollow.getInt("FollowingID");
		    		System.out.println("Follow found");
		    		user.setFollow(1);
		    	}catch (Exception e){
		    		System.out.println("Follow not found");
		    		user.setFollow(0);
		    	}
		    	
				ps_query_getFollowing = con.prepareStatement("SELECT count(*) as total from Following where UserID=?;");
				ps_query_getFollowing.setInt(1, ProfileID);
		    	rs_query_getFollowing = ps_query_getFollowing.executeQuery();
		    	
		    	rs_query_getFollowing.next();
		    	user.setFollowing(rs_query_getFollowing.getInt("total"));
				
				ps_query_getFollower = con.prepareStatement("SELECT count(*) as total from Following where FollowingUserID=?;");
				ps_query_getFollower.setInt(1, ProfileID);;
				rs_query_getFollower = ps_query_getFollower.executeQuery();
		    	
				rs_query_getFollower.next();
				user.setFollower(rs_query_getFollower.getInt("total"));
				
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
			          if (ps_query_getFollow != null) {
			        	  ps_query_getFollow.close();
			          }
			          if (rs_query_getFollow != null) {
			        	  rs_query_getFollow.close();
			          }
			          if (ps_query_getFollowing != null) {
			        	  ps_query_getFollowing.close();
			          }
			          if (rs_query_getFollowing != null) {
			        	  rs_query_getFollowing.close();
			          }
			          if (rs_query_getFollower != null) {
			        	  rs_query_getFollower.close();
			          }
			          if (rs_query_getFollower != null) {
			        	  rs_query_getFollower.close();
			          }
			      } catch (SQLException sqle) {
				      System.out.println(sqle);
			      }
			  }
			
			
			//------------
			
			profile.setPosts(posts);
			profile.setUser(user);
			
		
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
		
	
	  	ProfileWrapper wrapper = new ProfileWrapper();
		wrapper.setProfile(profile);
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
	
		
	class ProfileWrapper{
		
		Profile profile = new Profile();
		String id;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Profile getProfile() {
			return profile;
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
		}
		
	}
	
} 