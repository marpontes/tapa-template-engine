package com.oncase.tapa.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * The engine that is reponsible for compiling templates for 
 * <a target="_blank" href="http://github.com/marpontes/tapa">Tapa Plugin.</a>
 * <p>This class will be instantiated as a spring-bean from plugin.spring.xml
 * <p>The way to retrieve it is by calling the method getBean() from 
 * IPluginManager.<p>
 * Once the object is retrieved, it can be operated using reflection.
 * This way, you won't need to import its classes to the code.
 * 
 * @author <a target="_blank" href="http://about.me/marpontes">
 * {@literal @}marpontes</a>
 */
public class TapaTemplateFactory {
	
	private FileLoader loader;
	private PebbleEngine engine;
	
	/**
	 * The constructor receives no parameter, once the factory is supposed to know
	 * by configuration, where are the templates and what is their suffix .
	 */
	public TapaTemplateFactory()  {
		
		loader = new FileLoader();
		loader.setSuffix(getTemplatesSuffix());
		loader.setPrefix(TapaTemplateHelper.getTemplatesPath());
		
		engine = new PebbleEngine(loader);
		engine.setTemplateCache(null);
		
	}
	
	/**
	 * This method is used to retrieve a String that contains the required template.
	 * However, by providing no context, it compiles with no variables.
	 * 
	 * @param templateName 	The template name - usually the filename without extension
	 * @return				The template without any variables applied
	 * @throws IOException 
	 * @throws PebbleException 
	 */
	public String getTemplate() throws PebbleException, IOException {
		
		return getTemplate(null);
		
	}
	
	/**
	 * This method is used to retrieve a String that contains the required template.
	 * When supplied a map of variables and their values, it returns the compiled template.
	 * 
	 * @param templateName 	The template name - usually the filename without extension
	 * @param context		A map of variables and their values for compilation
	 * @param t				Template to be returned. If null will return the current enabled 
	 * @return				The compiled template with all variables considered
	 * @throws PebbleException 
	 * @throws IOException 
	 */
	public String getTemplate(Map<String, Object> context, String templateName) 
			throws PebbleException, IOException{
		
		final String templateRootUrl = 
				TapaTemplateHelper.getCurrentTemplateRootUrl(templateName);
		
		PebbleTemplate template = engine.getTemplate(templateName);
		Writer writer = new StringWriter();
		
		
		if(context==null){
			template.evaluate(writer);
		}else{
			Map<String,Object> templateConfigContext = TapaTemplateHelper.getConfigContext(
					templateName	);
			
			if(templateConfigContext != null && templateConfigContext.size() > 0)
				context.putAll(templateConfigContext);
			
			context.put("TAPA_TEMPLATE_ROOT_URL", templateRootUrl);
			template.evaluate(writer, context);
		}
		
		return writer.toString()+"\n"+TapaTemplateHelper.getTapaConfirmComment();
		
	}
	
	/**
	 * This method is used to retrieve a String that contains the required template.
	 * When supplied a map of variables and their values, it returns the compiled template.
	 * 
	 * @param templateName 	The template name - usually the filename without extension
	 * @param context		A map of variables and their values for compilation 
	 * @return				The compiled template with all variables considered
	 * @throws PebbleException 
	 * @throws IOException 
	 */
	public String getTemplate(Map<String, Object> context) 
			throws PebbleException, IOException{
		return getTemplate(context, TapaTemplateHelper.getCurrentTemplate());
	} 
	
	/**
	 * @return The templates suffix - files extension
	 */
	private String getTemplatesSuffix(){
		
		return "/index.html";
		
	}

	
}
