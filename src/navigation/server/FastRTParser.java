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
import java.util.List;

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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Eric
 * This class parses the site:
 * http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED.cgi
 * It is assumed that measurements such as ozone, cloud, etc values are constant.
 */
public class FastRTParser {

    // Parses http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED.cgi and
    // returns the minimum amount of time needed to be spent in the sun for
    // effective Vitamin D consumption.
    public String parse() throws URISyntaxException, ClientProtocolException, IOException {
        String website = "http://zardoz.nilu.no/cgi-bin/olaeng/VitD_quartMEDandMED.cgi";

        // POST key, value pairs
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("month", "90"));
        qparams.add(new BasicNameValuePair("mday", "14"));
        qparams.add(new BasicNameValuePair("latitude", "50.5"));
        qparams.add(new BasicNameValuePair("longitude", "4.2"));
        qparams.add(new BasicNameValuePair("sza_angle", "60"));
        qparams.add(new BasicNameValuePair("skin_index", "5"));
        qparams.add(new BasicNameValuePair("exposure_timing", "0"));
        qparams.add(new BasicNameValuePair("start_time", "10.5"));
        qparams.add(new BasicNameValuePair("body_exposure", "25"));
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
        String exposureTime = "";

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
            Elements content = doc.getElementsByTag("p");

            // Resides on the last node of the <p> subtree, which is not an element.
            // Request returns in hours:minutes format.
            System.out.println(content.last().childNode(content.last().childNodeSize() - 1));
            exposureTime = content.last().childNode(content.last().childNodeSize() - 1).toString();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return exposureTime;
    }

}
