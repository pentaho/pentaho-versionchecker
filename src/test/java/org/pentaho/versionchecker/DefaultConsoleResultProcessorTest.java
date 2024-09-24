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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class DefaultConsoleResultProcessorTest extends TestCase {

  public void testSetOutput() {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream( bs );

    try {
      rp.setOutput( ps );
    } catch ( Exception e ) {
      fail();
    }

    try {
      rp.setOutput( null );
      fail();
    } catch ( Exception ignored ) {
      // Catch the exception
    }
  }

  public void testSetError() {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream( bs );

    try {
      rp.setError( ps );
    } catch ( Exception e ) {
      fail();
    }

    try {
      rp.setError( null );
      fail();
    } catch ( Exception e ) {
      // Catch the exception
    }
  }

  public void testHandleException() {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream( bs );

    rp.setError( ps );
    rp.handleException( new ExceptionMock( ) );

    assertEquals( "Exception Mock", bs.toString().trim() );
  }

  public void testProcessResults( ) {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream( bs );

    rp.setOutput( ps );
    rp.processResults( "test results" );

    assertEquals( "test results", bs.toString().trim() );
  }

  class ExceptionMock extends Exception {
    @Override public String getMessage() {
      return "Exception Mock";
    }
    @Override public void printStackTrace( PrintStream s ) { }
  }
}
