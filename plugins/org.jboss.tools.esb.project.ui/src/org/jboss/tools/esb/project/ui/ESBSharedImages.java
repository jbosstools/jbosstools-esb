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
package org.jboss.tools.esb.project.ui;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ESBSharedImages {
	public static String WIZARD_NEW_PROJECT    = "WIZARD_NEW_PROJECT";

	private static ESBSharedImages instance() {
		if (instance == null)
			return new ESBSharedImages();
		return instance;
	}
	
	public static Image getImage(String key) {
		return instance().image(key);
	}
	
	public static ImageDescriptor getImageDescriptor(String key) {
		return instance().descriptor(key);
	}

	
	private static ESBSharedImages instance;
	private Hashtable<String, Object> images, descriptors;
	private ESBSharedImages() {
		instance = this;
		images = new Hashtable<String, Object>();
		descriptors = new Hashtable<String, Object>();
		Bundle pluginBundle = ESBProjectPlugin.getDefault().getBundle();
		
		descriptors.put(WIZARD_NEW_PROJECT, createImageDescriptor(pluginBundle, "/icons/obj16/EclipseCreateNewProject.png")); //$NON-NLS-1$
		Iterator<String> iter = descriptors.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			ImageDescriptor descriptor = descriptor(key);
			images.put(key,  descriptor.createImage());	
		}
	}
	
	public ImageDescriptor descriptor(String key) {
		return (ImageDescriptor) descriptors.get(key);
	}

	private ImageDescriptor createImageDescriptor (Bundle pluginBundle, String relativePath) {
		return ImageDescriptor.createFromURL(pluginBundle.getEntry(relativePath));
	}
	
	public Image image(String key) {
		return (Image) images.get(key);
	}
	
	protected void finalize() throws Throwable {
		Iterator<String> iter = images.keySet().iterator();
		while (iter.hasNext()) {
			Image image = (Image) images.get(iter.next());
			image.dispose();
		}
		super.finalize();
	}
}

