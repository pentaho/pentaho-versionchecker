/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
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

}
