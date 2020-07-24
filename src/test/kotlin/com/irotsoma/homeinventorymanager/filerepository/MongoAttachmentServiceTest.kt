package com.irotsoma.homeinventorymanager.filerepository


import com.irotsoma.homeinventorymanager.Utilities
import mu.KLogging
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class MongoAttachmentServiceTest {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    lateinit var mongoAttachmentService: MongoAttachmentService
    @Test
    fun mongoAttachments() {
        val file = File(this.javaClass.classLoader.getResource("picture.PNG")!!.file)
        var filestream = file.inputStream()
        val inputHash = Utilities.hashFile(filestream)
        filestream = file.inputStream()
        logger.debug{"Adding file to mongo..."}
        val attachmentId = mongoAttachmentService.addAttachment("test", MockMultipartFile("test", filestream))
        logger.debug{"Retrieving file from mongo..."}
        val attachmentFile = mongoAttachmentService.getAttachment(attachmentId)
        assert(attachmentFile != null) {"File could not be retrieved from MongoDB."}
        logger.debug{"File has been successfully retrieved."}
        val outputHash = Utilities.hashFile(attachmentFile!!.inputStream)
        assert(inputHash == outputHash) {"Hash does not match! Previous hash: $inputHash; New hash: $outputHash"}
        logger.debug{"Output file hash matches input file hash."}
        logger.debug{"Deleting file from mongo..."}
        mongoAttachmentService.deleteAttachment(attachmentId)
        assert(mongoAttachmentService.getAttachment(attachmentId) == null) {"File is still present in MongoDB!"}
        logger.debug{"File successfully deleted."}
    }

}