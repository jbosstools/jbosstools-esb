/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.esb.project.ui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Grid Qian
 */
public class UIUtils {
	/**
	 * A default padding value for horizontalResize().
	 */
	public final static int DEFAULT_PADDING = 35;

	String infoPopid_;

	public UIUtils(String infoPopid) {
		infoPopid_ = infoPopid;
	}

	public Button createRadioButton(Composite parent, String labelName,
			String tooltip, String infopop) {
		return createButton(SWT.RADIO, parent, labelName, tooltip, infopop);
	}

	public Button createCheckbox(Composite parent, String labelName,
			String tooltip, String infopop) {
		return createButton(SWT.CHECK, parent, labelName, tooltip, infopop);
	}

	public Button createPushButton(Composite parent, String labelName,
			String tooltip, String infopop) {
		return createButton(SWT.NONE, parent, labelName, tooltip, infopop);
	}

	public Button createButton(int kind, Composite parent, String labelName,
			String tooltip, String infopop) {
		Button button = new Button(parent, kind);

		tooltip = tooltip == null ? labelName : tooltip;
		button.setText(labelName);
		button.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(button,
					infoPopid_ + "." + infopop);

		return button;
	}

	public Combo createCombo(Composite parent, String labelName,
			String tooltip, String infopop, int style) {
		tooltip = tooltip == null ? labelName : tooltip;

		if (labelName != null) {
			Label label = new Label(parent, SWT.WRAP);
			label.setText(labelName);
			label.setToolTipText(tooltip);
		}

		Combo combo = new Combo(parent, style);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);

		combo.setLayoutData(griddata);
		combo.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(combo,
					infoPopid_ + "." + infopop);

		return combo;
	}

	public Text createText(Composite parent, String labelName, String tooltip,
			String infopop, int style) {
		tooltip = tooltip == null ? labelName : tooltip;

		if (labelName != null) {
			Label label = new Label(parent, SWT.WRAP);
			label.setText(labelName);
			label.setToolTipText(tooltip);
		}

		Text text = new Text(parent, style);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);

		text.setLayoutData(griddata);
		text.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(text,
					infoPopid_ + "." + infopop);

		return text;
	}

	public Composite createComposite(Composite parent, int columns) {
		return createComposite(parent, columns, -1, -1);
	}

	public Composite createComposite(Composite parent, int columns,
			int marginHeight, int marginWidth) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridlayout = new GridLayout();
		gridlayout.numColumns = columns;

		if (marginHeight >= 0)
			gridlayout.marginHeight = marginHeight;
		if (marginWidth >= 0)
			gridlayout.marginWidth = marginWidth;

		composite.setLayout(gridlayout);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		composite.setLayoutData(griddata);

		return composite;

	}

	public Group createGroup(Composite parent, String groupName,
			String tooltip, String infopop) {
		return createGroup(parent, groupName, tooltip, infopop, 1, -1, -1);
	}

	public Group createGroup(Composite parent, String groupName,
			String tooltip, String infopop, int columns, int marginHeight,
			int marginWidth) {
		Group newGroup = new Group(parent, SWT.NONE);
		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		GridLayout gridlayout = new GridLayout();

		gridlayout.numColumns = columns;

		if (marginHeight >= 0)
			gridlayout.marginHeight = marginHeight;
		if (marginWidth >= 0)
			gridlayout.marginWidth = marginWidth;

		tooltip = tooltip == null ? groupName : tooltip;
		newGroup.setLayout(gridlayout);
		newGroup.setText(groupName);
		newGroup.setLayoutData(griddata);
		newGroup.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(newGroup,
					infoPopid_ + "." + infopop);

		return newGroup;
	}

	public Tree createTree(Composite parent, String tooltip, String infopop,
			int style) {

		tooltip = tooltip == null ? "" : tooltip;

		Tree tree = new Tree(parent, style);

		tree.setLayoutData(createFillAll());
		tree.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(tree,
					infoPopid_ + "." + infopop);

		return tree;

	}

	public Table createTable(Composite parent, String tooltip, String infopop,
			int style) {

		tooltip = tooltip == null ? "" : tooltip;

		Table table = new Table(parent, style);

		// table.setLayoutData( createFillAll() );
		table.setToolTipText(tooltip);

		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(table,
					infoPopid_ + "." + infopop);

		return table;

	}

	public Label createHorizontalSeparator(Composite parent, int spacing) {
		Composite composite = createComposite(parent, 1, spacing, -1);

		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

		GridData griddata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		separator.setLayoutData(griddata);

		return separator;
	}

	public GridData createFillAll() {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		return data;
	}

	public void createInfoPop(Control ctrl, String infopop) {
		if (infopop != null)
			PlatformUI.getWorkbench().getHelpSystem().setHelp(ctrl,
					infoPopid_ + "." + infopop);
	}

	/**
	 * Resizes the width of the target composite so that it is as wide as the
	 * reference composite plus a padding value.
	 * 
	 * @param target
	 *            The composite to resize.
	 * @param reference
	 *            The reference composite
	 * @param padding
	 *            The padding value
	 */
	public void horizontalResize(Composite target, Composite reference,
			int padding) {

		Point originalSize = target.getSize();
		Point referenceSize = reference.getSize();

		padding = padding >= 0 ? padding : DEFAULT_PADDING;

		if (referenceSize.x + padding > originalSize.x)
			target.setSize(referenceSize.x + padding, originalSize.y);
	}
	
	public static void  writePropertyToFile(File file,String key, String value) throws IOException {
		Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file), "8859_1"));
	       out.write(key+"="+value+"\n");
	       out.close();
	}
	
	// file util
	public static void copyFile(String src, String dest) {
		InputStream is = null;
		FileOutputStream fos = null;

		try
		{
			is = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			int c = 0;
			byte[] array = new byte[1024];
			while ((c = is.read(array)) >= 0){
				fos.write(array, 0, c);
			}
		}
		catch (IOException e)	{
			e.printStackTrace();
		}
		finally	{
			try	{
				fos.close();
				is.close();
			}
			catch (IOException e)	{
				e.printStackTrace();
			}
		}
	}

	public static File createFileAndParentDirectories(String fileName) throws Exception {
		File file = new File(fileName);
		File parent = file.getParentFile();
		if (!parent.exists()){
			parent.mkdirs();
		}
		file.createNewFile();
		return file;
	}
	
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

	public static void deleteDirectories(File dir) {
		File[] children = dir.listFiles();
		for (int i = 0; i < children.length; i++){
			if (children[i].list() != null && children[i].list().length > 0){
				deleteDirectories(children[i]);
			}
			else{
				children[i].delete();
			}
		}
		dir.delete();
	}

	public static void deleteDirectories(String dir) {
		File directory = new File(dir);
		deleteDirectories(directory);
	}

	public static void createTargetFile(String sourceFileName, String targetFileName) 
							throws Exception {
		createTargetFile(sourceFileName, targetFileName, false);
	}

	public static void createTargetFile(String sourceFileName, String targetFileName, 
						boolean overwrite) throws Exception{
		File idealResultFile = new File(targetFileName);
		if (overwrite || !idealResultFile.exists())
		{
			createFileAndParentDirectories(targetFileName);
			copyFile(sourceFileName, targetFileName);
		}
	}

	public static boolean createDirectory(String directory){
		// Create a directory; all ancestor directories must exist
		boolean success = (new File(directory)).mkdir();
		if (!success) {
			// Directory creation failed
		}
		return success;  
	}

	public static boolean createDirectorys(String directory){
		// Create a directory; all ancestor directories must exist
		boolean success = (new File(directory)).mkdirs();
		if (!success) {
			// Directory creation failed
		}
		return success;  
	}

	//Copies all files under srcDir to dstDir.
	// If dstDir does not exist, it will be created.
	public static void copyDirectory(File srcDir, File dstDir) throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdir();
			}

			String[] children = srcDir.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(srcDir, children[i]),
						new File(dstDir, children[i]));
			}
		} else {
			copy(srcDir, dstDir);
		}
	}

	//Copies src file to dst file.
	// If the dst file does not exist, it is created
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static String addAnotherNodeToPath(String currentPath, String newNode) {
		return currentPath + File.separator + newNode;
	}
	
	public static String addNodesToPath(String currentPath, String[] newNode) {
		String returnPath=currentPath;
		for (int i = 0; i < newNode.length; i++) {
			returnPath = returnPath + File.separator + newNode[i];
		}
		return returnPath;
	}
	
	public static String addNodesToPath(StringBuffer currentPath, String[] pathNodes) {
		for (int i = 0; i < pathNodes.length; i++){
			currentPath.append(File.separator);
			currentPath.append(pathNodes[i]);
		}
		return currentPath.toString();
	}
	
	public static String addNodesToURL(String currentPath, String[] newNode) {
		String returnPath=currentPath;
		for (int i = 0; i < newNode.length; i++) {
			returnPath = returnPath + "/" + newNode[i];
		}
		return returnPath;
	}
	
    /**
     * Get the list of file with a prefix of <code>fileNamePrefix</code> &amp; an extension of
     * <code>extension</code>
     *
     * @param sourceDir      The directory in which to search the files
     * @param fileNamePrefix The prefix to look for
     * @param extension      The extension to look for
     * @return The list of file with a prefix of <code>fileNamePrefix</code> &amp; an extension of
     *         <code>extension</code>
     */
    public static File[] getMatchingFiles(String sourceDir, String fileNamePrefix, String extension) {
        List<File> fileList = new ArrayList<File>();
        File libDir = new File(sourceDir);
        String libDirPath = libDir.getAbsolutePath();
        String[] items = libDir.list();
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (fileNamePrefix != null && extension != null) {
                    if (item.startsWith(fileNamePrefix) && item.endsWith(extension)) {
                        fileList.add(new File(libDirPath + File.separator + item));
                    }
                } else if (fileNamePrefix == null && extension != null) {
                    if (item.endsWith(extension)) {
                        fileList.add(new File(libDirPath + File.separator + item));
                    }
                } else if (fileNamePrefix != null && extension == null) {
                    if (item.startsWith(fileNamePrefix)) {
                        fileList.add(new File(libDirPath + File.separator + item));
                    }
                } else {
                    fileList.add(new File(libDirPath + File.separator + item));
                }
            }
            return (File[]) fileList.toArray(new File[fileList.size()]);
        }
        return new File[0];
    }
    
    /**
     * Filter out files inside a <code>sourceDir</code> with matching <codefileNamePrefix></code>
     * and <code>extension</code>
     * @param sourceDir 		The directory to filter the files
     * @param fileNamePrefix	The filtering filename prefix 
     * @param extension			The filtering file extension
     */
    public static void filterOutRestrictedFiles(String sourceDir, String fileNamePrefix, String extension){
    	File[] resultedMatchingFiles = getMatchingFiles(sourceDir, fileNamePrefix, extension);
    	for (int i = 0; i < resultedMatchingFiles.length; i++) {
			File matchingFilePath = new File(resultedMatchingFiles[i].getAbsolutePath());
			matchingFilePath.delete();
		}
    }
	
}