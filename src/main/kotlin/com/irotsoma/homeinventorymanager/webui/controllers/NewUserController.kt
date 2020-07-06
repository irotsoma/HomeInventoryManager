package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.authentication.DataState
import com.irotsoma.homeinventorymanager.data.User
import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.data.UserRoles
import com.irotsoma.homeinventorymanager.webui.models.NewUserForm
import com.irotsoma.homeinventorymanager.webui.models.Option
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import java.util.regex.Pattern
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
@Lazy
@RequestMapping("/newuser")
@Secured("ROLE_ADMIN")
class NewUserController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    private val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun get(model: Model, session: HttpSession): String {
        addStaticAttributes(model)
        val roles = ArrayList<Option>()
        UserRoles.values().forEach{ roles.add(Option(it.value, false)) }
        model.addAttribute("roles", roles)
        return "newuser"
    }
    @PostMapping
    fun createUser(@Valid newUserForm: NewUserForm, bindingResult: BindingResult, model: Model, session: HttpSession): String {
        if (bindingResult.hasErrors()) {
            val processedMessages = ArrayList<String>()
            for (error in bindingResult.fieldErrors){
                if (error.field == "password"){
                    if (error.defaultMessage?.contains(Pattern.compile("([A-Z]|_)*:\\{").toRegex()) == true) {
                        for (message in error.defaultMessage!!.split("\u001E")) {
                            val parsedMessage = message.split(":")
                            processedMessages.add("<div data-toggle=\"tooltip\" data-placement=\"top\" title=\"${parsedMessage[1].replace("\"", "&quot;", false)}\">${parsedMessage[0]}</div>")
                            val messageString = processedMessages.joinToString(separator = "")
                            model.addAttribute("passwordError", messageString)
                        }
                    }
                    else {
                        model.addAttribute("${error.field}Error", error.defaultMessage)
                    }
                }
                else {
                    model.addAttribute("${error.field}Error", error.defaultMessage)
                }
            }
            //Send back previous values for fields
            if (newUserForm.username != null) {
                model.addAttribute("username1", newUserForm.username)
            }
            addStaticAttributes(model)
            val roles = ArrayList<Option>()
            UserRoles.values().forEach{ if (newUserForm.roles.contains(Option(it.value,true))) {roles.add(Option(it.value, true))} else {roles.add(Option(it.value,false)) }}
            model.addAttribute("roles", roles)
            return "newuser"
        }
        val roles = ArrayList<UserRoles>()
        UserRoles.values().forEach{if (newUserForm.roles.contains(Option(it.value,true))) {roles.add(it)} }

        val newUser = User(newUserForm.username!!.trim(), newUserForm.password!!.trim(), DataState.ACTIVE, roles)
        userRepository.saveAndFlush(newUser)

        model.addAttribute("pageTitle", messageSource.getMessage("newUserController.redirect.title", null, locale))
        model.addAttribute("message", messageSource.getMessage("newUserController.redirect.message", null, locale))
        model.addAttribute("location", "/")
        return "redirect"
    }
    fun addStaticAttributes(model:Model) {
        model.addAttribute("pageTitle", messageSource.getMessage("newUser.label", null, locale))
        model.addAttribute("usernameLabel", messageSource.getMessage("username.label",null,locale))
        model.addAttribute("passwordLabel", messageSource.getMessage("password.label",null,locale))
        model.addAttribute("passwordConfirmLabel", messageSource.getMessage("passwordConfirm.label",null,locale))
        model.addAttribute("userRolesLabel", messageSource.getMessage("roles.label",null,locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label",null,locale))
    }
}