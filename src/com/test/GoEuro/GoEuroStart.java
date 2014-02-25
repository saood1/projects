package com.test.GoEuro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * The main class which begins the execution
 */
public class GoEuroStart {
	private static String JSON_API = "https://api.goeuro.com/api/v1/suggest/position/en/name/";
	
	public static void main(String[] args) {
		try{
			//Check for null or empty string
			if(args == null || args.length ==0)
				throw new InValidArgumentsException(ResponseString.NO_ARGUMENT_PASSED);
			
			String searchStr = args[0];
			String url = JSON_API + searchStr;
			
			//Check for argument type
			if(!isAlpha(searchStr))
				throw new InValidArgumentsException(ResponseString.INCORRECT_ARGUMENT_TYPE);
			
			//Create a JSONProcessor object to start processing the JSON String
			JSONProcessor jp = new JSONProcessor(readJSONAPI(url));
	        jp.processJSONResponse();
	    }
		catch (Exception e){
			Logger.print(e.getMessage());
		}
	}
	
	/**
	 * This function overrides the need for validating an https url
	 * @throws Exception
	 */
	private static void createTrustManager() throws Exception{
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        
        //Install the all-trusting trust manager
	    try {
	    	SSLContext sc = SSLContext.getInstance("TLS");
	        sc.init(null, trustAllCerts, new SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } 
	    catch (Exception e) {
	    	throw e;
	    }
	}
	
	
	/**
	 * This function reads the response from the URL and creates a JSON Object
	 * @param searchStr
	 * @return
	 * @throws Exception
	 */
	private static String readJSONAPI(String searchStr) throws Exception{
		Logger.print("Fetching JSON response from " + searchStr);
		
		//Bypass the SSL certificate verification, we can remove this function if the certificates have been added to JVM certificates files
		createTrustManager();
		URL url = new URL(searchStr);
        URLConnection yc = url.openConnection();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine = "";
        StringBuilder sb = new StringBuilder();
        
		while ((inputLine = in.readLine()) != null) 
        	sb.append(inputLine);
        
        in.close();
        return sb.toString();
    }
	
	/**
	 * This function validates if the argument passed is other than alphabets
	 * @param name
	 * @return
	 */
	private static boolean isAlpha(String name) {
	    return name.matches("[a-zA-Z]+");
	}
	
	/**
	 * User defined Exception class to notify any invalid arguments passed 
	 */
	private static class InValidArgumentsException extends CustomException{
		public InValidArgumentsException(String mess) {
			super(mess);
		}
	}
}
