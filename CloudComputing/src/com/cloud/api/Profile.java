package com.cloud.api;
import java.util.ArrayList;

public class Profile {

	User user;
	ArrayList<Post> posts = new ArrayList<Post>();
	

	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ArrayList<Post> getPosts() {
		return posts;
	}
	public void setPosts(ArrayList<Post> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		
		String Feed = "Profile [ "+ user +"\n";
		
		for(Post posts : posts){
			Feed+=posts.toString()+"\n";
		}	
				
		return Feed+" ]\n";
	}
		
}
