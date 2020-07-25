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

import com.samskivert.mustache.Mustache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

/**
 * Controller advice object to allow for layout abstraction in mustache templates
 *
 * @author Justin Zak
 */
@ControllerAdvice
class LayoutAdvice {

    @Autowired
    private lateinit var compiler: Mustache.Compiler

    @Autowired
    private lateinit var mainMenu: MainMenu

    @ModelAttribute("layout")
    fun layout(model: Map<String, Any>): Mustache.Lambda {
        return Layout(compiler)
    }

    @ModelAttribute("title")
    fun pageTitle(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.title = frag.execute() }
    }

    @ModelAttribute("subTitle")
    fun pageSubTitle(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.subTitle = frag.execute() }
    }

    @ModelAttribute("menus")
    fun menus(): Iterable<Menu> {
        return mainMenu.menus
    }

    @ModelAttribute("scripts")
    fun scripts(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.scripts = frag.execute() }
    }

    @ModelAttribute("applicationTitle")
    fun applicationTitle(@ModelAttribute layout: Layout): String {
        return layout.applicationTitle
    }

    @ModelAttribute("stylesheets")
    fun stylesheets(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.stylesheets = frag.execute() }
    }
}
