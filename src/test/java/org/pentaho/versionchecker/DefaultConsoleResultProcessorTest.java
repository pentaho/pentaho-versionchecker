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
