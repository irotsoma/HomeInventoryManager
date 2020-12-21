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

import com.irotsoma.homeinventorymanager.data.InventoryItemRepository
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * A service for generating jasper reports
 *
 * @author Justin Zak
 * @property jasperReportConfig An autowired instance of the jasper configuration
 * @property inventoryItemRepository An autowired instance of the JPA repository for inventory items
 */
@Service
class JasperReportService {
    @Autowired
    private lateinit var jasperReportConfig: JasperReportConfig

    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository

    /**
     * Generates a JapserPrint instance for the given user ID and report type
     *
     * @param report An instance of JasperReportItem enum representing the type of report to generate
     * @param userId The user ID to generate the report for.
     * @return A JasperPrint instance of the report with data.
     */
    fun generateReport(report: JasperReportItem, userId: Int): JasperPrint? {
        val jrBeanCollectionDataSource = JRBeanCollectionDataSource(inventoryItemRepository.findByUserId(userId))
        return JasperFillManager.fillReport(compileReport(report), mutableMapOf() ,jrBeanCollectionDataSource)
    }

    /**
     * Function that compiles the report from a file and caches for later use
     *
     * @param report An instance of JasperReportItem enum representing the type of report to generate
     * @return A JasperReport instance of the report without data. (cached)
     */
    @Cacheable("reportCache")
    fun compileReport(report: JasperReportItem): JasperReport {
        val file = jasperReportConfig.getReportFile(report)
        return JasperCompileManager.compileReport(file.inputStream())
    }
}