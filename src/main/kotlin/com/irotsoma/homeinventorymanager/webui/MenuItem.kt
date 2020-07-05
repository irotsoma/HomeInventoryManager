package com.irotsoma.homeinventorymanager.webui

/**
 * Object representing items in a menu
 *
 * @author Justin Zak
 */
class MenuItem(){
    var nameProperty: String =""
    var name: String = ""
    var path: String = ""
    var validUserRoles: ArrayList<String> = ArrayList()
    var disabled = true
    constructor(nameProperty:String, name:String, path:String, validUserRoles: ArrayList<String>, disabled: Boolean = true):this(){
        this.nameProperty=nameProperty
        this.name=name
        this.path=path
        this.validUserRoles=validUserRoles
        this.disabled = disabled
    }
}