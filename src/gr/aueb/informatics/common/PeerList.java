/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.common;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author konstantinos
 */
public class PeerList extends Message{
    
    public PeerList(Object message){
        super(message);
    }
    /*
    public ArrayList<InetAddress> getPeerList(){
        return (ArrayList<InetAddress>)getMessage();
    }*/
    
}
