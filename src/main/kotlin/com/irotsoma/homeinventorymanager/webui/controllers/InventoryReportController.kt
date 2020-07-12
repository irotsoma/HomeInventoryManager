/*
 * Created by irotsoma on 7/9/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.reporting.JasperReportItem
import com.irotsoma.homeinventorymanager.reporting.JasperReportService
import mu.KLogging
import net.sf.jasperreports.engine.JasperExportManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*


@Controller
@Lazy
@RequestMapping("/inventoryreport/{id}")
@Secured("ROLE_USER")
class InventoryReportController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()

    @Autowired
    lateinit var jasperReportService: JasperReportService

    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping(produces = ["application/pdf"])
    fun get(@PathVariable id: String, timeZone: TimeZone): ResponseEntity<ByteArray> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return try {
            val jasperPrint = jasperReportService.generateReport(JasperReportItem.valueOf(id.toUpperCase()), userId)
            val outputFile: File = File.createTempFile("report_$id", ".pdf")
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.absolutePath)
            val headers = HttpHeaders()
            val timeStamp = DateTimeFormatter
                .ofPattern("yyyyMMddHHmmss")
                .withZone(timeZone.toZoneId())
                .format(Instant.now())
            headers.set("Content-Disposition", "inline;filename=report_${id}_${timeStamp}.pdf")
            ResponseEntity(outputFile.readBytes(), headers, HttpStatus.OK)
        } catch (e:Exception) {
            ResponseEntity.notFound().build()
        }
    }




}