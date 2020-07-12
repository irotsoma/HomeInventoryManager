/*
 * Created by irotsoma on 7/9/2020.
 */
package com.irotsoma.homeinventorymanager.reporting

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import java.io.File

@Configuration
@ConfigurationProperties(prefix="jasper.reporting")
class JasperReportConfig {
    var groupByRoomCategoryReportPath = ""

    var groupByPropertyCategoryReportPath = ""

    var groupByPropertyRoomReportPath = ""

    var groupByRoomReportPath = ""

    var groupByCategoryReportPath = ""

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