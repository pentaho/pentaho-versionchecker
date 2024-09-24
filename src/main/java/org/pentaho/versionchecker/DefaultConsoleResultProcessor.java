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

import java.io.PrintStream;

/**
 * Default result processor for the VersionCheck process which outputs the results to the console's output (System.out
 * for )
 * 
 * @author dkincade
 */
public class DefaultConsoleResultProcessor implements IVersionCheckErrorHandler, IVersionCheckResultHandler {
  /**
   * The print stream that is used for displaying the results. By default, this is <code>System.out</code>
   */
  private PrintStream out = System.out;

  /**
   * The print stream that is used for displaying the errors. By default, this is <code>System.err</code>
   */
  private PrintStream err = System.err;

  /**
   * Sets the print stream to use for displaying the results.
   * 
   * @throws IllegalArgumentExcpetion
   *           indicates the parameter passed is <code>null</code>
   */
  public void setOutput( PrintStream out ) {
    if ( out != null ) {
      this.out = out;
    } else {
      throw new IllegalArgumentException( VersionCheckResourceBundle
          .getString( "DefaultConsoleresultProcessor.ERROR_0001_NULL_PARAMETERS" ) ); //$NON-NLS-1$
    }
  }

  /**
   * Sets the print stream to use for displaying the errors.
   * 
   * @throws IllegalArgumentExcpetion
   *           indicates the parameter passed is <code>null</code>
   */
  public void setError( PrintStream err ) {
    if ( err != null ) {
      this.err = err;
    } else {
      throw new IllegalArgumentException( VersionCheckResourceBundle
          .getString( "DefaultConsoleresultProcessor.ERROR_0001_NULL_PARAMETERS" ) ); //$NON-NLS-1$
    }
  }

  /**
   * Display error information
   */
  public void handleException( Exception e ) {
    err.println( e.getMessage() );
    e.printStackTrace( err );
  }

  /**
   * Displays the results
   */
  public void processResults( String results ) {
    out.println( results );
  }
}
