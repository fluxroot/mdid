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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Phokham Nonava
 */
public class AnalysisMode extends AbstractOperationMode {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisMode.class);

	public AnalysisMode(Path hashFile, Path exceptionFile) throws IOException, NoSuchAlgorithmException {
		super(hashFile, exceptionFile, false);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(attrs);
		
		if (exceptionDatabase.contains(file.toString())) {
			logger.info("{} {}", SKIPPING, file.toString());
		} else {
			String oldHash = hashDatabase.get(file.toString());
			if (oldHash == null) {
				logger.info("{} {}", NEW, file.toString());
			} else {
				String hash = getHash(file);
				if (hash.equalsIgnoreCase(oldHash)) {
					logger.info("{} {}", EQUAL, file.toString());
				} else {
					logger.info("{} {}", MODIFIED, file.toString());
				}
				hashDatabase.mark(file.toString());
			}
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public void doFinal() {
		for (String path : hashDatabase.getUnmarked()) {
			logger.info("{} {}", DELETED, path);
		}

		super.doFinal();
	}

}
