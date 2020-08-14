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
import javax.validation.Valid

/**
 * Rest Controller for accessing properties
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 */
@Controller
@Lazy
@RequestMapping("/userinfo")
@Secured(value = ["ROLE_USER","ROLE_ADMIN"])
class UserInfoController {
    //TODO: add ability for admin to disable users
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * Called when loading the details screen
     *
     * @param model The Model holding attributes for the mustache templates.
     * @return The name of the mustache template to load.
     */
    @GetMapping
    fun get(model: Model): String {
        val authentication = SecurityContextHolder.getContext().authentication
        addStaticAttributes(model)
        model.addAttribute("username",authentication.name ?:"")
        model.addAttribute("userRoles",authentication.authorities?.map { it } ?: emptyList<String>())
        return "userinfo"
    }
    /**
     * Used to change a password using an ajax method
     *
     * @param changePasswordForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @return A FormResponse that contains a boolean parameter "validated" which is true if the add was successful or false if errors, and a map of field name to message for any errors.
     */
    @PostMapping("/ajax")
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
            return FormResponse("password error", false, mapOf(Pair("password", errorMessage)))
        }
        user.password = changePasswordForm.password
        userRepository.saveAndFlush(user)
        return FormResponse("password changed", true, null)
    }
    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
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