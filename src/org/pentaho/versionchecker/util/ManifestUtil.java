package org.pentaho.versionchecker.util;

import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Set of utility methods related to the manifest file.
 * <br/>
 * NOTE: if the manifest file can not be retrieved, these methods will not work
 * and will return <code>null</code>. The most common case for this is the the 
 * code is being run outside of a jar file.
 * @author dkincade
 */
public class ManifestUtil {
  /**
   * Retrieves the manifest information for the jar file which contains 
   * this utility class.
   * @return The Manifest file for the jar file which contains this utility class,
   * or <code>null</code> if the code is not in a jar file.
   */
  public static Manifest getManifest() {
    return getManifest(ManifestUtil.class);
  }
  
  /**
   * Retrieves the manifest information for the jar file which contains 
   * the specified class.
   * @return The Manifest file for the jar file which contains the specified class,
   * or <code>null</code> if the code is not in a jar file.
   */
  public static Manifest getManifest(Class clazz) {
    try {
      final URL codeBase = clazz.getProtectionDomain().getCodeSource().getLocation();
      if (codeBase.getPath().endsWith(".jar")) { //$NON-NLS-1$
        final JarInputStream jin = new JarInputStream(codeBase.openStream());
        return jin.getManifest();
      }
    } catch (Exception e) {
      // TODO handle this exception
    }
    return null;
  }
}
