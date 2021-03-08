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


import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.rdbms.Category
import com.irotsoma.homeinventorymanager.data.rdbms.CategoryRepository
import com.irotsoma.homeinventorymanager.data.rdbms.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.CategoryForm
import com.irotsoma.homeinventorymanager.webui.models.FormResponse
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

/**
 * Rest Controller for adding or editing categories
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property categoryRepository Autowired instance of the [Category] JPA repository
 */
@Controller
@Lazy
@RequestMapping("/categoryedit")
@Secured("ROLE_USER")
class CategoryEditController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()

    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * Gets an empty copy of the edit screen for adding a new record
     *
     * @param model The Model holding attributes for the mustache templates.
     * @returns The template name to load
     */
    @GetMapping
    fun new(model: Model): String {
        addStaticAttributes(model)
        return "categoryedit"
    }

    /**
     * Gets a copy of the edit screen with a record already populated for editing
     *
     * @param id The ID of the record to edit
     * @param model The Model holding attributes for the mustache templates.
     * @returns The template name to load or the name of the error template
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Int, model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val category = categoryRepository.findById(id)
        if (userId == null || category.isEmpty|| category.get().userId != userId) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            model.addAttribute("error", errorMessage)
            return "error"
        }
        addStaticAttributes(model)
        model.addAttribute("category", category.get())
        return "categoryedit"
    }

    /**
     * Saves a new record
     *
     * @param categoryForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @param model The Model holding attributes for the mustache templates.
     * @return A redirect back to the list page, an updated instance of the current record with validation errors, or the name of the error template
     */
    @PostMapping
    fun post(@Valid categoryForm: CategoryForm, bindingResult: BindingResult, model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            model.addAttribute("error", errorMessage)
            return "error"
        }

        val newCategory = Category(
                    userId,
                    categoryForm.categoryName.trim(),
                    DataState.ACTIVE
                )
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("category", newCategory)
            return "categoryedit"
        }
        try {
            categoryRepository.saveAndFlush(newCategory)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("category", newCategory)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                return "categoryedit"
            } else {
                throw e
            }
        }
        return "redirect:/category"
    }
    /**
     * Creates a new record from an ajax popup
     *
     * @param categoryForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @return A FormResponse that contains a boolean parameter "validated" which is true if the add was successful or false if errors, and a map of field name to message for any errors.
     */
    @PostMapping("/ajax")
    @ResponseBody fun postModal(@ModelAttribute @Valid categoryForm: CategoryForm, bindingResult: BindingResult) : FormResponse {
        val locale: Locale = LocaleContextHolder.getLocale()
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            return FormResponse(categoryForm.categoryName, false, errors)
        }
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            return FormResponse(categoryForm.categoryName, false, errors)
        }
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            return FormResponse(categoryForm.categoryName, false, mapOf(Pair("categoryName", errorMessage)))
        }
        val newCategory = Category(
            userId,
            categoryForm.categoryName.trim(),
            DataState.ACTIVE
        )
        val savedRecord =
            try {
                categoryRepository.saveAndFlush(newCategory)
            } catch (e: DataIntegrityViolationException){
                if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                    val errorMessage = messageSource.getMessage("nameUniqueness.error.message", null, locale)
                    return FormResponse(categoryForm.categoryName, false, mapOf(Pair("categoryName",errorMessage)))
                } else {
                    throw e
                }
            }
        return FormResponse(savedRecord.id.toString(), true, null)
    }
    /**
     * Saves an existing record being edited
     *
     * @param categoryForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @param model The Model holding attributes for the mustache templates.
     * @param id The id of the record to update.
     * @return A redirect back to the list page, an updated instance of the current record with validation errors, or the name of the error template
     */
    @PostMapping("/{id}")
    fun put(@Valid categoryForm: CategoryForm, bindingResult: BindingResult, model: Model, @PathVariable id: Int): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val category = categoryRepository.findById(id)
        if (userId == null || (category.isEmpty) || (category.isPresent && category.get().userId != userId)) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val updatedCategory = category.get().apply { this.name = categoryForm.categoryName.trim() }
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("category", updatedCategory)
            return "categoryedit"
        }
        try {
            categoryRepository.saveAndFlush(updatedCategory)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("category", updatedCategory)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                return "categoryedit"
            } else {
                throw e
            }
        }

        return "redirect:/category"
    }

    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("editCategory.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
    }
}
