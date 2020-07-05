package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.FieldMatch
import com.irotsoma.web.validation.ValidPassword
import javax.validation.constraints.NotEmpty

@FieldMatch.List([
    FieldMatch(first = "password", second = "passwordConfirm", message = "The password fields must match")
])
class NewUserForm {
    @NotEmpty
    var username: String? = null
    @NotEmpty
    @ValidPassword
    var password: String? = null
    @NotEmpty
    var passwordConfirm: String? = null
    var roles: Array<Option> = emptyArray()
}