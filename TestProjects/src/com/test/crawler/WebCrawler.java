package com.test.crawler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is a demonstration of a simple yet efficient crawler interm's of speed
 */
public class WebCrawler { 
	//create threads as per the processors for best efficiency
	private final static int MAX_THREADS = Runtime.getRuntime().availableProcessors();
	
	//Max urls to fetch
	private final static int MAX_URLS = 1000;
	
	//File location to store the results
	private final static String filePath = System.getProperty("user.dir") + File.separator + "results.txt";
	
    public static void main(String[] args) throws IOException, InterruptedException { 
        try{
        	long startTime = System.currentTimeMillis();
        	
        	//Check if no arguments were passed to this program
        	if(args == null || args.length<1)
        		throw new IllegalArgumentException(ResponseString.NO_ARGUMENT_PASSED);
        	
        	//Check if the argument passed is a valid url
        	String s = args[0];
        	if(!RegExPatterns.isValidURL(s))
        		throw new IllegalArgumentException(ResponseString.INCORRECT_ARGUMENT_TYPE);
        	
        	//Create an object of Crawl class
        	Crawl c = new Crawl(s, MAX_URLS);
        	Logger.print(ResponseString.CRAWLING_STARTED);
        	
        	//Spawn threads
        	ExecutorService exec = Executors.newFixedThreadPool(MAX_THREADS);
        	for(int i=0;i<MAX_THREADS;i++){
        		exec.execute(c);
        	}
        	
        	Logger.print(ResponseString.THREADS_CRAWLING.replace("$count", String.valueOf(MAX_THREADS)));
        	exec.shutdown();
        	
        	//Wait until all threads complete there tasks - technically for crawling 1000 urls, it should complete 
        	//in a few seconds, 24hr period is just an optimistic value
        	while (!exec.awaitTermination(24L, TimeUnit.HOURS));
        	
        	long endTime = System.currentTimeMillis();
        	long timeDiffInSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime-startTime);
        	
        	//Finished crawling, print the list and the timetaken
        	int resultCount = c.getCrawledListSize();
        	if(resultCount==0){
        		Logger.print(ResponseString.NO_RESULTS_GENERATED);
        	}
        	else{
        		c.writeCrawledResultsToFile(filePath);
            	Logger.print(ResponseString.CRAWLING_FINISHED.replace("$count", String.valueOf(resultCount)).replace("$file", filePath));
            	Logger.print(ResponseString.CRAWLING_TIME.replace("$count", String.valueOf(timeDiffInSeconds)));	
        	}
        }
        catch(Exception e){
        	Logger.print(e.getMessage());
        }
    }
}
