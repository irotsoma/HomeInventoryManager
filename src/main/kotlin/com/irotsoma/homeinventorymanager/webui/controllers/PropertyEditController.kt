/*
 * Created by irotsoma on 7/5/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.authentication.DataState
import com.irotsoma.homeinventorymanager.data.Property
import com.irotsoma.homeinventorymanager.data.PropertyRepository
import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.PropertyForm
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.validation.Valid

@Controller
@Lazy
@RequestMapping("/propertyedit")
@Secured("ROLE_USER")
class PropertyEditController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    private val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var propertyRepository: PropertyRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun new(model: Model) : String{
        addStaticAttributes(model)
        return "propertyedit"
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int, model: Model) : String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(id)
        if (userId == null || property.isEmpty || property.get().userId != userId){
            val errorMessage = messageSource.getMessage("property.access.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        addStaticAttributes(model)
        model.addAttribute("property", property.get())


        return "propertyedit"
    }

    @PostMapping
    fun put(@Valid propertyForm: PropertyForm, bindingResult: BindingResult, model: Model): String{
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(propertyForm.id)
        if (userId == null || (property.isEmpty && propertyForm.id != -1) || (property.isPresent && property.get().userId != userId)){
            val errorMessage = messageSource.getMessage("property.access.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val updatedProperty =
            if (propertyForm.id == -1) {
                Property(
                    userId,
                    propertyForm.name.trim(),
                    if (propertyForm.street.isNullOrBlank()) null else propertyForm.street!!.trim(),
                    if (propertyForm.city.isNullOrBlank()) null else propertyForm.city!!.trim(),
                    if (propertyForm.state.isNullOrBlank()) null else propertyForm.state!!.trim(),
                    if (propertyForm.postalCode.isNullOrBlank()) null else propertyForm.postalCode!!.trim(),
                    if (propertyForm.country.isNullOrBlank()) null else propertyForm.country!!.trim(),
                    DataState.ACTIVE
                )
            } else {
                property.get().apply {
                    this.name = propertyForm.name
                    this.addressStreet = if (propertyForm.street.isNullOrBlank()) null else propertyForm.street!!.trim()
                    this.addressCity = if (propertyForm.city.isNullOrBlank()) null else propertyForm.city!!.trim()
                    this.addressState = if (propertyForm.state.isNullOrBlank()) null else propertyForm.state!!.trim()
                    this.addressPostalCode = if (propertyForm.postalCode.isNullOrBlank()) null else propertyForm.postalCode!!.trim()
                    this.addressCountry = if (propertyForm.country.isNullOrBlank()) null else propertyForm.country!!.trim()
                }
            }
        propertyRepository.saveAndFlush(updatedProperty)

        return "redirect:/property"
    }


    fun addStaticAttributes(model: Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("editProperty.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("addressLabel", messageSource.getMessage("address.label", null, locale))
        model.addAttribute("streetLabel", messageSource.getMessage("address.street.label", null, locale))
        model.addAttribute("cityLabel", messageSource.getMessage("address.city.label", null, locale))
        model.addAttribute("stateLabel", messageSource.getMessage("address.state.label", null, locale))
        model.addAttribute("postalCodeLabel", messageSource.getMessage("address.postalCode.label", null, locale))
        model.addAttribute("countryLabel", messageSource.getMessage("address.country.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))

    }
}