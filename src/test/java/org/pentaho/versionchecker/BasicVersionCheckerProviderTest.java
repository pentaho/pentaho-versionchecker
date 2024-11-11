/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.versionchecker;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.pentaho.versionchecker.IVersionCheckDataProvider.*;

/**
 * Created by bmorrise on 10/28/15.
 */
public class BasicVersionCheckerProviderTest {

  BasicVersionCheckerProvider basicVersionCheckerProvider;

  @Rule
  public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

  @Before
  public void setUp() {
    basicVersionCheckerProvider = new BasicVersionCheckerProvider( BasicVersionCheckerProvider.class );
  }

  @Test
  public void testGetApplicationId() {
    assertThat( basicVersionCheckerProvider.getApplicationID(), notNullValue() );
  }

  @Test
  public void testGetApplicationVersion() {
    assertThat( basicVersionCheckerProvider.getApplicationVersion(), notNullValue() );
  }

  @Test
  public void testGetDepth() {
    int flags = DEPTH_VERBOSE_MASK | DEPTH_RC_MASK; // 18
    basicVersionCheckerProvider.setVersionRequestFlags( flags );
    System.setProperty( "os.name", "windows" );
    assertEquals( flags + DEPTH_WINDOWS_MASK, basicVersionCheckerProvider.getDepth() );
    System.setProperty( "os.name", "mac" );
    assertEquals( flags + DEPTH_MAC_MASK, basicVersionCheckerProvider.getDepth() );
    System.setProperty( "os.name", "linux" );
    assertEquals( flags + DEPTH_LINUX_MASK, basicVersionCheckerProvider.getDepth() );
    System.setProperty( "os.name", "" );
    assertEquals( flags + DEPTH_ALL_MASK, basicVersionCheckerProvider.getDepth() );
    System.clearProperty( "os.name" );
    assertEquals( flags + DEPTH_ALL_MASK, basicVersionCheckerProvider.getDepth() );
  }

}
