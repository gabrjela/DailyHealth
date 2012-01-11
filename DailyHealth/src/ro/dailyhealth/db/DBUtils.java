package ro.dailyhealth.db;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import ro.dailyhealth.MainActivity;
import ro.dailyhealth.Q;
import ro.dailyhealth.beans.MyLocation;
import ro.dailyhealth.beans.Nutritionist;
import ro.dailyhealth.beans.Review;
import ro.dailyhealth.beans.SportCenter;
import ro.dailyhealth.utils.Identifier;
import ro.dailyhealth.utils.Payload;

public class DBUtils {
	
	public static LinkedBlockingQueue<Nutritionist> nutritionists = null;
	public static LinkedBlockingQueue<SportCenter> fitnessCenters = null;
	public static LinkedBlockingQueue<SportCenter> karateCenters = null;
	public static LinkedBlockingQueue<SportCenter> judoCenters = null;
	public static LinkedBlockingQueue<SportCenter> yogaCenters = null;
	public static LinkedBlockingQueue<SportCenter> wrestlingCenters = null;
	
	public static LinkedBlockingQueue<MyLocation> myLocations = null;
	public static LinkedBlockingQueue<Review> reviews = null;
	
	public static LinkedBlockingQueue<String> notifications = new LinkedBlockingQueue<String>();
	
	public static Q q = new Q();
	public static String responseLineFromServer = null; 

	
	public static void prepareNutritionistsList() {
		nutritionists = new LinkedBlockingQueue<Nutritionist>();
	}
	
	public static void prepareFitnessList() {
		fitnessCenters = new LinkedBlockingQueue<SportCenter>();
	}
	
	public static void prepareKarateList() {
		karateCenters = new LinkedBlockingQueue<SportCenter>();
	}
	
	public static void prepareJudoList() {
		judoCenters = new LinkedBlockingQueue<SportCenter>();
	}
	
	public static void prepareYogaList() {
		yogaCenters = new LinkedBlockingQueue<SportCenter>();
	}
	
	public static void prepareWrestlingList() {
		wrestlingCenters = new LinkedBlockingQueue<SportCenter>();
	}
	
	public static void prepareMyLocationsList() {
		myLocations = new LinkedBlockingQueue<MyLocation>();
	}
	
	public static void prepareNotificationsList() {
		notifications = new LinkedBlockingQueue<String>();
	}
	
	public static void prepareReviewsList() {
		reviews = new LinkedBlockingQueue<Review>();
	}
	
	//init buffer lists
	public static void prepareAllLists() {
		prepareNutritionistsList();
		prepareFitnessList();
		prepareKarateList();
		prepareJudoList();
		prepareYogaList();
		prepareWrestlingList();
		
		prepareMyLocationsList();
		
		prepareNotificationsList();
		
		prepareReviewsList();
	}
	
	
	public static void addCenter(String line) throws InterruptedException {
		String[] fields = line.split("#");
		if (fields.length == 0) return; //unlikely to happen
		String type = fields[0];
		if (type.equalsIgnoreCase(Identifier.NUTRITIONIST)) addNutritionist(line);
		if (type.equalsIgnoreCase(Identifier.FITNESS)) addSportCenter(line, Identifier.FITNESS);
		if (type.equalsIgnoreCase(Identifier.KARATE)) addSportCenter(line, Identifier.KARATE);
		if (type.equalsIgnoreCase(Identifier.JUDO)) addSportCenter(line, Identifier.JUDO);
		if (type.equalsIgnoreCase(Identifier.YOGA)) addSportCenter(line, Identifier.YOGA);
		if (type.equalsIgnoreCase(Identifier.WRESTLING)) addSportCenter(line, Identifier.WRESTLING);
	}
	
	public static void addNutritionist(String line) throws InterruptedException {
		String[] fields = line.split("#");
		if (fields.length == 0) return;
		Nutritionist bean = new Nutritionist();
		bean.setId(Integer.parseInt(fields[1]));
		bean.setTitle(fields[2]);
		bean.setDescription(fields[3]);
		bean.setLatitude(Integer.parseInt(fields[4]));
		bean.setLongitude(Integer.parseInt(fields[5]));
		bean.setUsername(fields[6]);
		bean.setAddress(fields[7]);
		bean.setPhone(fields[8]);
		bean.setWeb(fields[9]);
		bean.setOpen(fields[10]);
		bean.setRating(Float.parseFloat(fields[11]));
		bean.setNoOfReviews(Integer.parseInt(fields[12]));
		
		nutritionists.put(bean);
	}
	
	public static void addSportCenter(String line, String category) throws InterruptedException {
		String[] fields = line.split("#");
		if (fields.length == 0) return;
		SportCenter bean = new SportCenter();
		bean.setId(Integer.parseInt(fields[1]));
		bean.setTitle(fields[2]);
		bean.setDescription(fields[3]);
		bean.setLatitude(Integer.parseInt(fields[4]));
		bean.setLongitude(Integer.parseInt(fields[5]));
		bean.setUsername(fields[6]);
		bean.setAddress(fields[7]);
		bean.setPhone(fields[8]);
		bean.setWeb(fields[9]);
		bean.setOpen(fields[10]);
		bean.setRating(Float.parseFloat(fields[11]));
		bean.setNoOfReviews(Integer.parseInt(fields[12]));
		bean.setCategory(category);
		
		if (category.equalsIgnoreCase(Identifier.FITNESS)) fitnessCenters.put(bean);
		if (category.equalsIgnoreCase(Identifier.KARATE)) karateCenters.put(bean);
		if (category.equalsIgnoreCase(Identifier.JUDO)) judoCenters.put(bean);
		if (category.equalsIgnoreCase(Identifier.YOGA)) yogaCenters.put(bean);
		if (category.equalsIgnoreCase(Identifier.WRESTLING)) wrestlingCenters.put(bean);
		
	}
	
	public static void addMyLocation (String line) throws InterruptedException {
		String[] fields = line.split("#");
		if (fields.length == 0) return;
		MyLocation bean = new MyLocation();
		bean.setLocation_id(Integer.parseInt(fields[1]));
		bean.setUsr_id(Integer.parseInt(fields[2]));
		bean.setLatitude(Integer.parseInt(fields[3]));
		bean.setLongitude(Integer.parseInt(fields[4]));
		
		myLocations.put(bean);
	}
	
	public static void addReview(String line) throws InterruptedException {
		/* line should look like this
		 * "review#review_id#usr_id#username#center_id#center_type#rating#review" */
		String[] fields = line.split("#");
		if (fields.length == 0) return;
		Review bean = new Review();
		bean.setReview_id(Integer.parseInt(fields[1]));
		bean.setUsr_id(Integer.parseInt(fields[2]));
		bean.setUsername(fields[3]);
		bean.setCenter_id(Integer.parseInt(fields[4]));
		bean.setCenter_type(Short.parseShort(fields[5]));
		bean.setRating(Float.parseFloat(fields[6]));
		bean.setReview(fields[7]);
		
		reviews.put(bean);
	}
	
	/* this is called after all data are loaded from DB */
	public static void attachReviews() {
		for (Review review : reviews) {
			switch (review.getCenter_type()) {
				case Identifier.NUTRIONIST_BROWSER:
					for (Nutritionist nutr : nutritionists) {
						if (nutr.getId() == review.getCenter_id()) {
							nutr.getReviews().add(review);
							break;
						}
					}
					break;
				case Identifier.FITNESS_BROWSER:
					for (SportCenter sportCenter : fitnessCenters) {
						if (sportCenter.getId() == review.getCenter_id()) {
							sportCenter.getReviews().add(review);
							break;
						}
					}
					break;
				case Identifier.KARATE_BROWSER:
					for (SportCenter sportCenter : karateCenters) {
						if (sportCenter.getId() == review.getCenter_id()) {
							sportCenter.getReviews().add(review);
							break;
						}
					}
					break;
				case Identifier.JUDO_BROWSER:
					for (SportCenter sportCenter : judoCenters) {
						if (sportCenter.getId() == review.getCenter_id()) {
							sportCenter.getReviews().add(review);
							break;
						}
					}
					break;
				case Identifier.YOGA_BROWSER:
					for (SportCenter sportCenter : yogaCenters) {
						if (sportCenter.getId() == review.getCenter_id()) {
							sportCenter.getReviews().add(review);
							break;
						}
					}
					break;
				case Identifier.WRESTLING_BROWSER:
					for (SportCenter sportCenter : wrestlingCenters) {
						if (sportCenter.getId() == review.getCenter_id()) {
							sportCenter.getReviews().add(review);
							break;
						}
					}
					break;
			}
		}
	}
	
	public static int getNutritionistsSize() {
		if (nutritionists != null) {
			return nutritionists.size();
		}
		return 0;
	}
	
	public static int getFitnessCentersSize() {
		if (fitnessCenters != null) {
			return fitnessCenters.size();
		}
		return 0;
	}
	
	public static int getKarateCentersSize() {
		if (karateCenters != null) {
			return karateCenters.size();
		}
		return 0;
	}
	
	public static int getJudoCentersSize() {
		if (judoCenters != null) {
			return judoCenters.size();
		}
		return 0;
	}
	
	public static int getYogaCentersSize() {
		if (yogaCenters != null) {
			return yogaCenters.size();
		}
		return 0;
	}
	
	public static int getWrestlingCentersSize() {
		if (wrestlingCenters != null) {
			return wrestlingCenters.size();
		}
		return 0;
	}
	
	public static int getMyLocationsSize() {
		if (myLocations != null) {
			return myLocations.size();
		}
		return 0;
	}
	
	public static void insertMyLocation (int usr_id, int latitude, int longitude) throws InterruptedException {
		MyLocation loc = new MyLocation();
		loc.setUsr_id(usr_id);
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		
		myLocations.put(loc);
		
		//send to db server
		String requestWrite = "insert into usr_locations (usr_id, latitude, longitude) values (" + usr_id + "," + latitude + "," + longitude + ");" ;
		MainActivity.txRequestLock.releaseLock(requestWrite);
			
	}
	
	public static boolean removeMyLocation (int usr_id, int latitude, int longitude) {
		boolean removed = false;
		
		//send to db server
		String requestWrite = "delete from usr_locations where usr_id=" + usr_id + " and latitude=" + latitude + " and longitude=" + longitude + ";";
		MainActivity.txRequestLock.releaseLock(requestWrite);
		
		for (MyLocation loc : myLocations) {
			if (loc.getLatitude() == latitude && loc.getLongitude() == longitude) {
				myLocations.remove(loc);
				removed = true;
				Log.i("DBUtils", "User location deleted : " + latitude + ", " + longitude);
				Log.d("DBUtils", "No. of user locations left after removal : " + myLocations.size());
				break;
			}
		}
		
		return removed;
	}
	
	public static Review insertReview(Object entity, int usr_id, String username, int center_id, short center_type, float rating, String review) throws InterruptedException { // insert review#usr_id#nutritionist_id#center_type#rating#review
		Review _review = new Review();
		_review.setUsr_id(usr_id);
		_review.setUsername(username);
		_review.setCenter_id(center_id);
		_review.setCenter_type(center_type);
		_review.setRating(rating);
		_review.setReview(review);
		
		reviews.put(_review);
		if (entity instanceof Nutritionist) {
			Nutritionist nutr = ((Nutritionist)entity);
			nutr.getReviews().add(_review);
			
			String notification = Payload.NOTIFICATION + "#" + MainActivity.usr_id + "#User " + MainActivity.username + " has posted a review on " + nutr.getTitle();
			Log.i("DBUtils", "Notification sent to server : " + notification);
			
			// "insert review#usr_id#nutritionist_id#center_type#rating#review"
		    String line =  new String("insert review#"
							    		+ usr_id + "#"
							    		+ center_id + "#"
							    		+ center_type + "#"
							    		+ rating + "#"
							    		+ review);
		    
		    MainActivity.txRequestLock.releaseLock(line);
			MainActivity.txRequestLock.releaseLock(notification);
		} else 
			if (entity instanceof SportCenter) {
				SportCenter fitn = ((SportCenter)entity);
				fitn.getReviews().add(_review);
				
				String notification = Payload.NOTIFICATION + "#" + MainActivity.usr_id + "#User " + MainActivity.username + " has posted a review on " + fitn.getTitle();
				Log.i("DBUtils", "Notification sent to server : " + notification);
				
				// insert review#usr_id#nutritionist_id#center_type#rating#review
			    String line =  new String("insert review#"
								    		+ usr_id + "#"
								    		+ center_id + "#"
								    		+ center_type + "#"
								    		+ rating + "#"
								    		+ review);
				
			    MainActivity.txRequestLock.releaseLock(line);
				MainActivity.txRequestLock.releaseLock(notification);
			}
		
		return _review;
		
	}
	
	
	public static Nutritionist insertNutritionist(int usr_id, String title, String description, String address, String phone, String open, String web, int latitude, int longitude) throws InterruptedException {
		/* must build this line 
		 * "insert nutritionist#usr_id#title#description#address#phone#open#web#latitude#longitude" */
		Nutritionist nutritionist = new Nutritionist();
		nutritionist.setUsr_id(usr_id);
		nutritionist.setTitle(title);
		nutritionist.setDescription(description);
		nutritionist.setAddress(address);
		nutritionist.setPhone(phone);
		nutritionist.setWeb(web);
		nutritionist.setLatitude(latitude);
		nutritionist.setLongitude(longitude);
		
		nutritionists.put(nutritionist);
		
		String notification = Payload.NOTIFICATION + "#" + MainActivity.usr_id + "#User " + MainActivity.username + " has added a nutritionist";
		
		// "insert nutritionist#usr_id#title#description#address#phone#open#web#latitude#longitude"
	    String line =  new String("insert nutritionist#"
						    		+ usr_id + "#"
						    		+ title + "#"
						    		+ description + "#"
						    		+ address + "#"
						    		+ phone + "#"
						    		+ open + "#"
						    		+ web + "#"
						    		+ latitude + "#"
						    		+ longitude);
		
	    MainActivity.txRequestLock.releaseLock(line);
		MainActivity.txRequestLock.releaseLock(notification);
		
		return nutritionist;
	}
	
	
	public static SportCenter insertSportCenter(int usr_id, String title, String description, String address, String phone, String open, String web, int latitude, int longitude, short centerType) throws InterruptedException {
		/* must build this line
		 * "insert fitness#usr_id#title#description#address#phone#open#web#latitude#longitude#centerType" */
		SportCenter fitn = new SportCenter();
		fitn.setUsr_id(usr_id);
		fitn.setTitle(title);
		fitn.setDescription(description);
		fitn.setAddress(address);
		fitn.setPhone(phone);
		fitn.setWeb(web);
		fitn.setLatitude(latitude);
		fitn.setLongitude(longitude);
		fitn.setCategory(Identifier.getLabel(centerType));
		
		switch (centerType) {
			case Identifier.FITNESS_BROWSER:
				fitnessCenters.put(fitn);
				break;
			case Identifier.KARATE_BROWSER:
				karateCenters.put(fitn);
				break;
			case Identifier.JUDO_BROWSER:
				judoCenters.put(fitn);
				break;
			case Identifier.YOGA_BROWSER:
				yogaCenters.put(fitn);
				break;
			case Identifier.WRESTLING_BROWSER:
				wrestlingCenters.put(fitn);
				break;
		}
		
		String notification = Payload.NOTIFICATION + "#" + MainActivity.usr_id + "#User " + MainActivity.username + " has added a " + Identifier.getLabel(centerType) + " club";
		
		// "insert fitness#usr_id#title#description#address#phone#open#web#latitude#longitude#centerType"
	    String line =  new String("insert fitness#"
						    		+ usr_id + "#"
						    		+ title + "#"
						    		+ description + "#"
						    		+ address + "#"
						    		+ phone + "#"
						    		+ open + "#"
						    		+ web + "#"
						    		+ latitude + "#"
						    		+ longitude + "#"
						    		+ centerType);
		
	    MainActivity.txRequestLock.releaseLock(line);
		MainActivity.txRequestLock.releaseLock(notification);
		
		return fitn;
		
	}		

}