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
public class RegisterMessage extends Message implements Serializable{
    
    public RegisterMessage(Object o){
        super(o);
    }
    
    public Node getNode(){
        return (Node)getMessage();
    }
}
