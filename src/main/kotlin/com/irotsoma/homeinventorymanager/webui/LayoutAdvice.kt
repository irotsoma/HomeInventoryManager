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
