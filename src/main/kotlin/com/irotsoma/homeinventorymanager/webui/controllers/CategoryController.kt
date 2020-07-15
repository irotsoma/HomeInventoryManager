/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.CategoryRepository
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
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpSession


@Controller
@Lazy
@RequestMapping("/category")
@Secured("ROLE_USER")
class CategoryController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()

    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var categoryRepository: CategoryRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun getList(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "category"
        val categories = categoryRepository.findByUserId(userId)
        model.addAttribute("category", categories)
        return "category"
    }

    @PostMapping("/{id}")
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val category = categoryRepository.findById(id)
        if (userId == null || category.isEmpty || category.get().userId != userId || action != "DELETE"){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        categoryRepository.delete(category.get())
        return "redirect:/category"
    }

    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("categoryList.label", null, locale))
        model.addAttribute("pageSubTitle", messageSource.getMessage("categoryList.subTitle", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("editLabel", messageSource.getMessage("edit.button.label",null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("tableTitle", messageSource.getMessage("categories.label", null, locale))
        model.addAttribute("activelyUsedMessage", messageSource.getMessage("record.activelyUsed.message", null, locale))
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
    }
}