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

import com.irotsoma.homeinventorymanager.data.DataState
import com.irotsoma.homeinventorymanager.data.UserRoles
import com.irotsoma.homeinventorymanager.data.rdbms.User
import com.irotsoma.homeinventorymanager.data.rdbms.UserRepository
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
import javax.validation.Valid

/**
 * Rest Controller for creating a new user
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 */
@Controller
@Lazy
@RequestMapping("/newuser")
@Secured("ROLE_ADMIN")
class NewUserController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var userRepository: UserRepository
    /**
     * Gets an the page for creating a new user.
     *
     * @param model The Model holding attributes for the mustache templates.
     * @returns The template name to load
     */
    @GetMapping
    fun get(model: Model): String {
        addStaticAttributes(model)
        val roles = ArrayList<Option>()
        UserRoles.values().forEach{ roles.add(Option(it.value, false)) }
        model.addAttribute("roles", roles)
        return "newuser"
    }
    /**
     * Saves a new user
     *
     * @param newUserForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @param model The Model holding attributes for the mustache templates.
     * @return A redirect back to the list page, an updated instance of the current record with validation errors, or the name of the error template
     */
    @PostMapping
    fun createUser(@Valid newUserForm: NewUserForm, bindingResult: BindingResult, model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            model.addAllAttributes(errors)
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
    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
    fun addStaticAttributes(model:Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("newUser.label", null, locale))
        model.addAttribute("usernameLabel", messageSource.getMessage("username.label",null,locale))
        model.addAttribute("passwordLabel", messageSource.getMessage("password.label",null,locale))
        model.addAttribute("passwordConfirmLabel", messageSource.getMessage("passwordConfirm.label",null,locale))
        model.addAttribute("userRolesLabel", messageSource.getMessage("roles.label",null,locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label",null,locale))
    }
}