package com.irotsoma.homeinventorymanager.webui.controllers

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.servlet.http.HttpSession

@Controller
@Lazy
@RequestMapping("/userinfo")
@Secured("ROLE_USER")
class UserInfoController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    private val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource

    @GetMapping
    fun get(model: Model,session: HttpSession): String {
        val authentication = SecurityContextHolder.getContext().authentication
        model.addAttribute("pageTitle", messageSource.getMessage("userDetails.label", null, locale))
        model.addAttribute("usernameLabel", messageSource.getMessage("username.label",null,locale))
        model.addAttribute("userRolesLabel", messageSource.getMessage("roles.label",null,locale))
        model.addAttribute("detailsLabel", messageSource.getMessage("details.label",null, locale))
        model.addAttribute("username",authentication.name ?:"")
        model.addAttribute("userRoles",authentication.authorities?.map { it } ?: emptyList<String>())

        return "userinfo"
    }
}