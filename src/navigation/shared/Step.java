package navigation.shared;

import org.json.JSONException;
import org.json.JSONObject;

import navigation.server.HttpSender;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Jerrid This class is the Steps within a route that correspond to
 * portions of a street path. This class is used to initialize the step
 * information and also initialize the segments and get/set UV data for each
 * segment After the UV data for each segment is ready,
 */
public class Step {

    private String googleAPIJson;
    private int distance;
    private int duration;
    private LatLong start_location;
    private LatLong end_location;
    private double uva = 0;
    private double uvb = 0;
    private double uvi = 0;
    private String summary;
    private Segment[] segments;
    private final int minNoOfReadings = 6;
    private int numSegmentSlices = 0;
    private int numTimesInShadow = 0;
    private int numTimesInSun = 0;
    private double distanceInShadow, distanceInSun; //in km

    public double getDistanceInShadow() {
        return distanceInShadow;
    }

    public double getDistanceInSun() {
        return distanceInSun;
    }

    /**
     * @throws Exception Used to initialize the Steps and also set the Steps
     */
    public void initialize() throws Exception {
        JsonElement jElementStep = new JsonParser().parse(this.googleAPIJson);
        JsonObject jObjectStep = jElementStep.getAsJsonObject();
        this.distance = Integer.parseInt(jObjectStep.getAsJsonObject("distance").get("value").toString());
        this.duration = Integer.parseInt(jObjectStep.getAsJsonObject("duration").get("value").toString());
        this.start_location = new LatLong(Double.parseDouble(jObjectStep.getAsJsonObject("start_location").get("lat").toString()), Double.parseDouble(jObjectStep.getAsJsonObject("start_location").get("lng").toString()));
        this.end_location = new LatLong(Double.parseDouble(jObjectStep.getAsJsonObject("end_location").get("lat").toString()), Double.parseDouble(jObjectStep.getAsJsonObject("end_location").get("lng").toString()));
        this.summary = jObjectStep.get("html_instructions").toString().toString().replace("\"", "");
        setSegments();
        setNumSegmentSlices();
        setUVValues();
        setTimesInShadowAndSun();
    }

    public void setTimesInShadowAndSun() {
        for (int i = 0; i < this.segments.length; i++) {
            Segment seg = this.segments[i];
            this.numTimesInShadow += seg.getNumTimesInShadow();
            this.numTimesInSun += seg.getNumTimesInSun();
            this.distanceInShadow += seg.getDistanceInShadow();
            this.distanceInSun += seg.getDistanceInSun();
        }
    }

    public double getUvi() {
        return uvi;
    }

    public void setUvi(double uvi) {
        this.uvi = uvi;
    }

    public int getNumTimesInShadow() {
        return this.numTimesInShadow;
    }

    public int getNumTimesInSun() {
        return this.numTimesInSun;
    }

    /**
     * @throws Exception Sets the end points for each segments and calls to
     * initilize them
     */
    public void setSegments() throws Exception {
        String endpoint = "http://www.yournavigation.org/api/1.0/gosmore.php";
        String parameters = "format=geojson&flat=" + this.getStart_point().getLatitude() + "&flon=" + this.getStart_point().getLongitude() + "&tlat=" + this.getEnd_point().getLatitude() + "&tlon=" + this.getEnd_point().getLongitude() + "&v=foot";
        //Grab the OpenStreetMaps route segments
        String jsonLine = HttpSender.sendGetRequest(endpoint, parameters);

        JsonElement jElement = new JsonParser().parse(jsonLine);
        JsonArray jArray = jElement.getAsJsonObject().getAsJsonArray("coordinates");
		// size -1 is done since we pair the segments and 
        //thus if there are n segments, there would be n-1 pairs
        segments = new Segment[jArray.size() - 1];
        String one;
        String two;
        String[] sp;
        LatLong p1;
        LatLong p2;

        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment();
            p1 = new LatLong();
            one = jArray.get(i).toString();
            one = one.replace('[', ' ');
            one = one.replace(']', ' ');
            sp = one.trim().split(",");
            p1.setLatitude(Double.parseDouble(sp[1]));
            p1.setLongitude(Double.parseDouble(sp[0]));

            p2 = new LatLong();
            two = jArray.get(i + 1).toString();
            two = two.replace('[', ' ');
            two = two.replace(']', ' ');
            sp = two.trim().split(",");
            p2.setLatitude(Double.parseDouble(sp[1]));
            p2.setLongitude(Double.parseDouble(sp[0]));

            segments[i].setStart_point(p1);
            segments[i].setEnd_point(p2);
            one = "";
            two = "";
            // after the end points of the segments is set, it calls to initialize the segment
            segments[i].initialize();
        }

    }

//	/**
//	 * Used to interpolate the UV values from the neighbors if number of points in the current segment are not more than or equal
//	 * to the minimum number of points required.
//	 */
//	private void setSegmentUVValues() {
//		
//		Segment[] segments = this.getSegments();
//		double weight =0.5;
//		int totalReadingsTillNow=0;
//		int left, right;
//		double numerator = 0, denominator =0;
//		int count =0;
//		for(int i=0;i<segments.length;i++)
//		{
//			totalReadingsTillNow = 0;
//			// if the number of readings are greater than the minimum number of readings required, then just continue
//			if(segments[i].getNo_of_readings()>= minNoOfReadings)
//				continue;
//			
//			// else try to interpolate the readings from the neighbors
//			left  = i-1;
//			right = i+1;
//			count = 0;
//		
//			//set initial readings in the numerator and denominator
//			numerator = weight*segments[i].getNo_of_readings()*segments[i].getUva();
//			denominator = segments[i].getNo_of_readings()*weight;
//			totalReadingsTillNow = segments[i].getNo_of_readings();
//			
//			while((left >= 0 || right < segments.length) && totalReadingsTillNow < minNoOfReadings )
//			{	
//				weight = weight-count*0.1;
//				//search left
//				if(left >=0)
//				{
//					// increment the total readings count, adjust the numerator and the denominator
//					totalReadingsTillNow+=segments[left].getNo_of_readings();
//					numerator += weight*segments[left].getNo_of_readings()*segments[left].getUva();
//					denominator+= segments[left].getNo_of_readings()*weight;
//					left--;
//					
//				}
//				
//				//search right
//				if(right< segments.length)
//				{
//					// increment the total readings count, adjust the numerator and the denominator
//					totalReadingsTillNow+=segments[right].getNo_of_readings();
//					numerator += weight*segments[right].getNo_of_readings()*segments[right].getUva();
//					denominator+= segments[right].getNo_of_readings()*weight;
//					right++;
//					
//				}
//				count++;
//			
//			}
//			segments[i].setNo_of_readings(totalReadingsTillNow);
//			segments[i].setUva(numerator/denominator);
//			
//		}
//		
//		
//	}
    /**
     * Take the average of readings of points in each segment and set is as the
     * UV exposure for that segment
     */
    private void setUVValues() {

        int segmentLength = segments.length;
        int totalNumSegmentSlices = 0;
        int numSegmentSlices;

        for (int i = 0; i < segmentLength; i++) {
            numSegmentSlices = segments[i].getNumSegmentSlices();
            totalNumSegmentSlices += numSegmentSlices;
            uvi += segments[i].getUvi() * numSegmentSlices;
        }

        // get the average readings
        if (totalNumSegmentSlices > 0) {
            uva /= totalNumSegmentSlices;
            uvb /= totalNumSegmentSlices;
            uvi /= totalNumSegmentSlices;
        }

    }

    public void setNumSegmentSlices() {
        int tempNumSegmentSlices;
        for (int i = 0; i < this.segments.length; i++) {
            tempNumSegmentSlices = this.segments[i].getNumSegmentSlices();
            this.numSegmentSlices += tempNumSegmentSlices;
        }
    }

    public int getNumSegmentSlices() {
        return this.numSegmentSlices;
    }

    /**
     * @return JSONObject, all the route information is encoded.
     * @throws JSONException
     */
    public JSONObject getJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("distance", this.distance);
        //JSONObject duration = new JSONObject();
        obj.put("duration", this.duration);
        //JSONObject start_location = new JSONObject();
        obj.put("start_location", this.start_location.getJson());
        //JSONObject end_location = new JSONObject();
        obj.put("end_location", this.end_location.getJson());
		//obj.put("uv",this.getUVJson());
        //JSONObject summary = new JSONObject();
        obj.put("summary", this.summary);

        return obj;

    }

    public JSONObject getUVJson() throws JSONException {
        JSONObject uv = new JSONObject();
        uv.put("uva", this.uva);
        uv.put("uvb", this.uvb);
        return uv;

    }

    @Override
    public String toString() {

        return distance + "," + duration + "," + start_location + "," + end_location + "," + uva + "," + uvb + "," + summary;
    }

    public String getGoogleAPIJson() {
        return googleAPIJson;
    }

    public void setGoogleAPIJson(String googleAPIJson) {
        this.googleAPIJson = googleAPIJson;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LatLong getStart_point() {
        return start_location;
    }

    public void setStart_point(LatLong start_point) {
        this.start_location = start_point;
    }

    public LatLong getEnd_point() {
        return end_location;
    }

    public void setEnd_point(LatLong end_point) {
        this.end_location = end_point;
    }

    public double getUva() {
        return uva;
    }

    public void setUva(double uva) {
        this.uva = uva;
    }

    public double getUvb() {
        return uvb;
    }

    public void setUvb(double uvb) {
        this.uvb = uvb;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public void setSegments(Segment[] segments) {
        this.segments = segments;
    }
}
