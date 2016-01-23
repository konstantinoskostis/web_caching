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
public class DisconnectMessage extends Message implements Serializable{
    
    public DisconnectMessage(Object o){
        super(o);
    }
}
