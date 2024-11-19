/**
 * 
 */
package com.main.thirdpartyfunctions;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

import com.main.models.ClaimFilter;

/**
 * @author v-atamilselvan
 * @author v-hsulaiman
 * 
 */
public class Jenkins {

	private static final String url = "https://testopslab.emblemhealth.com:8443/job/Facets/job/%s/job/ClaimCreationByIDandFilters/buildWithParameters?token=REACT_CREATE&CLAIM_ID=%s&CLAIM_FILTERS=%s&SOURCEENV=%s";
	private static final String hoc3_data_url = "hhttps://testopslab.emblemhealth.com:8443/job/iQTDS/job/HOC3_DataFinder/";
	private static final String testops_username = "v-atamilselvan";
	private static final String testops_apiToken = "11ead736b67731e149964ef4d3a5117acb";
	private static final String automation_username = "e2e_automation";
	private static final String automation_apiToken = "11ead736b67731e149964ef4d3a5117acb";

	private List<String> logs;
	private String queueLocation;
	private String buildNumber;
	private String buildURL;

	public Jenkins() {
		logs = new ArrayList<String>();
	}

	public String trigger() throws InterruptedException, IOException {
		URI uri = URI.create(url);
		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = getcredsProvider(uri);

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();

		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		HttpGet httpGet = new HttpGet(uri);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		HttpResponse response = httpClient.execute(host, httpGet, localContext);

		return response.getStatusLine().toString();
	}

	public Jenkins triggerOnlineClaimCreation(String env, String claimID) throws Exception {
		String jobUrl = String.format(url, env, claimID);
		URI uri = URI.create(jobUrl);
		HttpHost host = getHttpHost(uri);
		HttpClient httpClient = getHttpClient(uri);
		HttpPost httpPost = new HttpPost(uri);
		HttpClientContext localContext = getClientContext(host);
		HttpResponse response = httpClient.execute(host, httpPost, localContext);

		System.out.println(EntityUtils.toString(response.getEntity()));

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
			logs.add("Jenkins job is not triggered in the url:" + jobUrl);
			return this;
		}

		this.queueLocation = response.getHeaders("Location")[0].getValue();
		logs.add("Jenkins job is created by queue:" + queueLocation);
		System.out.println("Jenkins job is created by queue:" + queueLocation);

		return this;

	}

	public Jenkins triggerOnlineClaimCreation(String env,String claimID, List<ClaimFilter> claimFilters, String sourceEnv)
			throws Exception {
		String claimFiltersUnEncoded = claimFilters.stream().map(cf -> cf.toString()).collect(Collectors.joining("~"));
		String jobUrl = String.format(url, env, claimID, URLEncoder.encode(claimFiltersUnEncoded, "UTF-8"), sourceEnv);
		//System.out.println(StringUtils.repeat("#", 100));
		System.out.println(jobUrl);
		//System.out.println(StringUtils.repeat("#", 100));

		URI uri = URI.create(jobUrl);
		HttpHost host = getHttpHost(uri);
		HttpClient httpClient = getHttpClient(uri);
		HttpPost httpPost = new HttpPost(uri);
		HttpClientContext localContext = getClientContext(host);
		HttpResponse response = httpClient.execute(host, httpPost, localContext);

		System.out.println(EntityUtils.toString(response.getEntity()));

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
			logs.add("Jenkins job is not triggered in the url:" + jobUrl);
			return this;
		}

		this.queueLocation = response.getHeaders("Location")[0].getValue();
		logs.add("Jenkins job is created by queue:" + queueLocation);
		System.out.println("Jenkins job is created by queue:" + queueLocation);

		return this;

	}

	public String getJobStatus(String url) throws Exception {
		URI jobUri = URI.create(url + "api/json");
		HttpClient jobClient = getHttpClient(jobUri);
		HttpHost jobHost = getHttpHost(jobUri);

		HttpResponse jobResponse = jobClient.execute(jobHost, new HttpGet(jobUri), getClientContext(jobHost));

		if (jobResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			return null;

		JSONObject job = new JSONObject(EntityUtils.toString(jobResponse.getEntity()));

		if (job.has("result"))
			return job.getString("result");

		return null;
	}

	public HashMap<String, String> getJobLogs(String url) throws Exception {
		HashMap<String, String> response = new HashMap<String, String>();

		URI jobUri = URI.create(url + "/logText/progressiveText?start=0");
		HttpClient jobClient = getHttpClient(jobUri);
		HttpHost jobHost = getHttpHost(jobUri);

		HttpResponse jobResponse = jobClient.execute(jobHost, new HttpGet(jobUri), getClientContext(jobHost));
		
		response.put("StatusCode", String.valueOf(jobResponse.getStatusLine().getStatusCode()));
		if (jobResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			System.out.println("job response is " + jobResponse.getStatusLine().getStatusCode());
			return response;
		}
		;

		String respString = EntityUtils.toString(jobResponse.getEntity());
		StringBuilder logBuilder = new StringBuilder();
		boolean line_started = false;
		String createdClaimID = StringUtils.EMPTY;
		String[] newLineArr = respString.split(System.lineSeparator());

		loop: for (int i = 0; i < newLineArr.length; i++) {

			String line = newLineArr[i];
			if (StringUtils.startsWith(line.trim(), "Running Test") && !line_started)
				line_started = true;

			if (!line_started)
				continue;

			logBuilder.append(line + System.lineSeparator());

			if (StringUtils.contains(line, "Status-PASS :: Claim ID:")) {
				createdClaimID = StringUtils.substringAfter(line, "Status-PASS :: Claim ID:").trim();
				break loop;
			}
		}
		response.put("FinalStatus", StringUtils.EMPTY);
		

		String lastline = newLineArr[newLineArr.length-1];
		if (lastline.contains("Finished")) {
			response.put("FinalStatus", lastline.split(":")[1].trim());
		}

		response.put("logs", logBuilder.toString());
		response.put("claimID", createdClaimID);
		
		return response;

	}

	public Jenkins checkQueue() throws Exception {

		if (queueLocation == null)
			return this;

		System.out.println("Checking queue:" + queueLocation);

		URI queueURI = URI.create(queueLocation + "api/json/");
		HttpClient queueClient = getHttpClient(queueURI);
		HttpHost queueHost = getHttpHost(queueURI);

		/*
		 * check build info for 20 seconds before return null
		 */
		for (int i = 1; i <= 20; i++) {
			if (this.buildNumber != null && this.buildURL != null)
				break;

			Thread.sleep(1000);
			System.out.format("Waited %d seconds \n", i);

			HttpResponse queueResponse = queueClient.execute(queueHost, new HttpGet(queueURI),
					getClientContext(queueHost));

			if (queueResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("could not get queue details for location:" + queueLocation);
				if (i < 20) {
					continue;
				} else {
					logs.add("could not get queue details for location:" + queueLocation);
					return this;
				}
			}

			JSONObject queueResponseData = new JSONObject(EntityUtils.toString(queueResponse.getEntity()));

			if (!queueResponseData.isNull("executable")) {
				JSONObject execResponse = queueResponseData.getJSONObject("executable");
				logs.add("build created in jenkins");

				this.buildNumber = execResponse.getString("number");
				this.buildURL = execResponse.getString("url");
				logs.add("build number " + this.buildNumber + " @ " + this.buildURL);
			}
		}

		return this;
	}

	public CredentialsProvider getcredsProvider(URI uri) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		if (uri.toString().toLowerCase().contains("testops")) {
			credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(testops_username, testops_apiToken));
		} else if (uri.toString().toLowerCase().contains("njsgehalmsa01qa")) {
			credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(automation_username, automation_apiToken));
		} 

		return credsProvider;
	}

	private HttpClient getHttpClient(URI uri) throws Exception {

		CredentialsProvider credsProvider = getcredsProvider(uri);

		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

		BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
				socketFactoryRegistry);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
				.setConnectionManager(connectionManager).setDefaultCredentialsProvider(credsProvider).build();

		return httpClient;
	}

	private HttpClientContext getClientContext(HttpHost host) {

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();

		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		return localContext;
	}

	private HttpHost getHttpHost(URI uri) {
		return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public String getBuildURL() {
		return buildURL;
	}

	public String getLogs() {
		return logs.stream().collect(Collectors.joining("\n"));
	}

	public String executeDataJob(String dbname, Long reqId, Long runId) throws Exception {
		String buildID = null;
		String url = StringUtils.appendIfMissing(hoc3_data_url, "/") + "buildWithParameters?";

		String params = String.format("dbname=facets.hoc3.%s&query_req_id=%d&run_id=%d", dbname, reqId, runId);

		url = url + params;

		URI uri = URI.create(url);
		HttpHost host = getHttpHost(uri);
		HttpClient httpClient = getHttpClient(uri);
		HttpPost httpPost = new HttpPost(uri);
		HttpClientContext localContext = getClientContext(host);
		HttpResponse response = httpClient.execute(host, httpPost, localContext);

		/*
		 * if post not successful return return -1 and do not proceed
		 */
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED)
			throw new IllegalStateException("Could not trigger job");

		String queue = response.getHeaders("Location")[0].getValue();

		URI queueURI = URI.create(queue + "api/json/");
		HttpClient queueClient = getHttpClient(queueURI);
		HttpHost queueHost = getHttpHost(queueURI);

		/*
		 * check build info for 20 seconds before return null
		 */
		for (int i = 1; i <= 20; i++) {

			if (buildID != null)
				break;

			Thread.sleep(1000);

			HttpResponse queueResponse = queueClient.execute(queueHost, new HttpGet(queueURI),
					getClientContext(queueHost));

			if (queueResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {

				if (i < 20) {
					continue;
				} else {
					throw new IllegalStateException("Could not create jenkins build");
				}
			}

			JSONObject queueResponseData = new JSONObject(EntityUtils.toString(queueResponse.getEntity()));

			if (!queueResponseData.isNull("executable")) {
				JSONObject execResponse = queueResponseData.getJSONObject("executable");
				buildID = execResponse.getString("number");
			}

		}

		return buildID;
	}
}
