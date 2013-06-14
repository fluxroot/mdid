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
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Phokham Nonava
 */
public final class Mdid {

    private static final Logger logger = LoggerFactory.getLogger(Mdid.class);

    private Mdid() {
    }

    public static void main(String[] args) {
        try {
            Configuration.getInstance().parseArgumens(args);

            AbstractOperationMode mode = Configuration.getInstance().mode;
            Files.walkFileTree(Configuration.getInstance().path, mode);
            mode.doFinal();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("An error occured: {}", e.getLocalizedMessage());
        }
    }

}
