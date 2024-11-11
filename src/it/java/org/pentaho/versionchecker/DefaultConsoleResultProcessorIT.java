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
