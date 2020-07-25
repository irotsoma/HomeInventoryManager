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