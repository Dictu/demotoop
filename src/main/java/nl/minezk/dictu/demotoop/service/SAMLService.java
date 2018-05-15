package nl.minezk.dictu.demotoop.service;

import nl.minezk.dictu.demotoop.model.ToopException;

public interface SAMLService {

	/**
	 * Returns signed and Base64 encoded AuthnRequest.
	 * @param issuer
	 * @param destination
	 * @param providerName
	 * @return
	 * @throws ToopException
	 */
	public String base64AuthnRequest(String issuer, String destination, String providerName) throws ToopException;

	/**
	 * Returns metadata for this Service Provider as xml.
	 * @return
	 */
	public String getSPMetadata(String issuer, String assertionConsumerServiceURL) throws ToopException;
	
	
}
