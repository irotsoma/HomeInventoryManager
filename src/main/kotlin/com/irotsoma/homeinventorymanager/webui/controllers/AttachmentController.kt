/*
 * Created by irotsoma on 7/17/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.filerepository.AttachmentService
import com.irotsoma.homeinventorymanager.webui.models.FormResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Controller
@Lazy
@RequestMapping("/attachment")
@Secured("ROLE_USER")
class AttachmentController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()

    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    private lateinit var attachmentService: AttachmentService

    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping("/image/{id}", produces=[MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE])
    fun getImage(@PathVariable id: Int): ResponseEntity<InputStreamResource> {
        val attachment = attachmentService.getAttachment(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        if (attachment.userId != userId) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        if (attachment.dataType !in listOf(MediaType.IMAGE_GIF, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)){
            return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }
        return ResponseEntity.ok(InputStreamResource(attachment.inputStream))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): ResponseEntity<InputStreamResource> {
        val attachment = attachmentService.getAttachment(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        if (attachment.userId != userId) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.name}\"")
            .contentType(attachment.dataType).body(InputStreamResource(attachment.inputStream))
    }

    @PostMapping("/ajax")
    @ResponseBody fun post(@RequestParam("attachmentFile") file: MultipartFile?, @RequestParam("attachmentName") attachmentName: String?): FormResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        val locale: Locale = LocaleContextHolder.getLocale()
        if (file == null || file.isEmpty) {
            return FormResponse("attachmentFile", false, mapOf(Pair("attachmentFile", messageSource.getMessage("fileMissing.error.message", null, locale))))
        }
        if (attachmentName.isNullOrBlank()) {
            return FormResponse("attachmentName", false, mapOf(Pair("attachmentName", messageSource.getMessage("nameMissing.error.message", null, locale))))
        }
        attachmentService.addAttachment(attachmentName, userId, file)
        return FormResponse("attachment", true, null)
    }


}