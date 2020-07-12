/*
 * Created by irotsoma on 7/12/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.data.Attachment
import com.irotsoma.homeinventorymanager.data.AttachmentEntity
import com.irotsoma.homeinventorymanager.data.AttachmentRepository
import com.irotsoma.homeinventorymanager.data.DataState
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AttachmentService {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Autowired
    lateinit var attachmentRepository: AttachmentRepository
    @Autowired
    lateinit var mongoAttachmentService: MongoAttachmentService

    fun addAttachment(name: String, userId: Int, fileInfo: MultipartFile) : AttachmentEntity{
        val mongoAttachmentId = mongoAttachmentService.addAttachment(name, fileInfo)
        val attachment = Attachment(mongoAttachmentId, name, userId, DataState.ACTIVE)
        val savedAttachment = attachmentRepository.save(attachment)
        return AttachmentEntity(savedAttachment, mongoAttachmentService.getAttachment(mongoAttachmentId)!!)
    }

    fun deleteAttachment(attachment: Attachment){
        mongoAttachmentService.deleteAttachment(attachment.mongoId)
        attachmentRepository.delete(attachment)
    }
    fun deleteAttachment(attachmentEntity: AttachmentEntity){
        mongoAttachmentService.deleteAttachment(attachmentEntity.mongoAttachment.attachmentId)
        attachmentRepository.delete(attachmentEntity.attachment)
    }

    fun getAttachment(id: Int): AttachmentEntity?{
        val attachment = attachmentRepository.findById(id)
        if (attachment.isEmpty) {
            return null
        }
        val mongoAttachment = mongoAttachmentService.getAttachment(attachment.get().mongoId)
        if (mongoAttachment == null) {
            logger.error{"Unable to access mongo resource"}
            return null
        }
        return AttachmentEntity(attachment.get(), mongoAttachment)
    }
}