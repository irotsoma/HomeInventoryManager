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

package com.irotsoma.homeinventorymanager.reporting.jasper

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import java.io.File

/**
 * A configuration object to hold the settings for jasper reports contained in the external config file
 *
 * @author Justin Zak
 * @property groupByRoomCategoryReportPath Path to the report grouped by room and category
 * @property groupByPropertyCategoryReportPath Path to the report grouped by property and category
 * @property groupByPropertyRoomReportPath Path to the report grouped by property and room
 * @property groupByRoomReportPath Path to the report grouped by room.
 * @property groupByCategoryReportPath Path to the report grouped by category.
 */
@Configuration
@ConfigurationProperties(prefix="reporting.jasper")
class JasperReportConfig {
    var groupByRoomCategoryReportPath = ""

    var groupByPropertyCategoryReportPath = ""

    var groupByPropertyRoomReportPath = ""

    var groupByRoomReportPath = ""

    var groupByCategoryReportPath = ""

    /**
     * Gets a file object linked to the jasper report based on the JasperReportItem enum
     *
     * @param report Instance of JasperReportItem representing the report grouping desired.
     * @return A File object representing the location of the report requested.
     */
    fun getReportFile(report: JasperReportItem): File {
        val reportFilePath = when(report) {
            JasperReportItem.CATEGORY -> groupByCategoryReportPath
            JasperReportItem.PROPERTY_CATEGORY -> groupByPropertyCategoryReportPath
            JasperReportItem.PROPERTY_ROOM -> groupByPropertyRoomReportPath
            JasperReportItem.ROOM -> groupByRoomReportPath
            JasperReportItem.ROOM_CATEGORY -> groupByRoomCategoryReportPath
        }
        return ResourceUtils.getFile(reportFilePath)
    }
}