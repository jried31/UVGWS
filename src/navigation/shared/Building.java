package navigation.shared;

import com.vividsolutions.jts.geom.Coordinate;
import org.opengis.feature.Feature;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Eric
 */
public class Building {

    private long id;
    private double height;
    private org.opengis.geometry.BoundingBox bounds = null;
    private LatLong centerPoint = null;
    
    DirectPosition lowerCorner = null;
    DirectPosition upperCorner = null;
        
    public LatLong getCenterPoint(){
        return this.centerPoint;
    }
    public BoundingBox getBounds(){
        return bounds;
    }
    
    public Coordinate[] getCorners(){
        LatLong lc = this.getLowerCorner();
        LatLong uc = this.getUpperCorner();

        Coordinate[] coords  = new Coordinate[] {
            new Coordinate(lc.getLongitude(), lc.getLatitude()), 
            new Coordinate(lc.getLongitude(), uc.getLatitude()),
            new Coordinate(uc.getLongitude(), uc.getLatitude()), 
            new Coordinate(uc.getLongitude(), lc.getLatitude()),
            new Coordinate(lc.getLongitude(), lc.getLatitude()) };
        return coords;
    }
    public LatLong getLowerCorner(){
        double []lc = lowerCorner.getCoordinate();
        return new LatLong(lc[1],lc[0]);
    }
    
    public LatLong getUpperCorner(){
        double []uc = upperCorner.getCoordinate();
        return new LatLong(uc[1],uc[0]);
    }
    
    public Building (Feature feature)
    {
        bounds = feature.getDefaultGeometryProperty().getBounds();
        lowerCorner = bounds.getLowerCorner();
        upperCorner = bounds.getUpperCorner();

        double []lc = lowerCorner.getCoordinate();
        double []uc = upperCorner.getCoordinate();
        
        this.centerPoint = new LatLong(.5*lc[1] + .5*uc[1], .5*lc[0] + .5*uc[0]);
        this.id = Long.valueOf(feature.getProperty("BLD_ID").getValue().toString());
        this.height = Double.valueOf(feature.getProperty("HEIGHT").getValue().toString());
        
        //b = area.parseMultipolygon(feature);
        //b.setGeometry(buildingBounds);
    }
    
    public Building() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
