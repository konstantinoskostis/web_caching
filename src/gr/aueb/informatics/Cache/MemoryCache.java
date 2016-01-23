package gr.aueb.informatics.Cache;


import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author konstantinos
 */
public class MemoryCache {

    /**
     * maximum number of pages in the cache
     */
    private final static int MAX_PAGES = 3;
    
    /**
     * memoryCache keeps in an array all the pages that a peer has
     */
    private static Webpage[] memoryCache;
    
    /**
     * shows how many pages are in cache
     */
    private static int position;
    
    /**
     * shows if client is disconnected from the system
     * in this case we must stop updating the cache
     */
    public static boolean clientDisconnected;

    /**
     * constructor, initializes the array of web-pages and sets position to -1 ,
     * indicating that there no pages in cache
     */
    public MemoryCache() {
        memoryCache = new Webpage[MAX_PAGES];
        for(int i=0; i<memoryCache.length; i++){
            memoryCache[i] = new Webpage();
        }
        position = -1;
        clientDisconnected = false;
        System.out.println("Starting content update thread...");
        new Thread(new CacheContentUpdater()).start();
    }
    
    /**
     * 
     * @return the memory cache as an array
     */
    public static Webpage[] getMemoryCache(){
        return memoryCache;
    }
    
    /**
     * @return index of current page
     * if position is -1 then there are no items in the cache
     * if position is 2 then cache is full
     */
    public static int getPosition(){
        return position;
    }
    
    /**
     * 
     * @return ArrayList containing the names of web-pages that exist in cache 
     */
    public ArrayList<String> getPages(){
        ArrayList<String> pages = new ArrayList<String>();
        for(Webpage w : memoryCache)
            pages.add(w.getName());
        return pages;
    }

    /**
     * 
     * @return true if cache is full
     */
    public boolean cacheIsFull() {
        return (position == MAX_PAGES-1);
    }

    /**
     * 
     * @return returns true if cache is empty
     */
    public static boolean cacheIsEmpty() {
        return (position == -1);
    }

    /**
     * 
     * @param page , web-page to add in cache , maybe it will replace another page
     * @param content , content of new web-page
     * @param time , Current time in milliseconds
     */
    public void addPage(String page, Vector<String> content, long time) {
        if (!cacheIsFull()) { //if cache is not full
            System.out.println("Cache is not full!!!");
            //if page exists , then update counter,contents and time
            if (exists(page)) {
                System.out.println("Page found");
                int ind = getIndexOf(page);
                memoryCache[ind].setGetCounter(memoryCache[ind].getGetCounter() + 1);
                memoryCache[ind].setContentVector(content);
                memoryCache[ind].setTime(time);
            } else { //if page does not exist, create new one
                System.out.println("Creating new webpage");
                Webpage w = new Webpage();
                w.setName(page);
                w.setContentVector(content);
                w.setTime(time);
                w.setGetCounter(1);
                ++position;
                memoryCache[position] = w;
            }
            
        } else { //cache is full apply replacement algorithm
            System.out.println("Cache is full!!!\n");
            if (exists(page)) { //if page exists , then update counter,contents and time
                System.out.println("Page found");
                int ind = getIndexOf(page);
                memoryCache[ind].setGetCounter(memoryCache[ind].getGetCounter() + 1);
                memoryCache[ind].setContentVector(content);
                memoryCache[ind].setTime(time);
            } else { //replace page using replacement algorithm
                int index = replacementAlgorithm();
                System.out.println("Page at index "+index+" will be replaced");
                //create a new webpage and copy everything to memoryCache[index]
                System.out.println("Creating new webpage");
                Webpage w = new Webpage();
                w.setName(page);
                w.setContentVector(content);
                w.setTime(time);
                w.setGetCounter(1);
                memoryCache[index].copy(w);
            }  
        }
    }
    
    /**
     * @param name, the page to search in cache
     * @return true if page exists in cache
     */
    public static boolean exists(String name){
        for(Webpage w: memoryCache){
            if(w.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param name , the page to search in cache
     * @return the index of page in cache [0-2] or -1 if it does not exist
     */
    private static int getIndexOf(String name){
        int index = -1;
        for(int i=0; i<memoryCache.length; i++){
            if(memoryCache[i].getName().equalsIgnoreCase(name)){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * it may choose LRU or LFU replacement policy
     * @return an integer in [0-2] that shows which page to replace 
     */
    private int replacementAlgorithm() {
        int indexToReturn;
        if ((memoryCache[0].getGetCounter() == memoryCache[1].getGetCounter() && memoryCache[1].getGetCounter() == memoryCache[2].getGetCounter()) //eg: 5,5,5
                || (memoryCache[0].getGetCounter() > memoryCache[1].getGetCounter() && memoryCache[1].getGetCounter() == memoryCache[2].getGetCounter())//eg:5,2,2
                || (memoryCache[1].getGetCounter() > memoryCache[0].getGetCounter() && memoryCache[0].getGetCounter() == memoryCache[2].getGetCounter())//eg:2,5,2
                || (memoryCache[2].getGetCounter() > memoryCache[0].getGetCounter() && memoryCache[0].getGetCounter() == memoryCache[1].getGetCounter())//eg:2,2,5
                ) {
            indexToReturn = LRU();
            System.out.println("LRU replacement algorithm executed!!!");
        } else {
            indexToReturn = LFU();
            System.out.println("LFU replacement algorithm executed!!!");
        }
        return indexToReturn;
    }

    /**
     * LFU: replace the page that has been used the minimum of times
     * @return an integer in [0-2] that shows which page to replace 
     */
    private int LFU() {
        int minPos = 0;
        for (int i = 1; i < 3; i++) {
            if (memoryCache[i].getGetCounter() < memoryCache[minPos].getGetCounter()) {
                minPos = i;
            }
        }
        return minPos;
    }

    /**
     * if all the pages have all the same frequency,
     * then look at time and replace the page has not been recently used
     * @return 
     */
    private int LRU() {
        int minPos = 0;
        for (int i = 1; i < 3; i++) {
            if (memoryCache[i].getTime() < memoryCache[minPos].getTime()) {
                minPos = i;
            }
        }
        return minPos;
    }//if there is a tie , look at time , find the one that has not been used recently

    public File fetch(String name){
        if(!name.contains(".html")){
            name = name + ".html";
        }
        return new File(name);
    }
    
    /**
     * shows contents of cache
     */
    public void showMemoryCache() {
        for (Webpage w : memoryCache) {
            System.out.println(w.toString());
        }
    }
    
    /**
     * 
     * @param page , to search in cache
     * @return content of page if this exists in cache
     */
    public static Vector<String> getPage(String page){
        if(exists(page)){
            Vector<String> result = new Vector(memoryCache[getIndexOf(page)].getContentVector());
            return result;
        }
        return null;
    }
    
    public static void notifyContentUpdater(){
        clientDisconnected = true;
        System.out.println("Notifying content update thread!!!");
    }
    
}
