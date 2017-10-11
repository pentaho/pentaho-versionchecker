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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Checks for updated software information for the specified applications. <br/>
 * This class gets version information from the supplied <code>IVersionCheckDataProvider</code> and communicates the
 * results to the list of supplied <code>IVersionCheckResultHandler</code>. If an error occurs during processing, the
 * error information will be passed along to the list of supplied <code>IVersionCheckErrorHandler</code>.
 *
 * @author dkincade
 */
@SuppressWarnings( { "rawtypes", "unchecked" } )
public class VersionChecker {

  private static final String PENTAHO_DIR = ".pentaho"; //$NON-NLS-1$
  private static final String VERCHECK_PROPS_FILENAME = ".vercheck"; //$NON-NLS-1$
  public static final String VERCHECK_CANT_SAVE_GUID = "12345-67890-09876-54321"; //$NON-NLS-1$

  public static final boolean DEBUGGING = false; // Set to false for final deliverable, and true for verbose output.

  // property name constants
  private static final String PROP_ROOT = "versionchk"; //$NON-NLS-1$
  private static final String PROP_SYSTEM_GUID = PROP_ROOT + ".guid"; //$NON-NLS-1$
  private static final String PROP_UPDATE = "update"; //$NON-NLS-1$
  private static final String PROP_LASTCHECK = "lastcheck"; //$NON-NLS-1$

  //
  // So, you're probably asking "why are these now static variables?", huh? Well, the
  // answer is that, once we've figured out the stuff, we want to keep it hanging out
  // in the VM. That way, we're not having to re-calculate things over and over.
  //
  private static boolean isWritable = true;
  static File propsDirectory;
  private static File propsFile;
  private static Properties props;
  private static String guid;

  static {
    if ( DEBUGGING ) {
      System.out.println( "Static Initializer" ); //$NON-NLS-1$
    }
    init();
  }

  public static void init() {
    try {
      isWritable = true;
      propsDirectory = null;
      propsFile = null;
      guid = null;
      props = new Properties();

      String homeDir = getHomeDir();
      if ( DEBUGGING ) {
        System.out.println( "Home Directory: " + homeDir ); //$NON-NLS-1$
      }
      if ( homeDir == null ) {
        isWritable = false;
        if ( DEBUGGING ) {
          System.out.println( "*** Cannot Write Properties ***" ); //$NON-NLS-1$
        }
      } else {
        propsDirectory = new File(
          homeDir + ( homeDir.endsWith( "/" ) ? "" : File.separator ) + PENTAHO_DIR ); //$NON-NLS-1$ //$NON-NLS-2$
        propsDirectory.mkdirs();
        String propsPath = propsDirectory.getCanonicalPath();
        propsFile =
          new File( propsPath + ( propsPath.endsWith( "/" ) ? "" : File.separator )
            + VERCHECK_PROPS_FILENAME ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( DEBUGGING ) {
          System.out.println( "Properties Path: " + propsPath ); //$NON-NLS-1$
        }
      }
    } catch ( Throwable th ) {
      isWritable = false;
    }
    loadProperties();
    LoadOrGenerateGuid();
  }

  public static boolean getIsWritable() {
    return isWritable;
  }

  public static String getPropertiesDirectory() throws IOException {
    return ( propsDirectory != null ) ? propsDirectory.getCanonicalPath() : null;
  }

  private static void LoadOrGenerateGuid() {
    guid = props.getProperty( PROP_SYSTEM_GUID );
    if ( DEBUGGING ) {
      System.out.println( "Loaded GUID: " + guid ); //$NON-NLS-1$
    }
    if ( guid == null ) {
      // generate guid
      generateGUID();
      // save guid
      props.setProperty( PROP_SYSTEM_GUID, guid );
      saveProperties();
    }
  }

  private static void generateGUID() {
    if ( isWritable ) {
      guid = UUIDUtil.getUUIDAsString();
      if ( DEBUGGING ) {
        System.out.println( "Generated GUID: " + guid ); //$NON-NLS-1$
      }
    } else {
      guid = VERCHECK_CANT_SAVE_GUID;
    }
  }

  private static boolean testWritabilityOfFolder( String testName ) {
    boolean rtn = false;
    if ( ( testName != null ) && ( testName.length() > 0 ) ) {
      String test1 = testName + ( testName.endsWith( "/" ) ? "" : File.separator )
        + "test_pentaho_write_.txt"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      File testThisDir = new File( test1 );
      try {
        if ( !testThisDir.exists() ) {
          // What if we can create the test_pentaho_write_.txt file, but we can't delete
          // it? Well, this will check that, and will only try creating the file if
          // it isn't there. Someone could bypass this directory if:
          //
          // a. They create the test_pentaho_write_.txt file in the folder
          // b. ACL out the ability to delete that file
          // c. Create a .vercheck file that's unwritable in the same folder.
          //
          testThisDir.createNewFile();
        }
        rtn = true;
        testThisDir.delete();
      } catch ( IOException ignored ) {
        // Ignored on purpose
        // If we're allowed to write, we're OK - we don't *have* to be able
        // to delete the temporary file we've created.
        if ( DEBUGGING ) {
          ignored.printStackTrace();
        }
      }
    }
    return rtn;
  }

  private static String getHomeDir() {
    // First, try the users' home directory.
    String homeDir = System.getProperty( "user.home" ); //$NON-NLS-1$
    if ( ( homeDir == null ) || ( homeDir.length() == 0 ) || !( testWritabilityOfFolder( homeDir ) ) ) {
      // OK, that didn't work. Try the folder this program was
      // launched from. Can I write there?
      homeDir = "."; //$NON-NLS-1$
      if ( !testWritabilityOfFolder( homeDir ) ) {
        // Uuugh - last resort, but I should be
        // able to write to the temp directory.
        // If not, then we can't write anywhere, so
        // return null.
        homeDir = System.getProperty( "java.io.tmpdir" ); //$NON-NLS-1$
        if ( !testWritabilityOfFolder( homeDir ) ) {
          homeDir = null;
        }
      }
    }
    return homeDir;
  }

  private static Properties loadProperties() {
    if ( isWritable ) {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream( propsFile );
        props.clear();
        props.load( fis );
      } catch ( Exception e ) {
        // suppress any loading issues
        if ( DEBUGGING ) {
          e.printStackTrace();
        }
      } finally {
        try {
          if ( fis != null ) {
            fis.close();
          }
        } catch ( Exception e ) {
          if ( DEBUGGING ) {
            e.printStackTrace();
          }
          // suppress any closing issues
        }
      }
    }
    return props;
  }

  public static String getGuid() {
    return guid;
  }

  private static void saveProperties() {
    if ( isWritable ) {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream( propsFile );
        props.store( fos, "Pentaho Version Checker Properties" ); //$NON-NLS-1$
      } catch ( Exception e ) {
        if ( DEBUGGING ) {
          e.printStackTrace();
        }
        // suppress any saving issues
      } finally {
        try {
          if ( fos != null ) {
            fos.close();
          }
        } catch ( Exception e ) {
          if ( DEBUGGING ) {
            e.printStackTrace();
          }
          // suppress any closing issues
        }
      }
    }
  }

  /**
   * The data provider that will be used to retrieve the data about this instance of the running application
   */
  protected IVersionCheckDataProvider dataProvider;

  /**
   * The set of results handlers that will be called every time a version check occurs.
   */
  protected final Set resultHandlers = new HashSet();

  /**
   * The set of error handlers that will be called in the event there is an error while during processing
   */
  protected final Set errorHandlers = new HashSet();

  /**
   * Default URL used if none is provided - read from the resource bundle
   */
  private static final String DEFAULT_URL = VersionCheckResourceBundle.getString( "VersionChecker.CODE_default_url" );
  //$NON-NLS-1$

  private static final String DEFAULT_TIMEOUT_MILLIS = VersionCheckResourceBundle
    .getString( "VersionChecker.CODE_default_timeout_millis" ); //$NON-NLS-1$

  /**
   * Default constructor
   */
  public VersionChecker() {
  }

  /**
   * Sets the data provider that will be used to retrieve the data about this instance of the running application. If
   * this method is called multiple times, the last data provider specified will be used.
   */
  public void setDataProvider( IVersionCheckDataProvider dataProvider ) {
    this.dataProvider = dataProvider;
  }

  /**
   * Adds a results handler to the list of handlers that will be called with the version check results every time the
   * check occurs.
   */
  public void addResultHandler( IVersionCheckResultHandler resultHandler ) {
    if ( resultHandler != null ) {
      resultHandlers.add( resultHandler );
    }
  }

  /**
   * Removes the specified result handler from the list of result handlers.
   */
  public void removeResultHandler( IVersionCheckResultHandler resultHandler ) {
    resultHandlers.remove( resultHandler );
  }

  /**
   * Adds an error handler to the list of error handlers that will be notified when an error occurs.
   */
  public void addErrorHandler( IVersionCheckErrorHandler errorHandler ) {
    if ( errorHandler != null ) {
      errorHandlers.add( errorHandler );
    }
  }

  /**
   * Removes an error handler from the list of error handlers what will be notified when an error occurs;
   */
  public void removeErrorHandler( IVersionCheckErrorHandler errorHandler ) {
    errorHandlers.remove( errorHandler );
  }

  /**
   * Performs the version check by sending the request to the Pentaho server and passing the results off to the
   * specified results checker. If an error is encountered, the error handlers will be notified. <br>
   * NOTE: If no DataProvider is specified, this method will still execute.
   */
  public void performCheck( boolean ignoreExistingUpdates ) {
    final HttpRequestBase httpMethod = getHttpMethod();
    try {
      int timeout = 30_000;
      try {
        timeout = Integer.parseInt( DEFAULT_TIMEOUT_MILLIS );
      } catch ( Exception e ) {
        // ignore
        if ( DEBUGGING ) {
          e.printStackTrace();
        }
      }

      // Set the URL and parameters
      setURL( httpMethod, guid );

      RequestConfig requestConfig = RequestConfig.custom()
        .setSocketTimeout( timeout )
        .setConnectTimeout( timeout )
        .build();

      HttpClient httpClient =
        defaultHttpClient != null ? defaultHttpClient : buildHttpClient( requestConfig );

      // Execute the request
      httpMethod.setHeader( HttpHeaders.CONTENT_TYPE, "application/json" );
      HttpResponse httpResponse = httpClient.execute( httpMethod );
      StatusLine statusLine = httpResponse.getStatusLine();
      final int resultCode = statusLine.getStatusCode();

      if ( resultCode != HttpURLConnection.HTTP_OK ) {
        throw new Exception( VersionCheckResourceBundle
          .getString( "VersionChecker.ERROR_0002_IS_NOT_OK_RESPONSE" + resultCode ) ); //$NON-NLS-1$
      }

      HttpEntity entity = httpResponse.getEntity();
      String resultXml = EntityUtils.toString( entity );

      resultXml = checkForUpdates( dataProvider, resultXml, props, ignoreExistingUpdates );

      // Pass the results along
      processResults( resultXml );

      // save properties file with updated timestamp
      // note that any updates changed above will be saved also
      if ( dataProvider != null ) {
        String lastCheckProp = PROP_ROOT + "." + dataProvider.getApplicationID() + "." + //$NON-NLS-1$ //$NON-NLS-2$
          dataProvider.getApplicationVersion() + "." + PROP_LASTCHECK; //$NON-NLS-1$
        props.setProperty( lastCheckProp, new Date().toString() );
        saveProperties();
      }

      // Clean up
      httpMethod.releaseConnection();

    } catch ( Exception e ) {
      // IOException covers URIExcecption and HttpException
      if ( DEBUGGING ) {
        e.printStackTrace();
      }
      handleException( e );
    }
  }

  private HttpClient buildHttpClient( RequestConfig requestConfig ) {
    CloseableHttpClient httpClient = HttpClientBuilder
      .create()
      .setDefaultRequestConfig( requestConfig )
      .build();
    return httpClient;
  }

  /**
   * This utility method checks for updates Update the .updates property, and also supports suppression of update if
   * requested
   *
   * @param resultXml             the xml from the server
   * @param propsToCheck          the global properties object
   * @param ignoreExistingUpdates true if we should ignore existing updates
   * @return original or suppressed resultXml
   */
  static String checkForUpdates( IVersionCheckDataProvider dataProvider, String resultXml, Properties propsToCheck,
                                 boolean ignoreExistingUpdates ) {
    if ( dataProvider != null ) {
      int updateLoc = resultXml.indexOf( "<update" ); //$NON-NLS-1$
      if ( updateLoc >= 0 ) {

        boolean found = true;
        while ( updateLoc >= 0 ) {
          // extract version and type the old fashioned way to avoid including libs
          int versionLocBegin = resultXml.indexOf( " version=\"", updateLoc ); //$NON-NLS-1$
          int versionLocEnd = resultXml.indexOf( "\"", versionLocBegin + 10 ); //$NON-NLS-1$
          String version = resultXml.substring( versionLocBegin + 10, versionLocEnd );
          int typeLocBegin = resultXml.indexOf( " type=\"", updateLoc ); //$NON-NLS-1$
          int typeLocEnd = resultXml.indexOf( "\"", typeLocBegin + 7 ); //$NON-NLS-1$
          String type = resultXml.substring( typeLocBegin + 7, typeLocEnd );
          int titleLocBegin = resultXml.indexOf( " title=\"", updateLoc ); //$NON-NLS-1$
          int titleLocEnd = resultXml.indexOf( "\"", titleLocBegin + 8 ); //$NON-NLS-1$
          String title = resultXml.substring( titleLocBegin + 8, titleLocEnd );

          String versionAndType = title + " " + version + " " + type; //$NON-NLS-1$ //$NON-NLS-2$

          // locate the version in the properties
          String updateProp = PROP_ROOT + "." + dataProvider.getApplicationID() + "." + //$NON-NLS-1$ //$NON-NLS-2$
            dataProvider.getApplicationVersion() + "." + PROP_UPDATE; //$NON-NLS-1$

          String updateVal = propsToCheck.getProperty( updateProp, "" ); //$NON-NLS-1$

          // if the version isn't in the list of updates
          if ( updateVal.indexOf( versionAndType ) < 0 ) {
            if ( updateVal.length() > 0 ) {
              updateVal += ","; //$NON-NLS-1$
            }
            updateVal += versionAndType;
            propsToCheck.setProperty( updateProp, updateVal );
            found = false;
          }

          // next update location
          updateLoc = resultXml.indexOf( "<update", updateLoc + 1 ); //$NON-NLS-1$
        }

        // if suppressExistingUpdates is true and all the updates
        // listed have been found before, suppress the update
        if ( found && ignoreExistingUpdates ) {
          return "<vercheck protocol=\"1.0\"/>"; //$NON-NLS-1$
        }
      }
    }
    return resultXml;
  }

  /**
   * Sets the URL (and parameters) for the request in the HttpMethod. The data provider information is sed to set the
   * parameters
   *
   * @param method the method which will have the URL set
   * @throws URISyntaxException Indicates an error creating the URI
   */
  protected void setURL( HttpRequestBase method, String guid ) throws URISyntaxException {
    String urlBase = null;
    final Map<String, String> parameters = new HashMap();

    // If we have a data provider, get the parameters from there
    if ( dataProvider != null ) {
      // Get the URL
      urlBase = dataProvider.getBaseURL();

      // Get the extra parameters
      final Map params = dataProvider.getExtraInformation();
      if ( params != null && params.size() > 0 ) {
        parameters.putAll( params );
      }

      // Add the specific parameters
      final String productID = dataProvider.getApplicationID();
      final String version = dataProvider.getApplicationVersion();
      final int depth = dataProvider.getDepth();
      final String vi = computeVI( productID );

      parameters.put( "depth", "" + depth ); //$NON-NLS-1$ //$NON-NLS-2$
      parameters.put( "prodID", productID ); //$NON-NLS-1$
      parameters.put( "version", version ); //$NON-NLS-1$
      parameters.put( "guid", guid ); //$NON-NLS-1$
      parameters.put( "vi", vi ); //$NON-NLS-1$
    }

    // Use the default URL if none is specified
    if ( urlBase == null ) {
      urlBase = getDefaultURL();
    }
    URI uri = buildURI( urlBase, parameters );
    method.setURI( uri );
  }

  /**
   * Creates the URL with query string based off the base url and the parameters passed.
   *
   * @param urlBase
   *          the first part of the url
   * @param parameters
   *          the parameters to add as part of the query string or null
   * @return the complete URL and query string
   */
  // The method is defined as package-protected in order to be accessible by unit tests
  protected URI buildURI( String urlBase, Map<String, String> parameters ) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder( urlBase );
    if ( parameters == null ) {
      return uriBuilder.build();
    }
    List<NameValuePair> queryParams = uriBuilder.getQueryParams();
    for ( Map.Entry<String, String> entry : parameters.entrySet() ) {
      String key = entry.getKey();
      if ( key != null ) {
        queryParams.add( new BasicNameValuePair( key, entry.getValue() ) );
      }
    }
    if ( !queryParams.isEmpty() ) {
      uriBuilder.setParameters( queryParams );
    }

    return uriBuilder.build();
  }

  /**
   * Returns the default URL. This is stored in the properties file
   *
   * @return the URL retrieved that should be used as the default URL
   */
  protected String getDefaultURL() {
    return DEFAULT_URL;
  }

  /**
   * Computes the VI field for the data provided. The VI is the MD5 encryption of the concatination of the productID and
   * the guid.
   */
  protected static final String computeVI( final String productID ) {
    return DigestUtils
      .md5Hex( ( productID == null ? "" : productID ) + ( guid == null ? "" : guid ) ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Passes the results along to each of the results processors specified. Each result processing will be handled in a
   * try/catch block to prevent an exception from chaining upward out of this flow of control. <br>
   * NOTE: This is not done using threads ... therefore (right or wrong), if one handler takes a long time to process,
   * the remaining handlers will have to wait
   *
   * @param results the results passed to each handler
   */
  protected void processResults( final String results ) {
    for ( final Iterator it = resultHandlers.iterator(); it.hasNext(); ) {
      try {
        final IVersionCheckResultHandler resultHandler = (IVersionCheckResultHandler) it.next();
        resultHandler.processResults( results );
      } catch ( final Throwable t ) {
        System.err.println( VersionCheckResourceBundle
          .getString(
            "VersionChecker.ERROR_0001_ERROR_THROWN_FROM_RESULTS_HANDLER" ) ); // TODO log message //$NON-NLS-1$
      }
    }
  }

  /**
   * Passes the exception information along to the exception handlers. Each exception handler's processing will be
   * contained in a try/catch block to prevent an error from chaining upwards out of this flow of control.
   */
  protected void handleException( final Exception e ) {
    for ( final Iterator it = errorHandlers.iterator(); it.hasNext(); ) {
      try {
        final IVersionCheckErrorHandler errorHandler = (IVersionCheckErrorHandler) it.next();
        errorHandler.handleException( e );
      } catch ( final Throwable t ) {
        System.err.println( VersionCheckResourceBundle
          .getString( "VersionChecker.ERROR_0001_ERROR_THROWN_FROM_ERROR_HANDLER" ) ); // TODO log message //$NON-NLS-1$
      }
    }
  }

  /**
   * Returns the HttpClient to be used during processing. If a default HttpClient is not specified, a new HttpClient
   * will be used. This exists for two reasons:
   * <ol>
   * <li>Allows subclasses to specify a HttpClient with different parameters
   * <li>Unit Testing
   * </ol>
   *
   * @return the HttpClient to be used for processing
   */
  protected HttpClient getHttpClient() {
    return defaultHttpClient != null ? defaultHttpClient : new DefaultHttpClient();
  }

  /**
   * Returns the GetMethod object to be used during processing. If a default GetMethod is not specified, a new GetMethod
   * will be used. This exists for two reasons:
   * <ol>
   * <li>Allows subclasses to specify a GetMethod with different parameters
   * <li>Unit Testing
   * </ol>
   * NOTE: This method returns a HttpMethod not specifically a GetMethod. This allows a subclass to change over to
   * PostMethod later.
   *
   * @return the HttpMethod to be used during processing.
   */
  protected HttpRequestBase getHttpMethod() {
    return defaultHttpMethod != null ? defaultHttpMethod : new HttpGet();
  }

  // **************************************************************************
  // * Used for Unit Testing *
  // **************************************************************************
  protected VersionChecker( final HttpClient defaultHttpClient, final HttpRequestBase defaultHttpMethod ) {
    this();
    setDefaultHttpClient( defaultHttpClient );
    setDefaultHttpMethod( defaultHttpMethod );
  }

  protected void setDefaultHttpClient( final HttpClient httpClient ) {
    this.defaultHttpClient = httpClient;
  }

  protected void setDefaultHttpMethod( final HttpRequestBase httpMethod ) {
    this.defaultHttpMethod = httpMethod;
  }

  private HttpClient defaultHttpClient;

  private HttpRequestBase defaultHttpMethod;
}
