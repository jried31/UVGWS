package parse.almonds;

public class Parse 
{
	private static String mApplicationId="WbDp7JEI27askcOboEqer63TlIPGKLmNZQM92ivU";
	private static String mRestAPIKey="c2x2ZDhw8XslE0hZVvu5eTlE5Fkp0IYKoY5jSJIW";;
	
	private static final String PARSE_API_URL = "https://api.parse.com";
	private static final String PARSE_API_URL_CLASSES = "/1/classes/";
	
	/**
	 * @param applicationId
	 * @param restAPIKey
	 */
	static public void initialize()
	{
	}
	
	static public String getApplicationId() {return mApplicationId;}
	static public String getRestAPIKey() {return mRestAPIKey;}
	static public String getParseAPIUrl() {return PARSE_API_URL;}
	static public String getParseAPIUrlClasses() {return getParseAPIUrl() + PARSE_API_URL_CLASSES;}
}
