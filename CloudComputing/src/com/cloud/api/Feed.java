package com.cloud.api;
import java.util.ArrayList;

public class Feed {

	Post post;
	ArrayList<Reply> replies = new ArrayList<Reply>();
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public ArrayList<Reply> getReplies() {
		return replies;
	}
	public void setReplies(ArrayList<Reply> replies) {
		this.replies = replies;
	}
	
	@Override
	public String toString() {
		
		String Feed = "Feed [ "+ post +"\n";
		
		for(Reply reply : replies){
			Feed+=reply.toString()+"\n";
		}	
				
		return Feed+" ]\n";
	}
		
}
