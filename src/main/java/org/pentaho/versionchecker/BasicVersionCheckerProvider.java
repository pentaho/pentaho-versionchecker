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

import java.util.Map;

import org.pentaho.versionchecker.util.VersionHelper;
import org.pentaho.versionchecker.util.VersionInfo;

@SuppressWarnings( "rawtypes" )
public class BasicVersionCheckerProvider implements IVersionCheckDataProvider {
  private volatile VersionInfo versionInfo;

  protected int versionRequestFlags = DEPTH_MINOR_MASK + DEPTH_GA_MASK;

  public BasicVersionCheckerProvider( Class clazz ) {
    versionInfo = VersionHelper.getVersionInfo( clazz );
  }

  public void setVersionRequestFlags( int flags ) {
    versionRequestFlags = flags;
  }

  /**
   * Returns the application id (code) for this application (the pentaho platform)
   */
  public String getApplicationID() {
    return versionInfo == null ? null : versionInfo.getProductID();
  }

  /**
   * Returns the application version number found in the manifest
   */
  public String getApplicationVersion() {
    return versionInfo == null ? null : versionInfo.getVersionNumber();
  }

  public void reinit( VersionInfo info ) {
    this.versionInfo = info;
  }

  /**
   * Returns the base url for the connection to the pentaho version checking server. Currently, there is no reason to
   * use anything other than the default.
   */
  public String getBaseURL() {
    return null;
  }

  /**
   * Returns the extra information that can be provided.
   */
  public Map getExtraInformation() {
    return null;
  }

  protected int computeOSMask() {
    try {
      String os = System.getProperty( "os.name" ); //$NON-NLS-1$
      if ( os != null ) {
        os = os.toLowerCase();
        if ( os.indexOf( "windows" ) >= 0 ) { //$NON-NLS-1$
          return DEPTH_WINDOWS_MASK;
        } else if ( os.indexOf( "mac" ) >= 0 ) { //$NON-NLS-1$
          return DEPTH_MAC_MASK;
        } else if ( os.indexOf( "linux" ) >= 0 ) { //$NON-NLS-1$
          return DEPTH_LINUX_MASK;
        } else {
          return DEPTH_ALL_MASK;
        }
      }
    } catch ( Exception e ) {
      return DEPTH_ALL_MASK;
      // ignore any issues
    }
    return DEPTH_ALL_MASK;
  }

  /**
   * generates the depth flags
   */
  public int getDepth() {
    return versionRequestFlags | computeOSMask();
  }
}
