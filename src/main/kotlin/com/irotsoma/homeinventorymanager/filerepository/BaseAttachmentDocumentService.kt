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

import org.springframework.web.multipart.MultipartFile

/**
 * Interface for creating services for attachment repositories like Mongo
 *
 * @author Justin Zak
 */
interface BaseAttachmentDocumentService {
    /**
     * Add an attachment to the repository
     *
     * @param name The name of the attachment
     * @param fileInfo A MultipartFile from the webservice that contains the attachment and metadata
     * @returns The unique id of the attachment in the repository
     */
    fun addAttachment(name: String, fileInfo: MultipartFile): String
    /**
     * Get an attachment from a document repository
     *
     * @param id The unique ID of the attachment in the repository
     * @returns The attachment and related metadata as an [AttachmentDocument] or null if the id does not exist
     */
    fun getAttachment(id: String): AttachmentDocument?

    /**
     * Deletes an attachment from the document repository
     *
     * @param id The unique ID of the attachment in the repository
     */
    fun deleteAttachment(id: String)
}