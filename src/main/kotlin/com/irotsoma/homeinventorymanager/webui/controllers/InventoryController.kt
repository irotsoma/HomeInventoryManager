/*
 * Created by irotsoma on 7/7/2020.
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
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpSession


@Controller
@Lazy
@RequestMapping("/inventory")
@Secured("ROLE_USER")
class InventoryController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    private val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun getList(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "inventory"
        val inventoryItem = inventoryItemRepository.findByUserId(userId)
        model.addAttribute("inventoryItem", inventoryItem)
        return "inventory"
    }

    @PostMapping("/{id}")
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String, model: Model): String{
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

    fun addStaticAttributes(model: Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("inventory.label", null, locale))
        model.addAttribute("pageSubTitle", messageSource.getMessage("inventory.subTitle", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("editLabel", messageSource.getMessage("edit.button.label",null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("tableTitle", messageSource.getMessage("inventory.label", null, locale))
        model.addAttribute("roomLabel", messageSource.getMessage("room.label", null, locale))
        model.addAttribute("categoryLabel", messageSource.getMessage("category.label", null, locale))
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
    }
}