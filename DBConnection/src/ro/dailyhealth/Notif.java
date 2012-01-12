package ro.dailyhealth;

import java.util.ArrayList;

public class Notif {
	ArrayList<String> notifications = null;
	boolean valueSet = false;
	
	public Notif() {
		notifications = new ArrayList<String>();
	}
	
	public synchronized ArrayList<String> getNotifications(int clientId) {
		try {
			wait();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException caught " + e.getMessage());
		}
		
		return notifications;
	}
	
	public synchronized void putNotification(String notification, int clientId) {
		notifications.add(notification);
		valueSet = true;
		notify();
	}
	
}
