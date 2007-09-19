package org.pentaho.versionchecker;

import java.util.Map;

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
  
  String getBaseURL();
  String getApplicationID();
  String getApplicationVersion();
  int getDepth();
  Map getExtraInformation();
}
