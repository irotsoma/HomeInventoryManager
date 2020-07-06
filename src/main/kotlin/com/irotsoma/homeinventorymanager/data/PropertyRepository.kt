/*
 * Created by irotsoma on 7/3/2020.
 */
package com.irotsoma.homeinventorymanager.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PropertyRepository: JpaRepository<Property, Int> {
    fun findByUserId(id: Int): List<Property>?
}