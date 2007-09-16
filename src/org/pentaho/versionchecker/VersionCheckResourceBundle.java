package org.pentaho.versionchecker;

import java.util.ResourceBundle;

public class VersionCheckResourceBundle {
  // Don't let anyone create an instance of this class ... it is a waste
  private VersionCheckResourceBundle() {
  }

  /**
   * Name of the resource bundle used by the version checker
   */
  private static final String BUNDLE_NAME = "org.pentaho.util.versionchecker.VersionChecker"; //$NON-NLS-1$

  /**
   * Resource bundle used by ther vesion checker
   */
  private static final ResourceBundle resources = ResourceBundle.getBundle(BUNDLE_NAME);

  /**
   * Retrieves a resource from the version manager resource bundle. 
   * @param key the key for the resource to be retrieved
   * @return the resource from the bundle. If the resource can not be found,
   * <code>null</code> will be returned.
   */
  public static String getString(final String key) {
     String result = null;
     if (resources != null) {
       result = resources.getString(key);
     } 
     return result;
  }
}
