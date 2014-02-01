package navigation.server;

import java.net.SocketTimeoutException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import navigation.server.HttpSender;
import navigation.shared.LatLong;


/**
 * @author Jerrid
 * does all the parsing for the Google API
 * It acts as a wrapper for parsing the JSON.
 */
public class API_Parser {

	public static String getStreetName(LatLong point) throws Exception
	{
		String endpoint = "http://maps.googleapis.com/maps/api/geocode/json";
		String parameters = "latlng="+point.getLatitude()+","+point.getLongitude()+"&sensor=false";
		return parseReverseGeoCodingApi(HttpSender.sendGetRequest(endpoint, parameters));
		
	}
	
	public static String parseReverseGeoCodingApi(String jsonLine)
	{
		JsonElement jElement = new JsonParser().parse(jsonLine);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray resultsArray = jObject.getAsJsonArray("routes");
		return resultsArray.get(0).getAsJsonObject().getAsJsonArray("address_components").get(1).getAsJsonObject().get("long_name").toString() ;
		
	}
	
	public static int getNumberOfRoutes(String jsonLine)
	{
		JsonElement jElement = new JsonParser().parse(jsonLine);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray resultsArray = jObject.getAsJsonArray("routes");
		return resultsArray.size();
	}
	
	public static String getRouteInformation(String jsonLine, int routeNumber)
	{
		JsonElement jElement = new JsonParser().parse(jsonLine);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray resultsArray = jObject.getAsJsonArray("routes");
		return resultsArray.get(routeNumber).toString();
		
	}
	public static String getStepInformation(String jsonLine, int stepNumber)
	{
		JsonElement jElement = new JsonParser().parse(jsonLine);
		JsonObject jObject = jElement.getAsJsonObject();
		JsonArray resultsArray = jObject.getAsJsonArray("legs").getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("steps");
		return resultsArray.get(stepNumber).toString();
		
	}
}
