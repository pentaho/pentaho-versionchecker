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


package org.pentaho.versionchecker.util;

@SuppressWarnings( "rawtypes" )
public interface IVersionHelper {

  public String getVersionInformation();

  public String getVersionInformation( Class clazz );

}
