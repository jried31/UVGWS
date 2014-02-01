/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to store UTM coordinate values, without this, you cannot tell 
 * @author Samson
 */
public class UtmLatLong {
    private Double latitude;
    private Double longitude;

    public UtmLatLong() {
    }

    public JSONObject getJson() throws JSONException
    {
        JSONObject loc =new JSONObject();
        loc.put("lat", this.latitude);
        loc.put("lng", this.longitude);
        //JSONObject o = new JSONObject();
        //o.put(obj, loc);
        return loc;
    }

//		public JSONObject getJson(String loc) throws JSONException
//		{
//			JSONObject obj = new JSONObject();
//			JSONObject lat = new JSONObject().put("lat",this.latitude);
//			JSONObject lng = new JSONObject().put("lat",this.longitude);
//			obj.put(loc,)
//			
//		}

    public UtmLatLong(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString()
    {
            return latitude+","+longitude;
    }
    public Double getLatitude() {
            return this.latitude;
    }

    public Double getLongitude() {
            return this.longitude;
    }

    public void setLatitude(Double latitude) {
            this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
            this.longitude = longitude;
    }
}
