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

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by bmorrise on 10/28/15.
 */
public class VersionHelperTest {

  VersionHelper versionHelper;

  @Before
  public void setUp() throws Exception {
    versionHelper = new VersionHelper();
  }

  @Test
  public void testGetVersionInformation() throws Exception {
    String versionInfo = versionHelper.getVersionInformation();
    assertThat( versionInfo, notNullValue() );
  }

  @Test
  public void testGetVersionInfo() throws Exception {
    VersionInfo versionInfo = VersionHelper.getVersionInfo();
    assertThat( versionInfo.getVersionMajor(), notNullValue() );
    assertThat( versionInfo.getVersionMinor(), notNullValue() );
    assertThat( versionInfo.getVersionBuild(), notNullValue() );
  }

  @Test
  public void testCreateVersionInfo() {
    VersionInfo versionInfo = VersionHelper.createVersionInfo( new MockManifest() );
    assertEquals( "PVC", versionInfo.getProductID() );
    assertEquals( "Pentaho Version Checker", versionInfo.getTitle() );
    assertEquals( "1", versionInfo.getVersionMajor() );
    assertEquals( "6", versionInfo.getVersionMinor() );
    assertEquals( "0", versionInfo.getVersionRelease() );
    assertEquals( "RC2", versionInfo.getVersionMilestone() );
    assertEquals( "400", versionInfo.getVersionBuild() );
  }

  private class MockManifest extends Manifest {
    private final Attributes attributes = new Attributes() {{
        put( new Attributes.Name( "Implementation-ProductID" ), "PVC" );
        put( new Attributes.Name( "Implementation-Title" ), "Pentaho Version Checker" );
        put( new Attributes.Name( "Implementation-Version" ), "1.6.0-RC2.400" );
      }};

    @Override public Attributes getMainAttributes() {
      return attributes;
    }
  }

}
