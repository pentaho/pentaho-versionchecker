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
