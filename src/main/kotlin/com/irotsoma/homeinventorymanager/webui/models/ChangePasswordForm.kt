/*
 *  Copyright (C) 2020  Justin Zak
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.irotsoma.homeinventorymanager.webui.models

import com.irotsoma.web.validation.FieldMatch
import com.irotsoma.web.validation.ValidPassword
import javax.validation.constraints.NotBlank
/**
 * Used to hold values and set validation rules using annotations for an HTML form for changing a password
 *
 * @author Justin Zak
 * @property password The new password.
 * @property confirmPassword The confirmation field for the new password.
 */
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match") //TODO: change once validation library allows for translation
class ChangePasswordForm {
    @NotBlank
    @ValidPassword
    var password = ""
    @NotBlank
    @ValidPassword
    var confirmPassword = ""
}