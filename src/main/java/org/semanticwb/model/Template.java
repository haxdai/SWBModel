/*
 * SemanticWebBuilder es una plataforma para el desarrollo de portales y aplicaciones de integración,
 * colaboración y conocimiento, que gracias al uso de tecnología semántica puede generar contextos de
 * información alrededor de algún tema de interés o bien integrar información y aplicaciones de diferentes
 * fuentes, donde a la información se le asigna un significado, de forma que pueda ser interpretada y
 * procesada por personas y/o sistemas, es una creación original del Fondo de Información y Documentación
 * para la Industria INFOTEC, cuyo registro se encuentra actualmente en trámite.
 *
 * INFOTEC pone a su disposición la herramienta SemanticWebBuilder a través de su licenciamiento abierto al público (‘open source’),
 * en virtud del cual, usted podrá usarlo en las mismas condiciones con que INFOTEC lo ha diseñado y puesto a su disposición;
 * aprender de él; distribuirlo a terceros; acceder a su código fuente y modificarlo, y combinarlo o enlazarlo con otro software,
 * todo ello de conformidad con los términos y condiciones de la LICENCIA ABIERTA AL PÚBLICO que otorga INFOTEC para la utilización
 * del SemanticWebBuilder 4.0.
 *
 * INFOTEC no otorga garantía sobre SemanticWebBuilder, de ninguna especie y naturaleza, ni implícita ni explícita,
 * siendo usted completamente responsable de la utilización que le dé y asumiendo la totalidad de los riesgos que puedan derivar
 * de la misma.
 *
 * Si usted tiene cualquier duda o comentario sobre SemanticWebBuilder, INFOTEC pone a su disposición la siguiente
 * dirección electrónica:
 *  http://www.semanticwebbuilder.org.mx
 */
package org.semanticwb.model;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.SWBPlatform;
import org.semanticwb.model.base.*;
import org.semanticwb.platform.SemanticObject;

// TODO: Auto-generated Javadoc
/**
 * The Class Template.
 */
public class Template extends TemplateBase 
{
    
    /** The siteid. */
    private String siteid=null;
    
    //private ArrayList parts;
    /** The objects. */
    private HashMap objects = new HashMap();

    //Para que se muestre funcionalidad de facebook como el boton, agregar las siguientes 2 líneas a los templates (Jorge)
    //<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
    //<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
    /** The DEFAU l_ html. */
public static final String DEFAUL_HTML="<template method=\"setHeaders\" Content-Type=\"text/html\"  response=\"{response}\" />\n" +
                           "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                           "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                           "<head>\n" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n" +
                            "<title>\n" +
                            "   <TOPIC METHOD=\"getDisplayName\" LANGUAGE=\"{user@getLanguage}\"/>\n" +
                            "</title>\n" +

                            "\n<!--\n" +
                            "Nota: No olvides incluir estos scripts en tus plantillas hechas por fuera\n" +
                            "***************************************************************************\n" +
                            "***************************************************************************\n" +
                            "-->\n" +
                            "<link rel='stylesheet' type='text/css' media='all' href='{webpath}/swbadmin/js/dojo/dijit/themes/soria/soria.css' />\n"+
                            "<link rel='stylesheet' type='text/css' media='all' href='{webpath}/swbadmin/css/swb_portal.css' />\n"+
                            //"<style type=\"text/css\">\n" +
                            //"    @import \"<webpath/>/swbadmin/js/dojo/dijit/themes/nihilo/nihilo.css\";\n" +
                            //"    @import \"<webpath/>/swbadmin/js/dojo/dijit/themes/tundra/tundra.css\";\n" +
                            //"    @import \""+ctx+"/swbadmin/js/dojo/dijit/themes/soria/soria.css\";\n" +
                            //"    @import \""+ctx+"/swbadmin/css/swb_portal.css\";\n" +
                            //"</style>\n" +
                            "<script type=\"text/javascript\">\n" +
                            "    var djConfig = {\n" +
                            "        parseOnLoad: true,\n" +
                            "        isDebug: false\n" +
                            "    };\n" +
                            "</script>\n" +
                            "<script type=\"text/javascript\" src=\"{webpath}/swbadmin/js/dojo/dojo/dojo.js\"></script>\n" +
                            //"<script type=\"text/javascript\" src=\"{webpath}/swbadmin/js/swb_.js\"></script>\n" +
                            "<script type=\"text/javascript\" src=\"{webpath}/swbadmin/js/swb.js\"></script>\n" +
                            "<!--\n" +
                            "***************************************************************************\n" +
                            "***************************************************************************\n" +
                            "-->\n\n" +
                            "</head>\n " +
                            "<body>\n   " +
                            "<div>\n" +
                            "   <Content></Content>\n" +
                            "</div>\n " +
                            "</body>\n" +
                            "</html>";

    
    /**
     * Instantiates a new template.
     * 
     * @param base the base
     */
    public Template(SemanticObject base)
    {
        super(base);
        objects.put("template", this);
        objects.put("user", new User(null));
        objects.put("topic", new WebPage(null));
        objects.put("request", HttpServletRequest.class);
        objects.put("response", HttpServletResponse.class);
        objects.put("webpath", SWBPlatform.getContextPath());
//        objects.put("distpath", SWBPortal.getDistributorPath());
    }
    
    /**
     * Gets the web site id.
     * 
     * @return the web site id
     */
    public String getWebSiteId()
    {
        if(siteid==null)
        {
            siteid=getWebSite().getId();
        }
        return siteid;
    }    
    
    /**
     * Reload.
     */
    public void reload() { }

    /**
     * Gets the file name.
     * 
     * @param version the version
     * @return the file name
     */
    public String getFileName(int version)
    {
        String ret=null;
        VersionInfo info=getLastVersion();
        while(info!=null && info.getVersionNumber()!=version)
        {
            info=info.getPreviousVersion();
        }
        if(info!=null)
        {
            ret=info.getVersionFile();
        }
        return ret;
    }
}
