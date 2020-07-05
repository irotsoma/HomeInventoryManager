/*
 * Created by irotsoma on 7/4/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.PropertyRepository
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
@RequestMapping("/property")
@Secured("ROLE_USER")
class PropertyController {
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
    fun get(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "property"
        val properties = propertyRepository.findByUserId(userId)
        model.addAttribute("property", properties)
        return "property"
    }

    @RequestMapping("/{id}", method = [RequestMethod.POST])
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String): String{
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(id)
        if (userId == null || property.isEmpty || property.get().userId != id){
            return "error"
        }
        propertyRepository.delete(property.get())
        return "redirect:/property"
    }

    fun addStaticAttributes(model:Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("propertyList.label", null, locale))
        model.addAttribute("propertyNameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("propertyAddressLabel", messageSource.getMessage("address.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteButtonLabel", messageSource.getMessage("delete.label",null, locale))
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
    }
}