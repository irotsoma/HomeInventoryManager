/*
 * Created by irotsoma on 7/23/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.FieldMatch
import com.irotsoma.web.validation.ValidPassword
import javax.validation.constraints.NotBlank

@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
class ChangePasswordForm {
    @NotBlank
    @ValidPassword
    var password = ""
    @NotBlank
    @ValidPassword
    var confirmPassword = ""
}