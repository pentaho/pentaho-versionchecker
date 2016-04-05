/*
 * Copyright 2002 - 2013 Pentaho Corporation.  All rights reserved.
 * 
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. TThe Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package org.pentaho.versionchecker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;

@SuppressWarnings( { "unchecked", "rawtypes", "deprecation" } )
public class VersionCheckerTest extends TestCase {

  /**
   * Tests the setDataProvider method and makes sure it works with null and without null
   */
  public void testSetDataProvider() {
    
  }

  public void testUnwritableUserHomeDirectory() {
    
  }

  /**
   * Tests the addResultHandler and removeRestulHandler methods
   */
  public void testResultHandler() {
    
  }

  /**
   * Tests the error handler capabilities
   */
  public void testErrorHandler() {
    
  }

  /**
   * Tests the method that sets the URL in the HttpMethod
   */
  public void testSetURL() {
    
  }

  public void testCreateURL() {
    
  }

  public void testCheckForUpdates() {

  }

  /**
   * Mock GetMethod that allows for the tracking of the URL and the returning of sample results
   */
  private class MockGetMethod extends GetMethod {

    public String responseBody = "sample response"; //$NON-NLS-1$

    public String getResponseBodyAsString() {
      return responseBody;
    }

    private URI _uri = null;

    public int setURICount = 0;

    public void setURI( URI uri ) throws URIException {
      _uri = uri;
      ++setURICount;
      super.setURI( uri );
    }

  };

  /**
   * Mock HttpClient class that prevents the executeMethod method from executing
   */
  private class MockHttpClient extends HttpClient {
    public int responseCode = HttpURLConnection.HTTP_OK;

    public int executeMethod( HostConfiguration arg0, HttpMethod arg1, HttpState arg2 ) throws IOException,
      HttpException {
      return responseCode;
    }

    public int executeMethod( HostConfiguration arg0, HttpMethod arg1 ) throws IOException, HttpException {
      return responseCode;
    }

    public int executeMethod( HttpMethod arg0 ) throws IOException, HttpException {
      return responseCode;
    }
  };

  /**
   * Mock IVersionCheckDataProvider implementation that allows for canned responses and method call counting
   */
  private class MockDataProvider implements IVersionCheckDataProvider {
    public int getApplicationIDCallCount = 0;

    public String applicationID = "prd"; //$NON-NLS-1$

    public String getApplicationID() {
      ++getApplicationIDCallCount;
      return applicationID;
    }

    public String applicationVersion = "1.6.0-RC1.123"; //$NON-NLS-1$

    public String getApplicationVersion() {
      return applicationVersion;
    }

    public int getDepth() {
      return 154;
    }

    public String baseURL = "http://test.pentho.org:8080/sample"; //$NON-NLS-1$

    public String getBaseURL() {
      return baseURL;
    }

    public HashMap extraInformation = new HashMap();

    public Map getExtraInformation() {
      return extraInformation;
    }

    public void setVersionRequestFlags( int value ) {
      // TODO Auto-generated method stub

    }
  };

  private class MockResultHandler implements IVersionCheckResultHandler {
    public String results = null;

    public int processResultsCount = 0;

    public boolean throwException = false;

    public void processResults( String resultsStr ) {
      ++processResultsCount;
      this.results = resultsStr;
      if ( throwException ) {
        throw new NullPointerException( "Test" ); //$NON-NLS-1$
      }
    }
  };

  private class MockErrorHandler implements IVersionCheckErrorHandler {

    public int errorCount = 0;

    public boolean throwException = false;

    public void handleException( Exception e ) {
      ++errorCount;
      if ( throwException ) {
        throw new NullPointerException( "Test" ); //$NON-NLS-1$
      }
    }
  };
}
