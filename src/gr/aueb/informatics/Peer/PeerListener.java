package gr.aueb.informatics.Peer;


import java.net.ServerSocket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author konstantinos
 */
public class PeerListener extends Thread{

    ServerSocket peerListener;
    
    public PeerListener() {
        try{
            peerListener = new ServerSocket(7000);
        }catch(Exception e){
            System.err.println("Could not initialize the Peer Listener!!!");
            System.exit(0);
        }
    }

    
    
    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Start listening for other peers...");
        while(true){
            
            try{
               new Thread(new PeerService(peerListener.accept())).start(); 
            }catch(Exception e){
                
            }
            
        }//end while
        
    }//end run
    
}
