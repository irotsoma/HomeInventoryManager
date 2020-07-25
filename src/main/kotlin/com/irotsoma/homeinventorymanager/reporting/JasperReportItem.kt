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

package com.irotsoma.homeinventorymanager.reporting

/**
 * An enum listing the various jasper reports available
 *
 * @property value A string representation of the enum entry.
 */
enum class JasperReportItem (val value: String){
    CATEGORY("CATEGORY"),
    ROOM("ROOM"),
    ROOM_CATEGORY("ROOM_CATEGORY"),
    PROPERTY_CATEGORY("PROPERTY_CATEGORY"),
    PROPERTY_ROOM("PROPERTY_ROOM")
}