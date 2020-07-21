/*
 * Created by irotsoma on 7/12/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.data.*
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class AttachmentService {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository
    @Autowired
    private lateinit var mongoAttachmentService: MongoAttachmentService
    @Autowired
    private lateinit var inventoryItemAttachmentLinkRepository: InventoryItemAttachmentLinkRepository

    fun addAttachment(name: String, userId: Int, fileInfo: MultipartFile) : AttachmentEntity {
        val mongoAttachmentId = mongoAttachmentService.addAttachment(name, fileInfo)
        var fileType = MediaType.APPLICATION_OCTET_STREAM
        if (fileInfo.contentType != null){
            try {
                fileType = MediaType.parseMediaType(fileInfo.contentType!!)
            } catch (e: InvalidMediaTypeException){
                //ignore and default to octet stream
            }
        }
        val attachment = Attachment(mongoAttachmentId, name, File(fileInfo.originalFilename ?:"").extension, fileType.toString(),  userId, DataState.ACTIVE)
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

    fun attachToInventoryItem(attachmentId: Int, inventoryItemId: Int): Int {
        val link = InventoryItemAttachmentLink(inventoryItemId, attachmentId)
        val savedLink = inventoryItemAttachmentLinkRepository.save(link)
        return savedLink.id!!
    }

    fun detachFromInventoryItem(attachmentId: Int, inventoryItemId: Int){
        val link = inventoryItemAttachmentLinkRepository.findByInventoryItemIdAndAttachmentId(inventoryItemId,attachmentId)
        if (link != null) {
            inventoryItemAttachmentLinkRepository.delete(link)
        }
        //if no inventory items are using this attachment, delete it
        //TODO: when implementing future functionality to allow user to maintain attachment repository remove this
        if (inventoryItemAttachmentLinkRepository.findByAttachmentId(attachmentId).isEmpty()){
            deleteAttachment(attachmentRepository.findById(attachmentId).get())
        }
    }
}