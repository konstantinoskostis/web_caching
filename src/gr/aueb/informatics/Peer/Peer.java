package gr.aueb.informatics.Peer;

import gr.aueb.informatics.common.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import gr.aueb.informatics.Cache.MemoryCache;
import gr.aueb.informatics.Statistics.PeerStatistics;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author konstantinos
 */
public class Peer {

    /**
     * instance of class Node has IP , provider and Geolocation information
     */
    private Node me;
    /*peer-server communication*/
    /**
     * a socket connection between this peer and the Server
     */
    private Socket connection;
    /**
     * the input stream of the socket peer reads from this one
     */
    private ObjectInputStream input;
    /**
     * the output stream of the socket peer writes to this one
     */
    private ObjectOutputStream output;
    /*peer-peer communication*/
    private Socket connectionToPeer;
    private ObjectInputStream inputFromPeer;
    private ObjectOutputStream outputToPeer;
    /**
     * the IP of the Server so that the peer can connect to it
     */
    private String serverIP;
    /**
     * array of providers
     */
    private String[] providers = {"Otenet", "Comcast", "AT&T", "Time warner cable", "Optimum online"};

    /*the cache of this peer*/
    static MemoryCache memoryCache;
    /*statistics of a peer*/
    PeerStatistics statistics;
    /*scanner to read*/
    Scanner scanner;
    Scanner scanner2;
    /*choices for downloading from peers*/
    final static int RANDOM_PEER = 1;
    final static int PROVIDER_NAME = 2;
    final static int GEOGRAPHICALLY_NEAREST_PEER = 3;
    
    /*boolean that shows if this peer asks again the master after a peer connection
     it avoids miscalculating durations of request*/
    boolean askMasterAgain;

    /**
     *
     * @param serverIP , the address of the server to connect Constructor
     */
    public Peer(String serverIP) {
        me = null;
        connection = null;
        input = null;
        output = null;
        connectionToPeer = null;
        inputFromPeer = null;
        outputToPeer = null;
        this.serverIP = serverIP;
        memoryCache = new MemoryCache();
        statistics = new PeerStatistics();
        scanner = new Scanner(System.in);
        scanner2 = new Scanner(System.in);
        askMasterAgain = false;
    }

    /**
     *
     * @param ip , address(of server) to connect, initialize a connection with
     * server, create socket , initialize input and output stream
     */
    private void initConnection(String ip) {
        try {
            connection = new Socket(ip, 8000);
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
        } catch (Exception e) {
            System.err.println("Could not create socket connection with Server");
            System.exit(0);
        }
    }

    /**
     * Random data generation
     *
     * @return node's data
     */
    private Node generateNodeData() {
        Node node = new Node();
        //set the provider of this node
        int sizeOfProviders = providers.length;
        Random r = new Random();
        int providerIndex = r.nextInt(sizeOfProviders);
        node.setProvider(providers[providerIndex]);

        //generate longitute
        float longitude = r.nextFloat() * 150f;
        //generate latitude
        float latitude = r.nextFloat() * 60f;
        //create location
        node.setLocation(longitude, latitude);
        //set ip(discover local ip)
        try {
            Socket s = new Socket("google.com", 80); 
            InetAddress ip = s.getLocalAddress();
            s.close();
            node.setIp(ip);
        } catch (Exception e) {
        }


        return node;
    }

    /**
     * sends a REGISTER message to the server an answer is returned
     */
    private void register() {
        try {
            me = generateNodeData();
            RegisterMessage reg = new RegisterMessage(me);
            output.writeObject(reg);
            output.flush();

            String message = input.readUTF();
            if (message.equalsIgnoreCase("OK")) {
                System.out.println("You are now registered to Master");
            }

        } catch (Exception e) {
            System.err.println("Could not send REGISTER message.");
            System.exit(1);
        }
    }

    /**
     * Disconnect from the Master
     */
    private void disconnect() {
        try {
            //first notify the thread that updates content of cache , to stop
            MemoryCache.notifyContentUpdater();
            //then disconnect from Master
            DisconnectMessage dm = new DisconnectMessage("DISCONNECT");
            output.writeObject(dm);
            output.flush();

            output.close();
            input.close();
            connection.close();
            System.out.println("Succesfully disconnected!!!");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error while disconnecting");
            System.exit(1);
        }

    }

    /**
     * Disconnects from a peer
     */
    private void disconnectFromPeer() {
        try {
            DisconnectMessage dm = new DisconnectMessage("DISCONNECT");
            outputToPeer.writeObject(dm);
            outputToPeer.flush();
            outputToPeer.close();
            inputFromPeer.close();
            connectionToPeer.close();
            System.out.println("Succesfully disconnected from peer!");
        } catch (Exception e) {
            System.err.println("Error while disconnecting from a peer");
            System.exit(1);
        }
    }

    public void printInfo() {
        System.out.println(me.toString());
    }
    
    /**
     *
     * @param host , the host to connect to
     * @param port , if port == 80 downloads from web server else download from
     * a peer
     * @param requestedPage , the page that a peer wants to download
     * @return
     */
    private Vector<String> getPage(String host, int port, String requestedPage) {

        if (port == 80) { //ask the requested page from web server

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

        } else { //ask for requestedPage from a peer , port 7000
            Vector<String> result = null;
            try {
                /*connect to a peer*/
                connectionToPeer = new Socket(InetAddress.getByName(host), port);
                outputToPeer = new ObjectOutputStream(connectionToPeer.getOutputStream());
                inputFromPeer = new ObjectInputStream(connectionToPeer.getInputStream());
                /*send the request to the peer*/
                GetMessage gm = new GetMessage(requestedPage);
                outputToPeer.writeObject(gm);
                outputToPeer.flush();
                /*get answer from peer*/
                Object answer = inputFromPeer.readObject();

                if (answer instanceof NoExist) {
                    //then ask again the master in case this peer has changed its cache
                    System.out.println("It seems there was a cache miss in peer with ip " + host);
                    System.out.println("Asking master again...");
                    //askMasterAgain = true;
                    getCommand(requestedPage);
                } else {
                    result = new Vector<String>((Vector<String>) answer);
                    System.out.println("Succesfully downloaded content of requested page from peer with ip " + host);
                }

            } catch (Exception e) {
            }

            return result;

        }


    }//connects to a webserver or a peer and gets a webpage

    /**
     * main menu of a client
     */
    public void start() {

        //init thread to listen for other peers
        new Thread(new PeerListener()).start();

        //connect to Master
        initConnection(serverIP);
        register();

        //start interacting with the user
        displayCommandsMenu();
        String line = scanner.nextLine(); //read line from user
        while (!line.equalsIgnoreCase("DISCONNECT")) { //if user does not disconnect
            if (line.startsWith("get")) { // a GET command consists of 'GET' and a string representing the name of the webpage
                try {
                    String[] getComponents = line.split(" ");
                    String webpage = getComponents[1]; //store the name of the webpage
                    if (MemoryCache.exists(webpage)) {
                        System.out.println(webpage + " already exists!!!\nYou can view the page using 'show' command");
                    } else {
                        getCommand(webpage); //ask master to see if the requested page exists in the system
                    }

                } catch (Exception e) {
                    System.err.println("An error occured when sending GET request.\n"
                            + "Your client will automatically disconnet from Master.");
                    disconnect();
                }
            } else if (line.equalsIgnoreCase("show")) {
                showCommand();
            } else if (line.startsWith("open")) { //maybe show the requested page from a browser
            } else if (line.startsWith("statistics")) {
                showPeerStatistics();
            } else {
                System.err.println("The command you gave cannot be recognised.");
            }



            displayCommandsMenu();
            line = scanner.nextLine();
        }
        disconnect();
    }

    /**
     *
     * @param webpage that a peer wants to download
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void getCommand(String webpage) throws IOException, ClassNotFoundException, InterruptedException {
        GetMessage gmessage = new GetMessage(webpage); //create a GET Message to send to Master asking for the above webpage
        output.writeObject(gmessage);
        output.flush();
        System.out.println("Get message sent!!!");

        Object answer = input.readObject();
        System.out.println("Answer read!!!");

        if (answer instanceof NoExist) {
            System.out.println("The page you requested does not exist in the system.\n"
                    + "The page will be received by the origin server...");
            //Thread.sleep(5000);
            String parts[] = webpage.split("/");
            long startTime = System.currentTimeMillis(); //time before the request 
            Vector<String> contents = getPage(parts[0], 80, parts[1]);
            long endTime = System.currentTimeMillis(); //time when page is downloaded
            System.out.println("Page downloaded");
            long duration = endTime - startTime;
            statistics.updateStatistics(duration);
            //System.out.println("Statistics updated");
            memoryCache.addPage(webpage, contents, endTime);  //update cache
            sendUpdatedCacheMessage();
        } else { // PeerList message
            System.out.println("The page exists in some other clients.\n"
                    + "A list of peers will be downloaded.\n"
                    + "How do you want to download the page?\n"
                    + "1.From a random peer in the list\n"
                    + "2.From a peer with the same provide\n"
                    + "3.From geographically nearest peer(euclidean distance)\n"); //if none of the peers has the same provider as me , download using longitude and latitude
            int choice = scanner2.nextInt(); //read choice from user
            while ((choice != 1) && (choice != 2) && (choice != 3)) {
                System.err.println("This choice does not exist!!!\n"
                        + "Please enter your choice:");
                choice = scanner2.nextInt();
            }
            
            PeerList listOfPeers = new PeerList((ArrayList<PeerCharacteristics>) answer); //get peer of list
            handlePeerList(listOfPeers, choice, webpage); //hanle the list
        }

    }

    /**
     * shows content of cache
     */
    private void showCommand() {
        memoryCache.showMemoryCache();
    }

    /**
     * shows statistics of a peer
     */
    private void showPeerStatistics() {
        statistics.showStatistics();
    }
    
    /**
     * displays the commands that a user can give
     */
    private void displayCommandsMenu(){
        System.out.println("***************************************"
                + "\n Please enter one of the following commands:"
                + "\n *********************************************"
                + "\n get 'url of page'(without the quotes, in order to download a webpage)"
                + "\n show (in order to see everything in your cache)"
                + "\n statistics (in order to see some statistics of your client)"
                + "\n disconnect (in order to disconnect from the system)"
                + "\n************************************************************************"
                + "\n Enter command: ");
    }

    /**
     * sends updated cache to master
     * @throws IOException 
     */
    private void sendUpdatedCacheMessage() throws IOException {
        System.out.println("Sending cache update...");
        ArrayList<String> pages = new ArrayList(memoryCache.getPages());
        UpdatedCacheMessage ucm = new UpdatedCacheMessage(pages);
        output.writeObject(ucm);
        output.flush();
        System.out.println("Cache update was sent!!!");
    }

    private void handlePeerList(PeerList list, int choice, String webpage) {
        ArrayList<PeerCharacteristics> pc = (ArrayList) list.getMessage();
        Node chosen = null;
        if (choice == RANDOM_PEER) {
            Random r = new Random();
            int peerIndex = r.nextInt(pc.size()); //1/n probability to all the peers
            chosen = pc.get(peerIndex).getNode();
        } else if (choice == PROVIDER_NAME) { //if any of the peers have the same provider then choose one of them
            boolean sameProvider = false;
            for(PeerCharacteristics peerCh : pc){
                if(peerCh.getNode().getProvider().equalsIgnoreCase(me.getProvider())){
                    sameProvider = true;
                    chosen = peerCh.getNode();
                    System.out.println("A peer with the same provider found!");
                    break;
                }
            }
            if(sameProvider == false){
                System.out.println("None of the peers has the same provider.\n"
                        + "Will try to download from the geographically nearest peer.");
                chosen = geographicallyNearestNode(pc);
            }
            
        } else if (choice == GEOGRAPHICALLY_NEAREST_PEER) {//download from peer with min(euclidean distance)
            chosen = geographicallyNearestNode(pc);
        }
        
        System.out.println("Peer with ip " + chosen.getIp().getHostAddress() + " is chosen!");
        long startTime = System.currentTimeMillis();
        Vector<String> contents = getPage(chosen.getIp().getHostAddress(), 7000, webpage);
        long endTime = System.currentTimeMillis();
        long duration = endTime-startTime;
        if (contents != null) {
            memoryCache.addPage(webpage, contents, endTime);
            statistics.updateStatistics(duration);
            try {
                sendUpdatedCacheMessage();
                disconnectFromPeer();
            } catch (Exception e) {
                System.err.println("Error while sending update message!!");
            }
        }

    }

    /**
     * @param list is the list of peers that Master sends to a peer when the
     * peer makes a request
     * @return
     */
    private ArrayList<PeerCharacteristics> sortPeersByEuclidean(ArrayList<PeerCharacteristics> list) {
        ArrayList<PeerCharacteristics> sorted = new ArrayList<PeerCharacteristics>(list);
        ArrayList<Float> distances = new ArrayList<Float>(); //holds eucldean distances
        //find euclidean distances
        for (int i = 0; i < sorted.size(); i++) { //compute all distances from me to all other peers
            distances.add(LocationPoint.getEuclideanDistance(me.getLocation(), sorted.get(i).getNode().getLocation()));
        }
        //sort
        for (int pass = 0; pass < sorted.size() - 1; pass++) {
            for (int j = 0; j < sorted.size() - 1; j++) {
                if (distances.get(j) > distances.get(j + 1)) {

                    //swap distances
                    float d = distances.get(j + 1);
                    distances.set(j + 1, distances.get(j));
                    distances.set(j, d);

                    //swap peers
                    PeerCharacteristics temp = sorted.get(j + 1);
                    sorted.set(j + 1, sorted.get(j));
                    sorted.set(j, temp);
                }
            }
        }
        return sorted;
    }//sorts all peers by euclidean distance , descending order

    private Node geographicallyNearestNode(ArrayList<PeerCharacteristics> pc){
        //sort the list according the euclidean distance, from smallest to greater
        ArrayList<PeerCharacteristics> sorted = sortPeersByEuclidean(pc);
        return sorted.get(0).getNode();
    }
    
    public static String getLocalIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr
                            .nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;

    }

    public static void main(String args[]) {
        System.out.println("Enter Master's IP:");
        Scanner sc = new Scanner(System.in);
        String masterIP = sc.nextLine();
        Peer p = new Peer(masterIP);
        p.start();
    }
}
