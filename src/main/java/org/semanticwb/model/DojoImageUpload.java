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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.semanticwb.Logger;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBRuntimeException;
import org.semanticwb.SWBUtils;
import org.semanticwb.base.util.ImageResizer;
import org.semanticwb.platform.SemanticLiteral;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;
import org.semanticwb.util.UploadFileRequest;
import org.semanticwb.util.UploadedFile;
import org.semanticwb.util.UploaderFileCacheUtils;

public class DojoImageUpload extends org.semanticwb.model.base.DojoImageUploadBase {

	/**
	 * The log.
	 */
	private static Logger LOG = SWBUtils.getLogger(DojoImageUpload.class);

	public DojoImageUpload(org.semanticwb.platform.SemanticObject base) {
		super(base);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticwb.model.FlashFileUpload#process(HttpServletRequest,
	 * SemanticObject, SemanticProperty)
	 */
	@Override
	public void process(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName) {
		String pname = getPropertyName(prop, obj, propName);
		// MAPS74 - 01032011 Parche en caso de que en la forma no se haya rendereado la
		// propiedad
		if (request.getParameter(pname + "_delFile") != null || request.getParameter(propName + "_new") != null
				|| request.getParameter(pname) != null) {
			if (request.getParameter(pname + "_delFile") != null) {
				if (prop.getCardinality() != 1) {
					String[] params = request.getParameterValues(pname + "_delFile");
					for (String valor : params) {
						delfile(obj, pname + "_" + valor);
						delfile(obj, "thmb_" + pname + "_" + valor);
						obj.removeLiteralProperty(prop, new SemanticLiteral(pname + "_" + valor));
						// temporal mientras se eliminan elementos creados previamente
						obj.removeLiteralProperty(prop, new SemanticLiteral(valor));
					}
				} else {
					delfile(obj, pname + "_" + request.getParameter(pname + "_delFile"));
					obj.removeProperty(prop);
				}
			}
			String destpath = UploaderFileCacheUtils.getHomepath() + "/" + obj.getWorkPath();
			File dir = new File(destpath);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new SWBRuntimeException("Can't create work directory " + dir);
			}
			String cad = request.getParameter(propName + "_new");
			if (cad == null) {
				cad = request.getParameter(pname);
			}
			List<UploadedFile> lista = UploaderFileCacheUtils.get(cad);
			for (UploadedFile arch : lista) {
				File orig = new File(arch.getTmpuploadedCanonicalFileName());
				File dest = new File(dir, pname + "_" + arch.getOriginalName());
				if (!orig.renameTo(dest)) {
					try {
						SWBUtils.IO.copy(orig.getCanonicalPath(), dest.getCanonicalPath(), false, null, null);
					} catch (IOException ex) {
						throw new SWBRuntimeException("Can't copy files", ex);
					}
				}
				try {
					imgPrpcess(dest);
					if (prop.getCardinality() != 1) {
						obj.addLiteralProperty(prop, new SemanticLiteral(pname + "_" + arch.getOriginalName()));
					} else {
						obj.setProperty(prop, arch.getOriginalName());
					}
				} catch (IOException IOE) {
					LOG.error(IOE);
				}
			}
			UploaderFileCacheUtils.clean(cad);
		}
	}

	/**
	 * Img prpcess.
	 *
	 * @param dest
	 *            the dest
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void imgPrpcess(File dest) throws IOException {
		// TODO: ImageResizer.
		String name = dest.getName();
		File path = dest.getParentFile();
		File file = new File(path, name);
		File tmpFile = new File(path, "tmping_" + name);
		if (getImgMaxWidth() > 0 && getImgMaxHeight() > 0) {
			dest.renameTo(tmpFile);
			if (isImgCrop()) {
				ImageResizer.resizeCrop(tmpFile, getImgMaxWidth(), getImgMaxHeight(), file,
						name.substring(name.lastIndexOf(".") + 1));
			} else {
				ImageResizer.resize(tmpFile, getImgMaxWidth(), getImgMaxHeight(), true, file,
						name.substring(name.lastIndexOf(".") + 1));
			}
			tmpFile.delete();
		}
		if (isImgThumbnail()) {
			if (isImgCrop()) {
				ImageResizer.resizeCrop(file, getImgThumbnailWidth(), getImgThumbnailHeight(),
						new File(path, "thmb_" + name), name.substring(name.lastIndexOf(".") + 1));
			} else {
				ImageResizer.resize(file, getImgThumbnailWidth(), getImgThumbnailHeight(), true,
						new File(path, "thmb_" + name), name.substring(name.lastIndexOf(".") + 1));
			}

			String thumbs = getImgThumbnailList();
			if (thumbs != null) {
				int x = 1;
				StringTokenizer st = new StringTokenizer(thumbs, "|");
				while (st.hasMoreTokens()) {
					String tk = st.nextToken();
					StringTokenizer st2 = new StringTokenizer(tk, "x");
					int w = Integer.parseInt(st2.nextToken());
					int h = Integer.parseInt(st2.nextToken());
					if (isImgCrop()) {
						ImageResizer.resizeCrop(file, w, h, new File(path, "thmb" + x + "_" + name),
								name.substring(name.lastIndexOf(".") + 1));
					} else {
						ImageResizer.resize(file, w, h, true, new File(path, "thmb" + x + "_" + name),
								name.substring(name.lastIndexOf(".") + 1));
					}
					x++;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticwb.model.FlashFileUpload#configFileRequest(SemanticProperty)
	 */
	protected UploadFileRequest configFileRequest(SemanticProperty prop) {
		boolean multiple = prop.getCardinality() != 1;
		HashMap<String, String> filtros = new HashMap<String, String>();
		if (null == getFileFilter() || "".equals(getFileFilter())) {
			filtros.put("Jpeg File", "*.jpg;*.jpeg");
			filtros.put("GIF File", "*.gif");
			filtros.put("PNG File", "*.png");
			filtros.put("All Images", "*.jpg;*.jpeg;*.gif;*.png");
		} else {
			String[] cads = getFileFilter().split("\\|");
			for (String line : cads) {
				String[] parts = line.split(":");
				filtros.put(parts[0], parts[1]);
			}
		}
		return new UploadFileRequest(filtros, multiple, getFileMaxSize());
	}

	@Override
	public String renderElement(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName,
			String type, String mode, String lang) {
		String ret = renderElement2(request, obj, prop, propName, type, mode, lang);
		String img = obj.getProperty(prop);
		if (img != null) {
			// imagen_Noticia_de_ejemplo_Hydrangeas.jpg ejemplo
			// thmb_iconoNombreArchivo_Evento_de_ejemplo_Chrysant antes se mostraba asi
			// se cambia así, ya que no siempre se define tener una imagen de thmb y se
			// controla el tamaño desde esta vista
			ret = ret + "<br/><image width=\"120\" height=\"120\"  src=\"" + SWBPlatform.getContextPath() + "/work"
					+ obj.getWorkPath() + "/" + propName + "_" + obj.getId() + "_" + img + "\">";
		}
		return ret;
	}

	public String renderElement2(HttpServletRequest request, SemanticObject obj, SemanticProperty prop, String propName,
			String type, String mode, String lang) {
		if (null == obj) {
			throw new SWBRuntimeException("No Semantic Object present");
		}

		String pname = getPropertyName(prop, obj, propName);

		String frmname = null;
		if (mode.equals("create")) {
			frmname = prop.getDomainClass().getURI();
		} else {
			frmname = obj.getURI();
		}
		frmname = frmname + "/form";

		StringBuilder buffer = new StringBuilder();
		String cad = UploaderFileCacheUtils.uniqueCad();
		UploadFileRequest ufq = configFileRequest(prop, pname);
		UploaderFileCacheUtils.putRequest(cad, ufq);
		request.getSession(true).setAttribute("fuCad", cad);

		String url = SWBPlatform.getContextPath() + "/multiuploader/" + obj.getModel().getModelObject().getId()
				+ "/home/" + cad;
		String eliminar = lang.equals("en") ? "Chose the files to delete" : "Selecione el(los) archivo(s) a eliminar";
		String error = lang.equals("en") ? "There was an error, try again please."
				: "Ocurrió un error, intente nuevamente.";
		String loading = lang.equals("en") ? "Uploading file, please wait..." : "Cargando archivo, por favor espere...";
		String done = lang.equals("en") ? "Upload succesful" : "La carga del(los) archivo(s) tuvo éxito";

		if (!"view".equals(mode)) {
			buffer.append("<input ").append("name=\"uploadedfile\" ").append("data-dojo-props=\" \n")
					.append("multiple:'").append(prop.getCardinality() != 1 ? "true" : "false").append("', \n")
					.append("uploadOnSelect:'true', \n").append("url:'").append(url).append("', \n")
					.append("submit: function(form) {}, \n").append("onBegin: function(){document.getElementById('")
					.append(pname).append("_lblStatus').innerHTML='").append(loading).append("';}, \n")
					.append("onComplete: function (result) {document.getElementById('").append(pname)
					.append("_lblStatus').innerHTML='").append(done).append("'; console.log('result:'+result);}, \n")
					.append("onCancel: function() {console.log('cancelled');}, \n")
					.append("onAbort: function() {console.log('aborted');}, \n")
					.append("onError: function (evt) {document.getElementById('").append(pname)
					.append("_lblStatus').innerHTML='").append(error).append("'; console.log(evt);}, \n").append("\" ")
					.append("type=\"file\" ").append("data-dojo-type=\"dojox.form.Uploader\" ")
					.append("label=\"Select File\" ").append("id=\"").append(pname).append("_defaultAuto\" ")
					.append("/>  ");

			buffer.append("<span id=\"").append(pname).append("_lblStatus\"></span>");
			buffer.append("<br/>\n");

			if (!"create".equals(mode) && obj.getProperty(prop) != null) {
				String name = obj.getProperty(prop);
				if (name.startsWith(pname)) {
					name = name.substring(pname.length() + 1);
				}
				if (prop.getCardinality() == 1) {
					if ("edit".equals(mode)) {
						buffer.append("Eliminar: <input dojoType=\"dijit.form.CheckBox\" id=\"" + pname
								+ "_delFile\" name=\"" + pname + "_delFile\" value=\"" + name + "\" /><a href=\""
								+ SWBPlatform.getContextPath() + "/work" + obj.getWorkPath() + "/" + propName + "_"
								+ obj.getId() + "_" + obj.getProperty(prop) + "\">" + name + "</a>\n");
					} else {
						buffer.append("&nbsp;<a href=\"" + SWBPlatform.getContextPath() + "/work" + obj.getWorkPath()
								+ "/" + propName + "_" + obj.getId() + "_" + obj.getProperty(prop) + "\">" + name
								+ "</a>");
					}
				}
			}

			buffer.append("<input type=\"hidden\" name=\"" + pname + "\" value=\"" + cad + "\" />\n");
			UploaderFileCacheUtils.put(cad, new java.util.LinkedList<UploadedFile>());

			if (!"create".equals(mode) && prop.getCardinality() != 1) {
				Iterator<SemanticLiteral> lista = obj.listLiteralProperties(prop);
				if (lista.hasNext()) {
					buffer.append(eliminar + ":<br><select dojoType=\"dijit.form.MultiSelect\" name=\"" + pname
							+ "_delFile\" multiple=\"multiple\" size=\"4\">\n");
					while (lista.hasNext()) {
						SemanticLiteral lit = lista.next();
						String fname = lit.getString();
						if (fname.startsWith(pname)) {
							fname = fname.substring(pname.length() + 1);
						}
						buffer.append("<option value=\"" + fname + "\">" + fname + "</option>");
					}
					buffer.append("</select>");
				}
			}
		} else {
			if (obj.getProperty(prop) != null) {
				if (prop.getCardinality() == 1) {
					String name = obj.getProperty(prop);
					if (name.startsWith(pname)) {
						name = name.substring(pname.length() + 1);
					}
					buffer.append("&nbsp;<a href=\"" + SWBPlatform.getContextPath() + "/work" + obj.getWorkPath() + "/"
							+ propName + "_" + obj.getId() + "_" + obj.getProperty(prop) + "\">" + name + "</a>");
				} else {
					Iterator<SemanticLiteral> lista = obj.listLiteralProperties(prop);
					if (lista.hasNext()) {
						while (lista.hasNext()) {
							SemanticLiteral lit = lista.next();
							String fname = lit.getString();
							String name = fname;
							if (fname.startsWith(pname)) {
								name = fname.substring(pname.length() + 1);
							}
							buffer.append(
									"&nbsp;<a href=\"" + SWBPlatform.getContextPath() + "/work" + obj.getWorkPath()
											+ "/" + propName + "_" + obj.getId() + "_" + fname + "\">" + name + "</a>");
						}
					}
				}
			}
		}
		return buffer.toString();
	}

}
