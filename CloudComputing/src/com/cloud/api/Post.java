package com.cloud.api;


public class Post {
		
	int postID;
	int userID;
	
	String postMessage;
	String postLink;
	
	String dateTime;
	
	int favoriteCount;
	int favorite;
	int replyCount;
	
	String userName;
	String name;
	String Lastname;
		
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastname() {
		return Lastname;
	}
	public void setLastname(String lastname) {
		Lastname = lastname;
	}
	public int getPostID() {
		return postID;
	}
	public void setPostID(int postID) {
		this.postID = postID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getPostMessage() {
		return postMessage;
	}
	public void setPostMessage(String postMessage) {
		this.postMessage = postMessage;
	}
	public String getPostLink() {
		return postLink;
	}
	public void setPostLink(String postLink) {
		this.postLink = postLink;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	@Override
	public String toString() {
		return "Post [postID=" + postID + ", userID=" + userID + ", postMessage=" + postMessage + ", postLink="
				+ postLink + ", dateTime=" + dateTime + ", favoriteCount=" + favoriteCount + ", favorite=" + favorite
				+ ", replyCount=" + replyCount + ", userName=" + userName + ", name=" + name + ", Lastname=" + Lastname
				+ "]";
	}
}
