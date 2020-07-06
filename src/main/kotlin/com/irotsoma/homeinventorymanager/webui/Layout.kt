package com.irotsoma.homeinventorymanager.webui

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template.Fragment
import java.io.Writer


/**
 * Layout object to remove the need for the explicit {{>layout}} in mustache templates
 *
 * @author Justin Zak
 */
class Layout(private val compiler: Mustache.Compiler) : Mustache.Lambda {

    var body: String = ""

    var applicationTitle = "Home Inventory Manager"

    var title = "Home Inventory Manager"

    var subTitle: String? = null

    var scripts: String = ""

    override fun execute(frag: Fragment, out: Writer) {
        body = frag.execute()
        compiler.compile("{{>layout}}").execute(frag.context(), out)
    }

}