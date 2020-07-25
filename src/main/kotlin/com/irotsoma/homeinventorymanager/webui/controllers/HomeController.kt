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
 * Rest controller for the home page
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 */
@Controller
@Lazy
internal class HomeController {
    @Autowired
    private lateinit var messageSource: MessageSource

    /**
     * Gets the home page
     *
     * @param model The Model holding attributes for the mustache templates.
     * @return The name of the template to load
     */
    @GetMapping("/")
    fun home(model: Model): String {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("home.label", null, locale))
        if (!SecurityContextHolder.getContext().authentication.authorities.any { r -> r.authority == "ROLE_ANONYMOUS" }) {
            model.addAttribute("isLoggedIn", true)
        }
        return "index"
    }
}