package ro.dailyhealth.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import ro.dailyhealth.MainActivity;
import ro.dailyhealth.Q;
import ro.dailyhealth.beans.MyLocation;
import ro.dailyhealth.beans.Nutritionist;
import ro.dailyhealth.beans.Review;
import ro.dailyhealth.beans.SportCenter;
import ro.dailyhealth.db.DBUtils;
import ro.dailyhealth.notification.MyDialogFragment;
import ro.dailyhealth.notification.Notif;
import ro.dailyhealth.notification.NotificationListenerThread;
import ro.dailyhealth.utils.Identifier;
import ro.dailyhealth.utils.LocationUtils;
import ro.dailyhealth.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class UserLocations extends MapActivity {
	  private final static int SHOW_MY_LOCATION = 11;
	  private final static int SET_MY_LOCATION = 12;
	  private final static int REMOVE_MY_LOCATION = 13;
	  
	  private final static String DISPLAYED_CHILD = "displayed_child";
	  private final static String DISPLAYED_CATEGORY = "displayed_CATEGORY";
	  
	  public static final String ACTION_DIALOG = "ro.dailyhealth.action.DIALOG";
	  
	  
	  private CustomMapView map = null;
	  private MapController mapController = null;
	  private ViewFlipper vf = null;
	  private ViewFlipper vfNutr = null;
	  private ViewFlipper vfFitn = null;
	  private ImageButton nutrCloseButton = null;
	  private ImageButton sportsCloseButton = null;
	  private ImageButton fitnCloseButton = null;
	  private ImageButton categBackButton = null;
	  private ImageButton nutrReviewsBackButton = null;
	  private ImageButton fitnReviewsBackButton = null;
	  
	  private ImageButton myLocationsClose = null;
	  
	  private ListView lv1 = null;
	  private ListView lv1Reviews = null;
	  private NutrAdapter lv1Adapter = null;
	  private NutrReviewsAdapter lv1ReviewsAdapter = null;
	  
	  private ListView lv2 = null;
	  private ListView lv2Reviews = null;
	  private SportCategoriesAdapter lv2Adapter = null;
	  private NutrReviewsAdapter lv2ReviewsAdapter = null;
	  
	  private ListView lv3 = null; //for my locations popup
	  private GridView grid = null;

	  private TextView sportCategTitle = null;
	  private CustomPopupMenu popup = null;
	  private SearchView searchViewNutr = null;
	  private TextView searchViewNutrTextView = null;
	  private SearchView searchViewFitn = null;
	  private TextView searchViewFitnTextView = null;
	  private ImageButton nutrAdd = null;
	  private ImageButton nutrAddReview = null;
	  private ImageButton fitnAdd = null;
	  private ImageButton fitnAddReview = null;
	  private MyLocationOverlay me = null;
	  private PopupPanel panel = null;
	  private TextView myLocationsItem = null;
	  private ProgressDialog m_ProgressDialog = null;
	  
	  //objects for Nutritionist description popup
	  private Nutritionist selectedNutr = null; // the selected nutritionist
	  private int selectedItemInList = 0;
	  private TextView nutrTitle = null;
	  private TextView nutrAddress = null;
	  private TextView nutrPhone = null;
	  private TextView nutrOpen = null;
	  private TextView nutrWeb = null;
	  private TextView nutrDescription = null;
	  private RatingBar nutrRatingBar = null;
	  private TextView noReviews = null;
	  
	  private int selectedLatitude = 0;
	  private int selectedLongitude = 0;
	  
	  //objects for Fitness description popup
	  private SportCenter selectedFitn = null; // the selected fitness club
	  private ImageView fitnIcon = null;
	  private TextView fitnHeader = null;
	  private TextView fitnTitle = null;
	  private TextView fitnAddress = null;
	  private TextView fitnPhone = null;
	  private TextView fitnOpen = null;
	  private TextView fitnWeb = null;
	  private TextView fitnDescription = null;
	  private RatingBar fitnRatingBar = null;
	  private TextView fitnNoReviews = null;
	  
	  private Runnable returnRes = null;
	  
	  private static final int DIALOG_ADD_DEFAULT = 0;
	  private static final int DIALOG_ADD_REVIEW_NUTRITIONIST = 1;
	  private static final int DIALOG_ADD_NUTRITIONIST = 2;
	  private static final int DIALOG_ADD_REVIEW_FITNESS = 3;
	  private static final int DIALOG_ADD_FITNESS = 4;
	  private static final int DIALOG_ADD_KARATE = 5;
	  private static final int DIALOG_ADD_JUDO = 6;
	  private static final int DIALOG_ADD_YOGA = 7;
	  private static final int DIALOG_ADD_WRESTLING = 8;
	  private int dialog_mode = DIALOG_ADD_DEFAULT;
	  
	  
	  private int mode = 0;
	  
	  private int displayedCategory = 0;
	  private int displayedChild = 0;
	  
	  private int[] fitnessIcons = null;
	  private int[] fitnessBigIcons = null;
	  private String fitnessLabels[] = null;
	  
	  private ArrayList<Nutritionist> nutritionists = null;
	  private ArrayList<SportCenter> fitnessCenters = null;
	  private ArrayList<SportCenter> karateCenters = null;
	  private ArrayList<SportCenter> judoCenters = null;
	  private ArrayList<SportCenter> yogaCenters = null;
	  private ArrayList<SportCenter> wrestlingCenters = null;
	  
	  private LayersOverlay[] layers = null;
	  
	  public static final int SWITCH_ACCOUNT = 1;
	  
	  public static Notif notif = null;
	  private NotificationListenerThread notificationListenerThread = null;
	  
	  public static final Q q = new Q();
	  
	  private String filterText = null;
	  
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.mapview);
	    
	    if (notificationListenerThread == null) {
		    notificationListenerThread = new NotificationListenerThread(this);
		    notificationListenerThread.start();//wait for notification
	    }
	    
	    displayedChild = (savedInstanceState == null) ? 0 : savedInstanceState.getInt(DISPLAYED_CHILD);
	    displayedCategory = (displayedChild == 3) ? savedInstanceState.getInt(DISPLAYED_CATEGORY) : 0;
	    
	    map = (CustomMapView)findViewById(R.id.map);
	    
	    mapController = map.getController();
	    double[] latlon = LocationUtils.geocode("Bucuresti", this);
	    mapController.setCenter(getPointFromMicrodegrees(latlon[0], latlon[1]));
	    mapController.setZoom(15);
	    
	    map.setBuiltInZoomControls(true);
	    map.setTraffic(false);
	    
	    me = new MyLocationOverlay(this, map);
	    map.getOverlays().add(me);
	    
	    vf = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
	    hidePopup();
	    
	    panel = new PopupPanel(R.layout.bubble_message);
	    
	    
	    vfNutr = (ViewFlipper) this.findViewById(R.id.ViewFlipperNutr);
	    vfNutr.setDisplayedChild(0);
	    
	    nutrCloseButton = (ImageButton) findViewById(R.id.nutrClose);
	    if (nutrCloseButton != null) {
	    	nutrCloseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (vfNutr.getDisplayedChild() == 1) { // on closes the search view
						lv1.clearTextFilter();
						searchViewNutrTextView.setText(null);
					}
					vfNutr.showNext();
				}
			});
	    }
	    
	    sportsCloseButton = (ImageButton) findViewById(R.id.sportsClose);
	    if (sportsCloseButton != null) {
	    	sportsCloseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					hidePopup();
				}
			});
	    }
	    
	    categBackButton = (ImageButton) findViewById(R.id.back);
	    if (categBackButton != null) {
	    	categBackButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showSportsPopup();
				}
			});
	    }
	    
	    
	    vfFitn = (ViewFlipper) this.findViewById(R.id.ViewFlipperFitn);
	    vfFitn.setDisplayedChild(0);
	    
	    fitnCloseButton = (ImageButton) findViewById(R.id.fitnClose);
	    if (fitnCloseButton != null) {
	    	fitnCloseButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (vfFitn.getDisplayedChild() == 1) { // on closes the search view
						lv2.clearTextFilter();
						searchViewFitnTextView.setText(null);
					}
					vfFitn.showNext();
				}
			});
	    }
	    
	    nutrReviewsBackButton = (ImageButton) findViewById(R.id.nutrReviewsBack);
	    if (nutrReviewsBackButton != null) {
	    	nutrReviewsBackButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showNutritionPopup();
					
				}
			});
	    }
	    
	    fitnReviewsBackButton = (ImageButton) findViewById(R.id.fitnReviewsBack);
	    if (fitnReviewsBackButton != null) {
	    	fitnReviewsBackButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showSportCategoryPopup(displayedCategory);
				}
			});
	    }
	    
	    myLocationsClose = (ImageButton) findViewById(R.id.myLocationsClose);
	    if (myLocationsClose != null) {
	    	myLocationsClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					hidePopup();
					hideLayer(getString(R.string.mylocations_layer));
					if (myLocationsItem != null)
						myLocationsItem.setText(getString(R.string.show_locations));
					
					panel.hide();
					
					//abort set/remove my location
					layers[Identifier.MY_LOCATIONS_BROWSER].abortSetMyLocation();
					layers[Identifier.MY_LOCATIONS_BROWSER].abortRemoveMyLocation();
				}
			});
	    }
	    
	    sportCategTitle = (TextView) findViewById(R.id.textViewCategory) ;
	    
	    // On nutritionist description page
	    nutrTitle = (TextView) findViewById(R.id.nutrTitle) ;
	    nutrAddress = (TextView) findViewById(R.id.nutrAddress) ;
	    nutrPhone = (TextView) findViewById(R.id.nutrPhone) ;
	    nutrOpen = (TextView) findViewById(R.id.nutrOpen) ;
	    nutrWeb = (TextView) findViewById(R.id.nutrWeb) ;
	    nutrDescription = (TextView) findViewById(R.id.nutrDescription) ;
	    nutrRatingBar = (RatingBar) findViewById(R.id.nutrRatingBar) ;
	    noReviews = (TextView) findViewById(R.id.no_reviews);
	    lv1Reviews = (ListView) findViewById(R.id.lv1Reviews);
	    
	    // On Fitness club description page
	    fitnIcon = (ImageView) findViewById(R.id.Icon_Fitness);
	    fitnHeader = (TextView) findViewById(R.id.Header_Fitness) ;
	    fitnTitle = (TextView) findViewById(R.id.fitnTitle) ;
	    fitnAddress = (TextView) findViewById(R.id.fitnAddress) ;
	    fitnPhone = (TextView) findViewById(R.id.fitnPhone) ;
	    fitnOpen = (TextView) findViewById(R.id.fitnOpen) ;
	    fitnWeb = (TextView) findViewById(R.id.fitnWeb) ;
	    fitnDescription = (TextView) findViewById(R.id.fitnDescription) ;
	    fitnRatingBar = (RatingBar) findViewById(R.id.fitnRatingBar) ;
	    fitnNoReviews = (TextView) findViewById(R.id.fitn_no_reviews);
	    lv2Reviews = (ListView) findViewById(R.id.lv2Reviews);
	    
	    lv1 = (ListView) findViewById(R.id.listView02);
	    lv1.setFocusable(true);
	    lv1.setFocusableInTouchMode(true);
	    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//make sure the corresponding layer is displayed
				showLayer(getString(R.string.nutr_layer));
				
				selectedNutr = (Nutritionist) parent.getItemAtPosition(position);
				selectedItemInList = position;
				int lat = selectedNutr.getLatitude();
				int lon = selectedNutr.getLongitude();
				GeoPoint geo = new GeoPoint(lat, lon);
				mapController.animateTo(geo);
				
				// show bubble message
				OverlayItem item = layers[0].searchItem(geo);
				View bubbleView = panel.getView();
				((TextView)bubbleView.findViewById(R.id.bubbleTitle))
					          .setText(item.getTitle());
				panel.show(geo);
				panel.setCategory(0);
				
				//open nutritionist popup
				vf.setDisplayedChild(5);
				if (selectedNutr.getTitle() != null) nutrTitle.setText(selectedNutr.getTitle());
				if (selectedNutr.getAddress() != null) nutrAddress.setText(selectedNutr.getAddress());
				if (selectedNutr.getPhone() != null) nutrPhone.setText(selectedNutr.getPhone());
				if (selectedNutr.getOpen() != null) nutrOpen.setText(selectedNutr.getOpen());
				if (selectedNutr.getWeb() != null) nutrWeb.setText(selectedNutr.getWeb());
				if (selectedNutr.getDescription() != null) nutrDescription.setText("\"" + selectedNutr.getDescription() + "\"");
				nutrRatingBar.setRating(selectedNutr.getRating());
				noReviews.setText(String.valueOf(selectedNutr.getNoOfReviews()));
				
				lv1ReviewsAdapter = new NutrReviewsAdapter(Identifier.NUTRIONIST_BROWSER, selectedNutr.getReviews());
				lv1Reviews.setAdapter(lv1ReviewsAdapter);

			}
		});
	    lv1.setTextFilterEnabled(true);
	    
	    searchViewNutr = (SearchView) findViewById(R.id.searchViewNutr);
	    setupSearchViewNutr();

		grid = (GridView) findViewById(R.id.gridView01);
		fitnessIcons = 
				new int[] {
				R.drawable.fitness,
				R.drawable.karate,
				R.drawable.judo,
				R.drawable.yoga,
				R.drawable.wrestling
		};
		fitnessBigIcons = new int[] {
				R.drawable.fitness_big,
				R.drawable.karate_big,
				R.drawable.judo_big,
				R.drawable.yoga_big,
				R.drawable.wrestling_big
		};
		fitnessLabels = new String[] {
				Identifier.FITNESS,
				Identifier.KARATE,
				Identifier.JUDO,
				Identifier.YOGA,
				Identifier.WRESTLING,
		};
		grid.setAdapter(new AppsAdapter());
		grid.setClickable(true);
		grid.setFocusableInTouchMode(true);
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long id) {
				showSportCategoryPopup(position);
				
				popup.getMenu().getItem(position + 1).setChecked(true); //the 1st is Nutritionists
				showLayer(fitnessLabels[position]);
				
			}
		});
		
		
	    lv2 = (ListView) findViewById(R.id.listView03); //adapter set on grid item click
	    lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectedFitn = (SportCenter) parent.getItemAtPosition(position);
				selectedItemInList = position;
				
				//make sure the corresponding layer is displayed
				showLayer(selectedFitn.getCategory());
				
				int lat = selectedFitn.getLatitude();
				int lon = selectedFitn.getLongitude();
				GeoPoint geo = new GeoPoint(lat, lon);
				mapController.animateTo(geo);
				int category = Identifier.getInt(selectedFitn.getCategory()); //sportCenter.getCategoryAsInt();
				
				// show bubble message
				OverlayItem item = layers[displayedCategory + 1].searchItem(geo);
				View bubbleView = panel.getView();
				((TextView)bubbleView.findViewById(R.id.bubbleTitle))
					          .setText(item.getTitle());
				panel.show(geo);
				panel.setCategory(category);
				
				//open center description popup
				vf.setDisplayedChild(6);
				fitnIcon.setImageResource(fitnessIcons[category - 1]);
				fitnHeader.setText(fitnessLabels[category - 1]);
				if (selectedFitn.getTitle() != null) fitnTitle.setText(selectedFitn.getTitle());
				if (selectedFitn.getAddress() != null) fitnAddress.setText(selectedFitn.getAddress());
				if (selectedFitn.getPhone() != null) fitnPhone.setText(selectedFitn.getPhone());
				if (selectedFitn.getOpen() != null) fitnOpen.setText(selectedFitn.getOpen());
				if (selectedFitn.getWeb() != null) fitnWeb.setText(selectedFitn.getWeb());
				if (selectedFitn.getDescription() != null) fitnDescription.setText("\"" + selectedFitn.getDescription() + "\"");
				fitnRatingBar.setRating(selectedFitn.getRating());
				fitnNoReviews.setText(String.valueOf(selectedFitn.getNoOfReviews()));
				
				lv2ReviewsAdapter = new NutrReviewsAdapter(category, selectedFitn.getReviews());
				lv2Reviews.setAdapter(lv2ReviewsAdapter);
			}
		});
	    lv2.setTextFilterEnabled(true);
	    
	    searchViewFitn = (SearchView) findViewById(R.id.searchViewFitn);
	    setupSearchViewFitn();
	    
	    lv3 = (ListView) findViewById(R.id.listView04);
	    lv3.setAdapter(new MyLocationsAdapter());
	    lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				switch (position) {
				case 0:
					myLocationsItem = (TextView) view.findViewById(R.id.myLocationsItem);
					if (myLocationsItem.getText().toString().equalsIgnoreCase(getString(R.string.show_locations))) {
						showLayer(getString(R.string.mylocations_layer));
						myLocationsItem.setText(R.string.hide_locations);
					} else {
						if (myLocationsItem.getText().toString().equalsIgnoreCase(getString(R.string.hide_locations))) {
							hideLayer(getString(R.string.mylocations_layer));
							myLocationsItem.setText(R.string.show_locations);
						}
					}
					mode = SHOW_MY_LOCATION;
					panel.hide();
					
					//abort set/remove my location
					layers[Identifier.MY_LOCATIONS_BROWSER].abortSetMyLocation();
					layers[Identifier.MY_LOCATIONS_BROWSER].abortRemoveMyLocation();
					
					break;
				case 1:
					Toast.makeText(UserLocations.this,
		                      getResources().getString(R.string.point_location), //(String)parent.getItemAtPosition(position),
		                      Toast.LENGTH_SHORT).show();
					mode = SET_MY_LOCATION;
					showLayer(getString(R.string.mylocations_layer));
					panel.hide();
					
					//abort set/remove my location
					layers[Identifier.MY_LOCATIONS_BROWSER].abortSetMyLocation();
					layers[Identifier.MY_LOCATIONS_BROWSER].abortRemoveMyLocation();
					
					break;
				case 2:
					Toast.makeText(UserLocations.this,
		                      getResources().getString(R.string.point_location), //(String)parent.getItemAtPosition(position),
		                      Toast.LENGTH_SHORT).show();
					mode = REMOVE_MY_LOCATION;
					showLayer(getString(R.string.mylocations_layer));
					panel.hide();
					
					//abort set/remove my location
					layers[Identifier.MY_LOCATIONS_BROWSER].abortSetMyLocation();
					layers[Identifier.MY_LOCATIONS_BROWSER].abortRemoveMyLocation();
					
					break;
				}
				
			}
		});
	    
	    
	    nutrAdd = (ImageButton) findViewById(R.id.nutrAdd);
	    nutrAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ADD_NUTRITIONIST);
				dialog_mode = DIALOG_ADD_NUTRITIONIST;
				
			}
		});
	    
	    nutrAddReview = (ImageButton) findViewById(R.id.nutrAddReview);
	    nutrAddReview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ADD_REVIEW_NUTRITIONIST);
				dialog_mode = DIALOG_ADD_REVIEW_NUTRITIONIST;
			}
		});
	    
	    fitnAddReview = (ImageButton) findViewById(R.id.fitnAddReview);
	    fitnAddReview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ADD_REVIEW_FITNESS);
				dialog_mode = DIALOG_ADD_REVIEW_FITNESS;
			}
		});
	    
	    fitnAdd = (ImageButton) findViewById(R.id.sportAdd);
	    fitnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("onClick mode=" + mode);
				switch (mode) {
					case Identifier.FITNESS_BROWSER:
						dialog_mode = DIALOG_ADD_FITNESS;
						showDialog(DIALOG_ADD_FITNESS);
						break;
					case Identifier.KARATE_BROWSER:
						dialog_mode = DIALOG_ADD_KARATE;
						showDialog(DIALOG_ADD_KARATE);
						break;
					case Identifier.JUDO_BROWSER:
						dialog_mode = DIALOG_ADD_JUDO;
						showDialog(DIALOG_ADD_JUDO);
						break;
					case Identifier.YOGA_BROWSER:
						dialog_mode = DIALOG_ADD_YOGA;
						showDialog(DIALOG_ADD_YOGA);
						break;
					case Identifier.WRESTLING_BROWSER:
						dialog_mode = DIALOG_ADD_WRESTLING;
						showDialog(DIALOG_ADD_WRESTLING);
						break;
				}
				
			}
		});
	    
	    
	    
	    if (!MainActivity.dataAlreadyLoaded) {
		    if (!MainActivity.rx.loadCentersCompleted ||
		    	!MainActivity.rx.loadMyLocationsCompleted ||
		    	!MainActivity.rx.loadNotificationCompleted) {
		    	
				    	m_ProgressDialog = ProgressDialog.show(UserLocations.this,    
				    						"Please wait...", null, true);
				    	
				    	returnRes = new Runnable() {
				            @Override
				            public void run() {
				            	DBUtils.attachReviews();
				            	setAdapters();
				            	
				    			m_ProgressDialog.dismiss();
				                MainActivity.dataAlreadyLoaded = true;
				            }
				    	};
				    	
				    	Thread loaderThread = new Thread(new Runnable() {
								@Override
								public void run() {
									//put/release lock for rx thread
									UserLocations.q.putLock();
									runOnUiThread(returnRes);
								}
				    		
				    	});
				    	loaderThread.start();
		    } else {
		    	//release lock for rx thread
		    	UserLocations.q.putLock();
		    	DBUtils.attachReviews();
		    	setAdapters(); // progress dialog not displayed
		    	MainActivity.dataAlreadyLoaded = true;
		    }
	    
	    } else { //dataAlreadyLoaded
	    	setAdapters();
	    }
	    
	  }
	  
	  
	  
	  private void setupSearchViewNutr() {
		  	searchViewNutr.setFocusable(false);
		    searchViewNutr.setFocusableInTouchMode(false);
		    
		    searchViewNutrTextView = getTextView(searchViewNutr);
		    //change the color of the search text
		    searchViewNutrTextView.setTextColor(R.color.gray);
		    
	        searchViewNutr.setIconifiedByDefault(false);
	        searchViewNutr.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	        	@Override
				public boolean onQueryTextChange(String newText) {
					if (TextUtils.isEmpty(newText)) {
			            lv1.clearTextFilter();
			        } else {
			        	lv1.setFilterText(newText.toString());
			        	lv1Adapter.notifyDataSetChanged();
			        	filterText = newText;
			        }
			        return true;
				}
	        	
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}
				
			});
	        
	        searchViewNutr.setSubmitButtonEnabled(false);
	        searchViewNutr.setQueryHint(getString(R.string.search_nutr_hint));
	        searchViewNutr.setOnCloseListener(new SearchView.OnCloseListener() {
				@Override
				public boolean onClose() {
					vfNutr.showNext();
					lv1.clearTextFilter();
					searchViewNutrTextView.setText(null);
					return true;
				}
			});
	  }
	  
	  private void setupSearchViewFitn() {
		  	searchViewFitn.setFocusable(false);
		  	searchViewFitn.setFocusableInTouchMode(false);
		    
		    searchViewFitnTextView = getTextView(searchViewFitn);
		    //change the color of the search text
		    searchViewFitnTextView.setTextColor(R.color.gray);
		    
		    searchViewFitn.setIconifiedByDefault(false);
		    searchViewFitn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	        	@Override
				public boolean onQueryTextChange(String newText) {
					if (TextUtils.isEmpty(newText)) {
			            lv2.clearTextFilter();
			        } else {
			        	System.out.println("newText=" + newText);
			        	lv2.setFilterText(newText.toString());
			        	lv2Adapter.notifyDataSetChanged();
			        	filterText = newText;
			        }
			        return true;
				}
	        	
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}
				
			});
	        
		    searchViewFitn.setSubmitButtonEnabled(false);
		    searchViewFitn.setQueryHint("Search " + fitnessLabels[displayedCategory] + " club");
		    searchViewFitn.setOnCloseListener(new SearchView.OnCloseListener() {
				@Override
				public boolean onClose() {
					vfFitn.showNext();
					lv2.clearTextFilter();
					searchViewFitnTextView.setText(null);
					return true;
				}
			});
	  }
	  
	  public void setAdapters() {
			/* populate the lists - create the adapters and attach them to the lists */
	    	LinkedBlockingQueue<Nutritionist> temp_n = DBUtils.nutritionists;
	    	nutritionists = new ArrayList<Nutritionist>(temp_n);
	    	lv1Adapter = new NutrAdapter();
	    	lv1.setAdapter(lv1Adapter);
	    	
	    	LinkedBlockingQueue<SportCenter> temp = DBUtils.fitnessCenters;
	    	fitnessCenters = new ArrayList<SportCenter>(temp);
	    	
	    	temp = DBUtils.karateCenters;
	    	karateCenters = new ArrayList<SportCenter>(temp);
	    	
	    	temp = DBUtils.judoCenters;
	    	judoCenters = new ArrayList<SportCenter>(temp);
	    	
	    	temp = DBUtils.yogaCenters;
	    	yogaCenters = new ArrayList<SportCenter>(temp);
	    	
	    	temp = DBUtils.wrestlingCenters;
	    	wrestlingCenters = new ArrayList<SportCenter>(temp);
	    	
		    // Creating the layers
		    layers = new LayersOverlay[fitnessLabels.length + 2]; //on count nutritionists and my locations layer as well
		    Drawable itemMarker = null;
			for (int i = 0; i < layers.length; i++) {
					switch (i) {
						case 0 : itemMarker = getResources().getDrawable(R.drawable.nutr3); break; //nutritionists
						case 6 : itemMarker = getResources().getDrawable(R.drawable.user2); break; //my locations
						default: itemMarker = getResources().getDrawable(fitnessIcons[i - 1]);
					}
				itemMarker.setBounds(0, 0, itemMarker.getIntrinsicWidth(),
						itemMarker.getIntrinsicHeight());
				layers[i] = new LayersOverlay(itemMarker, i);
				if (i != Identifier.MY_LOCATIONS_BROWSER) {
					map.getOverlays().add(layers[i]);
				}
		    }
			map.invalidate();
			
	  }

	public void addNutrToAdapter(NutrAdapter lv1Adapter, Nutritionist nutr) {
		  lv1Adapter.add(nutr);
		  lv1Adapter.notifyDataSetChanged();
	  }
	
	public void modifyNutr() {
		  lv1Adapter.notifyDataSetChanged();
	  }
	  
	@Override
	public void onResume() {
		super.onResume();
		vf.setDisplayedChild(displayedChild);
		if (displayedChild == 3) { // fitness category chosen
			showSportCategoryPopup(displayedCategory);
		}
			    
		me.enableCompass();
	}   
	  
	 @Override
	 public void onPause() {
	    super.onPause();
	    
	    me.disableCompass();
	 }

	@Override
	  protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putInt(DISPLAYED_CHILD, displayedChild);
		outState.putInt(DISPLAYED_CATEGORY, displayedCategory);
	  }
		
	 @Override
	  protected boolean isRouteDisplayed() {
	    return(true);
	  }
	  
	 private void saveState() {
		  displayedChild = vf.getDisplayedChild();
	 }
	  
	  
	private GeoPoint getPointFromMicrodegrees(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0),
	                          (int)(lon*1000000.0)));
	 }
	
	private GeoPoint getPoint(int lat, int lon) {
	    return(new GeoPoint(lat, lon));
	 }
	    
	
	class LayersOverlay extends ItemizedOverlay<OverlayItem> {
	    List<OverlayItem> items = new ArrayList<OverlayItem>();
	    private Drawable marker = null;
	    int layerCategory = -1;
	    private int temporaryPos = -1;
	    private int size = 0;
	    
	    static final int NONE = 0;
	    static final int DRAG = 1;
	    static final int ZOOM = 2;
	    int _mode = NONE;

	    // Remember some things for zooming
	    PointF start = new PointF();
	    PointF mid = new PointF();
	    PointF end = new PointF();
	    float oldDist = 1f;
	    
	    GeoPoint oldP = null;
	    GeoPoint p = null; // last point touched for new location
	    OverlayItem newItem = null;
	    Drawable newMarker = null;
	    Drawable _marker = null;// only for onTouch event
	    OverlayItem nullItem = null;

	    public LayersOverlay(Drawable marker, int layerCategory) {
	      super(marker);
	      this.marker = marker;
	      
	      this.layerCategory = layerCategory;
	      
	      boundCenterBottom(marker);
	      
	      items.clear();
	      
	      Iterator itr = null;
	      
	      switch(layerCategory) {
	      	case Identifier.NUTRIONIST_BROWSER :
	      		itr = DBUtils.nutritionists.iterator();
	      		while (itr.hasNext()) {
	      			Nutritionist nutr = (Nutritionist) itr.next();
	      			int lat = nutr.getLatitude();
	      			int lon = nutr.getLongitude();
	      			int itemId = nutr.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
                            nutr.getTitle(), nutr.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444440, 26666666), null, null);
	      		addItem(nullItem);
	      		break;
	      	case Identifier.FITNESS_BROWSER :
	      		itr = DBUtils.fitnessCenters.iterator();
	      		while (itr.hasNext()) {
	      			SportCenter fitnessCenter = (SportCenter) itr.next();
	      			int lat = fitnessCenter.getLatitude();
	      			int lon = fitnessCenter.getLongitude();
	      			int itemId = fitnessCenter.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					fitnessCenter.getTitle(), fitnessCenter.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444441, 26666666), null, null);
	      		addItem(nullItem);
	      		break;
	      	case Identifier.KARATE_BROWSER :
	      		itr = DBUtils.karateCenters.iterator();
	      		while (itr.hasNext()) {
	      			SportCenter karateCenter = (SportCenter) itr.next();
	      			int lat = karateCenter.getLatitude();
	      			int lon = karateCenter.getLongitude();
	      			int itemId = karateCenter.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					karateCenter.getTitle(), karateCenter.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444442, 26666666), null, null);
	      		addItem(nullItem);
	      		break;	
	      	case Identifier.JUDO_BROWSER :
	      		itr = DBUtils.judoCenters.iterator();
	      		while (itr.hasNext()) {
	      			SportCenter judoCenter = (SportCenter) itr.next();
	      			int lat = judoCenter.getLatitude();
	      			int lon = judoCenter.getLongitude();
	      			int itemId = judoCenter.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					judoCenter.getTitle(), judoCenter.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444443, 26666666), null, null);
	      		addItem(nullItem);
	      		break;
	      	case Identifier.YOGA_BROWSER :
	      		itr = DBUtils.yogaCenters.iterator();
	      		while (itr.hasNext()) {
	      			SportCenter yogaCenter = (SportCenter) itr.next();
	      			int lat = yogaCenter.getLatitude();
	      			int lon = yogaCenter.getLongitude();
	      			int itemId = yogaCenter.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					yogaCenter.getTitle(), yogaCenter.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444444, 26666666), null, null);
	      		addItem(nullItem);
	      		break;
	      	case Identifier.WRESTLING_BROWSER :
	      		itr = DBUtils.wrestlingCenters.iterator();
	      		while (itr.hasNext()) {
	      			SportCenter wrestlingCenter = (SportCenter) itr.next();
	      			int lat = wrestlingCenter.getLatitude();
	      			int lon = wrestlingCenter.getLongitude();
	      			int itemId = wrestlingCenter.getId();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					wrestlingCenter.getTitle(), wrestlingCenter.getDescription()));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444445, 26666666), null, null);
	      		addItem(nullItem);
	      		break;	
	      	case Identifier.MY_LOCATIONS_BROWSER :
	      		itr = DBUtils.myLocations.iterator();
	      		while (itr.hasNext()) {
	      			MyLocation myLocation = (MyLocation) itr.next();
	      			int lat = myLocation.getLatitude();
	      			int lon = myLocation.getLongitude();
	      			addItem(new OverlayItem(getPoint(lat, lon),
	      					getString(R.string.mylocation_bubble), null));
	      		}
	      		nullItem = new OverlayItem(new GeoPoint(44444446, 26666666), null, null);
	      		addItem(nullItem);
	      		
	      		break;	      		
	      }
	      
	      size = size();

	      callPopulate();
	      
	    }
	    
	    public OverlayItem searchItem(GeoPoint p) {
	    	for (OverlayItem item : items) {
	    		if (item.getPoint().equals(p)) {
	    			return item;
	    		}
	    	}
	    	return null;
	    }
	    
	    
	    @Override
	    protected OverlayItem createItem(int i) {
	    	synchronized (items) {
		    	if (i >= items.size()) {
		    		return null;
		    	}
		    	return(items.get(i));
	    	}
	    }
	    
	    OverlayItem oldItem = null;
	    
	    @Override
	    protected boolean onTap(int i) {
	    	if (items.size() == 0) return true; // no layer displayed 
	    	
	    	if ((dialog_mode == DIALOG_ADD_NUTRITIONIST) ||
	    			(mode == SET_MY_LOCATION) ||
	    			(dialog_mode == DIALOG_ADD_FITNESS) ||
	    			(dialog_mode == DIALOG_ADD_KARATE) ||
	    			(dialog_mode == DIALOG_ADD_JUDO) ||
	    			(dialog_mode == DIALOG_ADD_YOGA) ||
	    			(dialog_mode == DIALOG_ADD_WRESTLING) ) {
	    		return true;
	    	}
	    	
	    	OverlayItem item = items.get(i);
	    	if (item == null) {
	    		return super.onTap(i);
	    	}
	        GeoPoint geo = item.getPoint();
	        if (geo.getLatitudeE6() == 44444444) { // null mylocation item pressed
	        	return true;
	        }
	        
	        View view = panel.getView();
	        
	        //focus on the item in list
	        switch (layerCategory) {
	        	case Identifier.NUTRIONIST_BROWSER:
	        		lv1.setSelectionFromTop(i, 0);
	        		break;
	        	case Identifier.FITNESS_BROWSER:
	        	case Identifier.KARATE_BROWSER:
	        	case Identifier.JUDO_BROWSER:
	        	case Identifier.YOGA_BROWSER:
	        	case Identifier.WRESTLING_BROWSER:
	        		lv2.setSelectionFromTop(i, 0);
	        		break;
	        }
	        
	    	if (mode != REMOVE_MY_LOCATION) {
		        ((TextView)view.findViewById(R.id.bubbleTitle)).setText(item.getTitle());
		        if (geo != null) {
		        	panel.show(geo);
		        }
	    	} else {
	    		panel.setCategory(layerCategory);
	    		if (layerCategory != Identifier.MY_LOCATIONS_BROWSER) {
	    			((TextView)view.findViewById(R.id.bubbleTitle)).setText(item.getTitle());
	    			panel.show(geo);
	    		} else {
		    			// we are on MY_LOCATIONS_BROWSER layer
			    		if (oldItem != null && oldItem.equals(item)) {//same item taped
			    			try{
			    				removeItem(i);
			    				callPopulate();
								
								size = size();
								
								DBUtils.removeMyLocation(MainActivity.usr_id,
										oldItem.getPoint().getLatitudeE6(),
										oldItem.getPoint().getLongitudeE6());
								
								oldItem = null;
								
								panel.hide();
								panel.setCategory(-1);
								mode = SHOW_MY_LOCATION;
								
								Toast.makeText(UserLocations.this,
					                      R.string.mylocation_remove_bubble,
					                      Toast.LENGTH_SHORT).show();
								
			    			} catch (Exception e) {
			    				e.printStackTrace();
			    				Log.d(this.getClass().toString(), "Exception caught");
			    			}
		
			    		} else {//we may not have oldItem yet
			    			//show bubble message
				    		((TextView)view.findViewById(R.id.bubbleTitle))
					          .setText(getString(R.string.mylocation_remove_tap_bubble));
				    		
				    		if (geo != null)
				    			panel.show(geo);
				    		oldItem = item;
				    		
			    		}
	    		}
	    		
	    	}
	    	
	    return true;
	    }
	    
		@Override
	    public int size() {
			synchronized (items) {
				return(items.size());
			}
	    }
		
	    public void clear() {
	    	synchronized (items) {
	    		items.clear();
			}
	    	callPopulate();
	    }
	    
	    public void callPopulate() {
	    	setLastFocusedIndex(-1);
	        populate();
	    }
	    
	    public void callBoundCenterBottom(Drawable marker) {
	    	boundCenterBottom(marker);
	    }
	    
	    public void abortSetMyLocation() {
			if (newItem != null) {
				removeItem(temporaryPos);
				callPopulate();
				map.invalidate();
			}
			
			temporaryPos = -1;
			newItem = null;
			oldP = null;
	    }
	    
	    public void abortRemoveMyLocation() {
	    	oldItem = null;
	    }

	    private OverlayItem finalItem = null;
	    
	    public void setFinalItem(OverlayItem finalItem) {
	    	this.finalItem = finalItem;
	    }
	    
	    public OverlayItem getFinalItem() {
	    	return finalItem;
	    }
	    
	    private void addItem(OverlayItem overlayItem) {
	    	synchronized (items) {
	    		items.add(overlayItem);
	    	}
	    }
	    
	    private void addItem(int position, OverlayItem overlayItem) {
	    	synchronized (items) {
	    		items.set(position, overlayItem);
	    		items.add(nullItem);
	    	}
	    }
	    
	    private void removeItem(OverlayItem overlayItem) throws IndexOutOfBoundsException {
	    		try {
	    			synchronized (items) {
	    				items.remove(overlayItem);
	    			}
	    		} catch(IndexOutOfBoundsException e1){
	    			Log.d(this.getClass().toString(), "IndexOutOfBoundsException item");
	    			throw e1;
	    		}
	    }
	    
	    private void removeItem(int position) throws IndexOutOfBoundsException {
	    	try {
    			synchronized (items) {
    				items.remove(position);
    			}
    		} catch(IndexOutOfBoundsException e1){
    			Log.d(this.getClass().toString(), "IndexOutOfBoundsException position");
    			throw e1;
    		}
	    }
	    
	    
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			setFinalItem(null);
			selectedLatitude = 0;
			selectedLongitude = 0;
			
			if ((dialog_mode == DIALOG_ADD_NUTRITIONIST) ||
	    			(dialog_mode == DIALOG_ADD_FITNESS) ||
	    			(dialog_mode == DIALOG_ADD_KARATE) ||
	    			(dialog_mode == DIALOG_ADD_JUDO) ||
	    			(dialog_mode == DIALOG_ADD_YOGA) ||
	    			(dialog_mode == DIALOG_ADD_WRESTLING)) {
				if (dialog_mode == DIALOG_ADD_NUTRITIONIST) {
					_marker = getResources().getDrawable(R.drawable.nutr3);
				} else {
					_marker = getResources().getDrawable(fitnessIcons[dialog_mode - DIALOG_ADD_FITNESS]); // DIALOG_ADD_FITNESS is start index for sport categories
				}
				
				if (temporaryPos < 0) {
					if (size == 0) temporaryPos = 0;
					else temporaryPos = size -1 ; // the last is the null item
				}
				
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
			   		case MotionEvent.ACTION_DOWN: //first finger down only
				      start.set(event.getX(), event.getY());
				      _mode = DRAG;
				      break;
				   case MotionEvent.ACTION_POINTER_UP: //first finger lifted
				      _mode = NONE;
				      break;
				   case MotionEvent.ACTION_POINTER_DOWN: //second finger down
				      oldDist = spacing(event);
				      if (oldDist > 5f) {
				         midPoint(mid, event);
				         _mode = ZOOM;
				      }
				      break;
				   case MotionEvent.ACTION_UP: //second finger lifted
					   end.set(event.getX(), event.getY());
					   if ((_mode == DRAG) && (dist(end, start) == 0)) {
							p = mapView.getProjection().fromPixels(
						                    (int) event.getX(),
						                    (int) event.getY());
							if (newItem != null && newMarker != null && oldP != null) { // already chosen a location but not yet taped
								removeItem(temporaryPos);
								callPopulate();//for remove
								
								Point p2 = new Point(0,0);
								mapView.getProjection().toPixels(oldP, p2);
								Rect bounds = newItem.getMarker(0).getBounds();
								bounds.offset(p2.x, p2.y);
								if (bounds.contains((int) event.getX(), (int) event.getY())) {
									OverlayItem finalItem = new OverlayItem(oldP, getString(R.string.new_nutritionist_bubble), null);
									boundCenterBottom(_marker);
									finalItem.setMarker(_marker);
									addItem(temporaryPos, finalItem);
						    		callPopulate();
						    		map.invalidate();
						    		
					            	Toast.makeText(UserLocations.this,
					                      R.string.new_nutritionist_set_bubble,
					                      Toast.LENGTH_SHORT).show();
					            	panel.hide();
					            	panel.setCategory(-1);
					            	
					            	setFinalItem(finalItem); // in case user press Cancel in Dialog

					            	selectedLatitude = oldP.getLatitudeE6();
					            	selectedLongitude = oldP.getLongitudeE6();
					            	
					            	dialog.show();
					            	
						    		return false;
								}
							}
							oldP = p;
							
							if((temporaryPos == size - 1) && (items.size() > size)) { //already a location set on temporaryPos position
									removeItem(temporaryPos);
									callPopulate();//for remove
							}
							
							newItem = new OverlayItem(p, getString(R.string.new_nutritionist_bubble), null);
							newMarker = getResources().getDrawable(R.drawable.pinpoint);
							boundCenterBottom(newMarker);
							newItem.setMarker(newMarker);
							addItem(temporaryPos, newItem);
							callPopulate();
							
							// show bubble message
							View bubbleView = panel.getView();
							((TextView)bubbleView.findViewById(R.id.bubbleTitle))
								          .setText(getString(R.string.new_nutritionist_tap_bubble));
							panel.show(oldP);
							panel.setCategory(displayedCategory);
							
							
							return false;
					    } else {
					    	mapView.displayZoomControls(true);
						}
					}//end switch event.getAction		
			} //end if DIALOG_ADD_NUTRITIONIST
			
			else if (mode == SET_MY_LOCATION) {
				 System.out.println("onTouchEvent SET_MY_LOCATION");
					if (temporaryPos < 0) {
						if (size == 0) temporaryPos = 0;
						else temporaryPos = size -1 ; // the last is the null item
						}
					switch (event.getAction() & MotionEvent.ACTION_MASK) {
				   		case MotionEvent.ACTION_DOWN: //first finger down only
					      start.set(event.getX(), event.getY());
					      _mode = DRAG;
					      break;
					   case MotionEvent.ACTION_POINTER_UP: //first finger lifted
					      _mode = NONE;
					      break;
					   case MotionEvent.ACTION_POINTER_DOWN: //second finger down
					      oldDist = spacing(event);
					      if (oldDist > 5f) {
					         midPoint(mid, event);
					         _mode = ZOOM;
					      }
					      break;
					   case MotionEvent.ACTION_UP: //second finger lifted
						   end.set(event.getX(), event.getY());
						   if ((_mode == DRAG) && (dist(end, start) == 0)) {
								p = mapView.getProjection().fromPixels(
							                    (int) event.getX(),
							                    (int) event.getY());
								if (newItem != null && newMarker != null && oldP != null) { // already chosen a location but not yet taped
									Point p2 = new Point(0,0);
									mapView.getProjection().toPixels(oldP, p2);
									Rect bounds = newItem.getMarker(0).getBounds();
									bounds.offset(p2.x, p2.y);
									if (bounds.contains((int) event.getX(), (int) event.getY())) {
							    		newItem.setMarker(marker);
							    		callPopulate();
							    		map.invalidate();
							    		
						            	Toast.makeText(UserLocations.this,
						                      R.string.mylocation_set_bubble,
						                      Toast.LENGTH_SHORT).show();
						            	panel.hide();
						            	panel.setCategory(-1);
						            	
						            	mode = SHOW_MY_LOCATION;
						            	
						            	//insert into DB
						            	try {
											DBUtils.insertMyLocation(MainActivity.usr_id, oldP.getLatitudeE6(), oldP.getLongitudeE6());
										} catch (InterruptedException e) {
											Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
										}
						            	
						            	resetMode();
						            	
							    		return false;
									}
								}
								
								oldP = p;
								if((temporaryPos == size - 1) && (items.size() > size)) { //already a location set on temporaryPos position
										removeItem(temporaryPos);
										callPopulate();
								}
								
								newItem = new OverlayItem(p, getString(R.string.mylocation_bubble), null);
								newMarker = getResources().getDrawable(R.drawable.user2_point);
								boundCenterBottom(newMarker);
								newItem.setMarker(newMarker);
								addItem(temporaryPos, newItem);
								callPopulate();
								
								// show bubble message
								View bubbleView = panel.getView();
								((TextView)bubbleView.findViewById(R.id.bubbleTitle))
									          .setText(getString(R.string.mylocation_tap_bubble));
								panel.show(oldP);
								panel.setCategory(Identifier.MY_LOCATIONS_BROWSER);
								
								return false;
						    } else {
						    	mapView.displayZoomControls(true);
							}
						}//end switch event.getAction		
					} //end if SET_MY_LOCATION
			return false;//very important - we must have onTap as well for the bubble message		
					
		}
		
		public void putTitleOnFinalItem(String title) {
			OverlayItem finalItem = getFinalItem();
			OverlayItem finalItemWithTitle = new OverlayItem(finalItem.getPoint(), title, null);
			boundCenterBottom(_marker);
			finalItemWithTitle.setMarker(_marker);
			removeItem(temporaryPos);
			addItem(temporaryPos, finalItemWithTitle);
    		callPopulate();
    		map.invalidate();
		}

		private float spacing(MotionEvent event) {
			   float x = event.getX(0) - event.getX(1);
			   float y = event.getY(0) - event.getY(1);
			   return FloatMath.sqrt(x * x + y * y);
			}
		
		private float dist(PointF point1, PointF point2) {
			   float x = point2.x - point1.x;
			   float y = point2.y - point1.y;
			   return FloatMath.sqrt(x * x + y * y);
			}

		private void midPoint(PointF point, MotionEvent event) {
			   float x = event.getX(0) + event.getX(1);
			   float y = event.getY(0) + event.getY(1);
			   point.set(x / 2, y / 2);
		}

			
		public void resetMode() {
			if (mode > Identifier.WRESTLING_BROWSER) {
				mode = SHOW_MY_LOCATION;
			}
			size = size();
			temporaryPos = -1;
			newItem = null;
			newMarker = null;
			oldP = null;
			
			selectedLatitude = 0;
			selectedLongitude = 0;
		}
		
	  }
	
	
	public Dialog addReviewNutrDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.rate_review_dialog, null);
        final AlertDialog dialog =  new AlertDialog.Builder(this)
            .setIcon(R.drawable.comment_24) //android.R.attr.alertDialogIcon
            .setTitle("Rate and review")
            .setView(view)
            .create();
        
        Button ok = (Button) view.findViewById(R.id.ok);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final TextView error = (TextView) view.findViewById(R.id.error);
        
        ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	RatingBar newRatingBar = (RatingBar)(view.findViewById(R.id.newRatingBar));
            	TextView newReview = (TextView) view.findViewById(R.id.newReview);
				if (newReview.getText().toString().trim().length() > 0) {
                	try {
                		// also in arraylist, and notification sent
                		Review _review = DBUtils.insertReview(
                				selectedNutr, MainActivity.usr_id, MainActivity.username, selectedNutr.getId(), (short)Identifier.NUTRIONIST_BROWSER, newRatingBar.getRating(), newReview.getText().toString());
						
						lv1ReviewsAdapter.notifyDataSetChanged();
						lv1Adapter.notifyDataSetChanged();// rating and noOfReviews will change in lv1
						
						nutrRatingBar.setRating(selectedNutr.getRating());
						noReviews.setText(String.valueOf(selectedNutr.getNoOfReviews()));
						
						Toast.makeText(UserLocations.this,
			                      getResources().getString(R.string.review_added),
			                      Toast.LENGTH_SHORT).show();
						
						newRatingBar.setRating(0);
						newReview.setText("");
						
						error.setText("");
						
					} catch (InterruptedException e) {
						Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
					}
                	
                	dialog.dismiss();
				
				} else {
					error.setText(getString(R.string.add_review_error));
				}
			}
		});
        
        
        cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
        	
        });
        
        return dialog;
	}
	
	
	public Dialog addReviewFitnDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.rate_review_dialog_fitness, null);
        final AlertDialog dialog =  new AlertDialog.Builder(this)
            .setIcon(R.drawable.comment_24)
            .setTitle("Rate and review")
            .setView(view)
            .create();
        
        final RatingBar newRatingBar = (RatingBar)view.findViewById(R.id.RatingBar_Overall);
	  	//rating criteria
        final RatingBar ratingBarVestiary = (RatingBar)view.findViewById(R.id.RatingBar_Vestiary);
        final RatingBar ratingBarSpace = (RatingBar)view.findViewById(R.id.RatingBar_Space);
        final RatingBar ratingBarAmbiance = (RatingBar)view.findViewById(R.id.RatingBar_Ambiance);
        final RatingBar ratingBarEquipment = (RatingBar)view.findViewById(R.id.RatingBar_Equipment);
        final RatingBar ratingBarHygiene = (RatingBar)view.findViewById(R.id.RatingBar_Hygiene);
	  	
	  	newRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if (fromUser) {
					ratingBarVestiary.setRating(rating);
					ratingBarSpace.setRating(rating);
					ratingBarAmbiance.setRating(rating);
					ratingBarEquipment.setRating(rating);
					ratingBarHygiene.setRating(rating);
				}
			}
		});
	  	
	  	RatingBar.OnRatingBarChangeListener lsnr = new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if (fromUser) {
					newRatingBar.setRating( (ratingBarVestiary.getRating() + ratingBarSpace.getRating()
							+ ratingBarAmbiance.getRating() + ratingBarEquipment.getRating() + ratingBarHygiene.getRating()) / 5 );
				}
			}
		};
	  	
		ratingBarVestiary.setOnRatingBarChangeListener(lsnr);
	  	ratingBarSpace.setOnRatingBarChangeListener(lsnr);
	  	ratingBarAmbiance.setOnRatingBarChangeListener(lsnr);
	  	ratingBarEquipment.setOnRatingBarChangeListener(lsnr);
	  	ratingBarHygiene.setOnRatingBarChangeListener(lsnr);
	  	
        Button ok = (Button) view.findViewById(R.id.ok);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final TextView error = (TextView) view.findViewById(R.id.error);
        
        ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	RatingBar newRatingBar = (RatingBar)(view.findViewById(R.id.RatingBar_Overall));
            	TextView newReview = (TextView) view.findViewById(R.id.newReview);
				if (newReview.getText().toString().trim().length() > 0) {
                	try {
                		// also in arraylist, and notification sent
                		Review _review = DBUtils.insertReview(
                				selectedFitn, MainActivity.usr_id, MainActivity.username, selectedFitn.getId(), (short) (displayedCategory + 1), 
                				newRatingBar.getRating(), newReview.getText().toString());
						
						lv2ReviewsAdapter.notifyDataSetChanged();
						lv2Adapter.notifyDataSetChanged();// rating and noOfReviews will change in lv1
						
						fitnRatingBar.setRating(selectedFitn.getRating());
						fitnNoReviews.setText(String.valueOf(selectedFitn.getNoOfReviews()));
						
						Toast.makeText(UserLocations.this,
			                      getResources().getString(R.string.review_added),
			                      Toast.LENGTH_SHORT).show();
						
						newRatingBar.setRating(0);
						newReview.setText("");
						
						error.setText("");
						
					} catch (InterruptedException e) {
						Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
					}
                	
                	dialog.dismiss();
				
				} else {
					error.setText(getString(R.string.add_review_error));
				}
			}
		});
        
        
        cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        });
        
        return dialog;
	}
	
	private AlertDialog dialog = null;
	
	public Dialog addCenterDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.dialog_add_center, null);
        dialog =  new AlertDialog.Builder(this)
            .setTitle("Add " + Identifier.getLabel(mode) + (mode > 0? " club" : "") )
            .setView(view)
            .create();
        
        Button ok = (Button) view.findViewById(R.id.ok);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        
        final TextView title = (TextView) view.findViewById(R.id.title);
        final TextView description = (TextView) view.findViewById(R.id.description);
        final TextView address = (TextView) view.findViewById(R.id.address);
        final TextView phone = (TextView) view.findViewById(R.id.phone);
        final TextView open = (TextView) view.findViewById(R.id.open);
        final TextView web = (TextView) view.findViewById(R.id.web);
        final ImageButton pointOnMap = (ImageButton) view.findViewById(R.id.pointOnMap);
        pointOnMap.setFocusableInTouchMode(true);
        
        final TextView error = (TextView) view.findViewById(R.id.error);
        
        pointOnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				//hide all layers except for nutritionist
				for (int i = 1 ; i <= fitnessLabels.length; i++) { // including mylocations
					popup.getMenu().getItem(i).setChecked(false);
					hideLayer(Identifier.getLabel(i));
				}
				showLayer(Identifier.getLabel(mode));
				popup.getMenu().getItem(6).setTitle("Unselect All");
				panel.hide();
			}
        });
        
        ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((title.getText().toString().trim().length() > 0) &&
						(selectedLatitude > 0) &&
						(selectedLongitude > 0)) {
                	try {
                		System.out.println("ok onClick mode=" + mode);
                		switch (mode) {
							case Identifier.NUTRIONIST_BROWSER:
								// also in arraylist, and notification sent
		                		Nutritionist nutr = DBUtils.insertNutritionist(
		                				MainActivity.usr_id,
		                				title.getText().toString(),
		                				description.getText().toString(),
		                				address.getText().toString(),
		                				phone.getText().toString(),
		                				open.getText().toString(),
		                				web.getText().toString(),
		                				selectedLatitude,
		                				selectedLongitude);
		                		
		                		//put label on the newly created item
		                		layers[0].putTitleOnFinalItem(nutr.getTitle());							
		                		
		                		lv1Adapter.add(nutr);
			                	lv1Adapter.notifyDataSetChanged();
			                	if (filterText != null) {
			                		lv1Adapter.getFilter().filter(filterText);
			                	}
			                	lv1.setSelectionFromTop(lv1.getAdapter().getCount() - 1, 0);
								
								Toast.makeText(UserLocations.this,
					                      getResources().getString(R.string.nutr_added),
					                      Toast.LENGTH_SHORT).show();
			                	
								break;
	
								
							case Identifier.FITNESS_BROWSER:
							case Identifier.KARATE_BROWSER:
							case Identifier.JUDO_BROWSER:
							case Identifier.YOGA_BROWSER:
							case Identifier.WRESTLING_BROWSER:
								// also in arraylist, and notification sent
		                		SportCenter fitn = DBUtils.insertSportCenter(
		                				MainActivity.usr_id,
		                				title.getText().toString(),
		                				description.getText().toString(),
		                				address.getText().toString(),
		                				phone.getText().toString(),
		                				open.getText().toString(),
		                				web.getText().toString(),
		                				selectedLatitude,
		                				selectedLongitude,
		                				(short) mode);
		                		
		                		//put label on the newly created item
		                		layers[mode].putTitleOnFinalItem(fitn.getTitle());
								
		                		lv2Adapter.add(fitn);
			                	lv2Adapter.notifyDataSetChanged();
			                	if (filterText != null) {
			                		lv2Adapter.getFilter().filter(filterText);
			                	}
			                	lv2.setSelectionFromTop(lv2.getAdapter().getCount() - 1, 0);
			                	
								Toast.makeText(UserLocations.this,
					                      Identifier.getLabel(mode) + " club has been added",
					                      Toast.LENGTH_SHORT).show();
								break;
						}
						
						title.setText("");
						description.setText("");
						address.setText("");
						phone.setText("");
						open.setText("");
						web.setText("");
						title.requestFocus();
						
						error.setText("");
						
					} catch (InterruptedException e) {
						Log.d(this.getClass().toString(), "InterruptedException caught " + e.getMessage());
					}
                	
                	dialog.dismiss();
                	
                	dialog_mode = DIALOG_ADD_DEFAULT;
                	
                	layers[mode].resetMode();
                	
				} else {
					if (title.getText().toString().trim().length() == 0) {
						error.setText(getString(R.string.add_nutr_error1));
					} else {
						if ((selectedLatitude == 0) || (selectedLongitude == 0)) {
							error.setText(getString(R.string.add_nutr_error2));
						}
						
					}
				}
			}
		});
        
        
        cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (layers[mode].getFinalItem() != null) {
					layers[mode].removeItem(layers[mode].getFinalItem());
					layers[mode].callPopulate();
					map.invalidate();
				}
				dialog_mode = DIALOG_ADD_DEFAULT;
				
				error.setText("");
				
				layers[mode].resetMode();
			}
        });
        
        return dialog;
	}	
	
	@Override
	public Dialog onCreateDialog(int id) {
		 switch (id) {
		 	case DIALOG_ADD_REVIEW_NUTRITIONIST : return addReviewNutrDialog();
		 	case DIALOG_ADD_NUTRITIONIST : return addCenterDialog();
		 	case DIALOG_ADD_REVIEW_FITNESS : return addReviewFitnDialog();
		 	
		 	case DIALOG_ADD_FITNESS : return addCenterDialog();
		 	case DIALOG_ADD_KARATE : return addCenterDialog();
		 	case DIALOG_ADD_JUDO : return addCenterDialog();
		 	case DIALOG_ADD_YOGA : return addCenterDialog();
		 	case DIALOG_ADD_WRESTLING : return addCenterDialog();
		 }
		 return null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
		MenuItem item = menu.add(Menu.NONE, Menu.NONE,
				Menu.NONE,
                R.string.your_locations);
		
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	            public boolean onMenuItemClick(MenuItem item) {
	            	mode = SHOW_MY_LOCATION;
	            	vf.setDisplayedChild(4);
	                return true;
	            }
        });
        
    	TextView noText = (TextView) findViewById(R.id.noText);
    	popup = new CustomPopupMenu (this, noText);
    	popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
    	popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.all) {
					if (item.getTitle().toString().equalsIgnoreCase("Select All")) {
						for (int i = 0 ; i <= fitnessLabels.length; i++) { //on count nutritionists as well
							popup.getMenu().getItem(i).setChecked(true);
							showLayer(Identifier.getLabel(i));
						}
						item.setTitle("Unselect All");
						panel.hide();
					} else {
						for (int i = 0 ; i <= fitnessLabels.length; i++) {
							popup.getMenu().getItem(i).setChecked(false);
							hideLayer(Identifier.getLabel(i));
						}
						item.setTitle("Select All");
					}
				} else {
					if (item.isChecked()) {
						item.setChecked(false);
						hideLayer(item.getTitle().toString());
					} else {
						item.setChecked(true);
						showLayer(item.getTitle().toString());
					}
					popup.getMenu().getItem(fitnessLabels.length +1).setTitle("Select All");
					
				}
				return true;
			}
		});
        
        
		return true;
	}
	
	void hideLayer(String layerTitle) {
		if (layerTitle.equalsIgnoreCase(getString(R.string.nutr_layer))) {
			map.getOverlays().remove(layers[0]);
			map.invalidate();
			if (panel.getCategory() == 0) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.fitness_layer))) {
			map.getOverlays().remove(layers[1]);
			map.invalidate();
			if (panel.getCategory() == 1) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.karate_layer))) {
			map.getOverlays().remove(layers[2]);
			map.invalidate();
			if (panel.getCategory() == 2) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.judo_layer))) {
			map.getOverlays().remove(layers[3]);
			map.invalidate();
			if (panel.getCategory() == 3) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.yoga_layer))) {
			map.getOverlays().remove(layers[4]);
			map.invalidate();
			if (panel.getCategory() == 4) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.wrestling_layer))) {
//			layers[5].clear();
			map.getOverlays().remove(layers[5]);
			map.invalidate();
			if (panel.getCategory() == 5) {
				panel.hide();
			}
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.mylocations_layer))) {
			map.getOverlays().remove(layers[6]);
			map.invalidate();
			return;
		}
			
	}
	
	void showLayer(String layerTitle) {
		if (layerTitle.equalsIgnoreCase(getString(R.string.nutr_layer))) {
			map.getOverlays().remove(layers[0]);
			map.getOverlays().add(layers[0]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(0, map.getZoomLevel());
			layers[0].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[0].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(0).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.fitness_layer))) {
			map.getOverlays().remove(layers[1]);
			map.getOverlays().add(layers[1]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(1, map.getZoomLevel());
			layers[1].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[1].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(1).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.karate_layer))) {
			map.getOverlays().remove(layers[2]);
			map.getOverlays().add(layers[2]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(2, map.getZoomLevel());
			layers[2].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[2].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(2).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.judo_layer))) {
			map.getOverlays().remove(layers[3]);
			map.getOverlays().add(layers[3]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(3, map.getZoomLevel());
			layers[3].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[3].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(3).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.yoga_layer))) {
			map.getOverlays().remove(layers[4]);
			map.getOverlays().add(layers[4]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(4, map.getZoomLevel());
			layers[4].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[4].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(4).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.wrestling_layer))) {
			map.getOverlays().remove(layers[5]);
			map.getOverlays().add(layers[5]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(5, map.getZoomLevel());
			layers[5].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[5].items) {
				item.setMarker(_marker);
			}
			
			map.invalidate();
			popup.getMenu().getItem(5).setChecked(true);
			return;
		}
		if (layerTitle.equalsIgnoreCase(getString(R.string.mylocations_layer))) {
			map.getOverlays().remove(layers[6]);
			map.getOverlays().add(layers[6]);
			//scale the items according to zoom level
			Drawable _marker = map.scaleIcon(6, map.getZoomLevel());
			layers[6].callBoundCenterBottom(_marker);
			for (OverlayItem item : layers[6].items) {
				if (item.getPoint().getLatitudeE6() == 44444444) {
					item.setMarker(null);
				} else {
					item.setMarker(_marker);
				}
			}
			
			map.invalidate();
			return;
		}
			
	}
	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.nutritionists:
        	showNutritionPopup();
        	mode = Identifier.NUTRIONIST_BROWSER;
        	showLayer(getString(R.string.nutr_layer));
            return true;

        case R.id.fitness:
        	showSportsPopup();
            return true;

        case R.id.layers:
        	popup.show();
            return true;
            
        case R.id.traffic:
        	if (item.isChecked()) {
        		map.setTraffic(false);
        		item.setChecked(false);
        	} else {
        		map.setTraffic(true);
        		item.setChecked(true);
        	}
            return true;
            
        case R.id.satellite:
        	if (item.isChecked()) {
        		map.setSatellite(false);
        		item.setChecked(false);
        	} else {
        		map.setSatellite(true);
        		item.setChecked(true);
        	}
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void onNewIntent(Intent intent) {
        if (ACTION_DIALOG.equals(intent.getAction())) {
            displayDialog(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
	}
	
	private void dismissMyLocations() {
		hideLayer(getString(R.string.mylocations_layer));
		if (myLocationsItem != null) {
			myLocationsItem.setText(getString(R.string.show_locations));
		}
	}
	
	private void hidePopup() {
		vf.setDisplayedChild(0);
	}

	private void showNutritionPopup() {
		vf.setDisplayedChild(1);
		dismissMyLocations();
	}	
	
	private void showSportsPopup() {
		vf.setDisplayedChild(2);
		dismissMyLocations();
	}
	
	
	private void showSportCategoryPopup(int position) {
		dismissMyLocations();
		
		sportCategTitle.setText(fitnessLabels[position]);
		
	    // check if sport centers loaded from db
		switch (position) {
			case 0: //fitness
				lv2Adapter = new SportCategoriesAdapter(position, fitnessCenters);
			    lv2.setAdapter(lv2Adapter);
			    break;
			case 1: //karate
				lv2Adapter = new SportCategoriesAdapter(position, karateCenters);
			    lv2.setAdapter(lv2Adapter);
			    break;
			case 2: //judo
				lv2Adapter = new SportCategoriesAdapter(position, judoCenters);
			    lv2.setAdapter(lv2Adapter);
			    break;
			case 3: //yoga
				lv2Adapter = new SportCategoriesAdapter(position, yogaCenters);
			    lv2.setAdapter(lv2Adapter);
			    break;
			case 4: //wrestling
				lv2Adapter = new SportCategoriesAdapter(position, wrestlingCenters);
			    lv2.setAdapter(lv2Adapter);
			    break;		    
		}
		
		vf.setDisplayedChild(3);
		
		//hide the search view
		vfFitn.setDisplayedChild(0);
		
		//reset the search view 
		searchViewFitnTextView.setText(null);
		searchViewFitn.setQueryHint("Search " + fitnessLabels[position] + " club");
		lv2.clearTextFilter();	
				
		mode = position + 1;
		displayedCategory = position;
	}
	
	private class NutrAdapter extends ArrayAdapter<Nutritionist> {
		
		NutrAdapter() {
    		super(UserLocations.this, R.layout.row_nutr, nutritionists);
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row_nutr, parent, false);
    		
	    	TextView itemText = (TextView)row.findViewById(R.id.item);
	    	itemText.setText(getItem(position).getTitle());
	    		
	    	RatingBar ratingBar = (RatingBar) row.findViewById(R.id.ratingBar1);
	    	ratingBar.setRating(getItem(position).getRating());
	    		
	    	TextView no_reviews1 = (TextView) row.findViewById(R.id.no_reviews1);
	    	no_reviews1.setText(Integer.toString(getItem(position).getNoOfReviews()));
	    		
	    	TextView itemTextDescription = (TextView)row.findViewById(R.id.itemDescription);
	    	if (itemTextDescription != null)
	    		itemTextDescription.setText("\"" + getItem(position).getDescription() + "\"");
    		
    		return(row);
    	}

    }
	
	
	private class NutrReviewsAdapter extends ArrayAdapter<Review> {
		private int category;
		ArrayList<Review> reviews = null;
		
		NutrReviewsAdapter(int position, ArrayList<Review> reviews) {
    		super(UserLocations.this, R.layout.row_nutr_review, reviews);
    		this.category = position;
    		this.reviews = reviews;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row_nutr_review, parent, false);
    		
    		TextView username = (TextView)row.findViewById(R.id.username);
    		username.setText(reviews.get(position).getUsername());
    		
    		RatingBar ratingBar = (RatingBar)row.findViewById(R.id.ratingBar);
    		ratingBar.setRating(reviews.get(position).getRating());
    		
    		TextView review = (TextView)row.findViewById(R.id.review);
    		if (reviews.get(position).getReview() != null)
    			review.setText(reviews.get(position).getReview());
    		
    		return(row);
    	}
    } 
	
	private class MyLocationsAdapter extends ArrayAdapter<String> {
		String[] labels = null;
		
		MyLocationsAdapter() {
    		super(UserLocations.this, R.layout.row_mylocations, UserLocations.this.getResources().getStringArray(R.array.my_locations_items));
    		labels = UserLocations.this.getResources().getStringArray(R.array.my_locations_items);
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row_mylocations, parent, false);
    		
    		TextView itemText = (TextView)row.findViewById(R.id.myLocationsItem);
    		itemText.setText(labels[position]);
    		
    		return(row);
    	}
    }
	
    private class AppsAdapter extends BaseAdapter {
    	
        public View getView(int position, View convertView, ViewGroup parent) {
        	LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row_gridcell, parent, false);
    		
    		ImageView sportIcon = (ImageView) row.findViewById(R.id.sport_icon);
    		sportIcon.setImageDrawable(getResources().getDrawable(fitnessBigIcons[position]));
    		
    		TextView sportLabel = (TextView) row.findViewById(R.id.sport_label);
    		sportLabel.setText(fitnessLabels[position]);
    		
            return row;
        }


        public final int getCount() {
            return Math.min(32, fitnessBigIcons.length);
        }

        public final Object getItem(int position) {
            return getResources().getDrawable(fitnessBigIcons[position]);
        }

        public final long getItemId(int position) {
            return position;
        }
    }
    
	private class SportCategoriesAdapter extends ArrayAdapter<SportCenter> {
		private int category;
		
		SportCategoriesAdapter(int position, ArrayList<SportCenter> centers) {
    		super(UserLocations.this, R.layout.row_sportcategory, centers);
    		this.category = position;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row_sportcategory, parent, false);
    		
    		ImageView categIcon = (ImageView) row.findViewById(R.id.sportcateg_icon);
    		categIcon.setImageDrawable(getResources().getDrawable(fitnessIcons[category]));
    		
    		TextView itemText = (TextView)row.findViewById(R.id.item);
    		itemText.setText(getItem(position).getTitle());
    		
    		RatingBar ratingBar = (RatingBar) row.findViewById(R.id.ratingBar1);
    		ratingBar.setRating(getItem(position).getRating());
    		
    		TextView no_reviews1 = (TextView) row.findViewById(R.id.no_reviews1);
    		no_reviews1.setText(Integer.toString(getItem(position).getNoOfReviews()));
    		
    		TextView itemTextDescription = (TextView)row.findViewById(R.id.itemDescription);
    		if (getItem(position).getTitle() != null && getItem(position).getTitle().trim().length() > 0)
    			itemTextDescription.setText("\"" + getItem(position).getDescription() + "\"");
    		
    		return(row);
    	}
    } 
	
	
	private class CustomPopupMenu extends PopupMenu {
		
		public CustomPopupMenu(Context context, View anchor) {
			super(context, anchor);
		}

		@Override
		public void show() {
			super.show();
		}

		@Override
		public void dismiss() {
			show();
		}
		
	}
	
	
    void displayDialog(String text) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = MyDialogFragment.newInstance(text);

        // Show the dialog.
        newFragment.show(ft, "dialog");
    }
	
    private TextView getTextView(SearchView searchView) {
	    LinearLayout ll = (LinearLayout) ((LinearLayout) ((LinearLayout) searchView.getChildAt(0)).getChildAt(2)).getChildAt(0);
	    return (TextView) ll.getChildAt(1);
    }
    
    class PopupPanel {
        View popup;
        boolean isVisible = false;
        int category = -1;
        
        PopupPanel(int layout) {

        popup = getLayoutInflater().inflate(layout, map, false);
                      
        popup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              hide();
            }
          });
        }
        
        View getView() {
          return(popup);
        }
        
        public int getCategory() {
			return category;
		}

		public void setCategory(int category) {
			this.category = category;
		}

		void show(GeoPoint geo) {
		      MapView.LayoutParams lp = new MapView.LayoutParams(
		                MapView.LayoutParams.WRAP_CONTENT,
		                MapView.LayoutParams.WRAP_CONTENT,
		                geo,
		                MapView.LayoutParams.RIGHT|MapView.LayoutParams.BOTTOM);
	          
	          hide();
	          
	          map.addView(popup, lp);
	          isVisible = true;
        }
        
        void hide() {
	          if (isVisible) {
	            isVisible = false;
	            ((ViewGroup)popup.getParent()).removeView(popup);
	          }
        }
      }

}
