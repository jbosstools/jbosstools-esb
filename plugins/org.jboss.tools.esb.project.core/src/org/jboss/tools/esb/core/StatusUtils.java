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

package org.jboss.tools.esb.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

public class StatusUtils {
  public static IStatus errorStatus( String errorMessage ) {
    return new Status( IStatus.ERROR, ESBProjectCorePlugin.PLUGIN_ID, 0, errorMessage, null );
  }
  
  public static IStatus errorStatus( Throwable exc ) {
    String message = exc.getMessage();
    return new Status( IStatus.ERROR, ESBProjectCorePlugin.PLUGIN_ID, 0, message == null ? "" : message, exc );
  }
  
  public static IStatus errorStatus( String message, Throwable exc ) {
    return new Status( IStatus.ERROR, ESBProjectCorePlugin.PLUGIN_ID, 0, message, exc );
  }
  
  public static MultiStatus multiStatus( String message, IStatus[] children, Throwable exc ) {
    return new MultiStatus( ESBProjectCorePlugin.PLUGIN_ID, 0, children, message, exc );  
  }
  
  public static MultiStatus multiStatus( String message, IStatus[] children ) {
    return new MultiStatus( ESBProjectCorePlugin.PLUGIN_ID, 0, children, message, null );  
  }
  
  public static IStatus warningStatus( String warningMessage ) {
    return new Status( IStatus.WARNING, ESBProjectCorePlugin.PLUGIN_ID, 0, warningMessage, null );
  }
  
  public static IStatus warningStatus( String warningMessage, Throwable exc ) {
    return new Status( IStatus.WARNING, ESBProjectCorePlugin.PLUGIN_ID, 0, warningMessage, exc );
  }
  
  public static IStatus infoStatus( String infoMessage ) {
    return new Status( IStatus.INFO, ESBProjectCorePlugin.PLUGIN_ID, 0, infoMessage, null );
  }
}