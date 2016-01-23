/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.common;

import java.util.ArrayList;

/**
 *
 * @author konstantinos
 */
public class UpdatedCacheMessage extends Message{
    
    public UpdatedCacheMessage(Object message){
        super(message);
    }
    
    @Override
    public ArrayList<String> getMessage(){
        return (ArrayList<String>)super.getMessage();
    }
}
