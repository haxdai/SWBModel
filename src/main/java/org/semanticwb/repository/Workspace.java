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
package org.semanticwb.repository;

import org.semanticwb.Logger;
import org.semanticwb.SWBUtils;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.repository.base.WorkspaceBase;

/**
 * The Class Workspace.
 */
public class Workspace extends WorkspaceBase
{    
    
    /** The log. */
    static Logger log = SWBUtils.getLogger(Workspace.class);
    
    /**
     * Instantiates a new workspace.
     * 
     * @param base the base
     */
    public Workspace(SemanticObject base)
    {
        super(base);        
    }
    
    /**
     * Creates the workspace.
     * 
     * @param id the id
     * @param namespace the namespace
     * @return the org.semanticwb.repository. workspace
     */
    public static org.semanticwb.repository.Workspace createWorkspace(String id, String namespace)
    {

        log.debug("Creating workspace..." + id + " ...");
        org.semanticwb.repository.Workspace ws=WorkspaceBase.ClassMgr.createWorkspace(id, namespace);
        Unstructured root = Unstructured.ClassMgr.createUnstructured(ws);
        log.debug("Creating root node...");
        root.setName("jcr:root");
        root.setPath("/");        
        log.debug("Adding root node to workspace...");
        ws.setRoot(root);        
        log.debug("Workspace created...");
        return ws;
    }

    
}
