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

package org.pentaho.versionchecker.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by bmorrise on 10/28/15.
 */
public class VersionInfoTest {

  private static String PRODUCT_ID = "Product ID";
  private static String TITLE = "Title";
  private static String VERSION_NUMBER_ONE = "1.6.0.GA.500";
  private static String VERSION_NUMBER_TWO = "1.6.0-RC2.400";
  private static String TO_STRING = "fromManifest       = [true]\n" + "productID          = [Product ID]\n"
      + "title              = [Title]\n" + "versionMajor       = [1]\n" + "versionMinor       = [6]\n"
      + "versionRelease     = [0]\n" + "versionMilestone   = [GA]\n" + "versionBuild       = [500]\n"
      + "getVersionNumber() = [1.6.0.GA.500]\n";

  VersionInfo versionInfo;

  @Before
  public void setUp() {
    versionInfo = new VersionInfo();
  }

  @Test
  public void testSetVersion() {
    versionInfo.setVersion( VERSION_NUMBER_ONE );

    assertThat( "1", equalTo( versionInfo.getVersionMajor() ) );
    assertThat( "6", equalTo( versionInfo.getVersionMinor() ) );
    assertThat( "0", equalTo( versionInfo.getVersionRelease() ) );
    assertThat( "GA", equalTo( versionInfo.getVersionMilestone() ) );
    assertThat( "500", equalTo( versionInfo.getVersionBuild() ) );

    versionInfo.setVersion( VERSION_NUMBER_TWO );

    assertThat( "1", equalTo( versionInfo.getVersionMajor() ) );
    assertThat( "6", equalTo( versionInfo.getVersionMinor() ) );
    assertThat( "0", equalTo( versionInfo.getVersionRelease() ) );
    assertThat( "RC2", equalTo( versionInfo.getVersionMilestone() ) );
    assertThat( "400", equalTo( versionInfo.getVersionBuild() ) );
  }

  @Test public void testGetVersionNumber() {
    versionInfo.setVersion( VERSION_NUMBER_ONE );

    assertThat( VERSION_NUMBER_ONE, equalTo( versionInfo.getVersionNumber() ) );
  }

  @Test
  public void testToString() {
    versionInfo.setVersion( VERSION_NUMBER_ONE );
    versionInfo.setFromManifest( true );
    versionInfo.setTitle( TITLE );
    versionInfo.setProductID( PRODUCT_ID );

    assertThat( TO_STRING, equalTo( versionInfo.toString() ) );
    assertThat( true, equalTo( versionInfo.isFromManifest() ) );
    assertThat( TITLE, equalTo( versionInfo.getTitle() ) );
    assertThat( PRODUCT_ID, equalTo( versionInfo.getProductID() ) );

  }

}
