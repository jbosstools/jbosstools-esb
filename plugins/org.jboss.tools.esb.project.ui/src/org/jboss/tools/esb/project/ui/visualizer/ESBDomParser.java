/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.esb.project.ui.visualizer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.esb.project.ui.ESBProjectPlugin;
import org.jboss.tools.esb.project.ui.messages.JBossESBUIMessages;
import org.jboss.tools.esb.project.ui.visualizer.ESBNode.ESBType;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Take an incoming jboss-esb config file and parse it into nodes for the graphical view
 * @author bfitzpat
 *
 */
public class ESBDomParser {

	Document dom;
	ESBNodeWithChildren root;
	
	/**
	 * Utility method to verify that a file is an ESB config
	 * @param filepath
	 * @return
	 */
	public boolean isFileESBConfig ( String filepath ) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filepath);
			
			dom.getDocumentElement().normalize();
			
			if (dom.getDocumentElement().getTagName().equalsIgnoreCase("jbossesb")) { //$NON-NLS-1$
				return true;
			}
		}catch(ParserConfigurationException pce) {
			// ignore
		}catch(SAXException se) {
			// ignore
		}catch(IOException ioe) {
			// ignore
		}
		
		return false;
	}
	
	/**
	 * Parse the file into the nodes
	 * @param filepath
	 */
	public void parseXmlFile(String filepath){
		
		root = new ESBNodeWithChildren("Invisible Root"); //$NON-NLS-1$
		
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			try {
				if (filepath == null) {
					Bundle bundle = ESBProjectPlugin.getDefault().getBundle();
					IPath path = new Path("META-INF/jboss-esb.xml"); //$NON-NLS-1$
					URL setupUrl = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
					File setupFile = new File(FileLocator.toFileURL(setupUrl).toURI());
					if (setupFile.exists() && setupFile.canRead())
						filepath = setupFile.getAbsolutePath();
					else
						return;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filepath);
			dom.getDocumentElement().normalize();
			java.io.File tempFile = new java.io.File(filepath);
			String filename = tempFile.getName();
			root.setName(filename);
			root.setEsbObjectType(ESBType.ESB);
			File setupFile = new File(filepath);
			root.setData(setupFile);
			
			parseDocument();

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * Work through the configuration 
	 */
	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <providers> elements
		NodeList nl = docEle.getElementsByTagName("providers"); //$NON-NLS-1$
		if(nl != null && nl.getLength() > 0) {
			Element providersElement= null;
			for (int j = 0; j < nl.getLength(); j++) {
				if (nl.item(j) instanceof Element) {
					providersElement = (Element) nl.item(j);
					break;
				}
			}
			if(providersElement != null) {
				ESBNodeWithChildren providersRoot = new ESBNodeWithChildren(JBossESBUIMessages.ESBDomParser_Providers_Label);
				providersRoot.setEsbObjectType(ESBNode.ESBType.PROVIDER);
				processChildren(providersRoot, providersElement);
				root.addChild(providersRoot);
			}
		}

		//get a nodelist of <services> elements
		NodeList nl2 = docEle.getElementsByTagName("services"); //$NON-NLS-1$
		if(nl2 != null && nl2.getLength() > 0) {
			Element servicesElement= null;
			for (int j = 0; j < nl2.getLength(); j++) {
				if (nl2.item(j) instanceof Element) {
					servicesElement = (Element) nl2.item(j);
					break;
				}
			}
			if(servicesElement != null) {
				ESBNodeWithChildren servicesRoot = new ESBNodeWithChildren(JBossESBUIMessages.ESBDomParser_Services_Label);
				servicesRoot.setEsbObjectType(ESBNode.ESBType.SERVICE);
				processChildren(servicesRoot, servicesElement);
				root.addChild(servicesRoot);
			}
		}
	}
	
	/*
	 * Work down the children tree
	 * @param parent
	 * @param el
	 */
	private void processChildren ( ESBNodeWithChildren parent, Element el ) {
		el.normalize();
		parent.setData(el);
		
		if (parent.getEsbObjectType() == null) {
			String tag = el.getTagName();
			if (tag.endsWith("-bus") && el.getAttribute("busid") != null) { //$NON-NLS-1$ //$NON-NLS-2$
				parent.setEsbObjectType(ESBType.BUS);
			} else if (tag.endsWith("-listener") && el.getAttribute("busidref") != null) { //$NON-NLS-1$ //$NON-NLS-2$
				parent.setEsbObjectType(ESBType.LISTENER);
			} else if (tag.endsWith("-provider")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.PROVIDER);
			} else if (tag.equalsIgnoreCase("service")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.SERVICE);
			} else if (tag.equalsIgnoreCase("listeners")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.LISTENER);
			} else if (tag.equalsIgnoreCase("Actions")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.ACTION);
			} else if (tag.equalsIgnoreCase("action")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.ACTION);
			} else if (tag.equalsIgnoreCase("property")) { //$NON-NLS-1$
				parent.setEsbObjectType(ESBType.PROPERTY);
				return;
			}
		}
		NodeList children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element child = (Element) children.item(i);
				String name = child.getAttribute("name"); //$NON-NLS-1$
				if (name == null || name.trim().length() == 0) {
					name = child.getAttribute("busid"); //$NON-NLS-1$
				}
				if (name == null || name.trim().length() == 0) {
					name = child.getAttribute("dest-name"); //$NON-NLS-1$
				}
				if (name == null || name.trim().length() == 0) {
					name = child.getTagName();
				}
				if (name.equalsIgnoreCase("actions")) { //$NON-NLS-1$
					name = JBossESBUIMessages.ESBDomParser_Actions_Node_Label;
				} else if (name.equalsIgnoreCase("listeners")) { //$NON-NLS-1$
					name = JBossESBUIMessages.ESBDomParser_Listeners_Node_Label;
				}
				
				ESBNodeWithChildren childNode = new ESBNodeWithChildren(name);
				String ref = child.getAttribute("busidref"); //$NON-NLS-1$
				if (ref != null && ref.trim().length() > 0) {
					childNode.setRef(ref);
				}
				processChildren(childNode, child);
				if (childNode.getEsbObjectType() != null && !childNode.getEsbObjectType().equals(ESBType.PROPERTY)) { 
					parent.addChild(childNode);
					childNode.setEsbObjectType(parent.getEsbObjectType());
				}
			}
		}
	}
	
	/**
	 * Return the actual root
	 * @return
	 */
	public ESBNodeWithChildren getRoot() {
		return this.root;
	}

}
