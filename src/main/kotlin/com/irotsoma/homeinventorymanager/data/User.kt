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
@SQLDelete(sql = "UPDATE user SET state = 'deleted' WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "state = 'active'")
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