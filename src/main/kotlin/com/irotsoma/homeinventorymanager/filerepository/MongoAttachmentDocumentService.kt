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

/*
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.GridFSFile
import mu.KLogging
import org.apache.tika.config.TikaConfig
import org.apache.tika.io.TikaInputStream
import org.apache.tika.mime.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * Implementation of [BaseAttachmentDocumentService] for MongoDb
 *
 * @property gridFsTemplate An autowired instance of GridFsTemplate associated with the configured MongoDb
 * @property operations An autowired instance of GridFsOperations associated with the configured MongoDb
 * @author Justin Zak
 */
@Service
class MongoAttachmentDocumentService: BaseAttachmentDocumentService {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate

    @Autowired
    lateinit var operations: GridFsOperations

    override fun addAttachment(name: String, fileInfo: MultipartFile): String {
        val metadata = BasicDBObject()
        metadata["name"] = name
        val config = TikaConfig.getDefaultConfig()
        val detector = config.detector
        val stream = TikaInputStream.get(fileInfo.inputStream)
        val tikaMetadata = org.apache.tika.metadata.Metadata()
        tikaMetadata.add(org.apache.tika.metadata.Metadata.RESOURCE_NAME_KEY, fileInfo.originalFilename)
        val mediaType: MediaType = detector.detect(stream, tikaMetadata)
        metadata["mediaType"] = mediaType.toString()
        val id = gridFsTemplate.store(fileInfo.inputStream, fileInfo.name, mediaType.toString(), metadata)
        return id.toString()
    }

    override fun getAttachment(id: String): AttachmentDocument? {
        val file: GridFSFile = gridFsTemplate.findOne(Query(Criteria.where("_id").`is`(id))) ?: return null
        return AttachmentDocument(id, file.metadata!!["name"].toString(), operations.getResource(file).inputStream)
    }

    override fun deleteAttachment(id: String) {
        gridFsTemplate.delete(Query(Criteria.where("_id").`is`(id)))
    }
}