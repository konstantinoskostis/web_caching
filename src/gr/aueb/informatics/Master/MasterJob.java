package gr.aueb.informatics.Master;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import gr.aueb.informatics.common.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author konstantinos
 */
public class MasterJob extends Thread {

    Socket s; //the connected socket of a client
    ObjectInputStream input; //master reads 
    ObjectOutputStream output; //master writes
    Node node;
    boolean running;

    public MasterJob(Socket s) {
        try {
            this.s = s;
            input = new ObjectInputStream(s.getInputStream());
            output = new ObjectOutputStream(s.getOutputStream());
            running = true;
            System.out.println("Master initialized a new job");
        } catch (Exception e) {
            System.err.println("Master failed to initialize a new thread.");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Accepted a connection");
        try {
            Object received = input.readObject(); //read an object(command) from socket
            while (running) {
                if (received instanceof RegisterMessage) { //if a peer wants to register
                    RegisterMessage rm = (RegisterMessage) received;
                    handleRegisterMessage(rm);
                    //showDirectory();
                    System.out.println("Waiting for command...");
                    received = input.readObject();
                } else if (received instanceof GetMessage) { //if a peer asks for a webpage
                    GetMessage gm = (GetMessage) received;
                    handleGetMessage(gm);
                    //showDirectory();
                    System.out.println("Waiting for command...");
                    received = input.readObject();
                } else if (received instanceof UpdatedCacheMessage) {
                    UpdatedCacheMessage ucm = (UpdatedCacheMessage) received;
                    handleUpdatedCacheMessage(ucm);
                    //showDirectory();
                    System.out.println("Waiting for command...");
                    received = input.readObject();
                } else { //if a peer wants to disconnect
                    running = false;
                }

            }

        } catch (Exception e) {
            System.err.println("An error occured inside a thread");
            System.exit(0);
        } finally {
            try {
                handleDisconnectMessage(); //disconnect a peer
            } catch (Exception e) {
                System.err.println("Error while closing a connection");
            }
        }

    }

    private void handleRegisterMessage(RegisterMessage rm) throws IOException, ClassNotFoundException {
        System.out.println("I got a REGISTER message from :");

        Node n = rm.getNode();
        node = new Node(n); //keep the node for this thread
        synchronized (Master.masterDirectory) {
            Master.masterDirectory.addEntry(node);
        } //synchronized operation , multiple threads may want to write to directory simultaneously

        System.out.println(n.toString());

        output.writeUTF("OK");
        output.flush();

        System.out.println("Register message was handled succesfully!!!!!!");
    }

    private void handleGetMessage(GetMessage gm) throws IOException, ClassNotFoundException {
        String requestedWebpage = (String) gm.getMessage(); //the webpage requested
        System.out.println("Request for: "+requestedWebpage);
        //InetAddress clientIP = s.getInetAddress(); //the ip of the client that requested for the page
        ArrayList<PeerCharacteristics> peerList = new ArrayList<PeerCharacteristics>();
        int noExistanceCounter = 0; // counts how many peers do not have a page requested

        synchronized (Master.masterDirectory) {

            HashMap dir = Master.masterDirectory.getDirectory(); //Master's directory
            int allEntries = dir.size(); //all the entries in the directory
            Iterator it = dir.keySet().iterator(); //all the keys in the directory
            while (it.hasNext()) {
                Node temp = (Node) it.next();
                System.out.println("Looking at node: "+temp.getIp().getHostAddress());
                if (!temp.equals(node)) { //do not check the node that made the request
                    ArrayList<String> pages = (ArrayList<String>) dir.get(temp); //the pages that a node contains
                    if (pages != null) {
                        if (pages.contains(requestedWebpage)) {//search if requested page matches any page of this node
                            PeerCharacteristics pc = new PeerCharacteristics(temp);
                            peerList.add(pc);
                            System.out.println("Page found!!!");
                        } else {
                            ++noExistanceCounter;
                        }
                    } else { //pages
                        ++noExistanceCounter;
                    }
                }

            }//iterate through the dictionary

            if (noExistanceCounter == (allEntries - 1)) { //the page requested does not exist in the system then
                //then send a NOEXIST message
                NoExist no = new NoExist("Page does not exist");
                output.writeObject(no);
                output.flush();
                System.out.println("NOEXIST was sent!!!");
            } else {
                //send the list of IPs in a PeerList message to the peer that made the request
                PeerList listOfPeers = new PeerList(peerList);
                output.writeObject(peerList);
                output.flush();
                System.out.println("Peer list was sent!!!");
            }
        }
        System.out.println("GET message was handled succesfully.");
    }

    private void handleUpdatedCacheMessage(UpdatedCacheMessage ucm) {
        synchronized (Master.masterDirectory) {
            Master.masterDirectory.updateEntry(node,ucm.getMessage());
        }
        System.out.println("Master's Directory was updated!!!");
        //showDirectory();
    }

    private void handleDisconnectMessage() throws IOException {
        System.out.println("Client with ip " + node.getIp().getHostAddress() + " is diconnecting...");
        synchronized (Master.masterDirectory) {
            Master.masterDirectory.removeEntry(node);
        }
        this.input.close();
        this.output.close();
        this.s.close();

        System.out.println("Succesfully disconnected.");
    }

    private void showDirectory() {
        synchronized(Master.masterDirectory){
                Master.masterDirectory.showDirectory();
        }
    }
}
