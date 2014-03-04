/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import navigation.shared.LatLong;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import parse.almonds.ParseObject;
import parse.almonds.ParseQuery;
import parse.almonds.table.ParseUVReading;

/**
 *
 * @author jried31
 */
public class SunUtil {
    private double elevAngleSun=-1,azimuthAngle=-1,
            meanUVISun = 0,
	        meanUVIShade = 0,
                meanUVICloud = 0;
    private LatLong location=null;
    private String month;
    private String day;
    private String year;
    private String hour;
    private String minute;
    private String city = null;
    private String state = null;
    private String place = null;
    
    private final String interval = "10";
    private String referrerURI="http://aa.usno.navy.mil/data/docs/AltAz.php";
    private String url ="http://aa.usno.navy.mil/cgi-bin/aa_altazw.pl?FFX=1&obj=INTERVAL&xxy=2014&xxm=2&xxd=25&xxi=10&st=CA&place=los+angeles&ZZZ=END";
            
    private Timer timer=null;
    private TimerTask uviTaskHandler = new TimerTask(){
         public void run() {
             try {
                 updateUVI();
             } catch (parse.almonds.ParseException ex) {
                 Logger.getLogger(SunUtil.class.getName()).log(Level.SEVERE, null, ex);
             }
            //timer.scheduleAtFixedRate(uviTaskHandler, 1000, Constants.UVI_UPDATE_INTERVAL);
        }
    };
    public double getUVISun(){return meanUVISun;}
    public double getUVIShade(){return meanUVIShade;}
    public double getUVICloud(){return meanUVICloud;}
    
    public double getElevationAngle(){return elevAngleSun;}
    public double getAzmuthAngle(){return azimuthAngle;}
    public double getAzmuthAngleAsCartesian(){
        double azimuthCartesian=0;
        if (azimuthAngle > 0 && azimuthAngle <= 90) {
            azimuthCartesian = 90 - azimuthAngle;
        } else if (azimuthAngle > 90 && azimuthAngle <= 180) {
            azimuthCartesian = 360 - (azimuthAngle - 90);
        } else if (azimuthAngle > 180 && azimuthAngle <= 270) {
            azimuthCartesian = 180 + (270 - azimuthAngle);
        } else if (azimuthAngle > 270 && azimuthAngle < 360) {
            azimuthCartesian = 90 + (360 - azimuthAngle);
        }else{
            azimuthCartesian = 90;
        }
        return azimuthCartesian;
    }
    
    public double convertDegreeToRadian(double degree){
        return degree*(Math.PI/180);
    }
    public SunUtil(LatLong location) throws ParseException, parse.almonds.ParseException{
        this.location = location;
        //Retrieve the Sun Angle data from Website
        getSunAngleData();
        updateUVI();
        /*if(timer == null){
            timer = new Timer();
            //timer.scheduleAtFixedRate(this.uviTaskHandler, 1000, Constants.UVI_UPDATE_INTERVAL);
        }*/
    }
    
    public void updateUVI() throws parse.almonds.ParseException{
        Constants.InitializeParse();
        
        ParseQuery phenomenaQueryObject = new ParseQuery(Constants.TABLE_UV_DATA);
        phenomenaQueryObject.orderByDescending(ParseUVReading.TIMESTAMP);
	phenomenaQueryObject.setLimit(30);
        List<ParseObject> uvDataList = phenomenaQueryObject.find();
                    
        long time1, time2;
        Date now = new Date();
        time1 = now.getTime();
        int sunCount=0,
                shadeCount = 0,
                cloudCount = 0;
        
        for (int i = 0; i < uvDataList.size();i++){
            ParseObject uv = uvDataList.get(i);
            Date timestamp = uv.getDate(ParseUVReading.TIMESTAMP);
            String environment = uv.getString(ParseUVReading.ENVIRONMENT);
            int uvi = uv.getInt(ParseUVReading.UVI);
            //time2 = timestamp.getTime();

            //if (time1 - time2 <= 120000) {
            if(environment != null){
                if (environment.equals(Constants.CLASS_LABEL_IN_SUN)) {
                    if (sunCount <= 1) {
                        meanUVISun = uvi;
                    } else {
                        meanUVISun = (uvi + meanUVISun * (sunCount - 1)) / sunCount;
                    }
                    sunCount++;
                } else if (environment.equals(Constants.CLASS_LABEL_IN_CLOUD)) {
                    if (cloudCount <= 1) {
                        meanUVICloud = uvi;
                    } else {
                        meanUVICloud = (uvi + meanUVICloud * (cloudCount - 1)) / cloudCount;
                    }
                    cloudCount++;
                } else if (environment.equals(Constants.CLASS_LABEL_IN_SHADE)) {
                    if (shadeCount <= 1) {
                        meanUVIShade = uvi;
                    } else {
                        meanUVIShade = (uvi + meanUVIShade * (shadeCount - 1)) / shadeCount;
                    }
                    shadeCount++;
                }
            }
            //}
        }
    }
    
    public void computeSunAngles() throws ParseException{
        updateCurrentTime();
        String time = ((hour.length()==1?"0"+hour:hour)+":"+(minute.length()==1?"00":(minute.charAt(0)+"0")));//"13:30";//
        SunAngle angle = sunAngles.get(time);
        if(angle != null){
            azimuthAngle = angle.getAzimuth();
            elevAngleSun = angle.getAltitude();
            System.out.println("Azimuth Angle: "+azimuthAngle+ " Azimuth Cartesian: "+ this.getAzmuthAngleAsCartesian()+ " Elevation Angle: "+elevAngleSun);
        }
    }
    
    public void computeSunAngles(Calendar calendar,LatLong position){
        
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
        
        //int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        //double LT = getTimeInHours();
        final int dayOfYear = 347; //hard coded day of year
        final double LT = 10.25; //hard coded time
        
        final int gmt_adjust = -8; //neither
        final double LSTM = 15 * gmt_adjust; //local solar time meridian, in degrees
        final double B = Math.toRadians((360.0 / 365) * (dayOfYear - 81)); //in radians
        final double EoT = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B); // Equation of time, in degrees
        final double TC = 4 * (longitude - LSTM) + EoT; //Time Correction
        final double LST = LT + TC / 60; //LT = local time, LST = local solar time
        final double HRA = Math.toRadians(15 * (LST - 12)); //hour angle, in radians
        final double delta = Math.toRadians(23.45 * Math.sin(B));//in radians
        //elevation angle of the sun
        elevAngleSun = Math.asin((Math.sin(delta) * Math.sin(Math.toRadians(latitude)) + Math.cos(delta) * Math.cos(Math.toRadians(latitude)) * Math.cos(HRA)));//in radians

        System.out.println("LT (time): " + LT);
        
        //if the sun is not up
        //find out elevation angle for morning and night (what is the cutoff points)
        if (elevAngleSun <= 0) {
            return;
        }

        //azimuth angle
        final double theta = Math.acos((Math.sin(delta) * Math.cos(Math.toRadians(latitude)) - Math.cos(delta) * Math.sin(Math.toRadians(latitude)) * Math.cos(HRA)) / Math.cos(elevAngleSun));//in radians
        if (LST > 12 || HRA > 0) {
            azimuthAngle = 360 - Math.toDegrees(theta);
        } else {
            azimuthAngle = Math.toDegrees(theta);
        }
        
        // need to convert az here for different quadrants
        System.out.println("Azimuth:  " + azimuthAngle + " az after conversion: " + this.getAzmuthAngleAsCartesian());
    }
    
        /**
     * @return the time of day (i.e. 4:30 PM = 16.5)
     */
    public double getTimeInHours() {
        GregorianCalendar now = new GregorianCalendar();
        return now.get(Calendar.HOUR_OF_DAY) + ((double) now.get(Calendar.MINUTE)) / 60 + ((double) now.get(Calendar.SECOND)) / 3600;
    }

    
    public void updateCurrentTime() {
        GregorianCalendar now = new GregorianCalendar();
        month=Integer.toString(now.get(Calendar.MONTH)+1);
        day=Integer.toString(now.get(Calendar.DAY_OF_MONTH));
        year=Integer.toString(now.get(Calendar.YEAR));
        hour=Integer.toString(now.get(Calendar.HOUR));
        minute=Integer.toString(now.get(Calendar.MINUTE));
    }
        
     private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9].*";
          
    private HashMap<String,SunAngle> sunAngles;
    private void getSunAngleData() throws ParseException{
        getLocationInformation();
        updateCurrentTime();
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Referer", referrerURI);
        //httppost.addHeader("Host","aa.usno.navy.mil");
        String responseString=null;
     // Execute HTTP Post Request
        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
            nameValuePairs.add(new BasicNameValuePair("FFX", "1"));
            nameValuePairs.add(new BasicNameValuePair("ZZZ", "END"));
            nameValuePairs.add(new BasicNameValuePair("sun", "10"));
            nameValuePairs.add(new BasicNameValuePair("place", city));
            nameValuePairs.add(new BasicNameValuePair("st", state));
            nameValuePairs.add(new BasicNameValuePair("obj", interval));
            nameValuePairs.add(new BasicNameValuePair("xxi", interval));
            nameValuePairs.add(new BasicNameValuePair("xxd", day));//day
            nameValuePairs.add(new BasicNameValuePair("xxy", year));//year
            nameValuePairs.add(new BasicNameValuePair("xxm", month));//month
            
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            System.out.println(httppost.getURI().toString());
            
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            String []token;
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                
                token = responseString.split("\n");
                sunAngles = new HashMap<String,SunAngle>();
                for (String row : token) {
                    boolean val = row.matches(TIME24HOURS_PATTERN);
                    if(val == true)
                    {
                        String data[] = row.split("\\s+");
                        sunAngles.put(data[0],new SunAngle(data[0],Float.valueOf(data[1]),Float.valueOf(data[2])));
                        //System.out.println(row + " " +val);
                    }
                }
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
       } catch (ClientProtocolException e) {
           // TODO Auto-generated catch block
       } catch (IOException e) {
           // TODO Auto-generated catch block
       }
    }

    
    
    private void getLocationInformation(){
        //Get Reverse Geoposition information
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try 
        {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            
            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(stringBuilder.toString());
            
            // get lat and lng value
            String location_string=null;
            //Get JSON Array called "results" and then get the 0th complete object as JSON        
            JSONObject address = jsonObject.getJSONArray("results").getJSONObject(0); 
            // Get the value of the attribute whose name is "formatted_string"
            JSONObject addressComponent = address.getJSONArray("address_components").getJSONObject(0); 
            
            JSONArray component = address.getJSONArray("address_components");
            boolean cityFound=false,stateFound=false;
            for(int i = 0;(i < component.length()) && (!cityFound || !stateFound);i++){
                addressComponent = component.getJSONObject(i);
                JSONArray types = addressComponent.getJSONArray("types");
                for(int j = 0;j < types.length();j++)
                {
                    if(types.getString(j).equals( "locality")){
                        city = addressComponent.getString("long_name");
                        cityFound = true;
                    }
                    
                    if(types.getString(j).equals( "administrative_area_level_1")){
                        state = addressComponent.getString("short_name");
                        stateFound = true;
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
