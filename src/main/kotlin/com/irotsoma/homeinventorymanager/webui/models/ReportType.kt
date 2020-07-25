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

/**
 * Data class to hold a list of report types for the web UI
 *
 * @author Justin Zak
 * @property id Unique identifier to be returned by the web form for the selected report type. Typically the string representation of a JasperReportItem instance
 * @property name The name to display to the user for this report type. To be pulled from internationalization files.
 */
data class ReportType(var id: String, var name: String)