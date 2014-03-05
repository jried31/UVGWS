package navigation.shared;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import navigation.shared.LatLong;
import navigation.util.Constants;
import org.geotools.geometry.jts.JTSFactoryFinder;


/**
 * @author Jerrid
 * This class is responsible to get the bounding box when we pass it two points
 * An object of this class should be created 
 * Its purpose is to mainly counter the error due to GPS readings by creating a box to get the points
 */
public final class BoundingBox {

	private LatLong minBoundingBuffer=null;
	private LatLong maxBoundingBuffer=null;
        
        private LatLong start = null;
        private LatLong end = null;
        private Polygon polygon = null;
	private double endpointBuffer = 100;
        
        //degreeVariance is the +- offset to which given a line segment it creates
	private double degreeVariance=0;

	public BoundingBox(LatLong p1, LatLong p2, double endpointBuffer,double width)
	{
            setEndpointBuffer(endpointBuffer);
            setBoundingBox(p1, p2, endpointBuffer, width);
	}
	
            // returns a custom bounding box - in this case a custom rectangle whose points are calculated
    // based on the given points and distance
    public Coordinate[] getBoundingBox() {
       return polygon.getCoordinates();
    }
    
    public Polygon getBoundingBoxAsPolygon() {
       return polygon;
    }
	/*
        Computes min & max bounding box using the slope formula y - y1 = m(x - x1), where 
        x1 and y1 are reference points m is slope and y,x are the new linear point.
        NOTE: Vertical points are accouonted for because only the longitude goes up
    
    REMEMBER: Coordinate system is X=Longitude, Y=Latitude
        */
        public void setBoundingBox(LatLong p1, LatLong p2, double endpointBuffer,double width)
	{
            LatLong minPoint=p1,maxPoint=p2;
            double alpha =0;
            
            /*Compute a small buffer space between two segment points up and down.
            Purpose is to account for Sun shadow angles that are cased at angles happen to be from a building out of the bounding box
                            maxBoundingBuffer
                            .
                            .
                            P1
                            |
                            |
                            P2 -- Person Here
                            .                    /\
                            .                     \ Sun Beam direction
                            minBoundingBuffer      \
                                                    \SUN HERE*/          
       
            if(p1.getLongitude() - p2.getLongitude() == 0){//Vertical line, so cannot compute slope formula. latitude is the variance
                alpha = Math.PI/2;
                if(p1.getLatitude() > p2.getLatitude()){
                    minPoint = p2;
                    maxPoint = p1;
                }
                
                this.setMinBoundingBuffer(new LatLong(minPoint.getLatitude() - this.getDegreeVariance(),minPoint.getLongitude()));
                this.setMaxBoundingBuffer(new LatLong(maxPoint.getLatitude() + this.getDegreeVariance(),maxPoint.getLongitude()));
            }else
            {//Diagnal or horizontal lines
                if(p1.getLongitude() > p2.getLongitude()){
                    minPoint = p2;
                    maxPoint = p1;
                }
                
                double dx = maxPoint.getLongitude() - minPoint.getLongitude(),
                    dy = maxPoint.getLatitude() - minPoint.getLatitude(),
                    slopeM = dy/dx;
                
                this.setMinBoundingBuffer(new LatLong(slopeM*(minPoint.getLongitude() - this.getDegreeVariance() - minPoint.getLongitude()) + minPoint.getLatitude(),minPoint.getLongitude() - this.getDegreeVariance()));
                this.setMaxBoundingBuffer(new LatLong(slopeM*(maxPoint.getLongitude() + this.getDegreeVariance() - maxPoint.getLongitude()) + maxPoint.getLatitude(),maxPoint.getLongitude() + this.getDegreeVariance()));
                
                //Compute the arctan w.r.t. the slope of the line segmentline
                alpha = Math.atan(slopeM);
            }
           
            
            //Compute the Polygon that represents the bounding box
            double theta = Math.PI / 2 - alpha, distanceConversion = width*Constants.DEGREE_VARIANCE_PER_FEET;
            double newPointX1 = minBoundingBuffer.getLatitude() + distanceConversion * Math.sin(theta);
            double newPointY1 = minBoundingBuffer.getLongitude() - distanceConversion * Math.cos(theta);  
            
            double newPointX2 = minBoundingBuffer.getLatitude() - distanceConversion * Math.sin(theta);
            double newPointY2 = minBoundingBuffer.getLongitude() + distanceConversion * Math.cos(theta);

            double newPointX3 = maxBoundingBuffer.getLatitude() - distanceConversion * Math.sin(theta);
            double newPointY3 = maxBoundingBuffer.getLongitude() + distanceConversion * Math.cos(theta); 

            double newPointX4 = maxBoundingBuffer.getLatitude() + distanceConversion * Math.sin(theta);
            double newPointY4 = maxBoundingBuffer.getLongitude() - distanceConversion * Math.cos(theta);  

            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
            Coordinate[] coords = {
                new Coordinate(newPointY1, newPointX1),
                new Coordinate(newPointY4, newPointX4),
                new Coordinate(newPointY3, newPointX3),
                new Coordinate(newPointY2, newPointX2),
                new Coordinate(newPointY1, newPointX1)
            };
            
            setStartPoint(p1);
            setEndPoint(p2);
                
            LinearRing ring = geometryFactory.createLinearRing(coords);
            LinearRing holes[] = null; // use LinearRing[] to represent holes
            polygon = geometryFactory.createPolygon(ring, holes);
	}
        
        public void setStartPoint(LatLong p1) {
            this.start = p1;
        }
        
        public LatLong getStartPoint() {
            return this.start;
        }
        
        public void setEndPoint(LatLong p2) {
            this.end = p2;
        }
	
        public LatLong getEndPoint() {
            return this.end;
        }
                
        
	public double getDistance(LatLong p1, LatLong p2)
	{
		long R = 6379; // km
		double dLat = Math.toRadians(p2.getLatitude()-p1.getLatitude());
		double dLon = Math.toRadians(p2.getLongitude()-p1.getLongitude());
		double lat1 = Math.toRadians(p1.getLatitude());
		double lat2 = Math.toRadians(p2.getLatitude());

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		return  (R * c);
	}
        
	public LatLong getMinBoundingBuffer() {
		return minBoundingBuffer;
	}
	public void setMinBoundingBuffer(LatLong min) {
		this.minBoundingBuffer = min;
	}
	public double getDegreeVariance() {
		return degreeVariance;
	}
	public double getEndpointBuffer() {
		return endpointBuffer;
	}
	public void setEndpointBuffer(double feet) {
		this.endpointBuffer = feet;
                this.degreeVariance = Constants.DEGREE_VARIANCE_PER_FEET*this.endpointBuffer;
	}
	public LatLong getMaxBoundingBuffer() {
		return maxBoundingBuffer;
	}
	public void setMaxBoundingBuffer(LatLong max) {
		this.maxBoundingBuffer = max;
	}
}
