package com.irotsoma.homeinventorymanager.filerepository


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
    @Autowired
    lateinit var mongoAttachmentService: MongoAttachmentService
    @Test
    fun attachments() {
        val file = File(this.javaClass.classLoader.getResource("picture.PNG")!!.file)
        var filestream = file.inputStream()
        val inputHash = Utilities.hashFile(filestream)
        filestream = file.inputStream()
        val attachmentId = mongoAttachmentService.addAttachment("test", MockMultipartFile("test", filestream))
        val attachmentFile = mongoAttachmentService.getAttachment(attachmentId)
        assert(attachmentFile != null)
        val outputHash = Utilities.hashFile(attachmentFile!!.inputStream)
        assert(inputHash == outputHash)
        //TODO: delete

    }

}