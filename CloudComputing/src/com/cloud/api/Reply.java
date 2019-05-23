package com.cloud.api;

public class Reply {
	
	int replyID;
	int postID;
	int userID;
	
	String replyMessage;
	String dateTime;
	
	int voteCount;
	int vote;
	
	String userName;
	String name;
	String Lastname;
	
	public int getReplyID() {
		return replyID;
	}
	public void setReplyID(int replyID) {
		this.replyID = replyID;
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
	public String getReplyMessage() {
		return replyMessage;
	}
	public void setReplyMessage(String replyMessage) {
		this.replyMessage = replyMessage;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String datetime) {
		this.dateTime = datetime;
	}
	public int getVoteCount() {
		return voteCount;
	}
	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}
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
	public int getVote() {
		return vote;
	}
	public void setVote(int vote) {
		this.vote = vote;
	}
	
	@Override
	public String toString() {
		return "Reply [replyID=" + replyID + ", postID=" + postID + ", userID=" + userID + ", replyMessage="
				+ replyMessage + ", dateTime=" + dateTime + ", voteCount=" + voteCount + ", vote=" + vote
				+ ", userName=" + userName + ", name=" + name + ", Lastname=" + Lastname + "]";
	}

}
