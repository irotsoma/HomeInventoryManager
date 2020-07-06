package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.RoomRepository
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
@RequestMapping("/room")
@Secured("ROLE_USER")
class RoomController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    private val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var roomRepository: RoomRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun getList(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "room"
        val properties = roomRepository.findByUserId(userId)
        model.addAttribute("room", properties)
        return "room"
    }

    @PostMapping("/{id}")
    fun delete(@PathVariable id: Int, @RequestParam("action") action: String, model: Model): String{
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val room = roomRepository.findById(id)
        if (userId == null || room.isEmpty || room.get().userId != userId || action != "DELETE"){
            val errorMessage = messageSource.getMessage("data.access.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        roomRepository.delete(room.get())
        return "redirect:/room"
    }

    fun addStaticAttributes(model:Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("roomList.label", null, locale))
        model.addAttribute("pageSubTitle", messageSource.getMessage("roomList.subTitle", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("editLabel", messageSource.getMessage("edit.button.label",null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("tableTitle", messageSource.getMessage("rooms.label", null, locale))
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
    }
}