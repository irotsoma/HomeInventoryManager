/*
 * Created by irotsoma on 7/3/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.authentication.DataState
import com.irotsoma.homeinventorymanager.data.Attachment
import com.irotsoma.homeinventorymanager.data.AttachmentRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentTest {
    @Autowired
    lateinit var attachmentRepository: AttachmentRepository
    @Autowired
    lateinit var mongoAttachmentService: MongoAttachmentService
    @Test
    fun testAttachment(){
        val file = File(this.javaClass.classLoader.getResource("picture.PNG")!!.file)
        var filestream = file.inputStream()
        val inputHash = Utilities.hashFile(filestream)
        filestream = file.inputStream()
        val id = mongoAttachmentService.addAttachment("test",MockMultipartFile("test",filestream))
        val attachment = Attachment(id, DataState.ACTIVE)
        attachmentRepository.save(attachment)
    }
}