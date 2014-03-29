package com.test.crawler;

/**
 * This class defines the static error codes and there respective messages
 */
public final class ResponseString {
	private ResponseString(){}
	
	//Error messages
	public static String NO_ARGUMENT_PASSED = "No arguments were passed to the program, please check the documentation and retry";
	public static String INCORRECT_ARGUMENT_TYPE = "This program accepts a valid URL in the format http(s)://www.<url>.com, please correct the same and retry";
	public static String CRAWL_PROGRESS_STOPPED = "A serious problem occured due to which the crawler stopped: ";
	public static String CRAWL_PROGRESS_INTERFERED = "An error occured while crawling: ";
	public static String NO_RESULTS_GENERATED = "Crawler generated 0 results, the base url might be having a problem, please use a different one and try again\n";
	
	//General messages
	public static String CRAWLING_STARTED = "Crawling started..\n";
	public static String THREADS_CRAWLING = "There are $count threads crawling in parallel, please wait for them to finish\n";
	public static String CRAWLING_FINISHED = "Crawling finished, the crawler generated $count results are stored in $file \n";
	public static String CRAWLING_TIME = "Time taken to crawl : $count seconds \n";
}