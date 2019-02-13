package com.repeater.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mgnt.utils.StringUnicodeEncoderDecoder;
import com.repeater.app.WebRepeater;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handlers {

	public static class AllHandler implements HttpHandler {
		
		private String buildResponseURL(URI requestedUri){
			
			// Example: scheme://hostname:port/context-path/path/to/servlet/path/path-info?query#fragment
			// The request URI (returned by getRequestURI) corresponds to /context-path/path/to/servlet/path/path-info?query#fragment
			
			// send response
			String scheme = WebRepeater.destProtocol;
			String hostname = WebRepeater.destDomain;
			String port = WebRepeater.destPort;
			String url = requestedUri.toString();
			String fragment = "";
			String reponseURL = scheme + "://" + hostname + ":" + port + url;
			
			// Return
			return reponseURL;
		}
		
		private Map<String, Object> returnPostParameters(HttpExchange he) throws IOException{
			
			// parse request to get parameters
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			
			StringBuilder stringBuilder = new StringBuilder();
			
			br.lines().forEach( (String s) -> stringBuilder.append(s + "\n") );
			
			parseQuery(stringBuilder.toString(), parameters);
			
			// send response
			String response = "";
			for (String key : parameters.keySet()) response += key + " = " + parameters.get(key) + "\n";			 
			
			return parameters;			
		}

		@Override
		public void handle(HttpExchange he) throws IOException {

			// parse request
			URI requestedUri = he.getRequestURI();
			
			System.out.println("- RequestURI : " + buildResponseURL(requestedUri));
			
			// Create the response variable
			byte[] response = null;
			
			// Treat according the requested method
			if (he.getRequestMethod().equalsIgnoreCase("POST")) {
				
				// Read the posted parameters
				Map<String, Object> parameters = returnPostParameters(he);
				
				// Do the post according the protocol
				if(WebRepeater.getDestProtocol().equalsIgnoreCase("https")){
					response = HTTPClient.doHttpsPost(buildResponseURL(requestedUri), parameters);
				} else {
					response = HTTPClient.doHttpPost(buildResponseURL(requestedUri), parameters);
				}				

			} else if (he.getRequestMethod().equalsIgnoreCase("GET")) {	
				
				// Nothing needed here, the get variable are already included
				response = HTTPClient.doGet(buildResponseURL(requestedUri), he.getRequestHeaders());
				
			} else if (he.getRequestMethod().equalsIgnoreCase("HEAD")) {
				
				response = HTTPClient.doGet(buildResponseURL(requestedUri), he.getRequestHeaders());							
			}
			
			System.out.println("- ResponseCode : "+ HTTPClient.getResponseCode());
			/**/
			// Populate the same headers		
			for (Map.Entry<String, List<String>> entry : HTTPClient.getRespHeaders().entrySet()){
				if(
						entry.getKey() != null && 
						!"".equals(entry.getKey()) && 
						!"null".equals(entry.getKey()) &&
						!entry.getKey().equals("Transfer-Encoding") && 
						!entry.getValue().toString().replace("[", "").replace("]", "").equals("chunked")){
					he.getResponseHeaders().set(entry.getKey(), entry.getValue().toString().replace("[", "").replace("]", ""));
					System.out.println("	- Header : ["+ entry.getKey() + "] value : [" + entry.getValue().toString()+"]");
				}				
			}
			// Create variables
			String contentType = HTTPClient.getContentType(buildResponseURL(requestedUri));
			String contentEncoding = HTTPClient.getContentEncoding(buildResponseURL(requestedUri));

			// Set the headers
			if (!"null".equals(contentType) && !"".equals(contentType) && contentType != null)
				he.getResponseHeaders().set("Content-Type", contentType);
			if (!"null".equals(contentEncoding) && !"".equals(contentEncoding) && contentEncoding != null)
				he.getResponseHeaders().set("Content-Encoding", contentEncoding);
			
			
			
			// Set the cookies
			/**/List<String> cookies = HTTPClient.getCookies();
			if(cookies!=null && cookies.size() >0){
				he.getResponseHeaders().put("Set-Cookie", cookies);
			}
			
			// Write the response
			he.sendResponseHeaders(HTTPClient.getResponseCode(), response.length);
			OutputStream os = he.getResponseBody();
			os.write(response);
			os.close();	
		    
		}

	}

	public static class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			String response = "<h1><center><b>WEB REPEATER: </b>Server start successfully if you see this message</center></h1>";
			response += "<div style='width:50%;float:left;'><ul>";
			response += "<li><h3>Source Port: "+WebRepeater.getSrcPort()+" </h3></li>";
			response += "<li><h3>Source Domain: "+WebRepeater.getSrcDomain()+" </h3></li>";
			response += "<li><h3>Source Protocol: "+WebRepeater.getSrcProtocol()+" </h3></li>";
			response += " </ul></div>";
			
			response += "<div style='width:50%;float:right;'><ul>";
			response += "<li><h3>Destination Port: "+WebRepeater.getDestPort()+" </h3></li>";
			response += "<li><h3>Destination Domain: "+WebRepeater.getDestDomain()+" </h3></li>";
			response += "<li><h3>Destination Protocol: "+WebRepeater.getDestProtocol()+" </h3></li>";
			response += " </ul></div>";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public static class EchoHeaderHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			Headers headers = he.getRequestHeaders();
			Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
			String response = "";
			for (Map.Entry<String, List<String>> entry : entries)
				response += entry.toString() + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static class EchoGetHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = he.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet())
				response += key + " = " + parameters.get(key) + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}

	}

	public static class EchoPostHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoPost handler...");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet())
				response += key + " = " + parameters.get(key) + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}
	}

	@SuppressWarnings("unchecked")
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}