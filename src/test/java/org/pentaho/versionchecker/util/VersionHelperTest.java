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
