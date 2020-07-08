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
@RequestMapping("/categoryedit")
@Secured("ROLE_USER")
class CategoryEditController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()

    private val locale: Locale = LocaleContextHolder.getLocale()

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
    fun put(@Valid categoryForm: CategoryForm, bindingResult: BindingResult, model: Model): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val category = categoryRepository.findById(categoryForm.id)
        if (userId == null || (category.isEmpty && categoryForm.id != -1) || (category.isPresent && category.get().userId != userId)) {
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val updatedProperty =
            if (categoryForm.id == -1) {
                Category(
                    userId,
                    categoryForm.name.trim(),
                    DataState.ACTIVE
                )
            } else {
                category.get().apply {
                    this.name = categoryForm.name
                }
            }
        categoryRepository.saveAndFlush(updatedProperty)

        return "redirect:/category"
    }


    fun addStaticAttributes(model: Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("editCategory.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
    }
}
