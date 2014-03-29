package com.test.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegExPatterns class having built in pattern matching strings and methods for crawler
 */
public final class RegExPatterns {
	//Pattern matcher string to extract the urls from href html tag 
	private final static Pattern href_url_pattern = Pattern.compile("href=\"(http.*?)\"", Pattern.CASE_INSENSITIVE);
	
	//Pattern matcher string to validate the urls
	private final static Pattern url_pattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", Pattern.CASE_INSENSITIVE);
		
	//Pattern matcher string to ignore urls containing the file extension
	private final static String ignore = ".*(gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|ppt|PPT|mpg|MPG|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS|asp|ASP|xxx|XXX|yyy|YYY|cs|CS|dll|DLL|refresh|REFRESH|htm|html|xml)$";
	
	//No need to create an instance of this class.
	private RegExPatterns(){}
	
	/**
	 * Check if the URL string passed to this function is valid or not
	 * @param urlString
	 * @return
	 */
	public static boolean isValidURL(String urlString){
		//If the pattern matches, then extract the urls from the web page
    	Matcher matcher = url_pattern.matcher(urlString);
    	return matcher.find();
    }
	
	/**
	 * Extract the URL string after scanning the HREF html tag
	 * @param urlText
	 * @return
	 */
	public static String getValidURLFromHREFTag(String urlText){
		Matcher matcher = href_url_pattern.matcher(urlText);
		if(!urlText.matches(ignore) && matcher.find()){
			String result = matcher.group(1).trim();
			if(!result.matches(ignore))
				return result;
		}
		return null;
	}
	
	/**
	 * This function is used for cleaning the url
	 * @param urlText
	 * @return
	 */
	public static String replaceSpecialCharacters(String urlText){
		urlText = urlText.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		int index = urlText.lastIndexOf("/");
		int len = urlText.length() - 1;
		
		//we don't want to return the url like www.example.com/, instead remove the last "/"
		if(index == len)
			urlText = urlText.substring(0, index);
		
		return urlText;
	}
}
