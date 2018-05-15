package nl.minezk.dictu.demotoop.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLConfigurationException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eidassaml.starterkit.EidasAttribute;
import eidassaml.starterkit.EidasContactPerson;
import eidassaml.starterkit.EidasLoA;
import eidassaml.starterkit.EidasNameIdType;
import eidassaml.starterkit.EidasNaturalPersonAttributes;
import eidassaml.starterkit.EidasOrganisation;
import eidassaml.starterkit.EidasRequestSectorType;
import eidassaml.starterkit.EidasResponse;
import eidassaml.starterkit.EidasSaml;
import eidassaml.starterkit.EidasSigner;
import eidassaml.starterkit.ErrorCodeException;
import eidassaml.starterkit.Utils;
import eidassaml.starterkit.person_attributes.EidasPersonAttributes;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import nl.minezk.dictu.demotoop.model.ToopException;
import nl.minezk.dictu.demotoop.model.User;

@Component
public class EidasSAMLService implements SAMLService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EidasSAMLService.class);
	
	@Autowired
	private KeyStore signingKeyStore;
	
	@Autowired
	private String signingKeyAlias;
	
	@Autowired
	private char[] signingKeyPass;
	
	@Autowired
	private KeyStore encryptionKeyStore;
	
	@Autowired
	private String encryptionKeyAlias;
	
	@Autowired
	private char[] encryptionKeyPass;
	
	@Autowired private String metadataOrganizationName;
	@Autowired private String metadataOrganizationDisplayName;
	@Autowired private String metadataOrganizationURL;
	@Autowired private String metadataOrganizationLanguage;
	
	@Autowired private String metadataContactSupportOrganizationName;
	@Autowired private String metadataContactSupportGivenName;
	@Autowired private String metadataContactSupportLastName;
	@Autowired private String metadataContactSupportTelNumber;
	@Autowired private String metadataContactSupportEmail;
	
	
	public User getUser(String base64EncodedSaml) throws ToopException {
		List<EidasAttribute> attributes = getAttributes(base64EncodedSaml);
		User.UserBuilder userBuilder = User.builder();
		for (EidasAttribute eidasAttribute : attributes) {
			if (EidasNaturalPersonAttributes.FirstName.getName().equals(eidasAttribute.getPersonAttributeType().getName())) {
				userBuilder.firstName(eidasAttribute.getLatinScript());
			}
			else if (EidasNaturalPersonAttributes.FamilyName.getName().equals(eidasAttribute.getPersonAttributeType().getName())) {
				userBuilder.lastName(eidasAttribute.getLatinScript());
			}
			else if (EidasNaturalPersonAttributes.DateOfBirth.getName().equals(eidasAttribute.getPersonAttributeType().getName())) {
				userBuilder.dateOfBirth(eidasAttribute.getLatinScript());
			}
			else if (EidasNaturalPersonAttributes.PersonIdentifier.getName().equals(eidasAttribute.getPersonAttributeType().getName())) {
				userBuilder.id(eidasAttribute.getLatinScript());
				String cc = eidasAttribute.getLatinScript().substring(0, 2);
				userBuilder.countryCode(cc);
			}
			else {
				LOGGER.warn("Ignoring attribute " + eidasAttribute.getPersonAttributeType().getName());
			}
		}
		return userBuilder.build();
	}
	
	public List<EidasAttribute> getAttributes(String base64EncodedSaml) throws ToopException {
		InputStream is = null;
		try {
			byte[] saml = Base64.getDecoder().decode(base64EncodedSaml);
			Utils.X509KeyPair[] decryptionKeyPairs = {getEidasSDKKeyPair(encryptionKeyAlias, encryptionKeyPass, encryptionKeyStore)};
			X509Certificate[]  signatureAuthors = getAllTrustedCertificates(signingKeyStore);
			is = new ByteArrayInputStream(saml);
			EidasResponse response = EidasSaml.ParseResponse(is, decryptionKeyPairs, signatureAuthors);
			if (StatusCode.SUCCESS.equals(response.getOpenSamlResponse().getStatus().getStatusCode().getValue())) {
				return response.getAttributes();
			}
			else {
				throw new ToopException("Login unsuccessfull.");
			}
		} catch (XMLConfigurationException | XMLParserException | UnmarshallingException | ErrorCodeException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new ToopException("Error parsing Eidas Response.", e);
		}
		finally {
			try {
				if (null!=is) {
					is.close();
				}
			} catch (IOException e) {
				LOGGER.warn("Possible memory leak.", e);
			}
		}
	}

	@Override
	public String getSPMetadata(String issuer, String assertionConsumerServiceURL) throws ToopException {
		try {
			return new String(buildAndSignSPMetadata(new SecureRandomIdentifierGenerator().generateIdentifier(), issuer, assertionConsumerServiceURL, DateTime.now().plusHours(24).toDate()), StandardCharsets.UTF_8);
		} catch (CertificateEncodingException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
				| XMLConfigurationException | IOException | XMLParserException | UnmarshallingException
				| MarshallingException | SignatureException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new ToopException("Unable to build SP Metadata", e);
		}
	}
	
	@Override
	public String base64AuthnRequest(String issuer, String destination, String providerName) throws ToopException {
		try {
			return java.util.Base64.getEncoder().encodeToString(buildAndSignAuthnRequest(issuer, destination, providerName));
		} catch (CertificateEncodingException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
				| XMLConfigurationException | IOException | XMLParserException | UnmarshallingException
				| MarshallingException | SignatureException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new ToopException("Unable to build AuthnRequest", e);
		}
	}
	
	public byte[] buildAndSignAuthnRequest(String issuer, String destination, String providerName) throws CertificateEncodingException, XMLConfigurationException, IOException, XMLParserException, UnmarshallingException, MarshallingException, SignatureException, TransformerFactoryConfigurationError, TransformerException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		return buildAndSignAuthnRequest(
				new SecureRandomIdentifierGenerator().generateIdentifier(),
				issuer,
				destination,
				providerName,
				getMinimalRequestedAttributes(),
				EidasLoA.Substantial
				);
	}
	
	private Map<EidasPersonAttributes, Boolean> getMinimalRequestedAttributes() {
		Map<EidasPersonAttributes, Boolean> req = new HashMap<>();
		req.put(EidasNaturalPersonAttributes.FirstName, true);
		req.put(EidasNaturalPersonAttributes.FamilyName, true);
		req.put(EidasNaturalPersonAttributes.DateOfBirth, true);
		req.put(EidasNaturalPersonAttributes.PersonIdentifier, true);
		return req;
	}

	public byte[] buildAndSignSPMetadata(String id, String issuer, String assertionConsumerServiceURL, Date validUntil) throws CertificateEncodingException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, XMLConfigurationException, IOException, XMLParserException, UnmarshallingException, MarshallingException, SignatureException, TransformerFactoryConfigurationError, TransformerException {
		return EidasSaml.CreateMetaDataNode(
				id, 
				issuer, 
				validUntil, 
				(X509Certificate)signingKeyStore.getCertificate(signingKeyAlias), 
				(X509Certificate)encryptionKeyStore.getCertificate(encryptionKeyAlias), 
				getOrganisation(), 
				getTechContact(), 
				getSupportContact(), 
				assertionConsumerServiceURL, 
				EidasRequestSectorType.Public, 
				getSupportedNameIdTypes(), 
				new EidasSigner(true, (PrivateKey)signingKeyStore.getKey(signingKeyAlias, signingKeyPass), (X509Certificate)signingKeyStore.getCertificate(signingKeyAlias)));
	}
	public byte[] buildAndSignAuthnRequest(String messageId, String issuer, String destination, String providerName, Map<EidasPersonAttributes, Boolean> requestedAttributes, EidasLoA loa) throws CertificateEncodingException, XMLConfigurationException, IOException, XMLParserException, UnmarshallingException, MarshallingException, SignatureException, TransformerFactoryConfigurationError, TransformerException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
			return EidasSaml.CreateRequest(
					messageId,
					issuer,
					destination, 
					providerName,
			    	new EidasSigner(true, (PrivateKey)signingKeyStore.getKey(signingKeyAlias, signingKeyPass), (X509Certificate)signingKeyStore.getCertificate(signingKeyAlias)),
			    	requestedAttributes,
			        EidasRequestSectorType.Public,
			        EidasNameIdType.Transient,
			        loa);
	}

	private static Utils.X509KeyPair getEidasSDKKeyPair(String alias, char[] keyPass, KeyStore keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		Utils.X509KeyPair keyPair = null;
		keyPair = new Utils.X509KeyPair(
        (PrivateKey) keyStore.getKey(alias, keyPass),
        (X509Certificate) keyStore.getCertificate(alias));
		return keyPair;
	}
	
	private static X509Certificate[] getAllTrustedCertificates(KeyStore keyStore) throws KeyStoreException {
		X509Certificate[] certificates = null;
        Enumeration<String> enumeration = keyStore.aliases();
        certificates = new X509Certificate[keyStore.size()];
        int i=0;
        while(enumeration.hasMoreElements()) {
            certificates[i++]=(X509Certificate) keyStore.getCertificate((String) enumeration.nextElement());
        }
        return certificates;
    }
	

	/**
	 *
	 * @return EidasOrganisation object filled with Organization properties.
	 */
	private EidasOrganisation getOrganisation() {
		return new EidasOrganisation(this.metadataOrganizationName,
				this.metadataOrganizationDisplayName,
				this.metadataOrganizationURL,
				this.metadataOrganizationLanguage);
	}

	/**
	 *
	 * @return EidasContactPerson filled with Support Contact properties
	 */
	private EidasContactPerson getSupportContact() {
		return new EidasContactPerson(this.metadataContactSupportOrganizationName,
				this.metadataContactSupportGivenName,
				this.metadataContactSupportLastName,
				this.metadataContactSupportTelNumber,
				this.metadataContactSupportEmail);
	}
	
	/**
	 *
	 * @return EidasContactPerson filled with Technical Contact properties
	 */
	private EidasContactPerson getTechContact() {
		return new EidasContactPerson(this.metadataContactSupportOrganizationName,
				this.metadataContactSupportGivenName,
				this.metadataContactSupportLastName,
				this.metadataContactSupportTelNumber,
				this.metadataContactSupportEmail);
	}

	/**
	 * Return a list of supported NameID types
	 *
	 * @return List of supported NameID Types (all available:
	 *         {@code Transient, Unspecified, Persistent})
	 */
	private List<EidasNameIdType> getSupportedNameIdTypes() {
		return Arrays.asList(EidasNameIdType.values());
	}
}
