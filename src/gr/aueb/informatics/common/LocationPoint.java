/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.common;

import java.io.Serializable;

/**
 *
 * @author konstantinos
 */
public class LocationPoint implements Serializable{
       
    private float longitude;
    private float latitude;
    
    public LocationPoint(float longitude,float latitude){
        setLongitude(longitude);
        setLatitude(latitude);
    }
    
    public LocationPoint(LocationPoint other){
        setLongitude(other.getLongitude());
        setLatitude(other.getLatitude());
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    
    /**
     * 
     * @param p1 , Location of first peer
     * @param p2 , Location of second peer
     * @return the Euclidean distance between p1 and p2
     */
    public static float getEuclideanDistance(LocationPoint p1 , LocationPoint p2){
        double term1 = Math.pow((double)((double)p1.getLongitude()-(double)p2.getLongitude()),2); //(x1-y1)^2
        double term2 = Math.pow((double)((double)p1.getLatitude()-(double)p2.getLatitude()),2); //(x2-y2)^2
        float distance = (float)Math.sqrt(term1+term2);
        return distance;
    }
    
}
