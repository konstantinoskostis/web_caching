package gr.aueb.informatics.Master;

import gr.aueb.informatics.common.*;
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
 * 
 * This class represents the information that the server keeps
 * for every node. For every node there is a list of web pages.
 * This list is actually the cache of its node.
 */

public class Directory {
    
    /**
     * A HashMap data structure to store information
     */
    HashMap<Node,ArrayList<String>> directory;
    ArrayList<String> initial;
    
    public Directory(){
        directory = new HashMap<>();
        /*initial = new ArrayList<>();
        for(int i=0; i<3; i++){
            initial.add("EMPTY");
        }*/
    }
    
    /**
     * 
     * @param node , the node to add to directory
     */
    public void addEntry(Node node){
        if(!directory.containsKey(node)){
            directory.put(node,null);
        }
    }
    
    /**
     * 
     * @param node , the node that must be updated
     * @param list , the cache of the node
     */
    public void updateEntry(Node node,ArrayList<String> list){
        if(directory.containsKey(node)){
            directory.put(node, list); //update value
        }
    }
    
    public void removeEntry(Node node){
        if(directory.containsKey(node)){
            directory.put(node, null); //make it's value null
            directory.remove(node); //and remove the specified node from master's directory
        }
    }
    
    public HashMap getDirectory(){
        return this.directory;
    }
    
    public void showDirectory(){
        System.out.println("Entries: "+directory.size());
        System.out.println("-----------------------------------");
        Iterator it = this.directory.keySet().iterator();
        int entries = 1;
        while(it.hasNext()){
            System.out.println("Entry "+entries);
            System.out.println("--------------");
            Node temp = (Node)it.next();
            ArrayList<String> pages = this.directory.get(temp);
            System.out.println("Node: "+temp.toString());
            if(pages.equals(null)){
                System.out.println("Pages: None");
            }else{
                System.out.println("Pages: ");
                for(String s:pages)
                    System.out.print(" "+s+" ");
            }
            ++entries;
        }
    }
       
}
