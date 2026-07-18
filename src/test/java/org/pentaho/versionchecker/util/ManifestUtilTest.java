/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.versionchecker.util;

import org.junit.Test;

import java.util.jar.Manifest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by bmorrise on 10/28/15.
 */
public class ManifestUtilTest {

  @Test
  public void testGetManifest() {
    Manifest manifest = ManifestUtil.getManifest();
    assertThat( manifest, nullValue() );
  }

}
