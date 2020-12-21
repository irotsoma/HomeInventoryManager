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

package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.*
import org.springframework.http.MediaType
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

/**
 * JPA Attachment Object
 * Soft deletes
 *
 * @author Justin Zak
 * @property id Database-generated ID for the record.
 * @property documentId Unique ID for the file in the mongoDB collection.
 * @property name Name of the attachment.
 * @property originalFileExtension The original file extension to be added back when downloading an attachment.
 * @property dataType The data Type of the file.
 * @property userId The user ID to whom this record belongs.
 * @property state Indicates if a user is enabled in the system.
 * @property created Indicates the date the entry was created. (read-only)
 * @property updated Indicates the date the entry was last updated. (read-only)
 * @property isUnsupportedType Transient calculated variable to determine if the application should enable previewing. (true or null)
 */
@Entity
@Table(name = "attachment")
@SQLDelete(sql = "UPDATE attachment SET state = 'DELETED', name = (SELECT CONCAT(name, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class Attachment (@Column(name = "document_id", nullable = false) val documentId: String,
                  @Column(name = "name", nullable = false) val name: String,
                  @Column(name = "original_file_extension", nullable = false) val originalFileExtension: String,
                  @Column(name = "data_type", nullable = false) val dataType: String,
                  @Column(name = "user_id", nullable = false) val userId: Int,
                  @Column(name = "state", nullable = false) @Enumerated(EnumType.STRING) var state: DataState
) {
    /** kotlin-logging implementation */
    companion object : KLogging()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id: Int = -1

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    var created: Date? = null
        private set

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    var updated: Date? = null
        private set

    @Transient
    var isUnsupportedType: Boolean? = null

    /**
     * Determines if the current record is of a predefined list of media types. If so sets to true, if not sets to null for use as a mustache flag.
     */
    @PostLoad
    fun calculateIsActivelyUsed(){
        isUnsupportedType = if (dataType in listOf(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE)) null else true
    }

}