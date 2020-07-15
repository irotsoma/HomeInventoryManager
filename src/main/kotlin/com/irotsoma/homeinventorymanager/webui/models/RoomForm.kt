/*
 * Created by irotsoma on 7/6/2020.
 */
package com.irotsoma.homeinventorymanager.webui.models

import javax.validation.constraints.NotBlank


class RoomForm {
    @NotBlank
    var roomName: String = ""

}