package ro.dailyhealth.notification;

import java.util.List;

import ro.dailyhealth.maps.UserLocations;
import ro.dailyhealth.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;


public class NotificationBuilder {
	static Context _context = null;
	
	private static final int NOTIFICATION_DEFAULT = 1;
	
    public static void showNotification(Context context, String text) {
    	_context = context;
    	getCurrentActivity();
        final Resources res = _context.getResources();
        final NotificationManager notificationManager = (NotificationManager) _context.getSystemService(
        		android.content.Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(_context)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setTicker(_context.getString(R.string.notification_text))
                .setContentIntent(getDialogPendingIntent(text)); //Tapped the notification entry.

            // Sets a custom content view for the notification, including an image button.
            RemoteViews layout = new RemoteViews(_context.getPackageName(), R.layout.notification);
            layout.setTextViewText(R.id.notification_title, _context.getString(R.string.app_name));
            layout.setOnClickPendingIntent(R.id.notification_button,
                    getDialogPendingIntent(text)); //Tapped the 'dialog' button in the notification.
            builder.setContent(layout);

            // Notifications in Android 3.0 now have a standard mechanism for displaying large
            // bitmaps such as contact avatars. Here, we load an example image and resize it to the
            // appropriate size for large bitmaps in notifications.
            Bitmap largeIconTemp = BitmapFactory.decodeResource(res,
                    R.drawable.app_icon_largest);
            Bitmap largeIcon = Bitmap.createScaledBitmap(
                    largeIconTemp,
                    res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                    res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                    false);
            largeIconTemp.recycle();

            builder.setLargeIcon(largeIcon);


        notificationManager.notify(NOTIFICATION_DEFAULT, builder.getNotification());
    }
    
    static PendingIntent getDialogPendingIntent(String dialogText) {
        return PendingIntent.getActivity(
        		_context,
                dialogText.hashCode(), // Otherwise previous PendingIntents with the same
                                       // requestCode may be overwritten.
                new Intent(UserLocations.ACTION_DIALOG)    //getCurrentActivity())
                        .putExtra(Intent.EXTRA_TEXT, dialogText)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        
                0);
    }
    
    static String getCurrentActivity() {
		// get a list of running processes and iterate through them
		ActivityManager am = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
			 
		// get the info from the currently running task
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			 
		Log.d("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
			 
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		Log.d("current task :", componentInfo.getPackageName());
		
		return componentInfo.getPackageName();
    }

}
