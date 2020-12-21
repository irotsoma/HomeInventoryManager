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

package com.irotsoma.homeinventorymanager.data

import com.irotsoma.homeinventorymanager.filerepository.AttachmentDocument
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType
import java.io.InputStream

/**
 * Data class that holds an instance of an Attachment JPA object and a related MongoAttachment object that holds an
 * inputstream for accessing the attachment file. Provides convenience methods and protects the objects by holding
 * them as internal properties.
 *
 * @author Justin Zak
 * @property attachment internal read only instance of the JPA attachment object.
 * @property attachmentDocument internal read only instance of the MongoAttachment object.
 * @property inputStream Returns the input stream associated with the MongoAttachment object (read-only)
 * @property name Returns the name of the attachment. (read-only)
 * @property id Returns the relational database unique ID of the Attachment object. (read-only)
 * @property userId Returns the user ID associated with the attachment (read-only)
 * @property dataType Returns the data type as a spring MediaType class. (read-only)
 * @property originalFileExtension Returns the original file extension of the attachment when it was uploaded. (read-only)
 */
data class AttachmentEntity(internal val attachment: Attachment, internal val attachmentDocument: AttachmentDocument) {
    val inputStream: InputStream
        get() {
            return attachmentDocument.inputStream
        }
    val name: String
        get() {
            return attachment.name
        }
    val id: Int
        get() {
            return attachment.id
        }
    val userId: Int
        get() {
            return attachment.userId
        }
    val dataType: MediaType
        get() {
            return try {
                MediaType.parseMediaType(attachment.dataType)
            } catch (e: InvalidMediaTypeException) {
                MediaType.APPLICATION_OCTET_STREAM
            }

        }
    val originalFileExtension: String
        get() {
            return attachment.originalFileExtension
        }
}