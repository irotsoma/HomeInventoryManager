/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers


import com.irotsoma.homeinventorymanager.data.Category
import com.irotsoma.homeinventorymanager.data.CategoryRepository
import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.UserRepository
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

    @GetMapping
    fun new(model: Model): String {
        addStaticAttributes(model)
        return "categoryedit"
    }

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
        try {
            categoryRepository.saveAndFlush(newCategory)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("category", newCategory)
                model.addAttribute("nameError", messageSource.getMessage("name.uniquenessError.message", null, locale))
                return "categoryedit"
            }
        }
        return "redirect:/category"
    }
    @PostMapping("/modal")
    @ResponseBody fun postModal(@ModelAttribute @Valid categoryForm: CategoryForm, bindingResult: BindingResult) : FormResponse {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            return FormResponse(categoryForm.categoryName, false, errors)
        }

        val locale: Locale = LocaleContextHolder.getLocale()
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
        try {
            categoryRepository.saveAndFlush(newCategory)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                val errorMessage = messageSource.getMessage("name.uniquenessError.message", null, locale)
                return FormResponse(categoryForm.categoryName, false, mapOf(Pair("categoryName",errorMessage)))
            }
        }
        return FormResponse(categoryForm.categoryName, true, null)
    }

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
        try {
            categoryRepository.saveAndFlush(updatedCategory)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_category_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("category", updatedCategory)
                model.addAttribute("nameError", messageSource.getMessage("name.uniquenessError.message", null, locale))
                return "categoryedit"
            }
        }

        return "redirect:/category"
    }


    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("editCategory.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
    }
}
