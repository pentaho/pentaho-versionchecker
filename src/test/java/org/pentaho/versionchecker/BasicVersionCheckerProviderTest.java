/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
