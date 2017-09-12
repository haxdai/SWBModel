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

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.semanticwb.model.base.FormElementBase;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;

/**
 * The Class GenericFormElement.
 */
public class GenericFormElement extends FormElementBase {
    
    /**
     * Instantiates a new generic form element.
     */
    public GenericFormElement() {
        super(new SemanticObject());
    }

    /**
     * Instantiates a new generic form element.
     * 
     * @param obj the obj
     */
    public GenericFormElement(SemanticObject obj) {
        super(obj);
    }

    /* (non-Javadoc)
     * @see org.semanticwb.model.base.FormElementBase#renderElement(javax.servlet.http.HttpServletRequest, org.semanticwb.platform.SemanticObject, org.semanticwb.platform.SemanticProperty, java.lang.String, java.lang.String, java.lang.String)
     */
    /**
     * Render element.
     * 
     * @param request the request
     * @param obj the obj
     * @param prop the prop
     * @param type the type
     * @param mode the mode
     * @param lang the lang
     * @return the string
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

        String ext = "";

        if (isDojo) {
            if (imsg == null) {
                if (prop.isDataTypeProperty() && prop.isNumeric()) {
                    imsg = getLocaleString("inv_number", lang);
                } else {
                    imsg = getLocaleString("inv_data", lang);
                }

                if (required) {
                    imsg = label + " " + getLocaleString("required", lang);
                }
            }

            if (pmsg == null) {
                pmsg = getLocaleString("enter", lang) + " " + label;
            }
        }

        if (disabled) {
            ext += " disabled=\"disabled\"";
        }

        if (prop.isDataTypeProperty()) {
            if (selectValues != null) {
                String value = request.getParameter(propName);

                if (value == null) {
                    value = obj.getProperty(prop);
                }

                ret.append("<span>");

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

                    ret.append("<label for=\"" + name + id + "\">");
                    ret.append("<input");

                    if (isDojo) {
                        ret.append(" dojoType=\"dijit.form.RadioButton\"");
                    }

                    if (mode.equals("view")) {
                        ret.append(" disabled=\"disabled\"");
                    }

                    ret.append(" id_=\"" + name + id + "\" name=\"" + name + "\"");

                    if (id.equals(value)) {
                        ret.append(" checked=\"checked\"");
                    }

                    ret.append(" value=\"" + id + "\" type=\"radio\" />" + val + "</label>");
                }

                ret.append("</span>");
            } else if (prop.isBoolean()) {
                String  checked = "";
                boolean value   = false;
                String  aux     = request.getParameter(propName);

                if (aux != null) {
                    value = true;
                } else {
                    value = obj.getBooleanProperty(prop);
                }

                if (value) {
                    checked = "checked=\"checked\"";
                }

                ret.append("<input type=\"checkbox\" id_=\"" + name + "\" name=\"" + name + "\" " + checked);

                if (isDojo) {
                    ret.append(" dojoType=\"dijit.form.CheckBox\"");
                }

                if (isDojo && required) {
                    ret.append(" required=\"" + required + "\"");
                }

                if (isDojo) {
                    ret.append(" promptMessage=\"" + pmsg + "\"");
                    ret.append(" invalidMessage=\"" + imsg + "\"");
                }

                ret.append(ext);

                if (mode.equals("view")) {
                    ret.append(" disabled=\"disabled\"");
                }

                ret.append("/>");
            } else if (prop.isDateTime()) {
                String value = request.getParameter(propName);

                if (value == null) {
                    value = obj.getProperty(prop);
                }

                if (value == null) {
                    value = "";
                }

                ret.append("<span _id=\"" + name + "\" name=\"" + name + "\">" + value + "</span>");
            } else if (prop.isInt() || prop.isLong()) {
                String value = request.getParameter(propName);

                if (value == null) {
                    value = obj.getProperty(prop);
                }

                if (value == null) {
                    value = "";
                }

                if (mode.equals("edit") || mode.equals("create") || mode.equals("filter")) {
                    ret.append("<input _id=\"" + name + "\" name=\"" + name + "\" value=\"" + value + "\"");

                    if (isDojo) {
                        ret.append(" dojoType=\"dijit.form.ValidationTextBox\"");
                        ret.append(" regExp=\"\\d+\"");
                    }

                    if (!mode.equals("filter") && isDojo) {
                        if(required)ret.append(" required=\"" + required + "\"");
                    }

                    if (isDojo) {
                        ret.append(" promptMessage=\"" + pmsg + "\"");
                        ret.append(" invalidMessage=\"" + imsg + "\"");
                    }

                    ret.append(" style=\"width:100px;\"");
                    ret.append(" " + getAttributes());

                    ret.append(ext);
                    ret.append("/>");
                } else if (mode.equals("view")) {
                    ret.append("<span _id=\"" + name + "\" name=\"" + name + "\">" + value + "</span>");
                }
            } else {
                String value = request.getParameter(propName);

                if (value == null) {
                    value = obj.getProperty(prop);
                }

                if (value == null) {
                    value = "";
                }

                value=value.replace("\"", "&quot;");

                if (mode.equals("edit") || mode.equals("create") || mode.equals("filter")) {
                    ret.append("<input _id=\"" + name + "\" name=\"" + name + "\" value=\"" + value + "\"");

                    if (isDojo) {
                        ret.append(" dojoType=\"dijit.form.ValidationTextBox\"");
                    }

                    if (!mode.equals("filter") || isDojo) {
                        if(required)ret.append(" required=\"" + required + "\"");
                    }

                    if (isDojo) {
                        ret.append(" promptMessage=\"" + pmsg + "\"");
                        ret.append(" invalidMessage=\"" + imsg + "\"");
                    }

                    ret.append(" style=\"width:300px;\"");
                    ret.append(" " + getAttributes());

                    if (isDojo) {
                        ret.append(" trim=\"true\"");
                    }

                    ret.append(ext);
                    ret.append("/>");
                } else if (mode.equals("view")) {
                    ret.append("<span _id=\"" + name + "\" name=\"" + name + "\">" + value + "</span>");
                }
            }
        } else if (prop.isObjectProperty()) {
            if (!name.startsWith("has"))
            {
                SemanticObject value = null;
                String         aux   = request.getParameter(propName);

                if (aux != null) {
                    value = SemanticObject.createSemanticObject(aux);
                } else {
                    value = obj.getObjectProperty(prop);
                }

                ret.append("<span>");
                                

                if (value != null) {
                    if("swb".equals(value.getSemanticClass().getPrefix()))
                    {
                        ret.append("<a href=\"?suri=" + value.getEncodedURI() + "\" onclick=\"addNewTab('" + value.getURI()
                               + "', null, '" + value.getDisplayName(lang) + "');return false;\">");
                        ret.append(value.getDisplayName());
                        ret.append("</a>");
                    }else
                    {
                        ret.append(value.getDisplayName());
                    }
                }
                ret.append("</span>");
            }
        }

        return ret.toString();
    }
}
