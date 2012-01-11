package ro.dailyhealth.beans;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ro.dailyhealth.db.DBUtils;

/* This class is intended to be used for all health centers
 * presented by the application, except for Nutritionist centers.
 * For current release these include :
 * 		Fitness centers
 *		Karate centers
 * 		Judo centers
 *		Yoga centers
 *		Wrestling centers.
 */
public class SportCenter {
	
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
	
	private String category;
	
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ArrayList<Review> getReviews() {
		return reviews;
	}
	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
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
	
	@Override
	public String toString() {
		return title;
	}	
	
}
