package ro.dailyhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ro.dailyhealth.db.DBUtils;
import ro.dailyhealth.maps.UserLocations;
import ro.dailyhealth.utils.Identifier;
import ro.dailyhealth.utils.Payload;

import android.util.Log;

public class RxThread extends Thread {
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	public boolean loadCentersCompleted = false;
	public boolean loadMyLocationsCompleted = false;
	public boolean loadNotificationCompleted = false;
	public boolean loadReviewsCompleted = false;
	
	private String userAuthenticationResponse = null;
	
	public RxThread() {
		super();
	}

	@Override
	public void run() {
		try {
			socket = new Socket("192.168.0.3",4444); // 93.114.42.247   192.168.0.3
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		
			Log.d("MainActivity", "RxThread started");
			
			//init buffer lists
			DBUtils.prepareAllLists();
			
			out.println(Payload.LOAD_SPORT_CENTERS);
			out.println(Payload.LOAD_REVIEWS);
				
			//read from server socket line by line
			String inputLine = null;
			while((inputLine = in.readLine()) != null) {
				System.out.println("inputLine:" + inputLine);

				//get the user login validation response from server 
				if (inputLine.startsWith(Payload.AUTHENTICATION)) {
					userAuthenticationResponse = inputLine;
				}

				if (inputLine.startsWith(Payload.NOTIFICATION)) {
					UserLocations.notif.putNotification(inputLine); 
				}

				if (inputLine.startsWith(Identifier.NUTRITIONIST) ||
					inputLine.startsWith(Identifier.FITNESS) ||
					inputLine.startsWith(Identifier.KARATE) ||
					inputLine.startsWith(Identifier.JUDO) ||
					inputLine.startsWith(Identifier.YOGA) ||
					inputLine.startsWith(Identifier.WRESTLING) ) {
					DBUtils.addCenter(inputLine);
				}
						 	
				if (inputLine.startsWith(Identifier.MY_LOCATIONS)) {
					DBUtils.addMyLocation(inputLine);
				}

				if (inputLine.startsWith(Payload.REVIEW)) {
					DBUtils.addReview(inputLine);
				}

				if (inputLine.equalsIgnoreCase(Payload.LOAD_SPORT_CENTERS_COMPLETED)) {
					loadCentersCompleted = true;
				}
				
				if (inputLine.equalsIgnoreCase(Payload.LOAD_MYLOCATIONS_COMPLETED)) {
					loadMyLocationsCompleted = true;
				}
				
				if (inputLine.equalsIgnoreCase(Payload.LOAD_REVIEWS_COMPLETED)) {
					loadReviewsCompleted = true;
				}
				
				if (loadCentersCompleted && loadMyLocationsCompleted && loadReviewsCompleted) {
					Log.d(this.getClass().toString(), "All data loaded from db.");
					
					// put/release lock for ProgressDialog thread
					UserLocations.q.putLock();
		
					loadCentersCompleted = false;
					loadMyLocationsCompleted = false;
					loadReviewsCompleted = false;
				}


				if (inputLine.equalsIgnoreCase(Payload.BYE)) { //server sent Bye to the client
					Log.d("MainActivity", "Bye from server");
					break;
				}
				    	
			}
					
			Log.d("MainActivity", "RxThread died");
					
			in.close();
			out.close();
			socket.close();
				
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	} //end run


	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public PrintWriter getOut() {
		return out;
	}
	
	public String getUserAuthenticationResponse() {
		return userAuthenticationResponse;
	}

	public void setUserAuthenticationResponse(String userAuthenticationResponse) {
		this.userAuthenticationResponse = userAuthenticationResponse;
	}
	
}
