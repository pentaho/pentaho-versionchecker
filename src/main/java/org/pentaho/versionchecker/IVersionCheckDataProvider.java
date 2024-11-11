/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.versionchecker;

import java.util.Map;

@SuppressWarnings( "rawtypes" )
public interface IVersionCheckDataProvider {

  // DEPTH MASKS
  static final int DEPTH_ALL_MASK = 1;
  static final int DEPTH_VERBOSE_MASK = 2;
  static final int DEPTH_MAJOR_MASK = 4;
  static final int DEPTH_MINOR_MASK = 8;
  static final int DEPTH_RC_MASK = 16;
  static final int DEPTH_GA_MASK = 32;
  static final int DEPTH_MILESTONE_MASK = 64;
  static final int DEPTH_WINDOWS_MASK = 128;
  static final int DEPTH_MAC_MASK = 256;
  static final int DEPTH_LINUX_MASK = 512;

  public String getBaseURL();

  public String getApplicationID();

  public String getApplicationVersion();

  public int getDepth();

  public Map getExtraInformation();

  public void setVersionRequestFlags( int value );

}
