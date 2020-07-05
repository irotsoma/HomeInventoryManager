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
class Property(@Column(name = "user_id", nullable = false) var userId: Int,
               @Column(name = "name", nullable = false) val name: String,
               @Column(name = "address_street", nullable = false) val addressStreet: String,
               @Column(name = "address_city", nullable = false) val addressCity: String,
               @Column(name = "address_state", nullable = false) val addressState: String,
               @Column(name = "address_postal_code", nullable = false) val addressPostalCode: String,
               @Column(name = "address_country", nullable = false) val addressCountry: String,
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