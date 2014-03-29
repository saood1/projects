package com.test.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements a runnable interface which is driven by multiple threads crawling urls in 
 * parallel
 */
public class Crawl implements Runnable{
	//Queue to hold scanned urls 
	private final Queue<String> queue = new LinkedList<String>();
	
	//Concurrent hashmap to store visited urls, this will be accessed by multiple threads
	private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
	
	//Max urls to crawl
	private final int urlCount;
	
    //Indicator to stop crawling when certain condition is met
    private volatile boolean stopCrawling = false;
	
    /**
     * Constructor taking the base url and the no of urls to crawl
     * @param baseURL
     * @param noOfURLSToCrawl
     */
    public Crawl(String baseURL, int noOfURLSToCrawl){
		queue.add(baseURL);
		urlCount = noOfURLSToCrawl;
	}
	
    /**
     * Runnable task operated by multiple threads
     * @param baseURL
     * @param noOfURLSToCrawl
     */
    public void run() {
		try{
			String strTemp = "";
			
			while (!stopCrawling) {
				String v = null;
				
				//The synchronized block is relatively small to reduce contention for locks and increase the crawl speed
				synchronized (queue) {
	        		while(queue.isEmpty() && !stopCrawling)
	        			queue.wait();
	        		
	        		v = queue.poll();
				}
				
				try{
					//We don't want to stream any more data if crawling is not needed
					if(stopCrawling) break;
					 
					//Read the contents of the web page from the URL
		        	URL my_url = new URL(v);
		        	BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
		        			        	
		        	while(null != (strTemp = br.readLine())){
		        		
		        		//If the pattern matches, then extract the url from the web page
		            	String url = RegExPatterns.getValidURLFromHREFTag(strTemp.trim());
		            	
		            	//Crawl as long the urlCount is reached
                        if(map.size()==urlCount){
                        	stopCrawling = true;
                        	break;
                        }
                        
		            	if(url!=null){
		            		url = RegExPatterns.replaceSpecialCharacters(url);
		            		String result = URLDecoder.decode(url, "UTF-8");
		            		
			            	//We don't want to re-visit already visited url
		            		while (!map.containsKey(result)) {
		            			map.putIfAbsent(result, result);
		            			
		            			//Obtain a lock on the queue to notify other threads
		    	            	synchronized (queue) {
		    	            		queue.add(result);
		    		            	queue.notify();
		    		            }
		    	            }	
		            	}
		            }
	            	br.close();
	            	
        			//Notify other threads to stop crawling if the queue is empty
	            	if(queue.isEmpty()){
	            		synchronized (queue){
    	            		queue.notifyAll();
    	            		stopCrawling = true;
	            		}
	            	}
	            }
	            catch (Exception e){
	    			synchronized (queue) {
	    				//A serious problem occured if the queue was empty and exception was caught here, we need to notify all threads to wake and stop crawling
            			if(queue.isEmpty()){
            				queue.notifyAll();
            				stopCrawling = true;
            				Logger.print(ResponseString.CRAWL_PROGRESS_STOPPED + e.getLocalizedMessage() + "\n");
            			}
            			else{
            				Logger.print(ResponseString.CRAWL_PROGRESS_INTERFERED + e.getLocalizedMessage() + "\n");
            			}
            		}
	    		}
	        }
		}
		catch (InterruptedException e){
			//Print any exceptions that occurred. This will cause the task to be terminated
			Logger.print(ResponseString.CRAWL_PROGRESS_STOPPED + e.getLocalizedMessage() + "\n");
		}
	}
    
    /**
     * A simple function to display the crawled urls by all threads
     */
    public void printCrawledURLS(){
    	for(String url: map.values())
    		Logger.print(url);
    }
    
    /**
     * Wrting the crawled results to a file
     * @param filePath
     * @throws IOException
     */
    public void writeCrawledResultsToFile(String filePath) throws IOException{
		FileWriter fw = new FileWriter(new File(filePath));
		BufferedWriter bw = new BufferedWriter(fw);
		for(String url: map.values()){
		    bw.write(url);
			bw.write("\n");
		}
		bw.close();
		fw.close();
	}
    
    /**
     * Get the size of the map
     * @return
     */
    public int getCrawledListSize(){
    	return map.size();
    }
}