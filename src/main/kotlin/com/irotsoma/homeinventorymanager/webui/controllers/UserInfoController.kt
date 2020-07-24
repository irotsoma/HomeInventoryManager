package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.webui.models.ChangePasswordForm
import com.irotsoma.homeinventorymanager.webui.models.FormResponse
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
@Lazy
@RequestMapping("/userinfo")
class UserInfoController {
    //TODO: add ability for admin to disable users
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var userRepository: UserRepository
    @GetMapping
    @Secured("ROLE_USER")
    fun get(model: Model,session: HttpSession): String {
        val authentication = SecurityContextHolder.getContext().authentication
        addStaticAttributes(model)
        model.addAttribute("username",authentication.name ?:"")
        model.addAttribute("userRoles",authentication.authorities?.map { it } ?: emptyList<String>())
        return "userinfo"
    }

    @PostMapping("/ajax")
    @Secured("ROLE_ADMIN")
    @ResponseBody
    fun post(@Valid changePasswordForm: ChangePasswordForm, bindingResult: BindingResult): FormResponse {
        val locale: Locale = LocaleContextHolder.getLocale()
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            val parsedErrors = hashMapOf<String,String>()
            errors.forEach { (key, value) -> parsedErrors[key.removeSuffix("Error")] = value }
            return FormResponse("password error", false, parsedErrors)
        }
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findByUsername(authentication.name)
        if (user == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn { errorMessage }
            return FormResponse("password error", false, mapOf(Pair("propertyName", errorMessage)))
        }
        user.password = changePasswordForm.password
        userRepository.saveAndFlush(user)
        return FormResponse("password changed", true, null)
    }

    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("userDetails.label", null, locale))
        model.addAttribute("usernameLabel", messageSource.getMessage("username.label",null,locale))
        model.addAttribute("passwordLabel", messageSource.getMessage("password.label",null,locale))
        model.addAttribute("changePasswordLabel", messageSource.getMessage("password.change.label",null,locale))
        model.addAttribute("confirmPasswordLabel", messageSource.getMessage("password.confirm.label",null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
        model.addAttribute("cancelLabel", messageSource.getMessage("cancel.button.label", null, locale))
        model.addAttribute("detailsLabel", messageSource.getMessage("details.label", null, locale))
        model.addAttribute("userRolesLabel",messageSource.getMessage("roles.label", null, locale))
        model.addAttribute("passwordChangeSuccessMessage",messageSource.getMessage("password.change.success.message", null, locale))


    }
}