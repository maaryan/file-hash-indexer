package com.maaryan.fhi.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.maaryan.fhi.excp.FileHashIndexerException;

public class FileHash {
	/**
	 * The MD2 message digest algorithm defined in RFC 1319.
	 */
	public static final String ALG_MD2 = "MD2";

	/**
	 * The MD5 message digest algorithm defined in RFC 1321.
	 */
	public static final String ALG_MD5 = "MD5";

	/**
	 * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
	 */
	public static final String ALG_SHA_1 = "SHA-1";

	/**
	 * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
	 */
	public static final String ALG_SHA_256 = "SHA-256";

	/**
	 * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
	 */
	public static final String ALG_SHA_384 = "SHA-384";

	/**
	 * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
	 */
	public static final String ALG_SHA_512 = "SHA-512";

	public static final String ALG_DEFAULT = ALG_SHA_512;

	private MessageDigest fileDigest;
	private Path file;
	private String fileHash;

	public FileHash(Path file, String hashAlgorithm) {
		this.file = file;
		hashAlgorithm = hashAlgorithm.toUpperCase();
		try {
			this.fileDigest = getMessageDigest(hashAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new FileHashIndexerException(hashAlgorithm
					+ " is not valid/supported MessageDigets algorithm.");
		}
	}

	public FileHash(Path file) {
		this(file, ALG_DEFAULT);
	}

	private static MessageDigest getMessageDigest(String algorithm)
			throws NoSuchAlgorithmException {
		algorithm = algorithm.toUpperCase();
		return MessageDigest.getInstance(algorithm);
	}

	public synchronized String getFileHashKey() throws IOException {
		return getFileHashKey(false);
	}

	public synchronized String getFileHashKey(boolean refresh) throws IOException {
		if (refresh || fileHash == null) {
			generateHashKey();
		}
		return fileHash;
	}

	private synchronized void generateHashKey() throws IOException {
		
		File file = this.file.toFile();
		try(FileInputStream data = new FileInputStream(file)){
			int STREAM_BUFFER_LENGTH = 1024*1024;
			final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
			int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

			while (read > -1) {
				fileDigest.update(buffer, 0, read);
				read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
			}
		}
		byte[] bytes = fileDigest.digest();
		fileHash = encodeHexString(bytes)+"rn"+file.length();
		
	}

	private static char[] toDigits = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String encodeHexString(final byte[] data) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return new String(out);
	}
}
