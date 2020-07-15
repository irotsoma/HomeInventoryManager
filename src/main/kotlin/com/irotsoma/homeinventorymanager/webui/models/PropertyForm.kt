package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotBlank

class PropertyForm {
    @NotBlank
    var propertyName: String = ""
    var street: String? = null
    var city: String? = null
    var state: String? = null
    var postalCode: String? = null
    var country: String? = null
}