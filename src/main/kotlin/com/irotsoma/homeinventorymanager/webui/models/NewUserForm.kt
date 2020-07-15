package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.FieldMatch
import com.irotsoma.web.validation.ValidPassword
import javax.validation.constraints.NotBlank

@FieldMatch.List([
    FieldMatch(first = "password", second = "passwordConfirm", message = "The password fields must match")
])
class NewUserForm {
    @NotBlank
    var username: String? = null
    @NotBlank
    @ValidPassword
    var password: String? = null
    @NotBlank
    var passwordConfirm: String? = null
    var roles: Array<Option> = emptyArray()
}