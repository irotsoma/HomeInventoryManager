/*
 * Created by irotsoma on 7/3/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.Utilities
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentServiceTest {
    @Autowired
    lateinit var attachmentService: AttachmentService
    @Test
    fun testAttachmentService(){
        val file = File(this.javaClass.classLoader.getResource("picture.PNG")!!.file)
        var filestream = file.inputStream()
        val inputHash = Utilities.hashFile(filestream)
        filestream = file.inputStream()
        val attachment = attachmentService.addAttachment("test", 1, MockMultipartFile("test",filestream))
        val outputAttachment = attachmentService.getAttachment(attachment.id)
        assert(outputAttachment != null)
        val outputFile =  File.createTempFile("out",".tmp")
        outputAttachment!!.inputStream.transferTo(outputFile.outputStream())
        attachmentService.deleteAttachment(attachment)
        val outputHash = Utilities.hashFile(outputFile.inputStream())
        assert(inputHash == outputHash)

    }
}