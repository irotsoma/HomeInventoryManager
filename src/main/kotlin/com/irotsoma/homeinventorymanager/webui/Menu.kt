package com.irotsoma.homeinventorymanager.webui

/**
 * Object representing a top level Menu
 *
 * @author Justin Zak
 */
class Menu() {
    var nameProperty: String = ""
    var name: String = ""
    var path: String? = null
    var validUserRoles: ArrayList<String> = ArrayList()
    var menuItems:ArrayList<MenuItem> = ArrayList()
    var containsMenuItems = false
    var disabled = true
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