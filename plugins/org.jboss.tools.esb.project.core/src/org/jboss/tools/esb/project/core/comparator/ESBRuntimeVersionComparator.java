/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.esb.project.core.comparator;

import java.util.Comparator;

/**
 * @author bfitzpat
 *
 */
public class ESBRuntimeVersionComparator implements Comparator<String> {

	public int compare(String arg0, String arg1) {
		Integer[] versions1 = parse(arg0, '.');
		Integer[] versions2 = parse(arg1, '.');
		if (versions1 != null && versions2 != null) {
			int compareMajor = versions1[0].compareTo(versions2[0]);
			int compareMinor = versions1[1].compareTo(versions2[1]);
			if (compareMajor == 0) {
				return compareMinor;
			} else {
				return compareMajor;
			}
		}
		return 0;
	}
	
	private Integer[] parse ( String input, char separator) {
		if (input == null) 
			return null;
		int pos = input.indexOf(separator);
		String out1 = input.substring(0, pos);
		String out2 = input.substring(pos + 1, input.length());
		if (out1 != null && out2 != null) {
			Integer intOut1 = Integer.parseInt(out1);
			Integer intOut2 = Integer.parseInt(out2);
			return new Integer[]{intOut1, intOut2};
		}
		return null;
	}

}
