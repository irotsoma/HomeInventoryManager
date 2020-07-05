/*
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import java.io.InputStream
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter


/**
 * A singleton object that contains shared Utilities for use by various cloudbackenc applications.
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