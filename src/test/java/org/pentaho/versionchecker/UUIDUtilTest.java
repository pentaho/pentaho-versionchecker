/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.versionchecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by bmorrise on 10/28/15.
 */
public class UUIDUtilTest {
  @Rule
  public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

  public void setUp() {
    UUIDUtil.eAddr = null;
    UUIDUtil.init();
  }

  @Test
  public void testGetUUIDAsString() {
    setUp();
    String uuid = UUIDUtil.getUUIDAsString();
    assertThat( uuid, notNullValue() );
  }

  @Test
  public void testGetUUIDWithPropertyAddress() {
    System.setProperty( "MAC_ADDRESS", "00:00:00:00:00:00" );
    setUp();
    String uuid = UUIDUtil.getUUIDAsString();
    assertThat( uuid, notNullValue() );
  }

}
