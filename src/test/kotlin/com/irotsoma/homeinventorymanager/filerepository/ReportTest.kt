/*
 * Created by irotsoma on 7/12/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import com.irotsoma.homeinventorymanager.reporting.JasperReportItem
import com.irotsoma.homeinventorymanager.reporting.JasperReportService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportTest {
    @Autowired
    private lateinit var jasperReportService: JasperReportService

    @Test
    fun generateAllReports() {
        for (report in JasperReportItem.values()) {
            val jasperPrint = jasperReportService.generateReport(report, 1)
            assert(jasperPrint != null)
        }
    }
}