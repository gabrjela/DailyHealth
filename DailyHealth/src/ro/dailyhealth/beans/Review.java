package ro.dailyhealth.beans;

public class Review {
	
	private int review_id = 0;
	private int usr_id = 0;
	private String username = null;
	private int center_id = 0;
	private float rating = 0;
	private String review = null;
	short center_type = 0;
	
	public int getReview_id() {
		return review_id;
	}
	public void setReview_id(int review_id) {
		this.review_id = review_id;
	}
	public int getUsr_id() {
		return usr_id;
	}
	public void setUsr_id(int usr_id) {
		this.usr_id = usr_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getCenter_id() {
		return center_id;
	}
	public void setCenter_id(int center_id) {
		this.center_id = center_id;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public short getCenter_type() {
		return center_type;
	}
	public void setCenter_type(short center_type) {
		this.center_type = center_type;
	}
	
}
