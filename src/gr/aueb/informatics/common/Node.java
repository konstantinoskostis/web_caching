package gr.aueb.informatics.common;

import java.io.Serializable;
import java.net.InetAddress;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author konstantinos
 */
public class Node implements Serializable{
    
    /**
     * the name of the provider
     */
    private String provider;
    
    /**
     * LocationPoint describe the geographical location
     */

    private LocationPoint location;
   
   /**
    * the IP address of this node
    */
    private InetAddress ip;
    
    public Node(){
        
    }
    
    public Node(String provider,float longitude,float latitude,InetAddress ip){
        setProvider(provider);
        //this.location = new LocationPoint(longitude, latitude);
        setIp(ip);
        
        setLocation(longitude,latitude);
    }
    
    public Node(String provider,float longitude,float latitude){
        setProvider(provider);
        setLocation(longitude, latitude);
        //this.location = new LocationPoint(longitude, latitude);
    }
    
    public Node(Node other){
        setProvider(other.getProvider());
        setIp(other.getIp());
        setLocation(other.getLocation().getLongitude(), other.getLocation().getLatitude());
        //location = new LocationPoint(other.getLocation());
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public void setLocation(float longitude,float latitude){
        this.location = new LocationPoint(longitude, latitude);
    }
    
    public LocationPoint getLocation(){
        return location;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public InetAddress getIp() {
        return ip;
    }
    
    
    @Override
    public String toString(){
        String result = "{ IP: "+getIp()+"\tProvider: "+getProvider()
                +"\tLongitude: "+getLocation().getLongitude()+"\tLatitude: "+getLocation().getLatitude() +" }";
        return result;
    }
    
}
