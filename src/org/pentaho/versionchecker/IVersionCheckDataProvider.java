package org.pentaho.versionchecker;

import java.util.Map;

public interface IVersionCheckDataProvider {
  String getBaseURL();
  
  String getGuid();
  String getApplicationID();
  String getApplicationVersion();
  Map getExtraInformation();
}
