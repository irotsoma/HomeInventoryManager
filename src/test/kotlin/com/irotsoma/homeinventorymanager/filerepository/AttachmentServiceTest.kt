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