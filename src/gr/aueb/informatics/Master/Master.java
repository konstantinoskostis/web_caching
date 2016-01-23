package gr.aueb.informatics.Master;



import java.net.ServerSocket;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author konstantinos
 */
public class Master {
    
    ServerSocket ss;
    public static Directory masterDirectory;
    
    public Master(){
        ss = null;
        masterDirectory = new Directory();
        initConnection();
    }
    
    private void initConnection(){
        try{
            ss = new ServerSocket(8000);
        }catch(Exception e){}
    }
    
    public void start() {
        System.out.println("Master started...");
        while (true) {
            try {
                new Thread(new MasterJob(ss.accept())).start();
            } catch (Exception e) {
                System.err.println("Cannot accept connection");
                System.exit(0);
            }
        }
    }
    
    public static void main(String args[]){
        new Master().start();
    }
    
}
