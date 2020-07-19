package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.Property
import com.irotsoma.homeinventorymanager.data.PropertyRepository
import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.FormResponse
import com.irotsoma.homeinventorymanager.webui.models.PropertyForm
import mu.KLogging
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid

@Controller
@Lazy
@RequestMapping("/propertyedit")
@Secured("ROLE_USER")
class PropertyEditController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
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
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(id)
        if (userId == null || property.isEmpty || property.get().userId != userId){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        addStaticAttributes(model)
        model.addAttribute("property", property.get())
        return "propertyedit"
    }

    @PostMapping
    fun post(@Valid propertyForm: PropertyForm, bindingResult: BindingResult, model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }

        val newProperty = Property(
            userId,
            propertyForm.propertyName.trim(),
            if (propertyForm.street.isNullOrBlank()) null else propertyForm.street!!.trim(),
            if (propertyForm.city.isNullOrBlank()) null else propertyForm.city!!.trim(),
            if (propertyForm.state.isNullOrBlank()) null else propertyForm.state!!.trim(),
            if (propertyForm.postalCode.isNullOrBlank()) null else propertyForm.postalCode!!.trim(),
            if (propertyForm.country.isNullOrBlank()) null else propertyForm.country!!.trim(),
            DataState.ACTIVE
        )
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("property", newProperty)
            return "propertyedit"
        }
        try {
            propertyRepository.saveAndFlush(newProperty)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_property_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("property", newProperty)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                return "propertyedit"
            } else {
                throw e
            }
        }
        return "redirect:/property"
    }

    @PostMapping("/{id}")
    fun put(@Valid propertyForm: PropertyForm, bindingResult: BindingResult, model: Model, @PathVariable id: Int): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(id)
        if (userId == null || property.isEmpty || (property.isPresent && property.get().userId != userId)){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }

        val updatedProperty = property.get().apply {
            this.name = propertyForm.propertyName.trim()
            this.addressStreet = if (propertyForm.street.isNullOrBlank()) null else propertyForm.street!!.trim()
            this.addressCity = if (propertyForm.city.isNullOrBlank()) null else propertyForm.city!!.trim()
            this.addressState = if (propertyForm.state.isNullOrBlank()) null else propertyForm.state!!.trim()
            this.addressPostalCode = if (propertyForm.postalCode.isNullOrBlank()) null else propertyForm.postalCode!!.trim()
            this.addressCountry = if (propertyForm.country.isNullOrBlank()) null else propertyForm.country!!.trim()
        }
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("property", updatedProperty)
            return "propertyedit"
        }
        try {
            propertyRepository.saveAndFlush(updatedProperty)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_property_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("property", updatedProperty)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                return "propertyedit"
            } else {
                throw e
            }
        }

        return "redirect:/property"
    }
    @PostMapping("/ajax")
    @ResponseBody
    fun postModal(@ModelAttribute @Valid propertyForm: PropertyForm, bindingResult: BindingResult) : FormResponse {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            return FormResponse(propertyForm.propertyName, false, errors)
        }
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            return FormResponse(propertyForm.propertyName, false, mapOf(Pair("propertyName", errorMessage)))
        }
        val newProperty = Property(
            userId,
            propertyForm.propertyName.trim(),
            if (propertyForm.street.isNullOrBlank()) null else propertyForm.street!!.trim(),
            if (propertyForm.city.isNullOrBlank()) null else propertyForm.city!!.trim(),
            if (propertyForm.state.isNullOrBlank()) null else propertyForm.state!!.trim(),
            if (propertyForm.postalCode.isNullOrBlank()) null else propertyForm.postalCode!!.trim(),
            if (propertyForm.country.isNullOrBlank()) null else propertyForm.country!!.trim(),
            DataState.ACTIVE
        )
        val savedRecord =
            try {
                propertyRepository.saveAndFlush(newProperty)
            } catch (e: DataIntegrityViolationException){
                if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_property_name_per_user"){
                    val errorMessage = messageSource.getMessage("nameUniqueness.error.message", null, locale)
                    return FormResponse(propertyForm.propertyName, false, mapOf(Pair("propertyName",errorMessage)))
                } else {
                    throw e
                }
            }
        return FormResponse(savedRecord.id.toString(), true, null)
    }
    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
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