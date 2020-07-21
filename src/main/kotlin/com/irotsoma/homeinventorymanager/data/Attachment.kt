/*
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.data

import mu.KLogging
import org.hibernate.annotations.*
import org.springframework.http.MediaType
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "attachment")
@SQLDelete(sql = "UPDATE attachment SET state = 'DELETED', name = (SELECT CONCAT(name, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class Attachment (@Column(name = "mongo_id", nullable = false) val mongoId: String,
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

    @PostLoad
    fun calculateIsActivelyUsed(){
        isUnsupportedType = if (dataType in listOf(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE)) null else true
    }

}