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

package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.rdbms.InventoryItem
import com.irotsoma.homeinventorymanager.data.rdbms.InventoryItemRepository
import com.irotsoma.homeinventorymanager.data.rdbms.UserRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

/**
 * Rest Controller for accessing the estimated value report
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property userRepository Autowired instance of the [InventoryItem] JPA repository.
 */
@Controller
@Lazy
@RequestMapping("/valuereport")
@Secured("ROLE_USER")
class ValueReportController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository
    /**
     * Called when loading the report
     *
     * @param model The Model holding attributes for the mustache templates.
     * @return The name of the mustache template to load.
     */
    @GetMapping
    fun get(model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null ){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val inventoryItems = inventoryItemRepository.findByUserId(userId)

        var estimatedValue = BigDecimal.valueOf(0.00)
        var estimatedReplacementValue = BigDecimal.valueOf(0.00)
        inventoryItems?.forEach {
            //for estimated value it adds up all estimated values and if estimated value is blank or 0 it uses purchase price instead
            if (it.estimatedValue != null && it.estimatedValue != BigDecimal.valueOf(0.00)){
                estimatedValue += it.estimatedValue!!
            } else if (it.purchasePrice != null) {
                estimatedValue += it.purchasePrice!!
            }
            //for estimated replacement value it adds up all purchase prices and if blank or 0 it uses estimated value
            var greaterValue = BigDecimal.valueOf(0.00)
            if (it.purchasePrice != null && it.purchasePrice != BigDecimal.valueOf(0.00)){
                greaterValue = it.purchasePrice!!
            }
            if (it.estimatedValue != null && it.estimatedValue!! > (it.purchasePrice ?: BigDecimal.valueOf(0.00))) {
                greaterValue = it.estimatedValue!!
            }
            estimatedReplacementValue += greaterValue
        }
        val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        model.addAttribute("estimatedValueAmount",  currencyFormatter.format(estimatedValue))
        model.addAttribute("estimatedReplacementValueAmount", currencyFormatter.format(estimatedReplacementValue))
        return "valuereport"
    }
    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("value.report.label", null, locale))
        model.addAttribute("estimatedValueLabel", messageSource.getMessage("estimatedValue.label", null, locale))
        model.addAttribute("estimatedReplacementValueLabel", messageSource.getMessage("estimatedReplacementValue.label", null, locale))
        model.addAttribute("disclaimerText", messageSource.getMessage("valueReport.disclaimer.message", null, locale))

    }
}