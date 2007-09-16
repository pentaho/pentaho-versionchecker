package org.pentaho.versionchecker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;

public class VersionCheckerTest extends TestCase {

  /**
   * Tests the setDataProvider method and makes sure it works with
   * null and without null 
   */
  public void testSetDataProvider() {
    MockDataProvider dataProvider = new MockDataProvider();
    MockHttpClient httpClient = new MockHttpClient();
    VersionChecker vc = new VersionChecker(httpClient, null);
    
    vc.setDataProvider(dataProvider);
    vc.performCheck();
    assertEquals(1, dataProvider.getApplicationIDCallCount);
    
    vc.setDataProvider(null);
    vc.performCheck();
    assertEquals(1, dataProvider.getApplicationIDCallCount);
    
    
    vc.setDataProvider(dataProvider);
    vc.performCheck();
    assertEquals(2, dataProvider.getApplicationIDCallCount);
  }
  
  /**
   * Tests the addResultHandler and removeRestulHandler methods
   */
  public void testResultHandler() {
    MockHttpClient httpClient = new MockHttpClient();
    MockGetMethod getMethod = new MockGetMethod();
    MockResultHandler resultsHandler1 = new MockResultHandler();
    MockResultHandler resultsHandler2 = new MockResultHandler();
    VersionChecker vc = new VersionChecker(httpClient, getMethod);
    
    vc.addResultHandler(resultsHandler1);
    vc.performCheck();
    assertEquals(1, resultsHandler1.processResultsCount);
    assertEquals(getMethod.responseBody, resultsHandler1.results);
    
    vc.addResultHandler(resultsHandler2);
    vc.removeResultHandler(null);
    resultsHandler1.results = null;
    vc.performCheck();
    assertEquals(2, resultsHandler1.processResultsCount);
    assertEquals(1, resultsHandler2.processResultsCount);
    assertEquals(getMethod.responseBody, resultsHandler1.results);
    assertEquals(getMethod.responseBody, resultsHandler2.results);

    vc.removeResultHandler(resultsHandler1);
    resultsHandler1.results = null;
    resultsHandler2.results = null;
    vc.performCheck();
    assertEquals(2, resultsHandler1.processResultsCount);
    assertEquals(2, resultsHandler2.processResultsCount);
    assertEquals(null, resultsHandler1.results);
    assertEquals(getMethod.responseBody, resultsHandler2.results);
    
    resultsHandler2.throwException = true;
    vc.performCheck();
    assertEquals(3, resultsHandler2.processResultsCount);
  }
  
  /**
   * Tests the error handler capabilities
   */
  public void testErrorHandler() {
    MockDataProvider dataProvider = new MockDataProvider();
    dataProvider.baseURL = "htp://test.pentaho.org/testing_page_doesnot_exist";
    VersionChecker vc = new VersionChecker();
    vc.setDataProvider(dataProvider);
    MockErrorHandler errorHandler1 = new MockErrorHandler();
    MockErrorHandler errorHandler2 = new MockErrorHandler();
    vc.addErrorHandler(errorHandler1);
    vc.performCheck();
    assertEquals(1, errorHandler1.errorCount);
    
    vc.addErrorHandler(errorHandler2);
    vc.addErrorHandler(null);
    vc.performCheck();
    assertEquals(2, errorHandler1.errorCount);
    assertEquals(1, errorHandler2.errorCount);
    
    vc.removeErrorHandler(errorHandler1);
    vc.removeErrorHandler(null);
    vc.performCheck();
    assertEquals(2, errorHandler1.errorCount);
    assertEquals(2, errorHandler2.errorCount);
    
    errorHandler2.throwException = true;
    vc.performCheck();
    assertEquals(3, errorHandler2.errorCount);
  }
  
  /**
   * Tests the method that sets the URL in the HttpMethod
   */
  public void testSetURL() {
    MockGetMethod httpMethod = new MockGetMethod();
    VersionChecker vc = new VersionChecker(null, httpMethod);
    vc.setURL(httpMethod);
    assertEquals(1, httpMethod.setQueryStringCount);
    assertEquals(vc.getDefaultURL(), httpMethod._queryString);

    MockDataProvider dataProvider = new MockDataProvider();
    vc.setDataProvider(dataProvider);
    vc.setURL(httpMethod);
    assertEquals(2, httpMethod.setQueryStringCount);
    assertTrue(httpMethod._queryString.startsWith("http://test.pentho.org:8080/sample?"));
  }
  
  public void testCreateURL() {
    Map params = new HashMap();
    String baseUrl = "http://www.pentaho.org/";
    String result = VersionChecker.createURL(baseUrl, params);
    assertEquals(baseUrl, result);
    
    String junk = "a1B2 !@#$%^&*()_-+={[}]|\\:;\"'<,>.?/~`";
    String encodedJunk = URLEncoder.encode(junk);
    params.put("junk", junk);
    result = VersionChecker.createURL(baseUrl, params);
    assertEquals(baseUrl+"?junk="+encodedJunk, result);

    params.put("one", "one");
    result = VersionChecker.createURL(baseUrl, params);
    assertTrue(result.indexOf("?one=one&") > 0 || result.indexOf("&one=one") > 0);
    
    params.clear();
    params.put("two", "two");
    baseUrl += "?one=one";
    result = VersionChecker.createURL(baseUrl, params);
    assertEquals(baseUrl+"&two=two", result);
    
    params.clear();
    params.put("two", "two");
    baseUrl += "&";
    result = VersionChecker.createURL(baseUrl, params);
    assertEquals(baseUrl+"two=two", result);
  }
  
  /**
   * Mock GetMethod that allows for the tracking of the URL and the 
   * returning of sample results 
   */
  private class MockGetMethod extends GetMethod {
    public int getResponseBodyCount = 0;
    public String responseBody = "sample response";
    public String getResponseBodyAsString() {
      ++getResponseBodyCount;
      return responseBody;
    }

    public String _queryString = null;
    public int setQueryStringCount = 0;
    public void setQueryString(String arg0) {
      _queryString = arg0;
      ++setQueryStringCount;
      super.setQueryString(arg0);
    }
  };

  /**
   * Mock HttpClient class that prevents the executeMethod method from executing 
   */
  private class MockHttpClient extends HttpClient {
    public int responseCode = HttpURLConnection.HTTP_OK;
    public int executeMethodCount = 0;
    public int executeMethod(HostConfiguration arg0, HttpMethod arg1, HttpState arg2) throws IOException, HttpException {
      ++executeMethodCount;
      return responseCode;
    }
    public int executeMethod(HostConfiguration arg0, HttpMethod arg1) throws IOException, HttpException {
      ++executeMethodCount;
      return responseCode;
    }
    public int executeMethod(HttpMethod arg0) throws IOException, HttpException {
      ++executeMethodCount;
      return responseCode;
    }
  };

  /**
   * Mock IVersionCheckDataProvider implementation that allows for canned responses
   * and method call counting
   */
  private class MockDataProvider implements IVersionCheckDataProvider {
    public int getApplicationIDCallCount = 0;
    public String applicationID = "prd";
    public String getApplicationID() {
      ++getApplicationIDCallCount;
      return applicationID;
    }

    public int getApplicationVersionCount = 0;
    public String applicationVersion = "1.6.0-RC1.123";
    public String getApplicationVersion() {
      ++getApplicationVersionCount;
      return applicationVersion;
    }

    public int getBaseURLCount = 0;
    public String baseURL = "http://test.pentho.org:8080/sample";
    public String getBaseURL() {
      ++getBaseURLCount;
      return baseURL;
    }

    public int getExtraInformationCount = 0;
    public HashMap extraInformation = new HashMap();
    public Map getExtraInformation() {
      ++getExtraInformationCount;
      return extraInformation;
    }

    public int getGuidCount = 0;
    public String guid = "a1b2-c3d4-e5f6-g7h8";
    public String getGuid() {
      ++getGuidCount;
      return guid;
    }
  };
  
  private class MockResultHandler implements IVersionCheckResultHandler {
    public String results = null;
    public int processResultsCount = 0;
    public boolean throwException = false;
    public void processResults(String results) {
      ++processResultsCount;
      this.results = results;
      if (throwException) {
        throw new NullPointerException("Test");
      }
    }
  };
  
  private class MockErrorHandler implements IVersionCheckErrorHandler {
    public Exception exception = null;
    public int errorCount = 0;
    public boolean throwException = false;
    public void handleException(Exception e) {
      ++errorCount;
      exception = e;
      if (throwException) {
        throw new NullPointerException("Test");
      }
    }
  }
}
