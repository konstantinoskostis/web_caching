package gr.aueb.informatics.Peer;

import gr.aueb.informatics.common.GetMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import gr.aueb.informatics.common.NoExist;
import gr.aueb.informatics.Cache.MemoryCache;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author konstantinos
 */
public class PeerService extends Thread{
    
    Socket peerSocket;
    ObjectInputStream input;
    ObjectOutputStream output;
    boolean running;
    
    public PeerService(Socket peerSocket){
        
        try {
            this.peerSocket = peerSocket;
            input = new ObjectInputStream(peerSocket.getInputStream());
            output = new ObjectOutputStream(peerSocket.getOutputStream());
            running = true;
        } catch (Exception e) {
        }
        
    }
    
    @Override
    public void run(){
        
        try {
            Object received = input.readObject();
            while (running) {
                if (received instanceof GetMessage) {
                    GetMessage gm = (GetMessage) received;
                    handleGetMessage(gm);
                    
                }else{
                    running = false;
                }
            }
            
            //dummy
            
        }catch(IOException | ClassNotFoundException e){
            System.err.println("An error occured while serving a peer!!!");
            try {
                output.writeObject("Error");
                output.flush();
            } catch (IOException ex) {
                Logger.getLogger(PeerService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        finally{
            handleDisconnect();
        }
        
    }
    
    private void handleGetMessage(GetMessage gm){
        
        try{
            String request = (String)gm.getMessage();
            Vector<String> result = MemoryCache.getPage(request);
            if(result == null){
                NoExist no = new NoExist("Does not exist");
                output.writeObject(no);
                output.flush();
            }else{ //send content of page
                output.writeObject(result);
                output.flush();
            }
        }catch(Exception e){
            
        }
        
    }
   
    private void handleDisconnect(){
        try{
            this.output.close();
            this.input.close();
            this.peerSocket.close();
        }catch(Exception e){
            
        }
    }
    
    
}
