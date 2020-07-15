package com.irotsoma.homeinventorymanager.webui.models

class Option()  {
    var id: String = ""
    var name: String = ""
    var selected: String = ""
    constructor(name: String): this(name,true)
    constructor(name: String, selected: Boolean) : this() {
        this.name = name
        this.selected = if (selected) "selected" else ""
    }
    constructor(id: String, name: String, selected: Boolean) : this() {
        this.id = id
        this.name = name
        this.selected = if (selected) "selected" else ""
    }
    override fun equals(other: Any?): Boolean{
        return if (other is Option){
            (id == other.id && name == other.name && selected == other.selected)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode() + name.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }

}