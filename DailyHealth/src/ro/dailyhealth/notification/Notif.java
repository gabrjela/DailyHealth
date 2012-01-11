package ro.dailyhealth.notification;

import android.util.Log;


public class Notif {
	private String notification = null;
	boolean valueSet = false;
	
	public Notif() {
	}
	
	public synchronized String getNotification() {
		if(!valueSet)
			try {
				wait();
			} catch (InterruptedException e) {
				Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
			}
		Log.d(this.getClass().toString(), "Received notification from server : " + notification);
		valueSet = false;
		notify();
		return notification;
	}
	
	public synchronized void putNotification(String notification) {
		if (valueSet)
			try {
				wait();
			} catch (InterruptedException e) {
				Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
			}
		
		valueSet = true;
		Log.d(this.getClass().toString(), "Send notification to server : " + notification);
		this.notification = notification;
		notify();
	}
	
}
