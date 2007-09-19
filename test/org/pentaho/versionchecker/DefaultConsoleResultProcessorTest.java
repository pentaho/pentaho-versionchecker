package org.pentaho.versionchecker;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import junit.framework.TestCase;

public class DefaultConsoleResultProcessorTest extends TestCase implements IVersionCheckDataProvider,
    IVersionCheckErrorHandler, IVersionCheckResultHandler {
  protected Throwable error = null;

  protected String results = null;

  public void testDefaultConsoleResultProcessor() {
    DefaultConsoleResultProcessor rp = new DefaultConsoleResultProcessor();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bs);
    rp.setOutput(ps);

    VersionChecker vc = new VersionChecker();
    vc.setDataProvider(this);
    vc.addErrorHandler(this);
    vc.addResultHandler(this);
    vc.addResultHandler(rp);
    vc.performCheck(false);

    assertNull(error);
    assertNotNull(results);
    assertTrue(bs.toString().startsWith(results));
  }

  public String getApplicationID() {
    return "POBS";
  }

  public String getApplicationVersion() {
    return "1.6.0.RC1.400";
  }

  public String getBaseURL() {
    // TODO Auto-generated method stub
    return "http://www.pentaho.com/versioncheck/index2.php?protocolVer=1.0&depth=154";
  }

  public Map getExtraInformation() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public int getDepth() {
    return 154;
  }

  public String getGuid() {
    // TODO Auto-generated method stub
    return "0000-0000-0000-0000";
  }

  public void handleException(Exception e) {
    e.printStackTrace();
    error = e;
  }

  public void processResults(String results) {
    System.out.println("RESULTS: "+results+"\n");
    this.results = results;
  }
}
