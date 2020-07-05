/*
 * Created by irotsoma on 7/4/2020.
 */
package com.irotsoma.homeinventorymanager.data
/**
 * List of user roles for the system
 * @property value Used to initialize the enum using a string representation of the value.
 *
 * @author Justin Zak
 */
enum class UserRoles(val value: String) {
    /**
     * Standard user
     */
    ROLE_USER("ROLE_USER"),
    /**
     * Administrative user
     */
    ROLE_ADMIN("ROLE_ADMIN");
}