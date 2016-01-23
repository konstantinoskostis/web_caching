/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.Cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author konstantinos
 */
public class CacheContentUpdater extends Thread{
    
    public CacheContentUpdater(){
        
    }
    
    /**
     * this thread sleeps for 30 sec
     * then wakes up and refreshes every 
     * page in the cache
     */
    @Override
    public void run(){
        while(MemoryCache.clientDisconnected == false){
            try {
                Thread.sleep(30000); //30 sec
                if(!MemoryCache.cacheIsEmpty()){
                    for(int i=0; i<=MemoryCache.getPosition(); i++){
                        if(!MemoryCache.getMemoryCache()[i].getName().equalsIgnoreCase("NO_NAME")){
                            contentUpdate(MemoryCache.getMemoryCache()[i].getName(), i);
                        }
                    }
                   
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CacheContentUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Stoping thread of content update...");
    }
    
    /**
     * the actual refresh/update method of pages
     * @param url
     * @param index 
     */
    private void contentUpdate(String url,int index){
        
        try{
            String [] components = url.split("/");
            Vector<String> contents = getPage(components[0],80,components[1]); //get new content
            long endTime = System.currentTimeMillis();
            MemoryCache.getMemoryCache()[index].setContentVector(contents);
            MemoryCache.getMemoryCache()[index].setTime(endTime);
        }catch(Exception e){
            
        }
    }
    
    /**
     * actually asks from a web server a certain page
     * @param host 
     * @param port
     * @param requestedPage
     * @return 
     */
    private Vector<String> getPage(String host, int port, String requestedPage) {


            Vector<String> v = new Vector<String>(); //a vector to fill in webpage as lines of text
            PrintWriter pw = null; //to write to socket
            BufferedReader br = null; //to read from socket

            try {

                InetAddress address = InetAddress.getByName(host); //get the address of the specified host/web server
                Socket socketWithWebServer = new Socket(address, port);//connect to the specified web server to port 80
                pw = new PrintWriter(socketWithWebServer.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(socketWithWebServer.getInputStream(), "UTF-8"));
                pw.println("GET /" + requestedPage + " HTTP/1.0\r\n"); //send the request
                pw.println("\r\n");

                String line = "";
                boolean more = true;
                while (more) {
                    line = br.readLine();
                    if (line != null) {
                        v.add(line); //add the next line to our vector
                        //System.out.println(line);
                    } else {
                        more = false;
                    }
                }//read the response from web server

                //close everything
                pw.close();
                br.close();
                socketWithWebServer.close();
            } catch (Exception e) {
                System.out.println("Error while getting webpage");
            }
            return v;

    }
    
}
