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

import com.fasterxml.jackson.annotation.JsonIgnore
import mu.KLogging
import org.hibernate.annotations.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

/**
 * JPA User Account Object
 * Soft deletes
 *
 * @author Justin Zak
 * @property id Database-generated ID for the user.
 * @property username Username of the user.
 * @property password User password encoded using BCrypt.
 * @property state Indicates if a user is enabled in the system.
 * @property created Indicates the date the entry was created. (read-only)
 * @property updated Indicates the date the entry was last updated. (read-only)
 */
@Entity
@Table(name = "user")
@SQLDelete(sql = "UPDATE username SET state = 'DELETED', username = (SELECT CONCAT(username, '--DELETED--', CURRENT_TIMESTAMP)) WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'ACTIVE'")
class User(@Column(name = "username", nullable = false, updatable = false, unique = true) val username: String,
           password: String,
           @Column(name = "state", nullable = false) @Enumerated(EnumType.STRING) var state: DataState,
           roles: List<UserRoles> ) {
    /** kotlin-logging implementation */
    companion object : KLogging() {
        /** the type of password encoder used to hash passwords before storing them */
        val PASSWORD_ENCODER: PasswordEncoder = BCryptPasswordEncoder()
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    var id: Int = -1

    @JsonIgnore
    @Column(name="password", nullable=false)
    var password: String? = PASSWORD_ENCODER.encode(password)
        set(value) {
            field = PASSWORD_ENCODER.encode(value)
        }

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
    @Column(name="role")
    private var roleList: List<String>? = roles.map{it.name}
    var roles: List<UserRoles>?
        set(value){
            roleList = value?.map{it.name}
        }
        get(){
            return roleList?.mapNotNull {
                try {
                    UserRoles.valueOf(it)
                } catch (e:IllegalArgumentException){
                    logger.warn{"The value $it is not a valid user role for user $username.  Ignoring value."}
                    null
                }
            }
        }

    override fun toString(): String {
        return username;
    }
}