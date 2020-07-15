package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.Room
import com.irotsoma.homeinventorymanager.data.RoomRepository
import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.RoomForm
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
@RequestMapping("/roomedit")
@Secured("ROLE_USER")
class RoomEditController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var roomRepository: RoomRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun new(model: Model) : String{
        addStaticAttributes(model)
        return "roomedit"
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int, model: Model) : String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val room = roomRepository.findById(id)
        if (userId == null || room.isEmpty || room.get().userId != userId){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        addStaticAttributes(model)
        model.addAttribute("room", room.get())
        return "roomedit"
    }

    @PostMapping
    fun post(@Valid roomForm: RoomForm, bindingResult: BindingResult, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        if (userId == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val newRoom = Room(
            userId,
            roomForm.roomName.trim(),
            DataState.ACTIVE
        )
        try {
            roomRepository.saveAndFlush(newRoom)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_room_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("room", newRoom)
                model.addAttribute("nameError", messageSource.getMessage("name.uniquenessError.message", null, locale))
                return "roomedit"
            }
        }

        return "redirect:/room"
    }

    @PostMapping("/{id}")
    fun put(@Valid roomForm: RoomForm, bindingResult: BindingResult, model: Model, @PathVariable id: Int): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val room = roomRepository.findById(id)
        if (userId == null || (room.isEmpty) || (room.isPresent && room.get().userId != userId)){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val updatedRoom = room.get().apply {
                this.name = roomForm.roomName.trim()
            }
        try {
            roomRepository.saveAndFlush(updatedRoom)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_room_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("room", updatedRoom)
                model.addAttribute("nameError", messageSource.getMessage("name.uniquenessError.message", null, locale))
                return "roomedit"
            }
        }

        return "redirect:/room"
    }

    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("editRoom.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        //model.addAttribute("disableJumbotron", "disable")
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
    }
}