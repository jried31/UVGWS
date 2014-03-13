/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import navigation.shared.LatLong;
import navigation.shared.Person;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 *
 * @author Eric
 * This class parses the site:
 * http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED.cgi
 * It is assumed that measurements such as ozone, cloud, etc values are constant.
 */
public class FastRTParser {

    // Parses http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED_v2.cgi and
    // returns the minimum amount of time needed to be spent in the sun for
    // effective Vitamin D consumption.
    public String[] parse(LatLong location, Person person, int month_number, int day_number, double hour_number) throws URISyntaxException, ClientProtocolException, IOException {
        String website = "http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED_v2.cgi";
        
        // uses current date for month and day inputs
        // TODO: change to date of activity once we decide on data format
        //Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        //note: zero-based, Jan == 0
        //int month_number = cal.get(Calendar.MONTH); //actual month number minus 1
        //int day_number = cal.get(Calendar.DAY_OF_MONTH);
        int[] month_days = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
        int month_day = month_days[month_number];
        String month = String.valueOf(month_day);
        String day = String.valueOf(day_number);
        String hour = String.valueOf(hour_number);
        
        // gets latitude and longitude of requested location
        double latitude_double = location.getLatitude();
        double longitude_double = location.getLongitude();
        String latitude = String.valueOf(latitude_double);
        String longitude = String.valueOf(longitude_double);
        
        int skinType_int = person.getSkinType();
        int SPF = person.getSPF();
        double bodyExposure_int = person.getBodyExposure();
        String skinType = String.valueOf(skinType_int);
        String bodyExposure = String.valueOf(bodyExposure_int);
        
        // POST key, value pairs
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        /* previous statements with default values
        //qparams.add(new BasicNameValuePair("month", "90"));
        //qparams.add(new BasicNameValuePair("mday", "14"));
        //qparams.add(new BasicNameValuePair("latitude", "50.5"));
        //qparams.add(new BasicNameValuePair("longitude", "4.2"));
        //qparams.add(new BasicNameValuePair("skin_index", "5"));
        //qparams.add(new BasicNameValuePair("exposure_timing", "0"));
        //qparams.add(new BasicNameValuePair("start_time", "10.5"));
        //qparams.add(new BasicNameValuePair("body_exposure", "25"));
        */
        qparams.add(new BasicNameValuePair("month", month));
        qparams.add(new BasicNameValuePair("mday", day));
        qparams.add(new BasicNameValuePair("latitude", latitude));
        qparams.add(new BasicNameValuePair("longitude", longitude));
        // sza_angle never used if we want Date, Time, Location used
        qparams.add(new BasicNameValuePair("sza_angle", "60"));
        // uses Fitzpatrick scale, range: 0-5 for Types I-VI
        qparams.add(new BasicNameValuePair("skin_index", skinType));
        // "1" selects 'Start time', instead of 'Around midday'
        qparams.add(new BasicNameValuePair("exposure_timing", "1"));
        // TODO: what is format/data type of time? (add to parameters)
        qparams.add(new BasicNameValuePair("start_time", hour));
        qparams.add(new BasicNameValuePair("body_exposure", bodyExposure));
        qparams.add(new BasicNameValuePair("dietary_equivalent", "1000"));
        qparams.add(new BasicNameValuePair("sky_condition", "0"));
        qparams.add(new BasicNameValuePair("aerosol_specification", "0"));
        qparams.add(new BasicNameValuePair("visibility", "25"));
        qparams.add(new BasicNameValuePair("angstrom_beta", "0.11"));
        qparams.add(new BasicNameValuePair("cloud_fraction", "50"));
        qparams.add(new BasicNameValuePair("wc_column1", "400"));
        qparams.add(new BasicNameValuePair("wc_column2", "400"));
        qparams.add(new BasicNameValuePair("wc_column3", "400"));
        qparams.add(new BasicNameValuePair("UVI", "3.4"));
        qparams.add(new BasicNameValuePair("ozone_column", "350"));
        qparams.add(new BasicNameValuePair("altitude", "0.150"));
        qparams.add(new BasicNameValuePair("surface", "0"));
        qparams.add(new BasicNameValuePair("albedo", "0"));
        qparams.add(new BasicNameValuePair("type", "2"));

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(website);
        httppost.setEntity(new UrlEncodedFormEntity(qparams));
        CloseableHttpResponse response = httpclient.execute(httppost);

        String responseString = "";
        String vitaminDExposureTime = "";
        String sunburnExposureTime = "";
        
        try {
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            InputStreamReader isr = new InputStreamReader(instream);
            BufferedReader rd = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();          
            try {
                String line = "";
                while ((line = rd.readLine()) != null) {
                    buffer.append(line);
                }
                responseString = buffer.toString();
                isr.close();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                instream.close();
            }

            Document doc = Jsoup.parse(responseString);
            Elements contentVitD = doc.getElementsContainingOwnText("UV exposure in order to obtain the desired amount of vitamin D:");
            Elements contentSunburn = doc.getElementsContainingOwnText("UV exposure in order to obtain a sunburn:");

            if (contentVitD.first().nextElementSibling().nextElementSibling().nextElementSibling().tagName().equals("blink")) {
                vitaminDExposureTime = "NA";
                System.out.println("NA-vitaminD");
            } else {
                // Request returns in hours:minutes format.
                vitaminDExposureTime = contentVitD.first().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextSibling().toString();
                System.out.println(vitaminDExposureTime);
            }
            
            if (contentSunburn.first().nextElementSibling().nextElementSibling().nextElementSibling().tagName().equals("blink")) {
                sunburnExposureTime = "NA";
                System.out.println("NA-sunburn");
            } else {
                // Request returns in hours:minutes format.
                sunburnExposureTime = contentSunburn.first().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling().nextSibling().toString();
                System.out.println(sunburnExposureTime);
            }

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        
        String times[] = new String[2];
        times[0] = vitaminDExposureTime;
        times[1] = sunburnExposureTime;
        
        return times;
    }

}
