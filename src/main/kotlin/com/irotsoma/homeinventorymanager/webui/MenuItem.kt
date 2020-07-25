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

/**
 * Object representing items in a dropdown menu
 *
 * @author Justin Zak
 * @property nameProperty The name of the message entry to use for translating the menu item name to the proper language
 * @property name The name of the menu
 * @property path The path that will be accessed when clicking this menu item.
 * @property disabled True if the menu item should be disabled for the current circumstances.
 * @property validUserRoles A list of user roles for which the menu item should be enabled.
 */
class MenuItem(){
    var nameProperty: String =""
    var name: String = ""
    var path: String = ""
    var validUserRoles: ArrayList<String> = ArrayList()
    var disabled = true

    /**
     * Secondary constructor that sets property values
     *
     * @param nameProperty The name of the message entry to use for translating the menu name to the proper language
     * @param name The name of the menu
     * @param path The path that will be accessed when clicking this menu item.
     * @param validUserRoles A list of user roles for which the menu item should be enabled.
     * @param disabled True if the menu item should be disabled for the current circumstances. (defaults to true)
     */
    constructor(nameProperty:String, name:String, path:String, validUserRoles: ArrayList<String>, disabled: Boolean = true):this(){
        this.nameProperty=nameProperty
        this.name=name
        this.path=path
        this.validUserRoles=validUserRoles
        this.disabled = disabled
    }
}