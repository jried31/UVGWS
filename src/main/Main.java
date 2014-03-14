package main;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import navigation.server.API_Parser;
import navigation.shared.BoundingBox;
import navigation.server.FastRTParser;
import navigation.server.HttpSender;
import navigation.server.NSFParser;
import navigation.util.BuildingUtils;
import navigation.shared.Building;
import navigation.util.Constants;
import navigation.shared.LatLong;
import navigation.shared.Routes;
import navigation.shared.Segment;
import navigation.util.SunUtil;
import navigation.util.Util;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * @author Jerrid
 * This call is used to call the other classes by passing the source and destination points
 * It also returns the routes with minimum uv exposure
 * The criteria needs to be specified in the commandline arguments
 * 1 => Minimum UVA, 2=> Minimum UVB, 3 => Minimum of Avg of UVA and UVB
 * sample commandline arguments to run the code and get the route with minimum UVA exposure
 * 1 34.066454 -118.45307 34.059504 -118.44777
 *
 * Remember All inputs require Longitude, Latitude order when location inputs go to Geotools (eg queries)
 */
public class Main {
    
        
        /**
         * @author Sam
         * @throws Exception 
         */
        public static void testIsInShadow() throws Exception
        {
            // Ronald Regan Medical Center:
            LatLong segStart = new LatLong(34.066694, -118.445256);
            LatLong segEnd = new LatLong(34.066693, -118.445256);

            // Test at different times of day.
            for (int hour = 5; hour < 19; hour++) {
                SunUtil sun = new SunUtil(segStart);
                Calendar time = new GregorianCalendar(2014, 3, 21, hour, 0, 0);
                sun.computeSunAngles(time, segEnd);
                System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
                // Prints time.
                System.out.println("Time = " + time.get(Calendar.HOUR_OF_DAY) + ":00");
                // Prints sun angles.
                System.out.println("Azimuth (deg from N to E) = " + sun.getAzmuthAngle());
                System.out.println("Azimuth (deg from E to N) = " + sun.getAzmuthAngleAsCartesian());
                System.out.println("Elevation = " + sun.getElevationAngle());
                // Generates a bounding box
                BoundingBox bb = new BoundingBox(segStart, segEnd, 500, 500);
                Polygon bbPoly = bb.getBoundingBoxAsPolygon();
                // Gets all buildings in the bounding box.
                BuildingUtils butil = new BuildingUtils();
                butil.setEndpointBuffer(528);
                ArrayList<Building> bbBuildings = butil.getBuildingsInBoundingBox(bbPoly);
                // Checks if any buildings block the sun.
                boolean inShadow = false;
                List<Building> sunBlockers = butil.getBuildingsBlockingSun(bbBuildings, sun, segStart);
                for (Building buildingBlockingSun : sunBlockers) {
                    double buildingHeight = buildingBlockingSun.getHeight();
                    LatLong buildingCenter = buildingBlockingSun.getCenterPoint();
                    double buildingDistance = Util.getDistance(segStart, buildingCenter);
                    double buildingElevation = Math.atan(buildingHeight/buildingDistance);
                    double sunElevation = sun.getElevationAngle();
                    System.out.println("Elevation of building (rads) = " + buildingElevation);
                    System.out.println("Elevation of sun (rads) = " + sunElevation);
                    if (sunElevation <= buildingElevation) {
                        System.out.println("THIS BUILDING BLOCKS THE SUN (" + buildingBlockingSun.getId() + ")");
                        inShadow = true;
                    }
                    else {
                        System.out.println("THIS BUILDING DOES NOT BLOCK THE SUN (" + buildingBlockingSun.getId() + ")");
                    }
                }
                System.out.println("IN SHADOW = " + inShadow);
                
                // Prints isInShadow.
                //Segment seg = new Segment();
                //seg.setStart_point(segStart);
                //seg.setEnd_point(segEnd);
                //seg.initialize();
                //System.out.println("Segment In shadow: " + seg.isInShadow(time));
                
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }
            // TODOS:
            // 1. Integrate this funcion with the segment UV algorithm.
            // 2. Verify the split() function works as expected.
            // 3. Pull UV irradence data from parse.com in the SunUtil class.
            System.exit(0);
        }
        
        /**
         * @author Sam
         * @throws Exception 
         */
        public static void testSegment() throws Exception
        {
            // Near Ronald Regan Medical Center:
            // * THIS SEGMENT SHOULD BE SPLIT!
            LatLong segStart = new LatLong(34.059026, -118.443059);
            LatLong segEnd = new LatLong(34.058794, -118.443914);
            
            // Creates an uninitialized segment.
            Segment s = new Segment();
            
            // Test the segment at various times of day.
            for (int hour = 13; hour <= 18; hour++) {
                Calendar startTime = new GregorianCalendar(2014, 3, 21, hour, 0, 0);
                s.setStart_point(segStart);
                s.setEnd_point(segEnd);
                s.setStart_time(startTime);
                s.setPace(5.0);
                s.initialize();
                
                System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVV");
                System.out.println("Time: " + startTime.get(Calendar.HOUR_OF_DAY));
                System.out.println("Start Point: " + s.getStart_point());
                System.out.println("End point: " + s.getEnd_point());
                System.out.println("Distance: " + Util.getDistance(segStart, segEnd));
                System.out.println("Segment slices: " + s.getNo_of_readings());
                System.out.println("Slices in sun: " + s.getNumTimesInSun());
                System.out.println("Slices in shadow: " + s.getNumTimesInShadow());
                System.out.println("Distance in sun: " + s.getDistanceInSun());
                System.out.println("Distance in shadow: " + s.getDistanceInShadow());
                System.out.println("Time in sun: " + s.getDurationInSun());
                System.out.println("Time in shadow: " + s.getDurationInShadow());
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");
                break;
            }
            
            System.exit(0);
        }
        
        public static void testRoutes() throws Exception {
            //weyburn to starbucks
            //LatLong source = new LatLong(Double.parseDouble("34.0616"), Double.parseDouble("-118.449733333333"));
            //LatLong destination = new LatLong(Double.parseDouble("34.0624333333333"), Double.parseDouble("-118.447266666667"));
            
            //starbucks to boelter
            LatLong source = new LatLong(Double.parseDouble("34.0624333333333"), Double.parseDouble("-118.447266666667"));
            LatLong destination = new LatLong(Double.parseDouble("34.0676833333333"), Double.parseDouble("-118.444916666667"));
                
            //Weyburn/Gayley to Strathmore/Gayley
            //LatLong source = new LatLong(Double.parseDouble("34.0619166666667"), Double.parseDouble("-118.448"));
            //LatLong destination = new LatLong(Double.parseDouble("34.0685333333333"), Double.parseDouble("-118.44885"));
            
            //Strathmore/Gayley to Strathmore/Westwood
            //LatLong source = new LatLong(Double.parseDouble("34.0685333333333"), Double.parseDouble("-118.44885"));
            //LatLong destination = new LatLong(Double.parseDouble("34.0688166666667"), Double.parseDouble("-118.445033333333"));
            
            String endpoint = "http://maps.googleapis.com/maps/api/directions/json";
            String requestParameters = "sensor=false&mode=walking&alternatives=true&origin="+source.getLatitude()+","+source.getLongitude()+"&destination="+destination.getLatitude()+","+destination.getLongitude();
            String googleMapsResult = HttpSender.sendGetRequest(endpoint, requestParameters);
            
            //System.out.println(googleMapsResult);
            Routes[] allRoutes;

            //Grab route alternatives
            int numberOfRoutes = API_Parser.getNumberOfRoutes(googleMapsResult);
            allRoutes = new Routes[numberOfRoutes];

            //Holds the JSON Google Maps route information to be returned to the calling function
            JSONArray allRoutesJSON =  new JSONArray();


            // initializing all routes
            for(int i = 0; i < 1; i++)
            {	
                allRoutes[i] = new Routes();
                allRoutes[i].setGoogleAPIJson(API_Parser.getRouteInformation(googleMapsResult, i));
                allRoutes[i].setStart_time(new GregorianCalendar(2014, 3, 6, 16, 0, 0));
                allRoutes[i].setPace(5);
                allRoutes[i].initialize();
                
                Routes r = allRoutes[i];
                System.out.println("\n=============================\n");
                System.out.println("Start Location:" + r.getStart_location());
                System.out.println("End Location: " + r.getEnd_location());
                System.out.println("Distance: " + r.getDistance());
                System.out.println("Distance in meters: " + r.getDistanceInMeters());
                System.out.println("Start Time: " + r.getStart_time().get(Calendar.HOUR_OF_DAY));
                System.out.println("No. segments in shadow: " + r.getNumTimesInShadow());
                System.out.println("No. segments in sun: " + r.getNumTimesInSun());
                System.out.println("Distance in shadow: " + r.getDistanceInShadow());
                System.out.println("Distance in sun: " + r.getDistanceInSun());
                System.out.println("Pace: " + r.getPace());
                System.out.println("Summary: " + r.getSummary());
                System.out.println("\n=============================\n");

                allRoutesJSON.put(i,allRoutes[i].getJson());
            }

            JSONObject obj = new JSONObject();
            obj.put("routes", allRoutesJSON);
            System.out.println(obj.toString());
            
            System.exit(0);
        }

	public static void main(String args[]) throws Exception
	{
            testRoutes();
            LatLong start_location = new LatLong(34.191046, -118.444362);//33.878458, -118.376632);//33.884801, -118.368365);//33.875960, -118.351002);//33.884801, -118.368365);//33.880005, -118.372799);//33.878458, -118.376632);//33.879198, -118.376963);
            LatLong end_location = new LatLong(34.192399, -118.444362);//33.878467, -118.375152);//33.883924, -118.367711);//33.876370, -118.349886);//33.883924, -118.367711);//33.879524, -118.372789);//33.878467, -118.375152);//33.877822, -118.377025);
            
            SunUtil util = new SunUtil(start_location);
            
            
            Segment s1 = new Segment();
            s1.setStart_point(start_location);
            s1.setEnd_point(end_location);
            s1.initialize();
            s1.isInShadow();
            
            if(true)return;
            
            
            BoundingBox bb = new BoundingBox (start_location, end_location,80,120);
            Coordinate[] polygonCoord = bb.getBoundingBox();
            
            
            System.out.println(bb.getStartPoint());
            System.out.println(bb.getEndPoint());
            System.out.println(bb.getMinBoundingBuffer());
            System.out.println(bb.getMaxBoundingBuffer());
            System.out.println("Points:");
            for(Coordinate c: polygonCoord){
                System.out.println(c.x + ", " + c.y);
            }
            
            ArrayList<Building> list = new ArrayList<Building>();
            BuildingUtils area = new BuildingUtils();
            File file = new File(Constants.PATH + Constants.SHP);
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            FeatureSource source = store.getFeatureSource();
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            FeatureType schema = source.getSchema();
            String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
            CoordinateReferenceSystem sourceCrs = schema.getGeometryDescriptor().getCoordinateReferenceSystem();

            // creates custom bounding box using input bounding box coordinates
            Polygon polygon = bb.getBoundingBoxAsPolygon();

            //ReferencedEnvelope bbox = new ReferencedEnvelope(polygonCoord[0].x,polygonCoord[0].y,polygonCoord[2].x,polygonCoord[2].y, sourceCrs);
            //Filter filter = ff.bbox(ff.property(geometryPropertyName), bbox);
            //FeatureCollection collection = source.getFeatures(filter);
    
            // filters buildings within the bounding box of the start and end points
            Filter filter = ff.intersects(ff.property(geometryPropertyName), ff.literal(polygon));
            FeatureCollection collection = source.getFeatures(filter);
            int size = collection.size();
            System.out.println("Collection size: " + size);
            FeatureIterator iterator = collection.features();

            Building b;
            try {
                while (iterator.hasNext()) {
                    Feature feature = iterator.next();
                    System.out.println(feature);
                    b = new Building(feature);
                    list.add(b);
                    System.out.println(feature.getIdentifier());
                    System.out.println(feature.getDefaultGeometryProperty().getValue());
                    System.out.println("Building ID: " + feature.getProperty("BLD_ID").getValue());
                    System.out.println("Height: " + feature.getProperty("HEIGHT").getValue());
                    System.out.println(feature.getDefaultGeometryProperty().getBounds());
                    System.out.println("Center of building: " + b.getCenterPoint());
                }
            } finally {
                iterator.close();
            }

            list = area.sortByDistance(list, start_location);
            
            
            
            if(true)return;
            
            NSFParser parser = new NSFParser();
            //These are the extracted weights from the CIE file
            Map<String, Double> cieWeights = parser.weightMap();
            
            
            //Returns the times where there exists energy from the 290 wavelength
            Set<String> hoursForVitaminD = parser.getVitaminD();
            System.out.println(hoursForVitaminD);
            
            //Computes the UVI from the excel file
            Map<String, Double> uvi = parser.getUVIndex();
            System.out.println(uvi);
            
           
/*		LatLong source = new LatLong(Double.parseDouble("34.06649"), Double.parseDouble("-118.452984"));
//		LatLong destination = new LatLong(Double.parseDouble("34.059468"), Double.parseDouble("-118.447705"));
            
//            LatLong source = new LatLong(Double.parseDouble("34.063623"), Double.parseDouble("-118.448182"));
//            LatLong destination = new LatLong(Double.parseDouble("34.060244"), Double.parseDouble("-118.44646"));
            
//            LatLong source = new LatLong(Double.parseDouble("34.060244"), Double.parseDouble("-118.44646"));
//            LatLong destination = new LatLong(Double.parseDouble("34.06079"), Double.parseDouble("-118.445479"));
            
//            LatLong source = new LatLong(Double.parseDouble("34.06079"), Double.parseDouble("-118.445479"));
//            LatLong destination = new LatLong(Double.parseDouble("34.06255"), Double.parseDouble("-118.445447"));
            
//            LatLong source = new LatLong(Double.parseDouble("34.06255"), Double.parseDouble("-118.445447"));
//            LatLong destination = new LatLong(Double.parseDouble("34.063623"), Double.parseDouble("-118.448182"));
            
            //weyburn to starbucks
//             LatLong source = new LatLong(Double.parseDouble("34.0616"), Double.parseDouble("-118.449733333333"));
//            LatLong destination = new LatLong(Double.parseDouble("34.0624333333333"), Double.parseDouble("-118.447266666667"));
            
            //starbucks to boelter
//            LatLong source = new LatLong(Double.parseDouble("34.0624333333333"), Double.parseDouble("-118.447266666667"));
//            LatLong destination = new LatLong(Double.parseDouble("34.0676833333333"), Double.parseDouble("-118.444916666667"));
                
            //Weyburn/Gayley to Strathmore/Gayley
            LatLong source = new LatLong(Double.parseDouble("34.0619166666667"), Double.parseDouble("-118.448"));
            LatLong destination = new LatLong(Double.parseDouble("34.0685333333333"), Double.parseDouble("-118.44885"));
            
            //Strathmore/Gayley to Strathmore/Westwood
//            LatLong source = new LatLong(Double.parseDouble("34.0685333333333"), Double.parseDouble("-118.44885"));
//            LatLong destination = new LatLong(Double.parseDouble("34.0688166666667"), Double.parseDouble("-118.445033333333"));
            
            
            
		// set source and destination depending on the arguments passed
		//if(args.length == 4 || Integer.parseInt(args[0]) == 0){
                //    source = new LatLong(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
                //    destination = new LatLong(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
		//}
		//else
		//{
                //    source = new LatLong(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                //    destination = new LatLong(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		// }
                
                
		String endpoint = "http://maps.googleapis.com/maps/api/directions/json";

		String requestParameters = "sensor=false&mode=walking&alternatives=true&origin="+source.getLatitude()+","+source.getLongitude()+"&destination="+destination.getLatitude()+","+destination.getLongitude();
		String googleMapsResult = HttpSender.sendGetRequest(endpoint, requestParameters);

		//System.out.println(googleMapsResult);
		Routes[] allRoutes;

                //Grab route alternatives
		int numberOfRoutes = API_Parser.getNumberOfRoutes(googleMapsResult);
		allRoutes = new Routes[numberOfRoutes];
                
                //Holds the JSON Google Maps route information to be returned to the calling function
		JSONArray allRoutesJSON =  new JSONArray();

                
		// initializing all routes
		// print all the segments on the route
//		for(int i = 0;i < allRoutes.length;i++)
//                for(int i = 2;i < 3;i++)
                int i = 0;
		{	
                    allRoutes[i] = new Routes();
                    allRoutes[i].setGoogleAPIJson(API_Parser.getRouteInformation(googleMapsResult, i));
                    allRoutes[i].initialize();
                    
                    allRoutesJSON.put(i,allRoutes[i].getJson());
		}

		JSONObject obj = new JSONObject();
		obj.put("routes", allRoutesJSON);
                
                //Selects the minimal route option based upon UV
		if(args.length == 4 || Integer.parseInt(args[0]) == 0)
			System.out.println(obj.toString());
		else
		{
			// return JSON with the selected criteria
			JSONObject output= new JSONObject();
			int expectedOutput = Integer.parseInt(args[0]);

			switch(expectedOutput)
			{
			case 1:
				//output.put("routes", allRoutes[getMinUVARoute(allRoutes)].getJson());
				break;

			case 2:
				//output.put("routes", allRoutes[getMinUVBRoute(allRoutes)].getJson());
				break;

			case 3:
				//output.put("routes", allRoutes[getMinUVABRoute(allRoutes)].getJson());
				break;

			}
                        Routes route = allRoutes[i];
                        System.out.println("route: " + route);
                        System.out.println("Number of times in shadow: " + route.getNumTimesInShadow());
                        System.out.println("Distance in shadow: " + route.getDistanceInShadow());
                        System.out.println("Number of times in sun: " + route.getNumTimesInSun());
                        System.out.println("Distance in sun: " + route.getDistanceInSun());
                        
                        System.out.println("Average UVI: " + route.getUvi());
                        System.out.println("Energy: " + Util.calculateE(route.getUvi(), route.getDistance()) + " J/m^2");
                        
                        double timeInSun = (route.getDistanceInSun() * 1000) / Constants.AVERAGE_WALKING_SPEED; // in seconds
                        double timeInShadow = (route.getDistanceInShadow() * 1000) / Constants.AVERAGE_WALKING_SPEED; // in seconds
                        System.out.println("Time in Sun: " + timeInSun + " seconds");
                        System.out.println("Time in Shadow: " + timeInShadow + " seconds");
                        System.out.println("Total Time: " + (timeInSun + timeInShadow) + " seconds");
                        System.out.println("Route distance: " + route.getDistance());
			System.out.println(output);

                        System.out.println("Shade percentage: " + (route.getDistanceInShadow()/(route.getDistanceInShadow() + route.getDistanceInSun())));
                        System.out.println("Blah: " + Util.calculateE(1.64, route.getDistance()) + " J/m^2");
                        
		}*/
                
	}

}
