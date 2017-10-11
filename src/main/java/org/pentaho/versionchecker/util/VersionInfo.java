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

/**
 * Bean that holds the results of the version information
 * 
 * @author dkincade
 */
public class VersionInfo {
  /**
   * The product id of the product
   */
  private String productID;

  /**
   * The text title of the product
   */
  private String title;

  /**
   * The major portion of the version number
   */
  private String versionMajor;

  /**
   * The minor portion of the version number
   */
  private String versionMinor;

  /**
   * The release portion of the version number
   */
  private String versionRelease;

  /**
   * The milestone portion of the version number
   */
  private String versionMilestone;

  /**
   * THe build number portion of the version number
   */
  private String versionBuild;

  /**
   * Boolean indicating if this was retrieved from the manifest (indicating that it is running from a build) or from
   * class files (indicating it is running from compiled class files).
   */
  private boolean fromManifest;

  public boolean isFromManifest() {
    return fromManifest;
  }

  public String getProductID() {
    return productID;
  }

  public String getTitle() {
    return title;
  }

  public String getVersionMajor() {
    return versionMajor;
  }

  public String getVersionMinor() {
    return versionMinor;
  }

  public String getVersionRelease() {
    return versionRelease;
  }

  public String getVersionMilestone() {
    return versionMilestone;
  }

  public String getVersionBuild() {
    return versionBuild;
  }

  public String getVersionNumber() {
    StringBuffer sb = new StringBuffer();
    if ( versionMajor != null ) {
      sb.append( versionMajor );
      if ( versionMinor != null ) {
        sb.append( '.' ).append( versionMinor );
        if ( versionRelease != null ) {
          sb.append( '.' ).append( versionRelease );
          if ( versionMilestone != null ) {
            sb.append( '.' ).append( versionMilestone );
            if ( versionBuild != null ) {
              sb.append( '.' ).append( versionBuild );
            }
          }
        }
      }
    }
    return sb.toString();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append( "fromManifest       = [" ).append( fromManifest ).append( "]\n" ); //$NON-NLS-1$  //$NON-NLS-2$
    sb.append( "productID          = [" ).append( productID ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "title              = [" ).append( title ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "versionMajor       = [" ).append( versionMajor ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "versionMinor       = [" ).append( versionMinor ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "versionRelease     = [" ).append( versionRelease ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "versionMilestone   = [" ).append( versionMilestone ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "versionBuild       = [" ).append( versionBuild ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append( "getVersionNumber() = [" ).append( getVersionNumber() ).append( "]\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    return sb.toString();
  }

  public void setFromManifest( boolean fromManifest ) {
    this.fromManifest = fromManifest;
  }

  public void setProductID( String productID ) {
    this.productID = productID;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public void setVersionMajor( String versionMajor ) {
    this.versionMajor = versionMajor;
  }

  public void setVersionMinor( String versionMinor ) {
    this.versionMinor = versionMinor;
  }

  public void setVersionRelease( String versionRelease ) {
    this.versionRelease = versionRelease;
  }

  public void setVersionMilestone( String versionMilestone ) {
    this.versionMilestone = versionMilestone;
  }

  public void setVersionBuild( String versionBuild ) {
    this.versionBuild = versionBuild;
  }

  /**
   * Sets the version fields by passing the whole string and parsing it. <br/>
   * NOTE: spaces will be changed to dots before parsing
   * 
   * @param version
   *          the version number (1.6.0.GA.500 or 1.6.0-RC2.400)
   */
  public void setVersion( String version ) {
    // Parse the version
    if ( version != null ) {
      String[] pieces = version.replace( '-', '.' ).split( "\\." ); //$NON-NLS-1$
      switch ( pieces.length ) {
        case 9: // just in case
        case 8: // just in case
        case 7: // just in case
        case 6: // just in case
        case 5:
          setVersionBuild( pieces[4] ); // intentional fall through
        case 4:
          setVersionMilestone( pieces[3] ); // intentional fall through
        case 3:
          setVersionRelease( pieces[2] ); // intentional fall through
        case 2:
          setVersionMinor( pieces[1] ); // intentional fall through
        case 1:
          setVersionMajor( pieces[0] ); // intentional fall through
        default: // do nothing
      }
    }
  }
}
