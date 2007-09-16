package org.pentaho.versionchecker;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Checks for updated software information for the specified applications.
 * <br/>
 * This class gets version information from the supplied <code>IVersionCheckDataProvider</code>
 * and communicates the results to the list of supplied <code>IVersionCheckResultHandler</code>.
 * If an error occurs during processing, the error information will be passed along to the list
 * of supplied <code>IVersionCheckErrorHandler</code>.
 * 
 * @author dkincade
 */
public class VersionChecker {

  /**
   * The data provider that will be used to retrieve the data
   * about this instance of the running application
   */
  protected IVersionCheckDataProvider dataProvider;

  /**
   * The set of results handlers that will be called every time
   * a version check occurs.
   */
  protected final Set resultHandlers = new HashSet();

  /**
   * The set of error handlers that will be called in the event there is
   * an error while during processing
   */
  protected final Set errorHandlers = new HashSet();
  
  /**
   * Default URL used if none is provided - read from the resource bundle
   */
  private static final String DEFAULT_URL = VersionCheckResourceBundle.getString("VersionChecker.CODE_default_url"); //$NON-NLS-1$

  /**
   * Default constructor
   */
  public VersionChecker() {
  }

  /**
   * Sets the data provider that will be used to retrieve the data
   * about this instance of the running application. If this method 
   * is called multiple times, the last data provider specified 
   * will be used.
   */
  public void setDataProvider(IVersionCheckDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Adds a results handler to the list of handlers that will be called
   * with the version check results every time the check occurs.
   */
  public void addResultHandler(IVersionCheckResultHandler resultHandler) {
    if (resultHandler != null) {
      resultHandlers.add(resultHandler);
    }
  }

  /**
   * Removes the specified result handler from the list of result handlers.
   */
  public void removeResultHandler(IVersionCheckResultHandler resultHandler) {
    resultHandlers.remove(resultHandler);
  }

  /**
   * Adds an error handler to the list of error handlers that will be notified
   * when an error occurs.
   */
  public void addErrorHandler(IVersionCheckErrorHandler errorHandler) {
    if (errorHandler != null) {
      errorHandlers.add(errorHandler);
    }
  }

  /**
   * Removes an error handler from the list of error handlers what will be notified
   * when an error occurs;
   */
  public void removeErrorHandler(IVersionCheckErrorHandler errorHandler) {
    errorHandlers.remove(errorHandler);
  }

  /**
   * Performs the version check by sending the request to the Pentaho server
   * and passing the results off to the specified results checker. If an error
   * is encountered, the error handlers will be notified.
   * <br>
   * NOTE: If no DataProvider is specified, this method will still execute.
   */
  public void performCheck() {
    final HttpClient httpClient = getHttpClient();
    final HttpMethod httpMethod = getHttpMethod();
    try {
      // Set the URL and parameters
      setURL(httpMethod);

      // Execute the request
      final int resultCode = httpClient.executeMethod(httpMethod);
      if (resultCode != HttpURLConnection.HTTP_OK) {
        throw new Exception("Invalid Result Code Returned: "+resultCode); //TODO - improve this
      }

      // Pass the results along
      processResults(httpMethod.getResponseBodyAsString());

      // Clean up
      httpMethod.releaseConnection();

    } catch (Exception e) {
      // IOException covers URIExcecption and HttpException
      handleException(e);
    }
  }

  /**
   * Sets the URL (and parameters) for the request in the HttpMethod.
   * The data provider information is sed to set the parameters
   * @param method the method which will have the URL set
   */
  protected void setURL(HttpMethod method) {
    String urlBase = null;
    final Map parameters = new HashMap();

    // If we have a data provider, get the parameters from there
    if (dataProvider != null) {
      // Get the URL
      urlBase = dataProvider.getBaseURL();

      // Get the extra parameters
      final Map params = dataProvider.getExtraInformation();
      if (params != null && params.size() > 0) {
        parameters.putAll(params);
      }

      // Add the specific parameters
      final String productID = dataProvider.getApplicationID();
      final String guid = dataProvider.getGuid();
      final String version = dataProvider.getApplicationVersion();
      final String vi = computeVI(productID, guid);
      parameters.put("prodID", productID); //$NON-NLS-1$
      parameters.put("version", version); //$NON-NLS-1$
      parameters.put("guid", guid); //$NON-NLS-1$
      parameters.put("vi", vi); //$NON-NLS-1$
    }

    // Use the default URL if none is specified
    if (urlBase == null) {
      urlBase = getDefaultURL();
    }

    // Set the url in the method
    method.setQueryString(createURL(urlBase, parameters));
  }
  
  /**
   * Returns the default URL. This is stored in the properties file
   * @return the URL retrieved that should be used as the default URL
   */
  protected String getDefaultURL() {
    return DEFAULT_URL;
  }

  /**
   * Creats the URL with query string based off the base url and the parameters passed
   * @param urlBase the first part of the url
   * @param parameters the parameters to add as part of the query string
   * @return the complete URL and query string
   */
  protected static String createURL(final String urlBase, Map parameters) {
    // Create the query string from the url and the parameters
    final StringBuffer queryString = new StringBuffer();
    queryString.append(urlBase);
    if (parameters != null) {
      String connector = ""; //$NON-NLS-1$
      if (urlBase.indexOf('?') == -1) {
        connector = "?"; //$NON-NLS-1$
      } else if (!urlBase.endsWith("&")) { //$NON-NLS-1$
        connector = "&"; //$NON-NLS-1$
      }

      for (final Iterator it = parameters.keySet().iterator(); it.hasNext();) {
        final Object key = it.next();
        if (key != null) {
          final Object obj = parameters.get(key);
          final String value = (obj != null ? obj.toString() : ""); //$NON-NLS-1$
          queryString.append(connector).append(URLEncoder.encode(key.toString())).append('=').append(URLEncoder.encode(value));
          connector = "&"; //$NON-NLS-1$
        }
      }
    }
    
    // Return the generated query string
    return queryString.toString();
  }

  /**
   * Computes the VI field for the data provided. The VI is the MD5 encryption of the 
   * concatination of the productID and the guid.
   */
  protected static final String computeVI(final String productID, final String guid) {
    return DigestUtils.md5Hex((productID == null ? "" : productID) + (guid == null ? "" : guid)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Passes the results along to each of the results processors specified.
   * Each result processing will be handled in a try/catch block to prevent
   * an exception from chaining upward out of this flow of control.
   * <br>
   * NOTE: This is not done using threads ... therefore (right or wrong),
   * if one handler takes a long time to process, the remaining handlers
   * will have to wait
   * @param results the results passed to each handler
   */
  protected void processResults(final String results) {
    for (final Iterator it = resultHandlers.iterator(); it.hasNext();) {
      try {
        final IVersionCheckResultHandler resultHandler = (IVersionCheckResultHandler) it.next();
        resultHandler.processResults(results);
      } catch (final Throwable t) {
        System.err.println(VersionCheckResourceBundle.getString("VersionChecker.ERROR_0001_ERROR_THROWN_FROM_RESULTS_HANDLER")); // TODO log message //$NON-NLS-1$
      }
    }
  }

  /**
   * Passes the exception information along to the exception handlers.
   * Each exception handler's processing will be contained in a try/catch
   * block to prevent an error from chaining upwards out of this flow
   * of control.
   */
  protected void handleException(final Exception e) {
    for (final Iterator it = errorHandlers.iterator(); it.hasNext();) {
      try {
        final IVersionCheckErrorHandler errorHandler = (IVersionCheckErrorHandler) it.next();
        errorHandler.handleException(e);
      } catch (final Throwable t) {
        System.err.println(VersionCheckResourceBundle.getString("VersionChecker.ERROR_0001_ERROR_THROWN_FROM_ERROR_HANDLER")); // TODO log message //$NON-NLS-1$
      }
    }
  }

  /**
   * Returns the HttpClient to be used during processing. If a default
   * HttpClient is not specified, a new HttpClient will be used. 
   * This exists for two reasons:
   * <ol>
   *   <li>Allows subclasses to specify a HttpClient with different parameters
   *   <li>Unit Testing
   * </ol>
   * @return the HttpClient to be used for processing
   */
  protected HttpClient getHttpClient() {
    return defaultHttpClient != null ? defaultHttpClient : new HttpClient();
  }

  /**
   * Returns the GetMethod object to be used during processing. If a default
   * GetMethod is not specified, a new GetMethod will be used.
   * This exists for two reasons:
   * <ol>
   *   <li>Allows subclasses to specify a GetMethod with different parameters
   *   <li>Unit Testing
   * </ol>
   * NOTE: This method returns a HttpMethod not specifically a GetMethod.
   * This allows a subclass to change over to PostMethod later.
   * @return the HttpMethod to be used during processing. 
   */
  protected HttpMethod getHttpMethod() {
    return defaultHttpMethod != null ? defaultHttpMethod : new GetMethod();
  }

  // **************************************************************************
  // * Used for Unit Testing                                                  *
  // **************************************************************************
  protected VersionChecker(final HttpClient defaultHttpClient, final HttpMethod defaultHttpMethod) {
    setDefaultHttpClient(defaultHttpClient);
    setDefaultHttpMethod(defaultHttpMethod);
  }

  protected void setDefaultHttpClient(final HttpClient httpClient) {
    this.defaultHttpClient = httpClient;
  }

  protected void setDefaultHttpMethod(final HttpMethod httpMethod) {
    this.defaultHttpMethod = httpMethod;
  }

  private HttpClient defaultHttpClient;

  private HttpMethod defaultHttpMethod;
}
