package com.repeater.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import com.sun.net.httpserver.Headers;

public class HTTPClient {
	
	private final static String USER_AGENT = "Mozilla/5.0";
	private static CookieManager cm = new CookieManager();
	private static List<HttpCookie> cookies;
	private static Map<String, List<String>> respHeaders;
	private static int ResponseCode = 200;
	
	public static int getResponseCode() {
		return ResponseCode;
	}

	public static List<String> getCookies(){
		
		List<String> values = new ArrayList<>();
		
		for (HttpCookie cookie : cookies) {			
			values.add(cookie.getName()+"="+cookie.getValue());
		}
		return values;
	}
	
	public static Map<String, List<String>> getRespHeaders(){
		
		return respHeaders;
	}
	
	public static String readStringFromURL(String requestURL) throws IOException
	{
		fixUntrustCertificate();
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    }
	}
	
	public static String getContentType(String requestURL){
		
		URL url;
		String contentType = "";
		HttpURLConnection connection;
		try {
			
			url = new URL(requestURL);
			connection = (HttpURLConnection) url.openConnection();
		    contentType = connection.getContentType();		    
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    return contentType;
	}
	
	public static String getContentEncoding(String requestURL){
		
		URL url;
		String encoding = null;
		HttpURLConnection connection;
		try {
			
			url = new URL(requestURL);
			connection = (HttpURLConnection) url.openConnection();
			encoding = connection.getContentEncoding();	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    return encoding;
	}
	
	public static byte[] doGet(String requestURL, Headers reqHeaders) throws IOException {

		
		// Check if the query of favicon.ico so cancel
		if(requestURL.endsWith("favicon.ico")){
			return null;
		}
		
		// Start build the stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		InputStream is = null;
		fixUntrustCertificate();
		URL url = new URL(requestURL);
		// To force the redirection
		HttpURLConnection.setFollowRedirects(false);
		
		// Get and store all cookies
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	    CookieHandler.setDefault(cm);	    
	    cookies = cm.getCookieStore().getCookies();
		
		try {
			// Check the response code
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			// set the requested headers
			for (Map.Entry<String, List<String>> entry : reqHeaders.entrySet()){
				con.setRequestProperty(entry.getKey(), entry.getValue().toString());
			}			
			con.setInstanceFollowRedirects(false);
			
			// Take the response code
			ResponseCode = con.getResponseCode();
			
			// Open the stream and read the reponse
			is = url.openStream();
			
			byte[] byteChunk = new byte[8692]; // Or whatever size you want to
												// read in at a time.
			int n;
			
			// Write the response to the client
			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);				
			}
			
			// Read the response headers
			respHeaders = con.getHeaderFields();
			
		} catch (IOException e) {
			System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
			e.printStackTrace();
			// Perform any other exception handling that's appropriate.
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return baos.toByteArray();
	}
	
	public static void fixUntrustCertificate() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

		} };

		SSLContext sc;
		try {
			
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// set the allTrusting verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	public static byte[] doPost(String buildResponseURL, Map<String, Object> parameters) throws IOException {
		
		URL obj = new URL(buildResponseURL);
		DataOutputStream wr = null;
		BufferedReader in = null;
		HttpsURLConnection con;
		int responseCode = 0;
		String inputLine;
		StringBuffer response = new StringBuffer();

		//add request header
		/*con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("charset","UTF-8");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		con.setRequestProperty("Content-Length",Integer.toString(parameters.toString().getBytes().length));*/
		
		try {
			
			con = (HttpsURLConnection) obj.openConnection();
			
			responseCode = con.getResponseCode();
			
			// Send post request
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			
			wr.writeBytes(parameters.toString());		
			
			/*wr.flush();
			wr.close();*/
			
			System.out.println("\nSending 'POST' request to URL : " + buildResponseURL);
			System.out.println("Post parameters : " + parameters.toString());
			System.out.println("Response Code : " + responseCode);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));			

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			/*in.close();	*/
			
		} catch (IOException e) {
			System.err.printf("Failed while reading bytes from %s: %s", obj.toExternalForm(), e.getMessage());
			e.printStackTrace();
			// Perform any other exception handling that's appropriate.
		}  catch (Exception e) {
			System.err.printf("Other Exception raised from %s: %s", obj.toExternalForm(), e.getMessage());
			e.printStackTrace();
			// Perform any other exception handling that's appropriate.
		} finally {
			if (wr != null) {				
				wr.close();
			}
			if (in != null) {				
				in.close();
			}
		}
		
		//Return the results
		System.out.println(response.toString());
		
		return response.toString().getBytes("UTF-8");
	}
	
	//Byte[] to byte[]
	static byte[] toPrimitives(Byte[] oBytes)
	{

	    byte[] bytes = new byte[oBytes.length];
	    for(int i = 0; i < oBytes.length; i++){
	        bytes[i] = oBytes[i];
	    }
	    return bytes;

	}
	
	//byte[] to Byte[]
	static Byte[] toObjects(byte[] bytesPrim) {

	    Byte[] bytes = new Byte[bytesPrim.length];
	    int i = 0;
	    for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
	    return bytes;

	}
	
	public static byte[] doHttpPost(String buildResponseURL, Map<String, Object> parameters) {

		// Convert parameters to bytes
		List<Byte> list = new ArrayList<Byte>();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			// System.out.println("Key : " + entry.getKey() + " Value : " +
			// entry.getValue());
			list.addAll(Arrays.asList(toObjects(entry.getKey().getBytes(StandardCharsets.UTF_8))));
			list.addAll(Arrays.asList(toObjects("=".getBytes(StandardCharsets.UTF_8))));
			list.addAll(Arrays.asList(toObjects(entry.getValue().toString().getBytes(StandardCharsets.UTF_8))));
		}

		byte[] postData = toPrimitives(list.toArray(new Byte[list.size()]));
		URL url;
		String inputLine;
		StringBuffer response = null;
		DataOutputStream wr = null;
		BufferedReader in = null;

		try {
			url = new URL(buildResponseURL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			
			conn.setRequestMethod( "POST" ); conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString(postData.length ));
			 
			conn.setUseCaches(false);
			wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);

			// Read the response
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				// Close connections
				if (wr != null) wr.close();
				if (in != null) in.close();
				
				// Return the results
				return response.toString().getBytes("UTF-8");
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// If there is problem
		return null;
	}
	
	public static byte[] doHttpsPost(String buildResponseURL, Map<String, Object> parameters) {

		// Convert parameters to bytes
		List<Byte> list = new ArrayList<Byte>();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			if(entry.getKey() != null && !"".equals(entry.getKey())){
				System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
				list.addAll(Arrays.asList(toObjects(entry.getKey().getBytes(StandardCharsets.UTF_8))));
				list.addAll(Arrays.asList(toObjects("=".getBytes(StandardCharsets.UTF_8))));
				list.addAll(Arrays.asList(toObjects(entry.getValue().toString().getBytes(StandardCharsets.UTF_8))));
			}			
		}

		byte[] postData = toPrimitives(list.toArray(new Byte[list.size()]));
		URL url;
		String inputLine;
		StringBuffer response = null;
		DataOutputStream wr = null;
		BufferedReader in = null;

		try {
			fixUntrustCertificate();
			url = new URL(buildResponseURL);
			
			// Get and store all cookies
			cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		    CookieHandler.setDefault(cm);	    
		    cookies = cm.getCookieStore().getCookies();

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			
			/*conn.setRequestMethod( "POST" ); conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString(postData.length ));*/
			 
			conn.setUseCaches(false);
			wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);

			// Read the response
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				// Close connections
				if (wr != null) wr.close();
				if (in != null) in.close();
				
				// Return the results
				return response.toString().getBytes("UTF-8");
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// If there is problem
		return null;
	}

}
