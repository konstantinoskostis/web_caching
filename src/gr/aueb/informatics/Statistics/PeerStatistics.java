/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.informatics.Statistics;

import java.util.ArrayList;

/**
 *This class represents statistics of a peer/node
 * @author konstantinos
 */
public class PeerStatistics {
    
    private int totalRequests;
    private float mean;
    private float sigma; //the population standard deviation
    private ArrayList<Integer> requests;
    private ArrayList<Long> durationsOfRequests;
    private ArrayList<Long> avgDurationsOfRequests;
    
    public PeerStatistics(){
        totalRequests = 0;
        requests = new ArrayList<>();
        durationsOfRequests = new ArrayList<>();
        avgDurationsOfRequests = new ArrayList<>();
    }
    
    private void increaseRequests(){
        ++totalRequests;
    }

    private void insertDuration(long duration){
        requests.add(totalRequests);
        durationsOfRequests.add(duration);
    }
    
    private void updateAvgDurations(){
        long sum = 0;
        
        for(int i=0; i<durationsOfRequests.size(); i++){
            int j = i+1;
            sum = sum + durationsOfRequests.get(i);
            avgDurationsOfRequests.add(i,sum/j);
        }
    }
    
    private void computeMean(){
        long sum = 0;
        for(int i=0; i<durationsOfRequests.size(); i++)
            sum += durationsOfRequests.get(i);
        mean = (float)sum/durationsOfRequests.size();
    }
    
    public float getMean(){
        return mean;
    }
    
    private void computeSigma(){
        long sum = 0;
        for(int i=0; i<durationsOfRequests.size(); i++){
            long time = durationsOfRequests.get(i);
            sum += Math.pow((time-getMean()), 2);
        }
        sigma = (float)(Math.sqrt(new Double(sum))/Math.sqrt(new Double(durationsOfRequests.size())));
    }
    
    public float getSigma(){
        return sigma;
    }
    
    public void updateStatistics(long duration){
        increaseRequests();
        insertDuration(duration);
        updateAvgDurations();
        computeMean();
        computeSigma();
    }
    
    public void showStatistics(){
        System.out.println("Total requests: "+totalRequests);
        for(int i=0; i<requests.size(); i++){
            System.out.println("Request "+requests.get(i) + ":\tDuration: "+durationsOfRequests.get(i) + ":\tAverage Duration: "+avgDurationsOfRequests.get(i));
        }
        System.out.println("Mean: "+getMean());
        System.out.println("Populated standard deviation: "+getSigma());
    }
}
