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
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var propertyRepository: PropertyRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun getList(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "property"
        val properties = propertyRepository.findByUserId(userId)
        model.addAttribute("property", properties)
        return "property"
    }

    @PostMapping("/{id}")
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val property = propertyRepository.findById(id)
        if (userId == null || property.isEmpty || property.get().userId != userId || action != "DELETE"){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        propertyRepository.delete(property.get())
        return "redirect:/property"
    }

    fun addStaticAttributes(model:Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("propertyList.label", null, locale))
        model.addAttribute("pageSubTitle", messageSource.getMessage("propertyList.subTitle", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("addressLabel", messageSource.getMessage("address.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("editLabel", messageSource.getMessage("edit.button.label",null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("tableTitle", messageSource.getMessage("properties.label", null, locale))
        model.addAttribute("activelyUsedMessage", messageSource.getMessage("record.activelyUsed.message", null, locale))
        //model.addAttribute("disableJumbotron", "disable")
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
    }
}