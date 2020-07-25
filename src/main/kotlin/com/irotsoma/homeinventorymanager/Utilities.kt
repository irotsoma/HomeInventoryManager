/*
 *  Copyright (C) 2020  Justin Zak
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.irotsoma.homeinventorymanager

import java.io.InputStream
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter


/**
 * A singleton object that contains shared Utilities
 *
 * @author Justin Zak
 */
object Utilities {
    /**
     * Returns the SHA1 hash of the file specified.
     *
     * @param inputStream A Java InputStream object to be hashed
     * @return The file has as a string.
     */
    fun hashFile(inputStream: InputStream): String {
        val messageDigest = MessageDigest.getInstance("SHA1")
        val dataBytes = ByteArray(1024)
        var readBytes = inputStream.read(dataBytes)
        while (readBytes > -1) {
            messageDigest.update(dataBytes, 0, readBytes)
            readBytes = inputStream.read(dataBytes)
        }
        val outputBytes: ByteArray = messageDigest.digest()
        return DatatypeConverter.printHexBinary(outputBytes)
    }
}