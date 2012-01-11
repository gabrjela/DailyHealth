package ro.dailyhealth.beans;

import java.util.ArrayList;

public class Nutritionist {
	
	private int id;
	private String title;
	private String description;
	private int latitude;
	private int longitude;
	// user who recommended it
	private int usr_id;
	private String username;
	private String address;
	private String phone;
	private String web;
	private String open;
	private int noOfReviews = 0;
	private float rating = 0;
	private ArrayList<Review> reviews = new ArrayList<Review>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLatitude() {
		return latitude;
	}
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}
	public int getLongitude() {
		return longitude;
	}
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getUsr_id() {
		return usr_id;
	}
	public void setUsr_id(int usr_id) {
		this.usr_id = usr_id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	
	public int getNoOfReviews() {
		return reviews.size();
	}
	
	public void setNoOfReviews(int noOfReviews) {
		this.noOfReviews = noOfReviews;
	}
	
	public float getRating() {
		if (reviews.size() == 0) return 0;
		
		float sum = 0;
		for (Review review : reviews) {
			sum += review.getRating();
		}
		return sum / reviews.size();
		
	}
	
	public void setRating(float rating) {
		this.rating = rating;
	}
	public ArrayList<Review> getReviews() {
		return reviews;
	}
	
	@Override
	public String toString() {
		return title;
	}

}
