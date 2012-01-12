package ro.dailyhealth;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import ro.dailyhealth.util.Identifier;


public class DbConnection {
	static public Connection con = null;
	
	private final static String NUTRITIONIST = "Nutritionists";
	public final static String FITNESS = "Fitness";
	public final static String KARATE = "Karate";
	public final static String JUDO = "Judo";
	public final static String YOGA = "Yoga";
	public final static String WRESTLING = "Wrestling";
	
	public final static String MY_LOCATIONS = "My locations";
	public final static String REVIEW = "review";
	
	int usr_id = 1;
	
	public static void openDBConnection() throws Exception {
	    try {
		    Class.forName("org.postgresql.Driver");
		    con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dailyhealth", "gabi", "gabi");
		   } catch(Exception e){
			e.printStackTrace();
			throw e;
		   }
	}
	
	public static ArrayList<String> fetchNutritionistsFromDb() throws Exception {
			  ArrayList<String> nutritionists = null;

		      nutritionists = new ArrayList<String>();
		      
		      Statement stmt = con.createStatement();
		      String line = null;
		      ResultSet rs = stmt.executeQuery("select * from nutritionist;");
		      
		      PreparedStatement prep = con.prepareStatement("select (username) from usr where id = ? ;");
		      ResultSet usr_rs = null;
		      while (rs.next()) {
				    int id = rs.getInt("id");
				    String title = rs.getString("title");
				    String description = rs.getString("description");
				    int lat = rs.getInt("latitude");
				    int lon = rs.getInt("longitude");
				    
				    String username = null;
				    //retrieve the user name
				    prep.setInt(1, id);
				    usr_rs = prep.executeQuery();
				    while (usr_rs.next()) {
				    	username = usr_rs.getString(1);
				    }
				    
				    String address = rs.getString("address");
				    String phone = rs.getString("phone");
				    String web = rs.getString("web");
				    String open = rs.getString("open");
				    
				    float rating = rs.getFloat("rating");
				    int noOfReviews = rs.getInt("no_reviews");
				    
				    line =  new String(NUTRITIONIST + "#"
				    					+ id + "#"
				    					+ title + "#"
				    					+ description + "#"
				    					+ lat + "#"
				    					+ lon + "#"
				    					+ username + "#"
				    					+ address + "#"
				    					+ phone + "#"
				    					+ web + "#"
				    					+ open + "#"
				    					+ rating + "#"
				    					+ noOfReviews);
				    nutritionists.add(line);
				}
		      prep.close();
		      if (usr_rs != null) usr_rs.close();
		      stmt.close();
		      rs.close();
		      
		   return nutritionists;

	}
	
	public static ArrayList<String> fetchSportCentersFromDb(String category) throws Exception {
		  ArrayList<String> whateverSportCenters = new ArrayList<String>();

	      Statement stmt = con.createStatement();
	      String line = null;
	      
	      ResultSet rs = stmt.executeQuery("select * from " + category + "center;");
	      
	      PreparedStatement prep = con.prepareStatement("select (username) from usr where id = ? ;");
	      ResultSet usr_rs = null;
	      while (rs.next()) {
			    int id = rs.getInt("id");
			    String title = rs.getString("title");
			    String description = rs.getString("description");
			    int lat = rs.getInt("latitude");
			    int lon = rs.getInt("longitude");
			    
			    String username = null;
			    //retrieve the user name
			    prep.setInt(1, id);
			    usr_rs = prep.executeQuery();
			    while (usr_rs.next()) {
			    	username = usr_rs.getString(1);
			    }
			    
			    String address = rs.getString("address");
			    String phone = rs.getString("phone");
			    String web = rs.getString("web");
			    String open = rs.getString("open");
			    
			    float rating = rs.getFloat("rating");
			    int noOfReviews = rs.getInt("no_reviews");
			    
			    line =  new String(category + "#"
			    					+ id + "#"
			    					+ title + "#"
			    					+ description + "#"
			    					+ lat + "#"
			    					+ lon + "#"
			    					+ username + "#"
			    					+ address + "#"
			    					+ phone + "#"
			    					+ web + "#"
			    					+ open + "#"
			    					+ rating + "#"
			    					+ noOfReviews);
			    whateverSportCenters.add(line);
			}
	      prep.close();
	      if (usr_rs != null) usr_rs.close();
	      stmt.close();
	      rs.close();
	      
	   return whateverSportCenters;

	}
	
	
	public static ArrayList<String> fetchMyLocationsFromDb(int usr_id) throws Exception {
		  ArrayList<String> myLocations = null;

		  myLocations = new ArrayList<String>();
	      
	      Statement stmt = con.createStatement();
	      String line = null;
	      ResultSet rs = stmt.executeQuery("select * from usr_locations where usr_id=" + usr_id + ";");
	      
	      while (rs.next()) {
	    	  	int location_id = rs.getInt("location_id");
			    int lat = rs.getInt("latitude");
			    int lon = rs.getInt("longitude");
			    
			    line =  new String(MY_LOCATIONS + "#"
			    					+ location_id + "#"
						    		+ usr_id + "#"
						    		+ lat + "#"
			    					+ lon);
			    myLocations.add(line);
			}
	      stmt.close();
	      rs.close();
	      
	   return myLocations;

	}
	
	public static ArrayList<String> fetchMyLocationsFromDb(String username) throws Exception {
		  int usr_id = (Integer) getUserInfo(username).get("usr_id");
		  System.out.println("usr_id=" + usr_id);
		
		  return fetchMyLocationsFromDb(usr_id);
	}
	
	public static ArrayList<String> fetchReviewsFromDb() throws Exception {
		  ArrayList<String> reviews = new ArrayList<String>();
	      
	      Statement stmt = con.createStatement();
	      String line = null;
	      ResultSet rs = stmt.executeQuery("select * from usr_reviews r LEFT JOIN usr u on r.usr_id=u.id;");
	      
	      while (rs.next()) {
	    	  	int review_id = rs.getInt("review_id");
			    int usr_id = rs.getInt("usr_id");
			    int center_id = rs.getInt("center_id");
			    short center_type = rs.getShort("center_type");
			    float rating = rs.getFloat("rating");
			    String review = rs.getString("review");
			    
			    String username = rs.getString("username");
			    
			    line =  new String(REVIEW + "#"
			    					+ review_id + "#"
						    		+ usr_id + "#"
						    		+ username + "#"
						    		+ center_id + "#"
						    		+ center_type + "#"
						    		+ rating + "#"
						    		+ review);
			    reviews.add(line);
			    System.out.println("line=" + line);
			}
	      stmt.close();
	      rs.close();
	      
	   return reviews;

	}	
	
	
	public static HashMap getUserInfo(String username) throws Exception {
		  HashMap info = new HashMap();
		  int usr_id = 1;
		  String emailAddress = null;
		  Statement stmt0 = con.createStatement();
	      ResultSet rs0 = stmt0.executeQuery("select * from usr where username like '" + username + "';");
	      while (rs0.next()) {
	    	  usr_id = rs0.getInt("id");
	    	  emailAddress = rs0.getString("email_address");
	    	  info.put("usr_id", usr_id);
	    	  info.put("email_address", emailAddress);
	    	  break;
	      }
	      
	      stmt0.close();
	      rs0.close();
	      
	      return info;
	}
	
	
	public static void insertUser(String username, String password) throws SQLException {
		PreparedStatement prep = con.prepareStatement(
			    "insert into usr (username,password) values (?,?);");
		prep.setString(1, username);
		prep.setString(2, password);
		 
		prep.executeUpdate();
		
		prep.close();
	}
	
	public static void insertReview(String inputLine) throws SQLException {
		/**
		 * inputLine should look like this
		 * "insert review#usr_id#center_id#center_type#rating#review"
		 */
		System.out.println("insertReview:" + inputLine);
		String[] fields = inputLine.split("#");
		PreparedStatement prep = con.prepareStatement(
			    "insert into usr_reviews (usr_id, center_id, center_type, rating, review) values (?,?,?,?,?);");
		prep.setInt(1, Integer.parseInt(fields[1]));
		prep.setInt(2, Integer.parseInt(fields[2]));
		prep.setShort(3, Short.parseShort(fields[3]));
		prep.setFloat(4, Float.parseFloat(fields[4]));
		prep.setString(5, fields[5]);
		 
		prep.executeUpdate();
		
		prep.close();
	}
	
	public static void insertNutritionist(String inputLine) throws SQLException {
		/**
		 * inputLine should look like this
		 * "insert nutritionist#usr_id#title#description#address#phone#open#web#latitude#longitude"
		 */
		String[] fields = inputLine.split("#");
		PreparedStatement prep = con.prepareStatement(
			    "insert into nutritionist (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);");
		prep.setInt(1, Integer.parseInt(fields[1])); //usr_id
		prep.setString(2, fields[2]); //title
		prep.setString(3, fields[3]); //description
		prep.setString(4, fields[4]); //address
		prep.setString(5, fields[5]); //phone
		prep.setString(6, fields[6]); //open
		prep.setString(7, fields[7]); //web
		prep.setInt(8, Integer.parseInt(fields[8])); //latitude
		prep.setInt(9, Integer.parseInt(fields[9])); //longitude
		 
		prep.executeUpdate();
		
		prep.close();
	}
	
	public static void insertSportCenter(String inputLine) throws SQLException {
		/**
		 * inputLine should look like this
		 * "insert fitness#usr_id#title#description#address#phone#open#web#latitude#longitude#centerType"
		 */
		String[] fields = inputLine.split("#");
		short centerType = Short.parseShort(fields[10]);
		String query = null;
		switch (centerType) {
			case Identifier.FITNESS_BROWSER: 
				query = "insert into fitnesscenter (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);";
			case Identifier.JUDO_BROWSER: 
				query = "insert into judocenter (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);";
			case Identifier.KARATE_BROWSER: 
				query = "insert into karatecenter (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);";
			case Identifier.YOGA_BROWSER: 
				query = "insert into yogacenter (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);";
			case Identifier.WRESTLING_BROWSER: 
				query = "insert into wrestlingcenter (usr_id, title, description, address, phone, open, web, latitude, longitude) values (?,?,?,?,?,?,?,?,?);";
		}
		
		PreparedStatement prep = con.prepareStatement(query);
		prep.setInt(1, Integer.parseInt(fields[1])); //usr_id
		prep.setString(2, fields[2]); //title
		prep.setString(3, fields[3]); //description
		prep.setString(4, fields[4]); //address
		prep.setString(5, fields[5]); //phone
		prep.setString(6, fields[6]); //open
		prep.setString(7, fields[7]); //web
		prep.setInt(8, Integer.parseInt(fields[8])); //latitude
		prep.setInt(9, Integer.parseInt(fields[9])); //longitude
		 
		prep.executeUpdate();
		
		prep.close();
	}
	
	public static void updateSettings(String inputLine, ClientThread clientTread) throws SQLException {
		/**
		 * inputLine should look like this
		 * "settings changed#usr_id#notif_status_bar#notif_by_email#email_address"
		 */
		String[] fields = inputLine.split("#");
		String id = fields[1];
		String notifStatusBar = fields[2];
		String notifByEmail = fields[3];
		String email = fields[4];
		PreparedStatement prep = null;
		if (email != null) {
			String query = "update usr set notif_status_bar=" + notifStatusBar + ", notif_by_email=" + notifByEmail + ", email_address ='" + email + "' where id=" + id + ";";
			prep = con.prepareStatement(query);
			
			clientTread.setNotifStatusBar(Boolean.valueOf(notifStatusBar));
			clientTread.setNotifByEmail(Boolean.valueOf(notifByEmail));
			clientTread.setEmailAddress(email);
			
		} else {
			String query = "update usr set notif_status_bar=" + notifStatusBar + ", notif_by_email=" + notifByEmail + " where id=" + id + ";";
			prep = con.prepareStatement(query);
			
			clientTread.setNotifStatusBar(Boolean.valueOf(notifStatusBar));
			clientTread.setNotifByEmail(Boolean.valueOf(notifByEmail));
		}
		 
		prep.executeUpdate();
		
		prep.close();
	}
	
	public static boolean validateAccount(String username, String password) throws SQLException {
		 Statement stmt = null;
		 String query = "select * from usr where username like '" + username + "' and password like '" + password + "';";
		 stmt = con.createStatement();
		 ResultSet rs = stmt.executeQuery(query);
		 if (rs.next()) {
			 return true;
		 }
		 
		return false;
	}
	
	public static boolean existingUsername(String username) throws SQLException {
		 Statement stmt = null;
		 String query = "select * from usr where username like '" + username + "';";
		 stmt = con.createStatement();
		 ResultSet rs = stmt.executeQuery(query);
		 if (rs.next()) {
			 return true;
		 }
		 
		return false;
	}
	
	
	public static void insertMyLocation(String query) throws SQLException {
		Statement stmt = con.createStatement();
		stmt.executeUpdate(query);
		
		stmt.close();
	}
	
	public static int removeMyLocation(String query) throws SQLException {
		int ret = 0;
		
		Statement stmt = con.createStatement();
		ret = stmt.executeUpdate(query);
		
		stmt.close();
		
		return ret;
	}
	
}