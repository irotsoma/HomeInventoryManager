package com.irotsoma.homeinventorymanager.webui.models

class FormResponse() {
    constructor(name: String?, validated: Boolean, errorMessages:Map<String, String>?): this(){
        this.name = name
        this.validated = validated
        this.errorMessages = errorMessages
    }
    var name: String? = null
    var validated: Boolean = false
    var errorMessages: Map<String, String>? = null
}