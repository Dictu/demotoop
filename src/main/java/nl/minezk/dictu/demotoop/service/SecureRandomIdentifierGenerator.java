package nl.minezk.dictu.demotoop.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

public class SecureRandomIdentifierGenerator {
	private SecureRandom random;

	public SecureRandomIdentifierGenerator() throws NoSuchAlgorithmException {
		random = SecureRandom.getInstance("SHA1PRNG");
	}

	public SecureRandomIdentifierGenerator(String algorithm) throws NoSuchAlgorithmException {
		random = SecureRandom.getInstance(algorithm);
	}

	public String generateIdentifier() {
		return generateIdentifier(16);
	}

	public String generateIdentifier(int size) {
		byte[] buf = new byte[size];
		random.nextBytes(buf);
		return "_".concat(DatatypeConverter.printHexBinary(buf));
	}
}
