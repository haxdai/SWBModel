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

import javax.servlet.http.HttpServletRequest;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;

/**
 * The Class LoginElement.
 */
public class LoginElement extends org.semanticwb.model.base.LoginElementBase {

	/** The log. */
	static Logger LOG = SWBUtils.getLogger(LoginElement.class);

	/**
	 * Instantiates a new login element.
	 * 
	 * @param base
	 *            the base
	 */
	public LoginElement(org.semanticwb.platform.SemanticObject base) {
		super(base);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticwb.model.Text#renderElement(javax.servlet.http.
	 * HttpServletRequest, org.semanticwb.platform.SemanticObject,
	 * org.semanticwb.platform.SemanticProperty, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String renderElement(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName,
			String type, String mode, String lang) {
		
		LOG.debug("Type: " + type);

		if (type.equals("dojo")) {
			setAttribute("isValid", "return validateElement('" + propName + "','" + getValidateURL(obj, prop)
					+ "',this.textbox.value);");
		} else {
			setAttribute("isValid", null);
		}

		return super.renderElement(request, obj, prop, propName, type, mode, lang);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticwb.model.base.FormElementBase#validate(javax.servlet.http.
	 * HttpServletRequest, org.semanticwb.platform.SemanticObject,
	 * org.semanticwb.platform.SemanticProperty)
	 */
	/**
	 * Validate.
	 * 
	 * @param request
	 *            the request
	 * @param obj
	 *            the obj
	 * @param prop
	 *            the prop
	 * @throws FormValidateException
	 *             the form validate exception
	 */
	@Override
	public void validate(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName)
			throws FormValidateException {
		
		SemanticObject ob = obj;
		if (obj == null) {
			ob = new SemanticObject();
		}

		super.validate(request, ob, prop, propName);

		String login = request.getParameter("usrLogin");

		LOG.debug("obj: " + ob.getDisplayName());

		String model = null;
		User cu = null;

		if (ob.instanceOf(User.sclass)) {
			cu = new User(ob);
			model = cu.getUserRepository().getId();
		} else if (ob.instanceOf(UserRepository.sclass)) {
			UserRepository ur = new UserRepository(ob);

			model = ur.getId();
		}

		if ((login == null) || (login.length() == 0) || (login.indexOf(' ') > -1) || (model == null)) {
			throw new FormValidateException(getLocaleString("errEmpty", "Login vacío o con espacios"));
		} else {
			if (isValidId(login)) {
				if (!((cu != null) && login.equalsIgnoreCase(cu.getLogin()))) {
					User tmpobj = SWBContext.getUserRepository(model).getUserByLogin(login);

					if (tmpobj != null) {
						throw new FormValidateException(getLocaleString("errBusy", "Login ya ocupado"));
					}
				}
			} else {
				throw new FormValidateException(getLocaleString("errInvalid", "Login con caracteres inválidos"));
			}
		}
	}

	/**
	 * Checks if is valid id.
	 * 
	 * @param id
	 *            the id
	 * @return true, if is valid id
	 */
	private boolean isValidId(String id) {
		boolean ret = true;

		if (id != null && id.length() >= 3) {
			for (int x = 0; x < id.length(); x++) {
				char ch = id.charAt(x);

				if (!(((ch >= '0') && (ch <= '9')) || ((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))
						|| (ch == '_') || (ch == '-') || (ch == '.') || (ch == '@'))) {
					ret = false;

					break;
				}
			}
		} else {
			ret = false;
		}

		return ret;
	}
}
