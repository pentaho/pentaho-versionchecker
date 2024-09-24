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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

/**
 * This is a copy of org.pentaho.util.UUIDUtil. We should move UUIDUtil to a commons project and remove this.
 * 
 * @author Will Gorman
 */
public class UUIDUtil {
  private static Log log = LogFactory.getLog( UUIDUtil.class );

  static boolean nativeInitialized = false;

  static UUIDGenerator ug;

  static org.safehaus.uuid.EthernetAddress eAddr;

  static {
    init();
  }

  // for testing purposes
  static void init() {
    ug = UUIDGenerator.getInstance();
    // Try loading the EthernetAddress library. If this fails, then fallback
    // to
    // using another method for generating UUID's.
    /*
     * This is always going to fail at the moment try { System.loadLibrary("EthernetAddress"); //$NON-NLS-1$
     * nativeInitialized = true; } catch (Throwable t) { //
     * log.warn(Messages.getErrorString("UUIDUtil.ERROR_0001_LOADING_ETHERNET_ADDRESS") ); //$NON-NLS-1$ //$NON-NLS-2$
     * // Ignore for now. }
     */

    // not used
    /*if ( nativeInitialized ) {
      try {
        com.ccg.net.ethernet.EthernetAddress ea = com.ccg.net.ethernet.EthernetAddress.getPrimaryAdapter();
        eAddr = new org.safehaus.uuid.EthernetAddress( ea.getBytes() );
      } catch ( Exception ex ) {
        log.error( VersionCheckResourceBundle.getString( "UUIDUtil.ERROR_0002_GET_MAC_ADDR" ), ex ); //$NON-NLS-1$
      } catch ( UnsatisfiedLinkError ule ) {
        log.error( VersionCheckResourceBundle.getString( "UUIDUtil.ERROR_0002_GET_MAC_ADDR" ), ule ); //$NON-NLS-1$
        nativeInitialized = false;
      }
    }*/

    /*
     * Add support for running in clustered environments. In this way, the MAC address of the running server can be
     * added to the environment with a -DMAC_ADDRESS=00:50:56:C0:00:01
     */
    if ( eAddr == null ) {
      String macAddr = System.getProperty( "MAC_ADDRESS" ); //$NON-NLS-1$
      if ( macAddr != null ) {
        // On Windows machines, people would be inclined to get the MAC
        // address with ipconfig /all. The format of this would be
        // something like 00-50-56-C0-00-08. So, replace '-' with ':' before
        // creating the address.
        //
        macAddr = macAddr.replace( '-', ':' );
        eAddr = new org.safehaus.uuid.EthernetAddress( macAddr );
      }
    }

    if ( eAddr == null ) {
      // Still don't have an Ethernet Address - generate a dummy one.
      eAddr = ug.getDummyAddress();
    }

    // Generate a UUID to make sure everything is running OK.
    UUID olduuId = ug.generateTimeBasedUUID( eAddr );
    if ( olduuId == null ) {
      log.error( VersionCheckResourceBundle.getString( "UUIDUtil.ERROR_0003_GENERATEFAILED" ) ); //$NON-NLS-1$
    }
  }

  public static String getUUIDAsString() {
    return getUUID().toString();
  }

  public static UUID getUUID() {
    UUID uuId = ug.generateTimeBasedUUID( eAddr );
    // while (uuId.toString().equals(olduuId.toString())) {
    // uuId = ug.generateTimeBasedUUID(eAddr);
    // }
    // olduuId = uuId;
    return uuId;
  }

}
