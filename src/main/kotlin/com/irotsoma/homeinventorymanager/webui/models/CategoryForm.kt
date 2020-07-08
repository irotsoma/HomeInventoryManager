/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotEmpty


class CategoryForm {
    var id: Int = -1
    @NotEmpty
    var name: String = ""
}