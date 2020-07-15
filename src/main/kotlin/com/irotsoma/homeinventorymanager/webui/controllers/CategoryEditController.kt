/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers


import com.irotsoma.homeinventorymanager.data.Category
import com.irotsoma.homeinventorymanager.data.CategoryRepository
import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.CategoryForm
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
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
                    categoryForm.name.trim(),
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
        val updatedCategory = category.get().apply { this.name = categoryForm.name.trim() }
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
