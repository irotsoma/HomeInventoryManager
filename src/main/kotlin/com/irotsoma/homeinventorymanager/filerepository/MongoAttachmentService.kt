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


@Service
class MongoAttachmentService {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate

    @Autowired
    lateinit var operations: GridFsOperations

    fun addAttachment(name: String, fileInfo: MultipartFile): String {
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

    fun getAttachment(id: String): MongoAttachment? {
        val file: GridFSFile = gridFsTemplate.findOne(Query(Criteria.where("_id").`is`(id))) ?: return null
        return MongoAttachment(id, file.metadata!!["name"].toString(), operations.getResource(file).inputStream)
    }

    fun removeAttachment(id: String) {
        gridFsTemplate.delete(Query(Criteria.where("_id").`is`(id)))
    }

    //TODO: P2 delete with timed service to clean up items that have been deleted for a long time for db cleanup
}