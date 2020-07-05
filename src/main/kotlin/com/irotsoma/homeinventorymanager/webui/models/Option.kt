package com.irotsoma.homeinventorymanager.webui.models

class Option()  {
    var name: String = ""
    var selected: String = ""
    constructor(name: String): this(name,true)
    constructor(name: String, selected: Boolean) : this() {
        this.name = name
        this.selected = if (selected) "selected" else ""
    }

    override fun equals(other: Any?): Boolean{
        return if (other is Option){
            (name == other.name && selected == other.selected)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }

}