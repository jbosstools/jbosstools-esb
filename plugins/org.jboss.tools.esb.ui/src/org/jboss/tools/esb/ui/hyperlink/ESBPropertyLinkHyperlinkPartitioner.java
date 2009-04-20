/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.esb.ui.hyperlink;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;

import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLClassHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLLinkHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author V.Kabanovich
 */
public class ESBPropertyLinkHyperlinkPartitioner extends XMLLinkHyperlinkPartitioner {
	public static final String ESB_XML_LINK_PARTITION = "org.jboss.tools.common.text.ext.xml.ESB_XML_LINK";

	static Set<String> linkPropertyNames = new HashSet<String>();
	static Set<String> classPropertyNames = new HashSet<String>();
	
	static {
		linkPropertyNames.add("smooksConfig"); //$NON-NLS-1$
		linkPropertyNames.add("resource-config"); //$NON-NLS-1$
		classPropertyNames.add("class-processor"); //$NON-NLS-1$
		classPropertyNames.add("incoming-type"); //$NON-NLS-1$
		classPropertyNames.add("message-store-class"); //$NON-NLS-1$
	}
	String propertyName = null;
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		if(linkPropertyNames.contains(propertyName)) {
			return ESB_XML_LINK_PARTITION;
		}
		if(classPropertyNames.contains(propertyName)) {
			return XMLClassHyperlinkPartitioner.XML_CLASS_PARTITION;
		}
		
		return ESB_XML_LINK_PARTITION;
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			IFile documentFile = smw.getFile();
			IProject project = documentFile.getProject();
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return false;
			
			Attr a = (Attr)n;
			Element p = a.getOwnerElement();
			NamedNodeMap as = p.getAttributes();
			Node c = as.getNamedItem("name");
			if(!(c instanceof Attr)) return false;
			propertyName = ((Attr)c).getValue();

			if(linkPropertyNames.contains(propertyName)) {
				return true;
			}

			if(classPropertyNames.contains(propertyName)) {
				return true;
			}

			return false;
		} finally {
			smw.dispose();
		}
	}

}
