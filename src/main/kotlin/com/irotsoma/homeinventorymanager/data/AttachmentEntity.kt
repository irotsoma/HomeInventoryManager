/*
 * Created by irotsoma on 7/12/2020.
 */
package com.irotsoma.homeinventorymanager.data

import com.irotsoma.homeinventorymanager.filerepository.MongoAttachment
import java.io.InputStream

data class AttachmentEntity(val attachment: Attachment, val mongoAttachment: MongoAttachment) {
    fun getInputStream(): InputStream{
        return mongoAttachment.inputStream
    }
    fun getName(): String {
        return attachment.name
    }
    fun getId(): Int {
        return attachment.id
    }
}