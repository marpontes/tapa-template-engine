package com.oncase.tapa.engine;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pentaho.platform.engine.core.system.PentahoSystem;

public class TapaTemplateHelper {
	
	public static String getTemplatesPath(){
		
		return PentahoSystem.getApplicationContext().getSolutionPath("") 
				+ "/system/tapa/resources/templates/";
	}

	public static String getCurrentTemplate(){
		String currentTemplate="";
		
        JSONParser parser = new JSONParser();
        
        try {
 
            Object obj = parser.parse(new FileReader(
            		getTemplatesPath()+"config.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            currentTemplate = (String) jsonObject.get("currentTemplate");
            
 
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return currentTemplate;
	} 

}
