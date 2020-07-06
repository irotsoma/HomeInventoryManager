/*
 * Created by irotsoma on 7/3/2020.
 */
package com.irotsoma.homeinventorymanager.data

import com.irotsoma.homeinventorymanager.authentication.DataState
import mu.KLogging
import org.hibernate.annotations.*
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "property")
@SQLDelete(sql = "UPDATE property SET state = 'deleted' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'active'")
class Property(@Column(name = "user_id", nullable = false, updatable = false) val userId: Int,
               @Column(name = "name", nullable = false) var name: String,
               @Column(name = "address_street") var addressStreet: String?,
               @Column(name = "address_city") var addressCity: String?,
               @Column(name = "address_state") var addressState: String?,
               @Column(name = "address_postal_code") var addressPostalCode: String?,
               @Column(name = "address_country") var addressCountry: String?,
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
}