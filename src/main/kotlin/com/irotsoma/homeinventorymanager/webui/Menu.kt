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
 * Object representing a top level Menu
 *
 * @author Justin Zak
 * @property nameProperty The name of the message entry to use for translating the menu name to the proper language
 * @property name The name of the menu
 * @property path The path that will be accessed when clicking this menu. (not used if the menu contains dropdown items)
 * @property containsMenuItems False to show if this menu should navigate when clicked on, True to show that the menu should drop down items when clicked on.
 * @property disabled True if the menu item should be disabled for the current circumstances.
 * @property menuItems A list of [MenuItem] objects for dropdown menus (not used if the menu is meant to navigate on click)
 * @property validUserRoles A list of user roles for which the menu should be enabled.
 */
class Menu() {
    var nameProperty: String = ""
    var name: String = ""
    var path: String? = null
    var validUserRoles: ArrayList<String> = ArrayList()
    var menuItems:ArrayList<MenuItem> = ArrayList()
    var containsMenuItems = false
    var disabled = true

    /**
     * Secondary constructor that sets the properties
     *
     * @param nameProperty The name of the message entry to use for translating the menu name to the proper language
     * @param name The name of the menu
     * @param path The path that will be accessed when clicking this menu. (not used if the menu contains dropdown items)
     * @param menuItems A list of [MenuItem] objects for dropdown menus (not used if the menu is meant to navigate on click)
     * @param validUserRoles A list of user roles for which the menu should be enabled.
     * @param disabled True if the menu item should be disabled for the current circumstances. (defaults to true)
     */
    constructor(nameProperty:String, name:String, path:String?, validUserRoles: ArrayList<String>, menuItems:ArrayList<MenuItem>, disabled:Boolean = true): this() {
        this.nameProperty=nameProperty
        this.name = name
        this.path=path
        this.validUserRoles=validUserRoles
        this.menuItems=menuItems
        if (menuItems.size > 0){
            containsMenuItems = true
        }
        this.disabled = disabled
    }
}