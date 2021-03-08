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

package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.rdbms.*
import com.irotsoma.homeinventorymanager.filerepository.mongodb.MongoAttachmentDocumentService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct

/**
 * Service for attachments using [AttachmentEntity]
 *
 * @author Justin Zak
 * @property attachmentRepository Autowired instance of JPA repository for attachments
 * @property attachmentDocumentService Instance of a [BaseAttachmentDocumentService] for manipulating MongoDb entries
 * @property inventoryItemAttachmentLinkRepository Autowired instance of JPA repository for inventory item to attachment links
 */
@Service
@Lazy
class AttachmentService() {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository
    @Autowired
    private lateinit var inventoryItemAttachmentLinkRepository: InventoryItemAttachmentLinkRepository
    @Autowired
    private lateinit var attachmentDocumentRepositoryConfiguration: AttachmentDocumentRepositoryConfiguration
    @Autowired
    private lateinit var mongoAttachmentDocumentService: MongoAttachmentDocumentService

    private var attachmentDocumentService: BaseAttachmentDocumentService? = null

    @PostConstruct
    fun getAttachmentDocumentService(){
        if (attachmentDocumentRepositoryConfiguration.type == "mongodb"){
            attachmentDocumentService = mongoAttachmentDocumentService
        }
    }

    /**
     * Add an attachment to both relational and document database
     *
     * @param name Attachment name
     * @param userId User ID of the owner of the attachment
     * @param fileInfo An instance of a Spring MultipartFile containing the attachment file
     * @return An instance of AttachmentEntity associated with this attachment
     */
    fun addAttachment(name: String, userId: Int, fileInfo: MultipartFile) : AttachmentEntity? {
        val attachmentId = attachmentDocumentService?.addAttachment(name, fileInfo)
        if (attachmentId == null) {
            logger.error{"Unable to access document database resource"}
            return null
        }
        var fileType = MediaType.APPLICATION_OCTET_STREAM
        if (fileInfo.contentType != null){
            try {
                fileType = MediaType.parseMediaType(fileInfo.contentType!!)
            } catch (e: InvalidMediaTypeException){
                //ignore and default to octet stream
            }
        }
        val attachment = Attachment(attachmentId, name, File(fileInfo.originalFilename ?:"").extension, fileType.toString(),  userId, DataState.ACTIVE)
        val savedAttachment = attachmentRepository.save(attachment)
        return AttachmentEntity(savedAttachment, attachmentDocumentService?.getAttachment(attachmentId)!!)
    }

    /**
     * Delete an attachment from both relational and document database with a JPA Attachment object
     *
     * @param attachment An Attachment object for deleting
     */
    fun deleteAttachment(attachment: Attachment){
        attachmentDocumentService?.deleteAttachment(attachment.documentId)
        attachmentRepository.delete(attachment)
    }
    /**
     * Delete an attachment from both relational and document database with an AttachmentEntity object
     *
     * @param attachmentEntity An AttachmentEntity object for deleting
     */
    fun deleteAttachment(attachmentEntity: AttachmentEntity){
        attachmentDocumentService?.deleteAttachment(attachmentEntity.attachmentDocument.attachmentId)
        attachmentRepository.delete(attachmentEntity.attachment)
    }

    /**
     * Gets an instance of AttachmentEntity for the given relational database attachment ID
     *
     * @param id ID of the relational database entry to use to retrieve the attachment.
     * @return An AttachmentEntity object
     */
    fun getAttachment(id: Int): AttachmentEntity?{
        val attachment = attachmentRepository.findById(id)
        if (attachment.isEmpty) {
            return null
        }
        val attachmentDocument = attachmentDocumentService?.getAttachment(attachment.get().documentId)
        if (attachmentDocument == null) {
            logger.error{"Unable to access document database resource"}
            return null
        }
        return AttachmentEntity(attachment.get(), attachmentDocument)
    }

    /**
     * Creates a link between an attachment and an inventory item
     *
     * @param attachmentId The ID of the relational database attachment entry to link
     * @param inventoryItemId The ID of the inventory item to link
     * @return The id of the link record.
     */
    fun attachToInventoryItem(attachmentId: Int, inventoryItemId: Int): Int {
        val link = InventoryItemAttachmentLink(inventoryItemId, attachmentId)
        val savedLink = inventoryItemAttachmentLinkRepository.save(link)
        return savedLink.id!!
    }

    /**
     * Removes the link between an attachment and an inventory item
     *
     * @param attachmentId The ID of the relational database attachment entry to unlink
     * @param inventoryItemId The ID of the inventory item to unlink
     */
    fun detachFromInventoryItem(attachmentId: Int, inventoryItemId: Int) {
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