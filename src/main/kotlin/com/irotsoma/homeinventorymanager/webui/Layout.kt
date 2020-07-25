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

    var stylesheets: String = ""

    override fun execute(frag: Fragment, out: Writer) {

        body = frag.execute()
        compiler.compile("{{>layout}}").execute(frag.context(), out)
    }

}