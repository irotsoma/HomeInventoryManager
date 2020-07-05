/*
 * Created by irotsoma on 6/30/2020.
 */
package com.irotsoma.homeinventorymanager.authentication


/**
 * Allows for a defined set of states for database entries
 *
 * @property value The string value of the enum instance
 */
enum class DataState(val value: String){
    /**
     * Entry is active and can be used
     */
    ACTIVE("ACTIVE"),
    /**
     * Entry is temporarily disabled and can not be used
     */
    DISABLED("DISABLED"),
    /**
     * Entry has been permanently deleted and can not be used
     */
    DELETED("DELETED");
}