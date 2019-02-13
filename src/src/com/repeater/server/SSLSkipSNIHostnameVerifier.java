package com.repeater.server;

/**
 * 
 */
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * 
 * During handshaking, if the URL's hostname and the server's identification hostname mismatch, the verification mechanism can call back to implementers 
 * of this interface to determine if this connection should be allowed.
 * 
 * This allows us to treat some incorrectly configured HTTPS / SNI target servers as valid.
 * 
 * @see javax.net.ssl.HttpsURLConnection#setHostnameVerifier(HostnameVerifier)
In the past we used to pass into the JVM runtime the option "-Djsse.enableSNIExtension=false" which would disable SNI extensions completely, bypassing any handshake checks. With the rewrite of Tradefeeds, this is now problematic, as the setting would apply to all threads executing.

 * @author GNaschenweng
 */
public class SSLSkipSNIHostnameVerifier implements HostnameVerifier {

  /**
   * 
   */
  public SSLSkipSNIHostnameVerifier() {
  }

  /* 
   * We always treat SNI issues as valid. This should only be used in valid and verified cases and not set as the default host name-verifier for all connections
   * (non-Javadoc)
   * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
   */
  @Override
  public boolean verify (String hostname, SSLSession session) {

    // Return true so that we implicitly trust hostname mismatch
    return true;
  }

}
