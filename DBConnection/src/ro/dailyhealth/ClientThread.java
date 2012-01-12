package ro.dailyhealth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.mail.MessagingException;
import ro.dailyhealth.util.SendMail;


public class ClientThread extends Thread {
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private int clientId = 0;
	private int usr_id = 0;
	private SendMail sendMail = null;
	
	private boolean notifStatusBar = true;
	private boolean notifByEmail = false;
	private String emailAddress = null;
	
	public ClientThread(Socket clientSocket, int clientId) {
		super();
		this.clientSocket = clientSocket;
		this.clientId = clientId;
		sendMail = new SendMail();
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
        out = null;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			ArrayList<String> nutritionists = new ArrayList<String>();
	        ArrayList<String> sportCenters = new ArrayList<String>();
	        ArrayList<String> myLocations = new ArrayList<String>();
	        ArrayList<String> reviews = new ArrayList<String>();
	        
	        
	        String inputLine = null;
	        while ((inputLine = in.readLine()) != null) {
	        	
	        	if ((inputLine != null) && (!inputLine.equalsIgnoreCase("Bye"))) {
	        			if (inputLine.equalsIgnoreCase(Payload.LOAD_SPORT_CENTERS)) { //"load data"
	        				MyServer.clients.add(this);
	        				
			        		try {
								nutritionists = DbConnection.fetchNutritionistsFromDb();
								for (int i = 0; i < nutritionists.size(); i++) {
									out.println(nutritionists.get(i));
								}
								sportCenters = DbConnection.fetchSportCentersFromDb(DbConnection.FITNESS);
								for (int i = 0; i < sportCenters.size(); i++) {
									out.println(sportCenters.get(i));
								}
								sportCenters = DbConnection.fetchSportCentersFromDb(DbConnection.KARATE);
								for (int i = 0; i < sportCenters.size(); i++) {
									out.println(sportCenters.get(i));
								}
								sportCenters = DbConnection.fetchSportCentersFromDb(DbConnection.JUDO);
								for (int i = 0; i < sportCenters.size(); i++) {
									out.println(sportCenters.get(i));
								}
								sportCenters = DbConnection.fetchSportCentersFromDb(DbConnection.YOGA);
								for (int i = 0; i < sportCenters.size(); i++) {
									out.println(sportCenters.get(i));
								}
								sportCenters = DbConnection.fetchSportCentersFromDb(DbConnection.WRESTLING);
								for (int i = 0; i < sportCenters.size(); i++) {
									out.println(sportCenters.get(i));
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
			        		
			        		out.println(Payload.LOAD_SPORT_CENTERS_COMPLETED);
			        	}
	        			
	        			
			        	if (inputLine.startsWith(Payload.LOAD_MYLOCATIONS)) {
			        		try {
			        			String[] fields = inputLine.split("#");
			        			String username = fields[1];
			        			
			        			myLocations = DbConnection.fetchMyLocationsFromDb(username);
								for (int i = 0; i < myLocations.size(); i++) {
									out.println(myLocations.get(i));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
			        		
			        		out.println(Payload.LOAD_MYLOCATIONS_COMPLETED);
			        	}
			        	
			        	
			        	if (inputLine.startsWith(Payload.LOAD_REVIEWS)) {
			        		try {
			        			reviews = DbConnection.fetchReviewsFromDb();
								for (int i = 0; i < reviews.size(); i++) {
									out.println(reviews.get(i));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
			        		
			        		out.println(Payload.LOAD_REVIEWS_COMPLETED);
			        	}			        	
			        	
			        	
				        if (inputLine.startsWith(Payload.INSERT_USER)) {
				        	String[] fields = inputLine.split("#");
				        	String username = fields[1];
				        	String password = fields[2];
				        	try {
				        		boolean existingUsername = DbConnection.existingUsername(username);
				        		if (!existingUsername) {
									DbConnection.insertUser(username, password);
									usr_id = (Integer) DbConnection.getUserInfo(username).get("usr_id");
									emailAddress = (String) DbConnection.getUserInfo(username).get("email_address");
									out.println(Payload.AUTHENTICATION + "#" + Payload.ACCOUNT_CREATED + "#" + usr_id);
				        		} else { //username in database
				        			boolean valid = DbConnection.validateAccount(username, password);
				        			if (valid) {
				        				usr_id = (Integer) DbConnection.getUserInfo(username).get("usr_id");
				        				emailAddress = (String) DbConnection.getUserInfo(username).get("email_address");
				        				out.println(Payload.AUTHENTICATION + "#" + Payload.VALID_ACCOUNT + "t#" + usr_id);
				        			} else {
				        				out.println(Payload.AUTHENTICATION + "#" + Payload.INVALID_ACCOUNT);
				        			}
				        		}
							} catch (SQLException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
				        }
				        
				        if (inputLine.startsWith("insert review")) {
							try {
								DbConnection.insertReview(inputLine);
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }
				        
				        if (inputLine.startsWith("insert nutritionist")) {
							try {
								DbConnection.insertNutritionist(inputLine);
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }	
				        
				        if (inputLine.startsWith("insert fitness")) {
							try {
								DbConnection.insertSportCenter(inputLine);
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }
				        
				        if (inputLine.startsWith(Payload.SETTINGS_CHANGED)) {
							try {
								DbConnection.updateSettings(inputLine, this);
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }					        
				        
				        if (inputLine.startsWith("insert into usr_locations")) {
				        	try {
								DbConnection.insertMyLocation(inputLine);
								out.println("insert mylocation completed");
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }
				        
				        if (inputLine.startsWith("delete from usr_locations")) {
				        	try {
								int ret = DbConnection.removeMyLocation(inputLine);
							} catch (SQLException e) {
								e.printStackTrace();
							}
				        }
				        
				        if (inputLine.startsWith("notification")) {
				        	broadcastNotification(inputLine);
				        }
				        
	        	}
	        	
	        	if ((inputLine != null) && (inputLine.equalsIgnoreCase("Bye"))) {
	        		break;
	        	}
	        }; //end while
	        
	        System.out.println("Server closed for client " + clientId);
	        
	        MyServer.clients.remove(this);
			
			in.close();
			out.close();
			clientSocket.close();
			
        } catch (IOException e) {
            System.err.println("Accept failed for client " + clientId);
        }
        
	} //end run
	

	public PrintWriter getOut() {
		return out;
	}


	private void broadcastNotification(String notification) {
		if (MyServer.clients != null) {
			Iterator<ClientThread> itr = MyServer.clients.iterator();
			while (itr.hasNext()) {
				ClientThread client = (ClientThread) itr.next();
				
				if (client.clientId == clientId) {
					continue;
				}
				
				System.out.println("Broadcast to clientId = " + client.clientId);
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				if (client.isAlive()) {
					//send mail
		        	if (client.isNotifByEmail() && (client.emailAddress != null)) {
			        	try {
							sendMail.sendMail(new String[] {client.emailAddress}, notification);
							System.out.println("Mail sent to " + client.clientId + " on address " + client.emailAddress);
						} catch (MessagingException e) {
							e.printStackTrace();
							System.out.println("Couldn't send mail to " + client.clientId + " on address " + client.emailAddress);
						}
		        	}
		        	
		        	//send notification to client
		        	if (client.isNotifStatusBar()) {
		        		client.getOut().println(notification);
		        	}
				}
				
			}
		}
		
	}


	public boolean isNotifStatusBar() {
		return notifStatusBar;
	}


	public void setNotifStatusBar(boolean notifStatusBar) {
		this.notifStatusBar = notifStatusBar;
	}


	public boolean isNotifByEmail() {
		return notifByEmail;
	}


	public void setNotifByEmail(boolean notifByEmail) {
		this.notifByEmail = notifByEmail;
	}


	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
}
