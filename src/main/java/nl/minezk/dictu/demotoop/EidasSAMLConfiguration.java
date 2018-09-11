package nl.minezk.dictu.demotoop;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EidasSAMLConfiguration {
	
	{
		Security.addProvider(new BouncyCastleProvider());
	}
	
	@Value("${eidas.node.service.provider.url}")
	public String eidasNodeServiceProviderURL;
	
	@Value("${demotoop.issuer.url}")
	public String demoToopIssuerURL;
	
	@Value("${demotoop.assertion.consumer.service.url}")
	public String demotoopAssertionConsumerServiceURL;
	
	@Value("${demotoop.providername}")
	public String demoToopProviderName;
	
	@Value("${signing.keystore.location}")
	public String signingKeyStoreLocation;
	
	@Value("${signing.keystore.password}")
	public char[] signingKeyStorePassword;
	
	@Value("${signing.keys.alias}")
	public String signingKeyAlias;
	
	@Value("${signing.key.password}")
	public char[] signingKeyPassword;
	
	@Value("${encryption.keystore.location}")
	public String encryptionKeyStoreLocation;
	
	@Value("${encryption.keystore.password}")
	public char[] encryptionKeyStorePassword;
	
	@Value("${encryption.keys.alias}")
	public String encryptionKeyAlias;
	
	@Value("${encryption.key.password}")
	public char[] encryptionKeyPassword;
	
	@Value("${metadata.organization.name}") private String metadataOrganizationName;
	@Value("${metadata.organization.displayname}") private String metadataOrganizationDisplayName;
	@Value("${metadata.organization.url}") private String metadataOrganizationURL;
	@Value("${metadata.organization.language}") private String metadataOrganizationLanguage;
	
	@Value("${metadata.contact.support.organization.name}") private String metadataContactSupportOrganizationName;
	@Value("${metadata.contact.support.givenname}") private String metadataContactSupportGivenName;
	@Value("${metadata.contact.support.lastname}") private String metadataContactSupportLastName;
	@Value("${metadata.contact.support.telnumber}") private String metadataContactSupportTelNumber;
	@Value("${metadata.contact.support.email}") private String metadataContactSupportEmail;
	
	@Bean
	public String demotoopAssertionConsumerServiceURL() {
		return this.demotoopAssertionConsumerServiceURL;
	}
	@Bean
	public String metadataOrganizationName() {
		return this.metadataOrganizationName;
	}
	@Bean
	public String metadataOrganizationDisplayName() {
		return this.metadataOrganizationDisplayName;
	}
	@Bean
	public String metadataOrganizationURL() {
		return this.metadataOrganizationURL;
	}
	@Bean
	public String metadataOrganizationLanguage() {
		return this.metadataOrganizationLanguage;
	}
	@Bean
	public String metadataContactSupportOrganizationName() {
		return this.metadataContactSupportOrganizationName;
	}
	@Bean
	public String metadataContactSupportGivenName() {
		return this.metadataContactSupportGivenName;
	}
	@Bean
	public String metadataContactSupportLastName() {
		return this.metadataContactSupportLastName;
	}
	@Bean
	public String metadataContactSupportTelNumber() {
		return this.metadataContactSupportTelNumber;
	}
	@Bean
	public String metadataContactSupportEmail() {
		return this.metadataContactSupportEmail;
	}
	@Bean
	public String eidasNodeServiceProviderURL() {
		return this.eidasNodeServiceProviderURL;
	}
	
	@Bean
	public String demotoopIssuerURL() {
		return this.demoToopIssuerURL;
	}
	
	@Bean String demotoopProviderName() {
		return this.demoToopProviderName;
	}
	
	@Bean
	public KeyStore signingKeyStore() {
		return getJksKeyStore(signingKeyStoreLocation, signingKeyStorePassword);
	}
	
	@Bean
	public String signingKeyAlias() {
		return this.signingKeyAlias;
	}
	
	@Bean
	public char[] signingKeyPass() {
		return this.signingKeyPassword;
	}
	
	@Bean
	public KeyStore encryptionKeyStore() {
		return getJksKeyStore(encryptionKeyStoreLocation, encryptionKeyStorePassword);
	}
	
	@Bean
	public String encryptionKeyAlias() {
		return this.encryptionKeyAlias;
	}
	
	@Autowired
	public char[] encryptionKeyPass() {
		return this.encryptionKeyPassword;
	}
	
	/**
	 * Returns jks {@link KeyStore} for given location and storePass with default keyStoreType.
	 * @param location absolute file path to jks keystore.
	 * @param storePass password to jks keystore
	 * @return
	 */
	private static KeyStore getJksKeyStore(String location, char[] storePass) {
		
		Path p = Paths.get(location);
		PasswordProtection pp = new PasswordProtection(storePass);
		try {
			return KeyStore.Builder.newInstance("jks", null, p.toFile(), pp).getKeyStore();
		} catch (KeyStoreException e) {
			throw new BeanCreationException("Could not initilize jks keystore for location: " + location, e);
		}
	}
}
