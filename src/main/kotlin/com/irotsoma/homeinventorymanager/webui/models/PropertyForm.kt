package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotEmpty

class PropertyForm {
    var id: Int = -1
    @NotEmpty
    var name: String = ""
    var street: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var country: String? = null
}