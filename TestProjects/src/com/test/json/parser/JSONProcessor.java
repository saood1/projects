package com.test.json.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * A singleton web service class which tracks players and there scores for each level
 */
public class JSONProcessor{
	private final String filePath = System.getProperty("user.dir");
	private JSONObject jsonObject;
	
	public JSONProcessor(String jsonStr){
	     //Create a JSON object from the parsed string
        jsonObject = (JSONObject) JSONSerializer.toJSON(jsonStr);  
	}
	
	public void processJSONResponse() throws Exception{
		StringBuilder sb = new StringBuilder();
		
		try{
			Logger.print("Parsing JSON response ...");
			
			//Check if the json object is null or its contents are null
			if(jsonObject == null || jsonObject.isNullObject())
				throw new JSONParserException(ResponseString.JSON_PARSING_ERROR);
			
			JSONArray results = jsonObject.getJSONArray(JSONKeys.RESULTS);
			
			//Check if the results JSON object is empty
			if(results.isEmpty())
				throw new CSVFileCreationFailureException(ResponseString.NO_CSV_FILE_CREATED);
			
			//Get a handle to the iterator object to loop through the child elements of results
			Iterator<JSONObject> it = results.iterator();
			while(it.hasNext()){
				JSONObject next = it.next();
			
				//Read the child elements of the "result" keys
				addCSVString(next.getString(JSONKeys.RESULTS__TYPE), sb, true);
				addCSVString(next.getString(JSONKeys.RESULTS_ID), sb, true);
				addCSVString(next.getString(JSONKeys.RESULTS_NAME), sb, true);
				addCSVString(next.getString(JSONKeys.RESULTS_TYPE), sb, true);
				
				//Read the child elements of the "geo_position" keys
				JSONObject geo = next.getJSONObject(JSONKeys.RESULTS_GEO_POSITION);
				addCSVString(geo.getString(JSONKeys.GEO_POSITION_LATITUDE), sb, true);
				addCSVString(geo.getString(JSONKeys.GEO_POSITION_LONGTITUDE), sb, false);
			}
			
			//Done parsing, now create and store the contents in the file
			File csvFile = createCSVFile();
			writeToFile(csvFile, sb.toString());
			Logger.print("Congratulations!! All transactions completed");
		}
		catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * Create a new CSF file, if the file is not already present and return the handle
	 * @return
	 * @throws IOException
	 */
	private File createCSVFile() throws IOException{
		String fileName = "GoEuro.csv";
		String path = filePath + File.separator + fileName;
		Logger.print("Creating a CSV file " + path);
		
		File f = new File(path);
		if(!f.exists())
			f.createNewFile();
		
		return f;
	}
	
	/**
	 * Write the contents to a file using the File handle object
	 * @param file
	 * @param contents
	 * @return
	 * @throws IOException
	 */
	private boolean writeToFile(File file, String contents) throws IOException{
		Logger.print("Writing the contents to the CSV file ...");
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(contents);
		bw.close();
		
		return true;
	}
	
	/**
	 * This functions dictates the format of the string
	 * @param message
	 * @param sb
	 * @param addComma
	 */
	private void addCSVString(String message, StringBuilder sb, boolean addComma){
		sb.append(message);
		if(addComma)
			sb.append(",");
		else
			sb.append("\n");
	}
	
	
	/**
	 * A custom exception class to notify the client for JSON parsing issues
	 */
	private class JSONParserException extends CustomException{
		public JSONParserException(String mess) {
			super(mess);
		}
	}
	
	/**
	 * A custom exception class to notify the client issues when creating a CSV file as output
	 */
	private class CSVFileCreationFailureException extends CustomException{
		public CSVFileCreationFailureException(String mess) {
			super(mess);
		}
	}
	
	
}
