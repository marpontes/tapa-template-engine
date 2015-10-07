package com.oncase.tapa.engine;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.catalina.core.ApplicationContextFacade;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

public class TapaTemplateHelper {
	
	public final static String TAPA_ID = "tapa";
	
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
	
	public static String getTapaConfirmComment(){
		
		return PentahoSystem.get(IPluginManager.class, PentahoSessionHolder.getSession()).getPluginSetting(
				TAPA_ID, "tapa-confirm-comment", "<!--nothing_at_all-->").toString();
		
	}
	
	public static Map<String,Object> getConfigContext(){
		
		JSONObject templateConfig = getTemplateCurrentConfig();
		if (templateConfig == null)
			return null;
		
		Map<String,Object> textTagsMap= new HashMap<String,Object>();
		
		JSONArray textTags = (JSONArray) templateConfig.get("textTags");
		if (textTags == null)
			return null;
		
		Iterator<JSONObject> tagsIterator = textTags.iterator();
		
		while(tagsIterator.hasNext()){
			JSONObject tag = tagsIterator.next();
			String key = tag.get("tag").toString();
			String value = tag.get("value").toString();
			textTagsMap.put(key, value);
		}
		return textTagsMap;
		
		
	}
	
	public static JSONObject getTemplateCurrentConfig(){
		
		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();
		final String currentTemplate = getCurrentTemplate();
		
		if(currentTemplate==null)
			return null;
		
		try {
			
			Object obj = parser.parse(new FileReader(
					getTemplatesPath()+currentTemplate+"/template-config.json"));
			jsonObject = (JSONObject) obj;
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return jsonObject;
	}
	
	/**
	 * @return A string with the name (folder) of the currently active template
	 */
	public static String getCurrentTemplateRootUrl(String template){
		
		final String contextPath = 
				( (ApplicationContextFacade) 
						PentahoSystem.getApplicationContext().getContext())
						.getContextPath();
		
		return contextPath+	"/content/tapa/resources/templates/" + template;
	}
	

}
