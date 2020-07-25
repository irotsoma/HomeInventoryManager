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