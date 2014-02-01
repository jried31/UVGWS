package navigation.shared;

import java.util.Date;


/**
 * @author Jerrid
 * This class is used to insert the exact UV data.
 * It contains all the necessary information !
 */
public class UVData {

	private double uva1=-1;
	private double uva2=-1;
	private double uvb1=-1;
	private double uvb2=-1;
	private LatLong point;
	private Date time;
	private String streetName;
	
	public LatLong getPoint() {
		return point;
	}
	public void setPoint(LatLong point) {
		this.point = point;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	
	
	public double getUva1() {
		return uva1;
	}
	public void setUva1(double uva1) {
		this.uva1 = uva1;
	}
	public double getUva2() {
		return uva2;
	}
	public void setUva2(double uva2) {
		this.uva2 = uva2;
	}
	public double getUvb1() {
		return uvb1;
	}
	public void setUvb1(double uvb1) {
		this.uvb1 = uvb1;
	}
	public double getUvb2() {
		return uvb2;
	}
	public void setUvb2(double uvb2) {
		this.uvb2 = uvb2;
	}
	public String toString()
	{
		return this.getTime()+","+this.getPoint().getLatitude()+","+this.getPoint().getLongitude()+","+this.getUva1()+","+this.getUva2()
				+","+this.getUvb1()+","+this.getUvb2()+","+this.getStreetName();
		
	}
}
