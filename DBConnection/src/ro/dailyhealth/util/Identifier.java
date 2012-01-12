package ro.dailyhealth.util;

public class Identifier {

	public final static String NUTRITIONIST = "Nutritionists";
	public final static String FITNESS = "Fitness";
	public final static String KARATE = "Karate";
	public final static String JUDO = "Judo";
	public final static String YOGA = "Yoga";
	public final static String WRESTLING = "Wrestling";
	
	public final static String MY_LOCATIONS = "My locations";
	
	public final static int NUTRIONIST_BROWSER = 0;
	public final static int FITNESS_BROWSER = 1;//
	public final static int KARATE_BROWSER = 2;//
	public final static int JUDO_BROWSER = 3;//
	public final static int YOGA_BROWSER = 4;//
	public final static int WRESTLING_BROWSER = 5;//
	public final static int MY_LOCATIONS_BROWSER = 6;//	
	
	
	public static String getLabel(int identifier) {
		switch (identifier) {
		case NUTRIONIST_BROWSER: return NUTRITIONIST;
		case FITNESS_BROWSER: return FITNESS;
		case KARATE_BROWSER: return KARATE;
		case JUDO_BROWSER: return JUDO;
		case YOGA_BROWSER: return YOGA;
		case WRESTLING_BROWSER: return WRESTLING;
		
		case MY_LOCATIONS_BROWSER: return MY_LOCATIONS;
		}
		
		return null;
	}
	
	public static int getInt(String label) {
		if (label.equalsIgnoreCase(NUTRITIONIST)) return NUTRIONIST_BROWSER;
		if (label.equalsIgnoreCase(FITNESS)) return FITNESS_BROWSER;
		if (label.equalsIgnoreCase(KARATE)) return KARATE_BROWSER;
		if (label.equalsIgnoreCase(JUDO)) return JUDO_BROWSER;
		if (label.equalsIgnoreCase(YOGA)) return YOGA_BROWSER;
		if (label.equalsIgnoreCase(WRESTLING)) return WRESTLING_BROWSER;
		
		if (label.equalsIgnoreCase(MY_LOCATIONS)) return MY_LOCATIONS_BROWSER;
		
		return -1;
	}
	
}
