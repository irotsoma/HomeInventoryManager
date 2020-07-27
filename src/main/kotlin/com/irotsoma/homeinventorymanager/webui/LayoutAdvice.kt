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

package com.irotsoma.homeinventorymanager.webui

import com.irotsoma.homeinventorymanager.webui.models.Locale
import com.samskivert.mustache.Mustache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

/**
 * Controller advice object to allow for layout abstraction in mustache templates
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property compiler Autowired instance of a mustache compiler
 * @property mainMenu Autowired instance of the MainMenu object used to construct the main menu for a nav bar
 */
@ControllerAdvice
class LayoutAdvice {
    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    private lateinit var compiler: Mustache.Compiler

    @Autowired
    private lateinit var mainMenu: MainMenu

    /**
     * Layout lambda method
     *
     * @return a mustache lambda that processes the overall layout fragment
     */
    @ModelAttribute("layout")
    fun layout(model: Map<String, Any>): Mustache.Lambda {
        return Layout(compiler)
    }

    /**
     * Title lambda method
     *
     * @return a mustache lambda that processes the title and adds it to the appropriate section of the layout template
     */
    @ModelAttribute("title")
    fun pageTitle(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.title = frag.execute() }
    }
    /**
     * Subtitle lambda method
     *
     * @return a mustache lambda that processes the subtitle and adds it to the appropriate section of the layout template
     */
    @ModelAttribute("subTitle")
    fun pageSubTitle(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.subTitle = frag.execute() }
    }

    /**
     * Menu mustache method
     *
     * @return main menus
     */
    @ModelAttribute("menus")
    fun menus(): Iterable<Menu> {
        return mainMenu.menus
    }

    /**
     * Scripts lambda method
     *
     * @return a mustache lambda that processes the scripts fragment and adds it to the appropriate section of the layout template
     */
    @ModelAttribute("scripts")
    fun scripts(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.scripts = frag.execute() }
    }

    /**
     * application title mustache method
     *
     * @return the application title
     */
    @ModelAttribute("applicationTitle")
    fun applicationTitle(@ModelAttribute layout: Layout): String {
        return layout.applicationTitle
    }

    /**
     * Stylesheets lambda method
     *
     * @return a mustache lambda that processes the stylesheets fragment and adds it to the appropriate section of the layout template
     */
    @ModelAttribute("stylesheets")
    fun stylesheets(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.stylesheets = frag.execute() }
    }

    /**
     * locale mustache method
     *
     * @return a map of locales and their names translated from messages keys to current language
     */
    @ModelAttribute("locales")
    fun locales(@ModelAttribute layout: Layout): Set<Locale> {
        val locale = LocaleContextHolder.getLocale()
        layout.locales.forEach { entry -> entry.name = messageSource.getMessage(entry.name,null, locale) }
        return layout.locales
    }

    /**
     * Change Locale menu label mustache method
     *
     * @return the translated label for the change locale menu label
     */
    @ModelAttribute("localesLabel")
    fun localesLabel(@ModelAttribute layout: Layout): String {
        val locale= LocaleContextHolder.getLocale()
        return messageSource.getMessage("changeLocale.label",null, locale)
    }

    /**
     * Copyright label mustache method
     *
     * @return the translated label for the change locale menu label
     */
    @ModelAttribute("copyrightLabel")
    fun copyrightLabel(@ModelAttribute layout: Layout): String {
        val locale= LocaleContextHolder.getLocale()
        return messageSource.getMessage("copyright.label",null, locale)
    }
}
