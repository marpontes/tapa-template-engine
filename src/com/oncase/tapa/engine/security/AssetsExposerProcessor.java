package com.oncase.tapa.engine.security;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pentaho.platform.api.engine.ObjectFactoryException;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.objfac.AggregateObjectFactory;
import org.pentaho.platform.util.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSourceEditor;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;


/**
 * This class injects rules to the bean <i>filterInvocationInterceptor</i>
 * in order to enable unauthenticated users to access some biserver URLs.
 * 
 * It implements a Spring BeanFactoryPostProcessor which takes advantage of the
 * moment when it's called to perform its job. 
 * 
 * Application contexts can auto-detect BeanFactoryPostProcessor beans in their 
 * bean definitions and apply them before any other beans get created.
 * 
 * @author <a target="_blank" href="http://about.me/marpontes">
 * {@literal @}marpontes</a>
 */
public class AssetsExposerProcessor implements BeanFactoryPostProcessor {
	
	/**
	 * Method that is called when the PostProcessor is detected.
	 * 
	 * This method does the main job. It gets the XML definition from
	 * the file /system/applicationContext-spring-security.xml,
	 * then creates a new FilterInvocationDefinitionSource and replaces
	 * the old definition with this new one.
	 * 
	 * @throws BeansException
	 * 
	 * @todo 	try not to rely on files to get bean definition
	 * @todo	replace the deprecated methods for the task
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {

		FilterSecurityInterceptor interceptor;
		String ods;
		
		try {
			
			interceptor = getFilterInvocationInterceptor();
			ods = getObjectDefinitionSource();
			
		} catch (ObjectFactoryException e) {
			Logger.error(BeanFactoryPostProcessor.class,
					"Error retrieving bean with id \"filterInvocationInterceptor\" from "
					+ "AggregateObjectFactory");
			return;
		} catch (DocumentException e) {
			Logger.error(BeanFactoryPostProcessor.class,
					"Error retrieving filterInvocationInterceptor.objectDefinitionSource"
					+ "from applicationContext-spring-security.xml");
			return;
		}

		
		boolean varsNotNull = (interceptor != null && ods != null);

		if(varsNotNull){
			
			String newOds = getReplacedODS(ods);

			final FilterInvocationDefinitionSource fids =
						getFilterInvocationDefinitionSource(newOds);
			
			interceptor.setObjectDefinitionSource(fids);
			Logger.info(AssetsExposerProcessor.class, 
					"applicationContext-spring-security.xml successfully patched.");
			
		}


	}

	/**
	 * This method returns a new FilterInvocationDefinitionSource that is
	 * built here from the source provided as a String parameter.
	 * 
	 * @param	odsVal	objectDefinitionSource text to be used.
	 * @return			a brand new FilterInvocationDefinitionSource 
	 */
	private FilterInvocationDefinitionSource getFilterInvocationDefinitionSource(
			String odsVal) {
		
		FilterInvocationDefinitionSourceEditor editor = 
				new FilterInvocationDefinitionSourceEditor();
		
		editor.setAsText(odsVal);

		final FilterInvocationDefinitionSource fids = 
				(FilterInvocationDefinitionSource) editor.getValue();
		return fids;
		
	}

	/**
	 * This method gets the FilterSecurityInterceptor from PentahoSystem.
	 * This returned object is where lives our objectDefinitionSource that
	 * is going to be replaced.
	 * 
	 * @return		The PentahoSystem's filterInvocationInterceptor
	 * @throws		ObjectFactoryException
	 */
	private FilterSecurityInterceptor getFilterInvocationInterceptor()
			throws ObjectFactoryException{

		FilterSecurityInterceptor interceptor=null;

		AggregateObjectFactory objFac = (AggregateObjectFactory) PentahoSystem.getObjectFactory();

		interceptor = (FilterSecurityInterceptor) objFac.get(
				FilterSecurityInterceptor.class,
				"filterInvocationInterceptor",
				PentahoSessionHolder.getSession()
				);

		return interceptor;

	}

	/**
	 * 
	 * @param url The url where the document is located at the disk
	 * @return A XML Document read from the provided string url
	 * @throws DocumentException
	 */
	private Document parse(String url) throws DocumentException {

		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(url));
		return document;

	}
	
	/**
	 * 
	 * @return A string containing the Pentaho System's objectDefinitionSource 
	 * which is part of the file <i>system/applicationContext-spring-security.xml
	 * </i>
	 * 
	 * @throws DocumentException
	 */
	private String getObjectDefinitionSource() throws DocumentException{

		final String url = PentahoSystem.getApplicationContext().getSolutionRootPath()+
				"/system/applicationContext-spring-security.xml";
		
		

		final String qry = "//*[@id='filterInvocationInterceptor']//*[@name='objectDefinitionSource']";
		String ods = null;

		Document document = parse(url);
		Node node = document.selectSingleNode(qry);

		if(node!=null && node.selectNodes("*").size() > 0){
			ods = ((Node) node.selectNodes("*").iterator().next()).getStringValue();
		}
		
		return ods;
	}

	/**
	 * 
	 * @param odsVal The current objectDefinitionSource that is going to be 
	 * patched
	 * 
	 * @return The patched objectDefinitionSource with our rules.
	 * 
	 * @todo get our rules from a configuration file
	 * @todo this is an ugly solution to the problem
	 */
	private String getReplacedODS(String odsVal){
		odsVal = odsVal
				.replace(
						"CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON", 
						"CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON\n"
						+ "\\A/content/tapa/resources/templates/([\\w\\-\\_]+)/assets/.*\\Z=Anonymous,Authenticated"
						);
		return odsVal;
	}





}
