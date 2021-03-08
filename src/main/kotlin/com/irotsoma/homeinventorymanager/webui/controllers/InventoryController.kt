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
import org.springframework.web.bind.annotation.*
import java.text.DecimalFormat
import java.util.*

/**
 * Rest Controller for accessing the inventory
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property inventoryItemRepository Autowired instance of the [InventoryItem] JPA repository
 */
@Controller
@Lazy
@RequestMapping("/inventory")
@Secured("ROLE_USER")
class InventoryController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository
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
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "inventory"
        val inventoryItem = inventoryItemRepository.findByUserId(userId)
        model.addAttribute("inventoryItem", inventoryItem)
        return "inventory"
    }
    /**
     * Called when deleting a record
     *
     * @param id The ID of the category to delete.
     * @param action A parameter to explicitly verify that deleting is requested.
     * @param model The Model holding attributes for the mustache templates.
     * @return A redirect to reload the list page or an error page
     */
    @PostMapping("/{id}")
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val inventoryItem = inventoryItemRepository.findById(id)
        if (userId == null || inventoryItem.isEmpty || inventoryItem.get().user?.id != userId || action != "DELETE"){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        inventoryItemRepository.delete(inventoryItem.get())
        return "redirect:/inventory"
    }
    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        val decimalFormat= DecimalFormat.getInstance(locale) as DecimalFormat
        model.addAttribute("pageTitle", messageSource.getMessage("inventory.label", null, locale))
        model.addAttribute("pageSubTitle", messageSource.getMessage("inventory.subTitle", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("editLabel", messageSource.getMessage("edit.button.label",null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("tableTitle", messageSource.getMessage("inventory.label", null, locale))
        model.addAttribute("roomLabel", messageSource.getMessage("room.label", null, locale))
        model.addAttribute("propertyLabel", messageSource.getMessage("property.label", null, locale))
        model.addAttribute("categoryLabel", messageSource.getMessage("category.label", null, locale))
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
        model.addAttribute("searchLabel", messageSource.getMessage("search.label", null, locale))
        model.addAttribute("currencySymbol", decimalFormat.decimalFormatSymbols.currencySymbol)
        model.addAttribute("decimalSeparator", decimalFormat.decimalFormatSymbols.decimalSeparator)
        model.addAttribute("thousandsSeparator", decimalFormat.decimalFormatSymbols.groupingSeparator)
        model.addAttribute("previousLabel", messageSource.getMessage("previous.label", null, locale))
        model.addAttribute("nextLabel", messageSource.getMessage("next.label", null, locale))
        model.addAttribute("paginationInfoMessage", messageSource.getMessage("paginationInfo.message", null, locale))
        model.addAttribute("paginationInfoFilteredMessage", messageSource.getMessage("paginationInfoFiltered.message", null, locale))
        model.addAttribute("lengthMenuMessage", messageSource.getMessage("lengthMenu.message", null, locale))
        model.addAttribute("loadingLabel", messageSource.getMessage("loading.button.label", null, locale))
        model.addAttribute("emptyTableMessage", messageSource.getMessage("emptyTable.message", null, locale))
        model.addAttribute("zeroRecordsMessage", messageSource.getMessage("zeroRecords.message", null, locale))

    }
}