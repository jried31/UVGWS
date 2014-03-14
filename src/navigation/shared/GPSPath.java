/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.shared;
import navigation.server.NSFParser;
import navigation.util.Util;
import java.util.ArrayList;
import java.util.Calendar;
import java.lang.IllegalArgumentException;

/**
 *
 * @author michaelbonilla
 */
public class GPSPath {
    private LatLong start_location;
    private LatLong end_location;
    private Calendar start_time;
    private Calendar end_time;
    private ArrayList<Track> tracks;
    double UVI;
    
    // One GPS coordinate with timestamp
    private class Track {
        public Calendar time;
        public LatLong coords;
    }
    
    //Initializes a GPSPath from a list of coordinates and timestamps
    //Coordinates and timestamps are stored in different lists and must be stored
    //in order relative to each other
    public GPSPath(ArrayList<Calendar> times, ArrayList<LatLong> coords, double UVI) {
        try 
        {
            this.UVI = UVI;
            int i=0;
            if (times.size() == 0 || (times.size() != coords.size()))
            {
                throw new IllegalArgumentException();
            }
            this.start_time = times.get(i);
            this.start_location = coords.get(i);
            for (i=0; i<=times.size()-1; i++)
            {
                Track t = new Track();
                t.time = times.get(i);
                t.coords = coords.get(i);
                tracks.add(t);
            }
            this.end_time = times.get(i);
            this.end_location = coords.get(i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public double computeTotalUVExposure()
    {
        NSFParser uvfinder = new NSFParser();
        Util utils = new Util();
        double UVI = uvfinder.getUVIndex();
        double totalExposure = 0;
        for (int i=0; i<this.tracks.size()-1; i++)
        {
            Track curTrack = tracks.get(i);
            Track nextTrack = tracks.get(i+1);
            double dis = utils.getDistance(curTrack.coords, nextTrack.coords);
            if (dis <= Segment.MAX_SEGMENT_SIZE)
            {
                try {
                    Segment s = new Segment(curTrack.coords, nextTrack.coords, curTrack.time, nextTrack.time, UVI);
                    totalExposure += s.getEffectiveUVDose();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                int numSegments = (int) Math.ceil(dis / Segment.MAX_SEGMENT_SIZE);
                
                //continue; //change this to split segment
            }
        }
        return totalExposure;
    }
}
