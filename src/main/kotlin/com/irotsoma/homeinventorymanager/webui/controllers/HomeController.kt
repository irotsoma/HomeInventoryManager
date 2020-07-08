package com.irotsoma.homeinventorymanager.webui.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*


/**
 *
 *
 * @author Justin Zak
 */
@Controller
@Lazy
internal class HomeController {
    val locale: Locale = LocaleContextHolder.getLocale()
    @Autowired
    private lateinit var messageSource: MessageSource
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("pageTitle", messageSource.getMessage("home.label", null, locale))
        if (!SecurityContextHolder.getContext().authentication.authorities.any { r -> r.authority == "ROLE_ANONYMOUS" }) {
            model.addAttribute("isLoggedIn", true)
        }
        return "index"
    }
}