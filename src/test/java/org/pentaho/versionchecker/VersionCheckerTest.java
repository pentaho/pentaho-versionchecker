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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings( { "unchecked", "rawtypes", "deprecation" } )
public class VersionCheckerTest extends TestCase {

  @Override
  protected void setUp() {
    recursiveDelete( VersionChecker.propsDirectory );
    // re-initialize the VersionChecker which will clear all info..
    VersionChecker.init();
  }

  @Override
  protected void tearDown() {
    recursiveDelete( VersionChecker.propsDirectory );
  }

  private void recursiveDelete( File file ) {
    File[] files = file.listFiles();
    if ( files != null ) {
      for ( File each : files ) {
        recursiveDelete( each );
      }
    }
    file.delete();
  }

  public void testComputeVi() {
    String result = VersionChecker.computeVI( null );
    assertNotNull( result );
  }

  public void testPerformCheck() {
    MockHttpClient httpClient = new MockHttpClient();
    MockGetMethod getMethod = new MockGetMethod();
    MockDataProvider dataProvider = new MockDataProvider();

    VersionChecker vc = new VersionChecker( httpClient, getMethod );
    vc.setDataProvider( dataProvider );
    vc.performCheck( true );

    assertEquals( 1, getMethod.releaseCount );
  }

  /**
   * Tests the setDataProvider method and makes sure it works with null and without null
   */
  public void testSetDataProvider() {
    MockDataProvider dataProvider = new MockDataProvider();
    MockHttpClient httpClient = new MockHttpClient();
    VersionChecker vc = new VersionChecker( httpClient, null );

    vc.setDataProvider( dataProvider );
    vc.performCheck( false );
    assertEquals( 2, dataProvider.getApplicationIDCallCount );

    vc.setDataProvider( null );
    vc.performCheck( false );
    assertEquals( 2, dataProvider.getApplicationIDCallCount );

    vc.setDataProvider( dataProvider );
    vc.performCheck( false );
    assertEquals( 4, dataProvider.getApplicationIDCallCount );

  }

  public void testUnwritableUserHomeDirectory() {
    String backupUserHomeFolder = System.getProperty( "user.home" ); //$NON-NLS-1$
    try {
      // Clear user.home property
      System.setProperty( "user.home", "" ); //$NON-NLS-1$ //$NON-NLS-2$
      // Next, re-initialize the VersionChecker which will clear all info..
      VersionChecker.init();
      assertEquals( VersionChecker.getIsWritable(), true );
      String versionCheckerPropertiesDirectory = null;
      try {
        versionCheckerPropertiesDirectory = VersionChecker.getPropertiesDirectory();
      } catch ( IOException ex ) {
        versionCheckerPropertiesDirectory = null;
      }

      // This checks that the fallbacks work.
      assertNotNull( versionCheckerPropertiesDirectory );

    } finally {
      System.setProperty( "user.home", backupUserHomeFolder ); //$NON-NLS-1$
    }
  }

  /**
   * Tests the addResultHandler and removeRestulHandler methods
   */
  public void testResultHandler() {
    MockHttpClient httpClient = new MockHttpClient();
    MockGetMethod getMethod = new MockGetMethod();
    MockResultHandler resultsHandler1 = new MockResultHandler();
    MockResultHandler resultsHandler2 = new MockResultHandler();
    VersionChecker vc = new VersionChecker( httpClient, getMethod );

    vc.addResultHandler( null );
    assertEquals( 0, vc.resultHandlers.size() );

    vc.addResultHandler( resultsHandler1 );
    vc.performCheck( false );
    assertEquals( 1, resultsHandler1.processResultsCount );
    assertEquals( getMethod.responseBody, resultsHandler1.results );

    vc.addResultHandler( resultsHandler2 );
    vc.removeResultHandler( null );
    resultsHandler1.results = null;
    vc.performCheck( false );
    assertEquals( 2, resultsHandler1.processResultsCount );
    assertEquals( 1, resultsHandler2.processResultsCount );
    assertEquals( getMethod.responseBody, resultsHandler1.results );
    assertEquals( getMethod.responseBody, resultsHandler2.results );

    vc.removeResultHandler( resultsHandler1 );
    resultsHandler1.results = null;
    resultsHandler2.results = null;
    vc.performCheck( false );
    assertEquals( 2, resultsHandler1.processResultsCount );
    assertEquals( 2, resultsHandler2.processResultsCount );
    assertEquals( null, resultsHandler1.results );
    assertEquals( getMethod.responseBody, resultsHandler2.results );

    resultsHandler2.throwException = true;
    vc.performCheck( false );
    assertEquals( 3, resultsHandler2.processResultsCount );
  }

  /**
   * Tests the error handler capabilities
   */
  public void testErrorHandler() {
    MockDataProvider dataProvider = new MockDataProvider();
    dataProvider.baseURL = "htp://test.pentaho.org/testing_page_doesnot_exist"; //$NON-NLS-1$
    VersionChecker vc = new VersionChecker();
    vc.setDataProvider( dataProvider );
    MockErrorHandler errorHandler1 = new MockErrorHandler();
    MockErrorHandler errorHandler2 = new MockErrorHandler();
    vc.addErrorHandler( errorHandler1 );
    vc.performCheck( false );
    assertEquals( 1, errorHandler1.errorCount );

    vc.addErrorHandler( errorHandler2 );
    vc.addErrorHandler( null );
    vc.performCheck( false );
    assertEquals( 2, errorHandler1.errorCount );
    assertEquals( 1, errorHandler2.errorCount );

    vc.removeErrorHandler( errorHandler1 );
    vc.removeErrorHandler( null );
    vc.performCheck( false );
    assertEquals( 2, errorHandler1.errorCount );
    assertEquals( 2, errorHandler2.errorCount );

    errorHandler2.throwException = true;
    vc.performCheck( false );
    assertEquals( 3, errorHandler2.errorCount );
  }

  /**
   * Tests the method that sets the URL in the HttpMethod
   */
  public void testSetURL() throws URISyntaxException {
    try {
      MockGetMethod httpMethod = new MockGetMethod();
      VersionChecker vc = new VersionChecker( null, httpMethod );
      vc.setURL( httpMethod, null );
      assertEquals( 1, httpMethod.setURICount );
      assertEquals( vc.getDefaultURL(), httpMethod.uri.toString() );

      MockDataProvider dataProvider = new MockDataProvider();
      vc.setDataProvider( dataProvider );
      vc.setURL( httpMethod, null );
      assertEquals( 2, httpMethod.setURICount );
      assertTrue( httpMethod.uri.toString().startsWith( "http://test.pentho.org:8080/sample?" ) ); //$NON-NLS-1$

      dataProvider.extraInformation = new HashMap() {{
        put( "a", "a" );
        put( "b", "b" );
      }};
      vc.setURL( httpMethod, null );
      assertEquals( 3, httpMethod.setURICount );
      assertTrue( httpMethod.uri.toString().contains( "a=a&b=b" ) );

      dataProvider.extraInformation = null;
      vc.setURL( httpMethod, null );
      assertEquals( 4, httpMethod.setURICount );

    } catch ( Exception e ) {
      fail( e.getMessage() );
    }
  }

  public void testCreateURL() throws URISyntaxException {
    HttpGet httpMethod = new HttpGet();
    VersionChecker versionChecker = new VersionChecker( null, httpMethod );
    Map params = new HashMap();
    String baseUrl = "http://www.pentaho.org/"; //$NON-NLS-1$
    String result = versionChecker.buildURI( baseUrl, params ).toString();
    assertEquals( baseUrl, result );
    result = versionChecker.buildURI( baseUrl, null ).toString();
    assertEquals( baseUrl, result );

    String junk = "a1B2 !@#$%^&*()_-+={[}]|\\:;\"'<,>.?/~`"; //$NON-NLS-1$
    String encodedJunk = URLEncoder.encode( junk );
    params.put( "junk", junk ); //$NON-NLS-1$
    result = versionChecker.buildURI( baseUrl, params ).toString();
    assertEquals( baseUrl + "?junk=" + encodedJunk, result ); //$NON-NLS-1$

    params.put( "one", "one" ); //$NON-NLS-1$ //$NON-NLS-2$
    result = versionChecker.buildURI( baseUrl, params ).toString();
    assertTrue( result.indexOf( "?one=one&" ) > 0 || result.indexOf( "&one=one" ) > 0 ); //$NON-NLS-1$ //$NON-NLS-2$

    params.clear();
    params.put( "two", "two" ); //$NON-NLS-1$ //$NON-NLS-2$
    baseUrl += "?one=one"; //$NON-NLS-1$
    result = versionChecker.buildURI( baseUrl, params ).toString();
    assertEquals( baseUrl + "&two=two", result ); //$NON-NLS-1$

    params.clear();
    params.put( "two", "two" ); //$NON-NLS-1$ //$NON-NLS-2$
    baseUrl += "&"; //$NON-NLS-1$
    result = versionChecker.buildURI( baseUrl, params ).toString();
    assertEquals( baseUrl + "two=two", result ); //$NON-NLS-1$

    params.clear();
    params.put( "a", "a" );
    params.put( null, null );
    baseUrl = "http://www.pentaho.org/sample?";
    result = versionChecker.buildURI( baseUrl, params ).toString();
    assertEquals( baseUrl + "a=a", result );
  }

  public void testCheckForUpdates() {
    String xmlTest = "<vercheck protocol=\"1.0\"/>"; //$NON-NLS-1$
    IVersionCheckDataProvider dataProvider = new MockDataProvider();
    Properties props = new Properties();
    String output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, false );
    assertEquals( xmlTest, output );
    assertEquals( props.size(), 0 );

    xmlTest = "<vercheck protocol=\"1.0\">\n" + //$NON-NLS-1$
      "<product id=\"\"><update title=\"\" version=\"\" type=\"\"/></product>\n" + //$NON-NLS-1$
      "</vercheck>"; //$NON-NLS-1$

    output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, false );
    assertEquals( xmlTest, output );
    assertEquals( props.getProperty( "versionchk.prd.1.6.0-RC1.123.update" ), "  " ); //$NON-NLS-1$ //$NON-NLS-2$

    output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, true );
    assertEquals( output, "<vercheck protocol=\"1.0\"/>" ); //$NON-NLS-1$
    assertEquals( props.getProperty( "versionchk.prd.1.6.0-RC1.123.update" ), "  " ); //$NON-NLS-1$ //$NON-NLS-2$

    xmlTest = "<vercheck protocol=\"1.0\">\n" + //$NON-NLS-1$
      "<product id=\"POBS\">\n" + //$NON-NLS-1$
      "<update title=\"Pentaho BI Suite\" version=\"1.1\" type=\"GA\"/>\n" + //$NON-NLS-1$
      "<update title=\"Pentaho BI Suite\" version=\"1.2\" type=\"GA\"/>\n" + //$NON-NLS-1$
      "</product>\n" + //$NON-NLS-1$
      "</vercheck>"; //$NON-NLS-1$

    props = new Properties();

    output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, true );
    assertEquals( xmlTest, output );
    assertEquals(
      props.getProperty( "versionchk.prd.1.6.0-RC1.123.update" ),
      "Pentaho BI Suite 1.1 GA,Pentaho BI Suite 1.2 GA" ); //$NON-NLS-1$ //$NON-NLS-2$

    output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, false );
    assertEquals( xmlTest, output );
    assertEquals(
      props.getProperty( "versionchk.prd.1.6.0-RC1.123.update" ),
      "Pentaho BI Suite 1.1 GA,Pentaho BI Suite 1.2 GA" ); //$NON-NLS-1$ //$NON-NLS-2$

    output = VersionChecker.checkForUpdates( dataProvider, xmlTest, props, true );
    assertEquals( output, "<vercheck protocol=\"1.0\"/>" ); //$NON-NLS-1$
    assertEquals(
      props.getProperty( "versionchk.prd.1.6.0-RC1.123.update" ),
      "Pentaho BI Suite 1.1 GA,Pentaho BI Suite 1.2 GA" ); //$NON-NLS-1$ //$NON-NLS-2$

  }

  /**
   * Mock GetMethod that allows for the tracking of the URL and the returning of sample results
   */
  private class MockGetMethod extends HttpRequestBase {
    private int releaseCount = 0;

    public String responseBody = "<xml>sample</xml>"; //$NON-NLS-1$

    private URI uri = null;

    public int setURICount = 0;

    public void setURI( URI uri ) {
      this.uri = uri;
      ++setURICount;
      super.setURI( uri );
    }

    @Override
    public String getMethod() {
      return null;
    }

    public void releaseConnection() {
      releaseCount++;
    }
  }

  /**
   * Mock HttpClient class that prevents the executeMethod method from executing
   */
  private class MockHttpClient implements HttpClient {
    @Override
    public HttpParams getParams() {
      return null;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
      return null;
    }

    @Override
    public HttpResponse execute( HttpUriRequest request ) throws IOException {
      BasicHttpResponse basicHttpResponse =
        new BasicHttpResponse( HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "reason" ); //$NON-NLS-1$
      BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
      basicHttpEntity.setContentType( new BasicHeader( "Content-Type", "text/plain" ) ); //$NON-NLS-1$ //$NON-NLS-2$
      String xml = "<xml>sample</xml>"; //$NON-NLS-1$
      byte[] xmlBytes = xml.getBytes();
      basicHttpEntity.setContent( new ByteArrayInputStream( xmlBytes ) );
      basicHttpResponse.setEntity( basicHttpEntity );
      return basicHttpResponse;

    }

    @Override
    public HttpResponse execute( HttpUriRequest request, HttpContext context )
      throws IOException {
      return null;
    }

    @Override
    public HttpResponse execute( HttpHost target, HttpRequest request )
      throws IOException {
      return null;
    }

    @Override
    public HttpResponse execute( HttpHost target, HttpRequest request, HttpContext context )
      throws IOException {
      return null;
    }

    @Override
    public <T> T execute( HttpUriRequest request, ResponseHandler<? extends T> responseHandler )
      throws IOException {
      return null;
    }

    @Override
    public <T> T execute( HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context )
      throws IOException {
      return null;
    }

    @Override
    public <T> T execute( HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler )
      throws IOException {
      return null;
    }

    @Override
    public <T> T execute( HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler,
                          HttpContext context ) throws IOException {
      return null;
    }
  }

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
      return DEPTH_VERBOSE_MASK | DEPTH_MINOR_MASK | DEPTH_RC_MASK | DEPTH_WINDOWS_MASK; // 154
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
      throw new UnsupportedOperationException();
    }
  }

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
  }

  private class MockErrorHandler implements IVersionCheckErrorHandler {

    public int errorCount = 0;

    public boolean throwException = false;

    public void handleException( Exception e ) {
      ++errorCount;
      if ( throwException ) {
        throw new NullPointerException( "Test" ); //$NON-NLS-1$
      }
    }
  }
}
