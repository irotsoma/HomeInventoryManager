/*
 * Created by irotsoma on 7/6/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotEmpty


class RoomForm {
    var id: Int = -1
    @NotEmpty
    var name: String = ""
}