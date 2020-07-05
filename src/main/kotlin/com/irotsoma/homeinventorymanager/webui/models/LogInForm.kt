package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotEmpty

class LogInForm {
    @NotEmpty
    var username: String? = null
    @NotEmpty
    var password: String? = null
    var rememberme: String? = null
}