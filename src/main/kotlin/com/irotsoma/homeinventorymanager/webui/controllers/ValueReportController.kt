/*
 * Created by irotsoma on 7/18/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.InventoryItemRepository
import com.irotsoma.homeinventorymanager.data.UserRepository
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
            if (it.purchasePrice != null && it.purchasePrice != BigDecimal.valueOf(0.00)){
                estimatedReplacementValue += it.purchasePrice!!
            } else if (it.estimatedValue != null) {
                estimatedReplacementValue += it.estimatedValue!!
            }
        }
        val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)
        model.addAttribute("estimatedValueAmount",  currencyFormatter.format(estimatedValue))
        model.addAttribute("estimatedReplacementValueAmount", currencyFormatter.format(estimatedReplacementValue))
        return "valuereport"
    }



    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("value.report.label", null, locale))
        model.addAttribute("estimatedValueLabel", messageSource.getMessage("estimatedValue.label", null, locale))
        model.addAttribute("estimatedReplacementValueLabel", messageSource.getMessage("estimatedReplacementValue.label", null, locale))
        model.addAttribute("disclaimerText", messageSource.getMessage("valueReport.disclaimer.message", null, locale))

    }
}