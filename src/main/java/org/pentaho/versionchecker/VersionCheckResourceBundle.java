/*
 * Copyright 2002 - 2017 Hitachi Vantara.  All rights reserved.
 * 
 * This software was developed by Hitachi Vantara and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. TThe Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.versionchecker;

import java.util.ResourceBundle;

public class VersionCheckResourceBundle {
  // Don't let anyone create an instance of this class ... it is a waste
  private VersionCheckResourceBundle() {
  }

  /**
   * Name of the resource bundle used by the version checker
   */
  private static final String BUNDLE_NAME = "org.pentaho.versionchecker.VersionChecker"; //$NON-NLS-1$

  /**
   * Resource bundle used by ther vesion checker
   */
  private static final ResourceBundle resources = ResourceBundle.getBundle( BUNDLE_NAME );

  /**
   * Retrieves a resource from the version manager resource bundle.
   * 
   * @param key
   *          the key for the resource to be retrieved
   * @return the resource from the bundle. If the resource can not be found, <code>null</code> will be returned.
   */
  public static String getString( final String key ) {
    String result = null;
    if ( resources != null ) {
      result = resources.getString( key );
    }
    return result;
  }
}
