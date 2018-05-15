package nl.minezk.dictu.demotoop;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfiguration {
	
	@Bean
	public HttpHost proxy() {
		return new HttpHost("cacheflow.nic.agro.nl", 8080);
	}
	
	@Bean
	public List<String> nonProxyHosts() {
		return  Arrays.asList("localhost", "127.0.0.1");
	}
	
	@Bean
	public RestTemplate localRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(localHttpClient());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	@Bean
	public RestTemplate restTemplate() 
	                throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	    HttpComponentsClientHttpRequestFactory requestFactory =
	                    new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setHttpClient(httpClient());
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    return restTemplate;
	 }
	
	@Bean
	public TrustStrategy acceptingTrustStrategy() {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		return acceptingTrustStrategy;
	}
	
	@Bean
	public HttpClient httpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(acceptingTrustStrategy())
                .build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	    CloseableHttpClient httpClient = HttpClients.custom()
	    				.setProxy(proxy())
	                    .setSSLSocketFactory(csf)
	                    .build();
	    return httpClient;
	}
	
	@Bean
	public HttpClient localHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(acceptingTrustStrategy())
                .build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	    CloseableHttpClient httpClient = HttpClients.custom()
	                    .setSSLSocketFactory(csf)
	                    .build();
	    return httpClient;
	}

	
}
