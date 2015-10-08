package com.oncase.tapa.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.PluginBeanException;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.jsp.messages.Messages;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.oncase.tapa.engine.TapaTemplateFactory;


@Path("/tapa/api/templateservice")
public class TapaApi {
	
	TapaTemplateFactory factory;
	
	public TapaTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(TapaTemplateFactory factory) {
		this.factory = factory;
	}

	@GET
	@Path("/preview")
	@Produces("text/html")
	public Response preview(@Context UriInfo info) 
			throws PebbleException, IOException  {
		
		Map<String, Object> tapaContext = new HashMap<String, Object>();
		
		int year = (new java.util.Date()).getYear() + 1900;
		tapaContext.put(  "TAPA_TITLE"         , Messages.getInstance().getString("UI.PUC.TITLE")  );
		tapaContext.put(  "TAPA_LOGIN_TITLE"   , Messages.getInstance().getString("UI.PUC.LOGIN.TITLE")  );
		tapaContext.put(  "TAPA_USERNAME"      , Messages.getInstance().getString("UI.PUC.LOGIN.USERNAME")  );
		tapaContext.put(  "TAPA_PASSWORD"      , Messages.getInstance().getString("UI.PUC.LOGIN.PASSWORD")  );
		tapaContext.put(  "TAPA_LOGIN"         , Messages.getInstance().getString("UI.PUC.LOGIN.LOGIN")  );
		tapaContext.put(  "TAPA_EVAL_LOGIN"    , Messages.getInstance().getString("UI.PUC.LOGIN.EVAL_LOGIN")  );
		tapaContext.put(  "TAPA_ADMIN_USER"    , Messages.getInstance().getString("UI.PUC.LOGIN.ADMIN_USER")  );
		tapaContext.put(  "TAPA_BUSINESS_USER" , Messages.getInstance().getString("UI.PUC.LOGIN.BUSINESS_USER")  );
		tapaContext.put(  "TAPA_GO"            , Messages.getInstance().getString("UI.PUC.LOGIN.GO")  );
		tapaContext.put(  "TAPA_COPYRIGHT"     , Messages.getInstance().getString("UI.PUC.LOGIN.COPYRIGHT", String.valueOf(year))  );
		tapaContext.put(  "TAPA_ERROR_CAPTION" , Messages.getInstance().getString("UI.PUC.LOGIN.ERROR.CAPTION")  );
		tapaContext.put(  "TAPA_LOGIN_ERROR"   , Messages.getInstance().getString("UI.PUC.LOGIN.ERROR")  );
		tapaContext.put(  "TAPA_LOGIN_OK"      , Messages.getInstance().getString("UI.PUC.LOGIN.OK")  );
		tapaContext.put(  "TAPA_LOGGEDIN"      , false  );
		
		String template = info.getQueryParameters().getFirst("template");
		String out = factory.getTemplate(tapaContext, template);
		return Response.status(200)
				.header("Content-Type", "text/html")
				.header("charset", "utf8").entity(out).build();
	}


}