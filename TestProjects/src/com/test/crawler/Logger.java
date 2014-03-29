package com.test.crawler;

/**
 * Simple Logger, more functionality can be added with new format rules for the message
 */
public final class Logger {
	//No need to create an instance of this class.
	private Logger(){}
	
	public static void print(String message){
		System.out.println(message);
	}
}
