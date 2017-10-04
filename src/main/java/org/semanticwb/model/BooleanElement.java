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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;
   /**
   * Elemento para valores booleanos configurable como CheckBox, Radio y Select
   * @author Hasdai Pacheco {ebenezer.sanchez@infotec.mx}
   */
public class BooleanElement extends org.semanticwb.model.base.BooleanElementBase 
{
	private final String PROP_DISABLED = " disabled=\"disabled\" ";
	private final String PROP_CHECKED = "checked=\"checked\" ";
	private final String PROP_PROMPT = " promptMessage=\"";
	private final String PROP_INVALIDMSG = " invalidMessage=\"";
	private HashMap<String, String> formElementparams = null; 
	
    public BooleanElement(org.semanticwb.platform.SemanticObject base)
    {
        super(base);
    }

    @Override
    public String renderElement(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName, String type, String mode, String lang) {
    		formElementparams = new HashMap<>();
    		formElementparams.put("name", propName);
    		formElementparams.put("mode", mode);
    		formElementparams.put("label", prop.getDisplayName(lang));
    		formElementparams.put("trueTitle", getDisplayTrueTitle(lang));
    		formElementparams.put("falseTitle", getDisplayFalseTitle(lang));
    		formElementparams.put("displayType", getDisplayType());
    		
    		SemanticObject theObj = obj;
    		if (obj == null) {
            theObj = new SemanticObject();
        }

        boolean isDojo   = type.equals("dojo");
        StringBuilder   ret         = new StringBuilder();
        String         label        = prop.getDisplayName(lang);
        SemanticObject sobj         = prop.getDisplayProperty();
        boolean        required     = prop.isRequired();
        String         pmsg         = null;
        String         imsg         = null;
        boolean        isDisabled   = false;
        String displayType          = getDisplayType();

        if (sobj != null) {
            DisplayProperty dobj = new DisplayProperty(sobj);
            pmsg         = dobj.getDisplayPromptMessage(lang);
            imsg         = dobj.getDisplayInvalidMessage(lang);
            isDisabled   = dobj.isDisabled();
        }

        if (isDojo) {
            if (imsg == null) {
                if (required) {
                    imsg = label + " es requerido.";

                    if (lang.equals("en")) {
                        imsg = label + " is required.";
                    }
                } else {
                    imsg = "Dato invalido.";

                    if (lang.equals("en")) {
                        imsg = "Invalid data.";
                    }
                }
            }

            if (pmsg == null) {
                pmsg = "Captura " + label + ".";

                if (lang.equals("en")) {
                    pmsg = "Enter " + label + ".";
                }
            }
        }
        formElementparams.put("pmsg", pmsg);
        formElementparams.put("imsg", imsg);

        if (prop.isDataTypeProperty()) {
            if (prop.isBoolean()) {
                boolean value   = false;
                String  aux     = request.getParameter(propName);

                if (aux != null) {
                    value = true;
                    if (aux.equals("false")) {
                        value = false;
                    }
                } else {
                    value = theObj.getBooleanProperty(prop);
                }

                if (displayType.equals("checkbox")) {
                		renderCheckbox(formElementparams, isDojo, required, value, isDisabled);
                } else if (displayType.equals("select")) {
                    renderSelect(formElementparams, isDojo, required, value, isDisabled);
                } else if (displayType.equals("radio")) {
                    renderRadio(formElementparams, isDojo, required, value, isDisabled);
                }
            } else {
                String value = request.getParameter(propName);
                if (value == null) {
                    value = theObj.getProperty(prop);
                }

                if (value == null) {
                    value = "";
                }

                value=value.replace("\"", "&quot;");
                formElementparams.put("value", value);
                renderInput(formElementparams, isDojo, isDisabled, required);
            }
        }

        return ret.toString();
    }
    
    private String renderCheckbox(HashMap<String, String> feParams, boolean isDojo, boolean isRequired, boolean isSelected, boolean isDisabled) {
    		StringBuilder ret = new StringBuilder();
    		String name =  feParams.get("name");
    		String mode =  feParams.get("mode");
    				
    		if (null != name && !name.isEmpty()) {
    			ret.append("<input type=\"checkbox\" id_=\"").append(name).append("\" name=\"").append(name).append("\" ");
    			if (isSelected) {
    				ret.append(PROP_CHECKED);
    			}
    			if(isRequired) {
    				ret.append("required=\"").append(isRequired).append("\" ");
    			}
            if (isDojo) {
                ret.append("dojoType=\"dijit.form.CheckBox\" ");
                ret.append(PROP_PROMPT).append(feParams.get("pmsg")).append("\" ");
                ret.append(PROP_INVALIDMSG).append(feParams.get("imsg")).append("\" ");
            }
            if (isDisabled || mode.equals("view")) {
            		ret.append(PROP_DISABLED);
            }
            ret.append("/>");
    		}
    		return ret.toString();
    }
    
    private String renderSelect(HashMap<String, String> feParams, boolean isDojo, boolean isRequired, boolean isSelected, boolean isDisabled) {
	    	StringBuilder ret = new StringBuilder();
	    	String name =  feParams.get("name");
	    	String mode =  feParams.get("mode");
	    	String trueTitle =  feParams.get("trueTitle");
	    	String falseTitle =  feParams.get("falseTitle");
	    	
	    	if (null != name && !name.isEmpty()) {
	    		ret.append("<select id_=\"").append(name).append("\" name=\"").append(name).append("\" ");
	    		if(isRequired) {
	    			ret.append("required=\"").append(isRequired).append("\" ");
	    		}
	        if (isDojo) {
	    			ret.append("dojoType=\"dijit.form.FilteringSelect\" ");
	            ret.append(PROP_PROMPT).append(feParams.get("pmsg")).append("\" ");
	            ret.append(PROP_INVALIDMSG).append(feParams.get("imsg")).append("\" ");
	        }
	    		if (isDisabled || mode.equals("view")) {
	    			ret.append(PROP_DISABLED);
	    		}
	        ret.append("/>");
	        ret.append("<option value=\"true\"").append(isSelected?"selected":"").append(" >").append(trueTitle).append("</option>");
	        ret.append("<option value=\"false\"").append(!isSelected?"selected":"").append(" >").append(falseTitle).append("</option>");
	        ret.append("</select>");
	    	}
	    	return ret.toString();
    }
    
    private String renderRadio(HashMap<String, String> feParams, boolean isDojo, boolean isRequired, boolean isSelected, boolean isDisabled) {
	    	StringBuilder ret = new StringBuilder();
	    	String name =  feParams.get("name");
	    	String mode =  feParams.get("mode");
	    	String trueTitle =  feParams.get("trueTitle");
	    	String falseTitle =  feParams.get("falseTitle");
	    	
	    	if (null != name && !name.isEmpty()) {
	    		//True radio option
	    		ret.append("<input type=\"radio\" id_=\"").append(name).append("\" id=\"").append(name).append("_True\" name=\"").append(name).append("\" ").append(isSelected?PROP_CHECKED:"").append("value=\"true\" ");
	    		if(isRequired) {
            		ret.append("required=\"").append(isRequired).append("\" ");
            }
            if (isDojo) {
                ret.append("dojoType=\"dijit.form.RadioButton\"");
                ret.append(PROP_PROMPT).append(feParams.get("pmsg")).append("\" ");
                ret.append(PROP_INVALIDMSG).append(feParams.get("imsg")).append("\" ");
            }
            if (isDisabled || mode.equals("view")) {
            		ret.append(PROP_DISABLED);
            }
            ret.append("/>");
            ret.append("<label for=\"").append(name).append("_True\">").append(trueTitle).append("</label> ");
            
            //False radio option
            ret.append("<input type=\"radio\" id_=\"").append(name).append("\" id=\"").append(name).append("_False\" name=\"").append(name).append("\" ").append(!isSelected?PROP_CHECKED:"").append(" value=\"false\" ");
            if(isRequired) {
	        		ret.append("required=\"").append(isRequired).append("\" ");
	        }
            if (isDojo) {
                ret.append("dojoType=\"dijit.form.RadioButton\" ");
                ret.append(PROP_PROMPT).append(feParams.get("pmsg")).append("\" ");
                ret.append(PROP_INVALIDMSG).append(feParams.get("imsg")).append("\" ");
            }
            if (isDisabled || mode.equals("view")) {
	        		ret.append(PROP_DISABLED);
	        }
            ret.append("/>");
            ret.append("<label for=\"").append(name).append("_False\">").append(falseTitle).append("</label>");
	    	}
	    	return ret.toString();
	}
    
    private String renderInput(HashMap<String, String> feParams, boolean isDojo, boolean isDisabled, boolean isRequired) {
    		StringBuilder ret = new StringBuilder();
    		String name =  feParams.get("name");
	    	String mode =  feParams.get("mode");
	    	String value =  feParams.get("value");
	    	
    		if (mode.equals("edit") || mode.equals("create") || mode.equals("filter")) {
            ret.append("<input _id=\"").append(name).append("\" name=\"").append(name).append("\" value=\"").append(value + "\" ");

            if (isDojo) {
                ret.append("dojoType=\"dijit.form.ValidationTextBox\" ");
            }

            if (!mode.equals("filter") || isDojo && isRequired) {
                ret.append("required=\"").append(isRequired).append("\" ");
            }

            if (isDojo) {
                ret.append(PROP_PROMPT).append(feParams.get("pmsg")).append("\" ");
            }

            if (isDojo) {
                ret.append(PROP_INVALIDMSG).append(feParams.get("imsg")).append("\" ");
            }

            ret.append("style=\"width:300px;\" ");
            ret.append(getAttributes());

            if (isDojo) {
                ret.append("trim=\"true\" ");
            }
            if (isDisabled) {
            		ret.append(PROP_DISABLED);
            }
            ret.append("/>");
        } else if (mode.equals("view")) {
            ret.append("<span _id=\"").append(name).append("\" name=\"").append(name).append("\">").append(value).append("</span>");
        }
    		return ret.toString();
    }
}

