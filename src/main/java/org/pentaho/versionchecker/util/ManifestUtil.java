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

package org.pentaho.versionchecker.util;

import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Set of utility methods related to the manifest file. <br/>
 * NOTE: if the manifest file can not be retrieved, these methods will not work and will return <code>null</code>. The
 * most common case for this is the the code is being run outside of a jar file.
 * 
 * @author dkincade
 */
public class ManifestUtil {
  /**
   * Retrieves the manifest information for the jar file which contains this utility class.
   * 
   * @return The Manifest file for the jar file which contains this utility class, or <code>null</code> if the code is
   *         not in a jar file.
   */
  public static Manifest getManifest() {
    return getManifest( ManifestUtil.class );
  }

  /**
   * Retrieves the manifest information for the jar file which contains the specified class.
   * 
   * @return The Manifest file for the jar file which contains the specified class, or <code>null</code> if the code is
   *         not in a jar file.
   */
  @SuppressWarnings( "rawtypes" )
  public static Manifest getManifest( Class clazz ) {
    Manifest retval = null;
    JarInputStream jin = null;
    try {
      final URL codeBase = clazz.getProtectionDomain().getCodeSource().getLocation();
      if ( codeBase.getPath().endsWith( ".jar" ) ) { //$NON-NLS-1$
        jin = new JarInputStream( codeBase.openStream() );
        retval = jin.getManifest();
        jin.close();
      }
    } catch ( Exception e ) {
      return null;
    } finally {
      if ( jin != null ) {
        try {
          jin.close();
        } catch ( Exception ex ) {
          // should log something here...
          retval = null;
        }
      }
    }
    return retval;
  }
}
