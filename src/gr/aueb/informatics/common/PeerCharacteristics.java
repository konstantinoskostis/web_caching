/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.common;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author konstantinos
 */
public class PeerCharacteristics implements Serializable{
    
    private Node node;
    
    public PeerCharacteristics(Node node){
        setNode(node);
    }
    
      public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

}
