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

import com.irotsoma.homeinventorymanager.data.rdbms.Category
import com.irotsoma.homeinventorymanager.data.rdbms.CategoryRepository
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
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Rest Controller for accessing categories
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property categoryRepository Autowired instance of the [Category] JPA repository
 */
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

    /**
     * Called when loading the list page
     *
     * @param model The Model holding attributes for the mustache templates.
     * @return The name of the mustache template to load.
     */
    @GetMapping
    fun getList(model: Model): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "category"
        val categories = categoryRepository.findByUserId(userId)
        model.addAttribute("category", categories)
        return "category"
    }

    /**
     * Called when deleting a category
     *
     * @param id The ID of the category to delete.
     * @param action A parameter to explicitly verify that deleting is requested.
     * @return A redirect to reload the category page or an error page
     */
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

    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
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
        model.addAttribute("searchLabel", messageSource.getMessage("search.label", null, locale))

    }
}