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
    fun title(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.title = frag.execute() }
    }

    @ModelAttribute("menus")
    fun menus(): Iterable<Menu> {
        return mainMenu.menus
    }

    @ModelAttribute("scripts")
    fun scripts(@ModelAttribute layout: Layout): Mustache.Lambda {
        return Mustache.Lambda { frag, _ -> layout.scripts = frag.execute() }
    }
}
