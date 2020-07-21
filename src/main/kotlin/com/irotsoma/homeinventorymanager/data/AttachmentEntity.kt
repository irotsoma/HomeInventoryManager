/*
 * Created by irotsoma on 7/12/2020.
 */
package com.irotsoma.homeinventorymanager.data

import com.irotsoma.homeinventorymanager.filerepository.MongoAttachment
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType
import java.io.InputStream

data class AttachmentEntity(internal val attachment: Attachment, internal val mongoAttachment: MongoAttachment) {
    val inputStream: InputStream
        get() {
            return mongoAttachment.inputStream
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