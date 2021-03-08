package com.irotsoma.homeinventorymanager.reporting.jasper

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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