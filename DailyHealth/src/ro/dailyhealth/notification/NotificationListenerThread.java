package ro.dailyhealth.notification;

import android.content.Context;
import ro.dailyhealth.maps.UserLocations;

public class NotificationListenerThread extends Thread {
	private Context context;
	
	public NotificationListenerThread(Context context)  {
		super();
		this.context = context;
	}

	@Override
	public void run() {
			String notification = null;
		
			while(true) {
				notification = UserLocations.notif.getNotification();
				String[] fields = notification.split("#");
				NotificationBuilder.showNotification(context, fields[2]);
			}
			
	}
	
}

