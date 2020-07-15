/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotBlank


class CategoryForm {
    @NotBlank
    var name: String = ""
}