/*
 * Created by irotsoma on 7/9/2020.
 */
package com.irotsoma.homeinventorymanager.reporting

import com.irotsoma.homeinventorymanager.data.InventoryItemRepository
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service


@Service
class JasperReportService {
    @Autowired
    private lateinit var jasperReportConfig: JasperReportConfig

    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository

    @Cacheable("reportCache")
    fun generateReport(report: JasperReportItem, userId: Int): JasperPrint? {
        val file = jasperReportConfig.getReportFile(report)
        val jrBeanCollectionDataSource = JRBeanCollectionDataSource(inventoryItemRepository.findByUserId(userId))
        val jasperReport = JasperCompileManager.compileReport(file.inputStream())
        return JasperFillManager.fillReport(jasperReport, mutableMapOf() ,jrBeanCollectionDataSource)
    }
}