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

package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.rdbms.UserRepository
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

/**
 * Rest Controller for accessing attachments
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property attachmentService Autowired instance of AttachmentService for manipulating attachments.
 */
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

    /**
     * Called to get an image version of an attachment
     *
     * @param id The ID of the image to return.
     * @return An InputStreamResource for accessing the image or a status code of Unsupported Media Type if the attachment is not an image.
     */
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
    /**
     * Called to download an attachment
     *
     * @param id The ID of the attachment to return.
     * @return An InputStreamResource for downloading the file.
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): ResponseEntity<InputStreamResource> {
        val attachment = attachmentService.getAttachment(id) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        if (attachment.userId != userId) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val extension = if (attachment.originalFileExtension.isNotBlank()){
                                    ".${attachment.originalFileExtension}"
                                } else {
                                    ""
                                }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.name}${extension}\"")
            .contentType(attachment.dataType).body(InputStreamResource(attachment.inputStream))
    }

    /**
     * Used for adding a new attachment via an ajax call from a popup
     *
     * @param attachmentFile The MultipartFile sent as "attachmentFile" parameter
     * @param attachmentName The Name of the attachment sent as "attachmentName" parameter
     * @return A FormResponse that contains a boolean parameter "validated" which is true if the add was successful or false if errors, and a map of field name to message for any errors.
     */
    @PostMapping("/ajax")
    @ResponseBody fun post(@RequestPart("attachmentFile") attachmentFile: MultipartFile?, @RequestParam("attachmentName") attachmentName: String?): FormResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        val locale: Locale = LocaleContextHolder.getLocale()
        if (attachmentFile == null || attachmentFile.isEmpty) {
            return FormResponse("attachmentFile", false, mapOf(Pair("attachmentFile", messageSource.getMessage("fileMissing.error.message", null, locale))))
        }
        if (attachmentName.isNullOrBlank()) {
            return FormResponse("attachmentName", false, mapOf(Pair("attachmentName", messageSource.getMessage("nameMissing.error.message", null, locale))))
        }
        attachmentService.addAttachment(attachmentName, userId, attachmentFile)
        return FormResponse("attachment", true, null)
    }


}