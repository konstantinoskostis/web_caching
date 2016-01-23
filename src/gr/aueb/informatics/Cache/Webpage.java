package gr.aueb.informatics.Cache;


import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author konstantinos
 */
public class Webpage {
 
    /**
     * the name of the web page
     */
    private String name;
    
    /**
     * the content of a web page
     */
    private Vector<String> contentVector;
    private String content;
    
    /**
     * time accessed/when downloaded
     */
    private long time; 
    
    /**
     * howMany times was the page asked
     */
    private int getCounter;
    
    public Webpage(){
       setName("NO_NAME");
       setContent("");
       contentVector = new Vector<>();
       setGetCounter(0);
    }

    /**
     * 
     * @param name of web page
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return name of web page
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param content 
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 
     * @return 
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param contentVector is the a vector that contains lines of text
     */
    public void setContentVector(Vector<String> contentVector) {
        //this.contentVector = contentVector;
        if (!this.contentVector.isEmpty()) {
            this.contentVector.removeAllElements();
        }
        for (int i = 0; i < contentVector.size(); i++) {
            this.contentVector.add(i, contentVector.get(i));
        }
    }

    /**
     * 
     * @return all text lines
     */
    public Vector<String> getContentVector() {
        return this.contentVector;
    }
   
    /**
     * 
     * @return time that page was downloaded
     */
    public long getTime() {
        return time;
    }

    /**
     * 
     * @param time the page was downloaded
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * 
     * @return the number of times a page was requested
     */
    public int getGetCounter() {
        return getCounter;
    }

    /**
     * 
     * @param getCounter the number of times a page was requested
     */
    public void setGetCounter(int getCounter) {
        this.getCounter = getCounter;
    }
    
    /**
     * 
     * @return a string of all the text lines in a web page
     */
    private String concat(){
        String c = "";
        if(getContentVector().size() > 0){
            for(int i=0; i<getContentVector().size(); i++)
                c += this.contentVector.get(i)+"\n";
        }else{
            c = "NO CONTENT";
        }
        return c;
    }
    
    /**
     * not used
     */
    public void putContentTogether(){ //concatenates vector to a string
        for(int i=0; i<getContentVector().size(); i++)
            this.content += this.contentVector.get(i)+"\n";
    }
    
    /**
     * 
     * @param other another object of Webpage type
     */
    public void copy(Webpage other){
        setName(other.getName());
        setContentVector(other.getContentVector());
        setGetCounter(other.getGetCounter());
        setTime(other.getTime());
    }
    
    public String toString(){
        String result="";

        result += "Name: "+getName() +"\nContent: \n"+concat();
        return result;
    }
}
