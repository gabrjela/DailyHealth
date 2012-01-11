package ro.dailyhealth.maps;

import java.util.List;

import ro.dailyhealth.R;
import ro.dailyhealth.maps.UserLocations.LayersOverlay;
import ro.dailyhealth.utils.Identifier;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CustomMapView extends MapView {
	
	private int[] icons = 
			new int[] {
			R.drawable.nutr3,
			R.drawable.fitness,
			R.drawable.karate,
			R.drawable.judo,
			R.drawable.yoga,
			R.drawable.wrestling,
			R.drawable.user2
	};
	Drawable _marker = getResources().getDrawable(R.drawable.pinpoint1);
	
	public CustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	private int oldZoomLevel = 15;

	@Override
	public void dispatchDraw(Canvas canvas) {
		  super.dispatchDraw(canvas);
		  if (getZoomLevel() != oldZoomLevel) {
			   Log.d("CustomMapView", "ZOOOMED " + getZoomLevel());
			   oldZoomLevel = getZoomLevel();
			   
			   for (Overlay layer : getOverlays()) {
				   if (layer instanceof LayersOverlay) {
					   List<OverlayItem> items = ((LayersOverlay)layer).items;

					   int layerCategory = ((LayersOverlay) layer).layerCategory;
					   _marker = scaleIcon(layerCategory, oldZoomLevel);
					   
					   ((LayersOverlay) layer).callBoundCenterBottom(_marker);
					   for (OverlayItem item : items) {
						   item.setMarker(_marker);
					   }
					   ((LayersOverlay) layer).callPopulate();
				   }
			   }
			   invalidate();
			   
		  }
	}
	
	
	public BitmapDrawable scaleIcon(int layer, int oldZoomLevel) {
		Bitmap iconTemp = BitmapFactory.decodeResource(
										getResources(),
									    icons[layer]);
		float percent = 0;
		if (oldZoomLevel >= 15) {
			percent = 1f;
		   }
		if (oldZoomLevel >= 13 && oldZoomLevel < 15) { //reduce icon size by 50%  
			percent = 0.5f;
		   }
		if (oldZoomLevel >= 5 && oldZoomLevel < 13) {
			percent = 0.1f;
		   }
		if (oldZoomLevel < 5) {
			percent = 0.01f;
		}
		
		if (layer == Identifier.MY_LOCATIONS_BROWSER) {
			percent = 1f;
		}
		
	    Bitmap scaledIcon = Bitmap.createScaledBitmap(
	    		iconTemp,
	    		(int) (iconTemp.getWidth() * percent) + 1,
	    		(int) (iconTemp.getHeight() * percent) + 1,
	            false);
	    
	    iconTemp.recycle();
	    
	    return new BitmapDrawable(scaledIcon);
	}
	

}
