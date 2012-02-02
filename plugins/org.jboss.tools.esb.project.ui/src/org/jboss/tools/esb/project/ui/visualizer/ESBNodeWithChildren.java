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

import java.util.ArrayList;

/**
 * Extend the base node so it can track a list of children
 * @author bfitzpat
 */
public class ESBNodeWithChildren extends ESBNode {

	private ArrayList<ESBNode> children;
	/**
	 * Constructor
	 * @param name
	 */
	public ESBNodeWithChildren(String name) {
		super(name);
		children = new ArrayList<ESBNode>();
	}
	/**
	 * Add a child to the child list
	 * @param child
	 */
	public void addChild(ESBNode child) {
		children.add(child);
		child.setParent(this);
	}
	/**
	 * Remove a child from the child list
	 * @param child
	 */
	public void removeChild(ESBNode child) {
		children.remove(child);
		child.setParent(null);
	}
	/**
	 * Get the list of children
	 * @return
	 */
	public ESBNode [] getChildren() {
		return (ESBNode [])children.toArray(new ESBNode[children.size()]);
	}
	/**
	 * Does the node have children?
	 * @return
	 */
	public boolean hasChildren() {
		return children.size()>0;
	}
}
