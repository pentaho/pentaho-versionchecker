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
