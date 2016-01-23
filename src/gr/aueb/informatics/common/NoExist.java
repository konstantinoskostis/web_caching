/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.common;


/**
 *
 * @author konstantinos
 */
public class NoExist extends Message{
    
    public NoExist(Object message){
        super(message);
    }
    
    @Override
    public String getMessage(){
        return (String)super.getMessage();
    }
    
}
