/*
** Copyright 2012 Phokham Nonava
**
** This file is part of Message Digest Integrity Database.
**
** Message Digest Integrity Database is free software: you can redistribute it and/or modify
** it under the terms of the GNU Lesser General Public License as published by
** the Free Software Foundation, either version 3 of the License, or
** (at your option) any later version.
**
** Message Digest Integrity Database is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with Message Digest Integrity Database.  If not, see <http://www.gnu.org/licenses/>.
*/
package mdid;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Phokham Nonava
 */
public abstract class AbstractOperationMode extends SimpleFileVisitor<Path> {

	protected static final String SKIPPING = "SKIPPING";
	protected static final String NEW =      "NEW     ";
	protected static final String EQUAL=     "EQUAL   ";
	protected static final String MODIFIED = "MODIFIED";
	protected static final String DELETED =  "DELETED ";
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractOperationMode.class);
	
	protected final HashDatabase hashDatabase;
	protected final ExceptionDatabase exceptionDatabase;
	private final MessageDigest messageDigest;

	public AbstractOperationMode(Path hashFile, Path exceptionFile) throws NoSuchAlgorithmException, IOException {
		Objects.requireNonNull(hashFile);

		hashDatabase = new HashDatabase(hashFile);
		
		if (exceptionFile == null) {
			exceptionDatabase = new ExceptionDatabase();
		} else {
			exceptionDatabase = new ExceptionDatabase(exceptionFile);
			exceptionDatabase.put(exceptionFile.toString());
		}
		
		exceptionDatabase.put(hashFile.toString());

		messageDigest = MessageDigest.getInstance(HashDatabase.MESSAGEDIGEST);
	}

	public AbstractOperationMode(Path hashFile, Path exceptionFile, boolean writable) throws NoSuchAlgorithmException, IOException {
		Objects.requireNonNull(hashFile);

		hashDatabase = new HashDatabase(hashFile, writable);

		if (exceptionFile == null) {
			exceptionDatabase = new ExceptionDatabase();
		} else {
			exceptionDatabase = new ExceptionDatabase(exceptionFile);
			exceptionDatabase.put(exceptionFile.toString());
		}
		
		exceptionDatabase.put(hashFile.toString());

		messageDigest = MessageDigest.getInstance(HashDatabase.MESSAGEDIGEST);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Objects.requireNonNull(dir);
		Objects.requireNonNull(attrs);

		if (exceptionDatabase.contains(dir.toString())) {
			logger.info("{} {}", SKIPPING, dir.toString());
			return FileVisitResult.SKIP_SUBTREE;
		} else {
			return FileVisitResult.CONTINUE;
		}
	}
	
	protected String getHash(Path file) throws IOException {
		Objects.requireNonNull(file);

		try (DigestInputStream digestInputStream = new DigestInputStream(new FileInputStream(file.toFile()), messageDigest)) {
			byte[] buffer = new byte[1024];
			while (digestInputStream.read(buffer) != -1) {
			}
			
			byte[] hashValue = messageDigest.digest();
			BigInteger bi = new BigInteger(1, hashValue);
			String hash = String.format("%0" + (hashValue.length << 1) + "x", bi);
			
			return hash;
		}
	}

	public void doFinal() {
		try {
			hashDatabase.close();
		} catch (IOException e) {
			logger.warn("Cannot close hash database");
		}
	}

}
