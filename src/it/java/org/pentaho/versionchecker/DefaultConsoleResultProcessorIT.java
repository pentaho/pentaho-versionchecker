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

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

public class DefaultConsoleResultProcessorIT extends TestCase implements IVersionCheckDataProvider,
    IVersionCheckErrorHandler, IVersionCheckResultHandler {
  protected Throwable error = null;

  protected String results = null;

  public void testDefaultConsoleResultProcessor() {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream( bs );
    rp.setOutput( ps );

    VersionChecker vc = new VersionChecker();
    vc.setDataProvider( this );
    vc.addErrorHandler( this );
    vc.addResultHandler( this );
    vc.addResultHandler( rp );
    vc.performCheck( true );

    assertNull( error );
    assertNotNull( results );
  }

  public String getApplicationID() {
    return "POBS"; //$NON-NLS-1$
  }

  public String getApplicationVersion() {
    return "1.6.0.RC1.400"; //$NON-NLS-1$
  }

  public String getBaseURL() {
    // Use the default
    return null;
  }

  @SuppressWarnings( "rawtypes" )
  public Map getExtraInformation() {
    return null;
  }

  public int getDepth() {
    return DEPTH_VERBOSE_MASK | DEPTH_MINOR_MASK | DEPTH_RC_MASK | DEPTH_WINDOWS_MASK; // 154
  }

  public void handleException( Exception e ) {
    e.printStackTrace();
    error = e;
  }

  public void processResults( String localResults ) {
    System.out.println( "RESULTS: " + localResults + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
    this.results = localResults;
  }

  public void setVersionRequestFlags( int value ) {
    // TODO Auto-generated method stub

  }
}
