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
public class Message implements Serializable{
    
    Object message;
    
    public Message(Object message){
        setMessage(message);
    }
    
    public void setMessage(Object message){
        this.message = message;
    }
    
    public Object getMessage(){
        return this.message;
    }
}
