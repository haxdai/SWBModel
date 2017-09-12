/*
 * SemanticWebBuilder es una plataforma para el desarrollo de portales y aplicaciones de integración,
 * colaboración y conocimiento, que gracias al uso de tecnología semántica puede generar contextos de
 * información alrededor de algún tema de interés o bien integrar información y aplicaciones de diferentes
 * fuentes, donde a la información se le asigna un significado, de forma que pueda ser interpretada y
 * procesada por personas y/o sistemas, es una creación original del Fondo de Información y Documentación
 * para la Industria INFOTEC, cuyo registro se encuentra actualmente en trámite.
 *
 * INFOTEC pone a su disposición la herramienta SemanticWebBuilder a través de su licenciamiento abierto al público ('open source'),
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.semanticwb.SWBPlatform;
import org.semanticwb.platform.SemanticClass;
import org.semanticwb.platform.SemanticLiteral;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;


   /**
   * SelectMultipleTree 
   */
public class SelectMultipleTree extends org.semanticwb.model.base.SelectMultipleTreeBase 
{
    public SelectMultipleTree(org.semanticwb.platform.SemanticObject base)
    {
        super(base);
    }
   
    /* (non-Javadoc)
     * @see org.semanticwb.model.base.FormElementBase#process(javax.servlet.http.HttpServletRequest, org.semanticwb.platform.SemanticObject, org.semanticwb.platform.SemanticProperty)
     */
    /**
     * Process.
     * 
     * @param request the request
     * @param obj the obj
     * @param prop the prop
     */
    @Override
    public void process(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName) {
        String vals[] = request.getParameterValues(propName);

        if (vals == null) {
            vals = new String[0];
        }

        obj.removeProperty(prop);

        if (prop.isObjectProperty()) {
            for (int x = 0; x < vals.length; x++) {
                obj.addObjectProperty(prop, SemanticObject.createSemanticObject(vals[x]));
            }
        } else {
            for (int x = 0; x < vals.length; x++) {
                obj.addLiteralProperty(prop, new SemanticLiteral(vals[x]));
            }
        }
    }    
    
    
    /**
 * Adds the object.
 * 
 * @param obj the obj
 * @param selected the selected
 * @param lang the lang
 * @param separator the separator
 * @return the string
 */
private String addObject(SemanticObject obj, ArrayList<String> vals, String lang, String separator) {
        String ret = "<option value=\"" + obj.getURI() + "\" ";

        if (vals.contains(obj.getURI())) {
            ret += "selected";
        }

        ret += ">" + separator + obj.getDisplayName(lang) + "</option>";

        if (separator.length() == 0) {
            separator = ">";
        }

        Iterator<SemanticObject> it = obj.listHerarquicalChilds();

        while (it.hasNext()) {
            SemanticObject child = it.next();

            ret += addObject(child, vals, lang, "--" + separator);
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see org.semanticwb.model.SelectOne#renderElement(javax.servlet.http.HttpServletRequest, org.semanticwb.platform.SemanticObject, org.semanticwb.platform.SemanticProperty, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String renderElement(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName, String type,
                                String mode, String lang) {
        if (obj == null) {
            obj = new SemanticObject();
        }

        boolean isDojo = type.equals("dojo");

        StringBuilder   ret          = new StringBuilder();
        String         name         = propName;
        String         label        = prop.getDisplayName(lang);
        SemanticObject sobj         = prop.getDisplayProperty();
        boolean        required     = prop.isRequired();
        String         pmsg         = null;
        String         imsg         = null;
        String         selectValues = null;
        boolean        disabled     = false;

        if (sobj != null) {
            DisplayProperty dobj = new DisplayProperty(sobj);

            pmsg = dobj.getDisplayPromptMessage(lang);
            imsg = dobj.getDisplayInvalidMessage(lang);
            selectValues = dobj.getDisplaySelectValues(lang);
            disabled     = dobj.isDisabled();
        }

        if (isDojo) {
            if (required && imsg == null) {
                imsg = label + " es requerido.";

                if (lang.equals("en")) {
                    imsg = label + " is required.";
                }
            }

            if (pmsg == null) {
                pmsg = "Captura " + label + ".";

                if (lang.equals("en")) {
                    pmsg = "Enter " + label + ".";
                }
            }
        }

        String ext = "";

        if (disabled) {
            ext += " disabled=\"disabled\"";
        }

        if (prop.isObjectProperty()) {
            ArrayList<String> vals   = new ArrayList<>();
            String auxs[] = request.getParameterValues(propName);


            if (auxs == null) {
                auxs = new String[0];
            }

            for (int x = 0; x < auxs.length; x++) {
                vals.add(auxs[x]);
            }

            Iterator<SemanticObject> it2 = obj.listObjectProperties(prop);

            while (it2.hasNext()) {
                SemanticObject semanticObject = it2.next();
                vals.add(semanticObject.getURI());
            }

            String value = obj.getDisplayName(lang);

            if (mode.equals("edit") || mode.equals("create")) {
                ret.append("<select name=\"" + name + "\""+" multiple=\"true\"");
                ret.append(" style=\"width:300px;\"");

                if(required)ret.append(" required=\"" + required + "\"");
                ret.append(" " + ext + ">");

                SemanticClass cls = prop.getRangeClass();
                Iterator<SemanticObject> it  = null;

                if (cls != WebPage.sclass) {
                    if (isGlobalScope()) {
                        if (cls != null) {
                            it = SWBComparator.sortSemanticObjects(lang, cls.listInstances());
                        } else {
                            it = SWBComparator.sortSemanticObjects(
                                lang,
                                SWBPlatform.getSemanticMgr().getVocabulary().listSemanticClassesAsSemanticObjects());
                        }
                    } else {
                        it = SWBComparator.sortSemanticObjects(lang, getModel().listInstancesOfClass(cls));
                    }

                    boolean hp = false;

                    if (cls != null) {
                        hp = cls.hasHerarquicalProperties();
                    }

                    while (it.hasNext()) {
                        SemanticObject sob = it.next();
                        
                        boolean deleted=false;
                        if(sob.instanceOf(Trashable.swb_Trashable))
                        {
                            deleted=sob.getBooleanProperty(Trashable.swb_deleted);
                        }

                        if(!deleted)
                        {                        

                            if (hp) {
                                if (!sob.hasHerarquicalParents()) {
                                    ret.append(addObject(sob, vals, lang, ""));
                                }
                            } else {
                                ret.append("<option value=\"" + sob.getURI() + "\" ");

                                if (vals.contains(sob.getURI())) {
                                    ret.append("selected=\"selected\"");
                                }

                                ret.append(">" + sob.getDisplayName(lang) + "</option>");
                            }
                        }
                    }
                } else {
                    WebSite site = SWBContext.getWebSite(getModel().getName());

                    if (site != null) {
                        WebPage home = site.getHomePage();

                        ret.append(addObject(home.getSemanticObject(), vals, lang, ""));
                    }
                }

                ret.append("</select>");
            } else if (mode.equals("view")) {
                ret.append("<span _id=\"" + name + "\" name=\"" + name + "\">" + value + "</span>");
            }
        } else {
            if (selectValues != null) {
                String value = request.getParameter(propName);

                if (value == null) {
                    value = obj.getProperty(prop);
                }

                ret.append("<select name=\"" + name + "\""+" multiple=\"true\"");
                ret.append(" style=\"width:300px;\"");
                ret.append(" " + ext + ">");

                StringTokenizer st = new StringTokenizer(selectValues, "|");

                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    int    ind = tok.indexOf(':');
                    String id  = tok;
                    String val = tok;

                    if (ind > 0) {
                        id  = tok.substring(0, ind);
                        val = tok.substring(ind + 1);
                    }

                    ret.append("<option value=\"" + id + "\" ");

                    if (id.equals(value)) {
                        ret.append("selected");
                    }

                    ret.append(">" + val + "</option>");
                }

                ret.append("</select>");
            }
        }

        return ret.toString();
    }    
    
}
