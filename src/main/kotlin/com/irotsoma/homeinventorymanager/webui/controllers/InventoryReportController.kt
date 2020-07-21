/*
 * Created by irotsoma on 7/9/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.UserRepository
import com.irotsoma.homeinventorymanager.reporting.JasperReportItem
import com.irotsoma.homeinventorymanager.reporting.JasperReportService
import com.irotsoma.homeinventorymanager.reporting.ReportType
import mu.KLogging
import net.sf.jasperreports.engine.JasperExportManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*


@Controller
@Lazy
@RequestMapping("/inventoryreport")
@Secured("ROLE_USER")
class InventoryReportController {
    /** kotlin-logging implementation*/
    private companion object : KLogging()

    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    lateinit var jasperReportService: JasperReportService

    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping
    fun get(model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        addStaticAttributes(model)
        val reportTypes: HashSet<ReportType> = hashSetOf()
        JasperReportItem.values().forEach {
            reportTypes.add(ReportType(it.value, messageSource.getMessage("inventoryReport.${it.value.toLowerCase()}.title", null, locale)))
        }
        model.addAttribute("reportTypes", reportTypes)

        return "inventoryreport"
    }

    @GetMapping(value = ["/report","/{reportType}"], produces = ["application/pdf"])
    fun getReport(@PathVariable reportType: String?, @RequestParam("reportType") reportTypeParam: String?, timeZone: TimeZone): ResponseEntity<ByteArray> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val report = reportType ?: reportTypeParam ?: return ResponseEntity.badRequest().build()
        return try {
            val jasperPrint = jasperReportService.generateReport(JasperReportItem.valueOf(report.toUpperCase()), userId)
            val outputFile: File = File.createTempFile("report_$report", ".pdf")
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.absolutePath)
            val headers = HttpHeaders()
            val timeStamp = DateTimeFormatter
                .ofPattern("yyyyMMddHHmmss")
                .withZone(timeZone.toZoneId())
                .format(Instant.now())
            headers.set("Content-Disposition", "inline;filename=report_${report}_${timeStamp}.pdf")
            ResponseEntity(outputFile.readBytes(), headers, HttpStatus.OK)
        } catch (e:Exception) {
            logger.warn { "Error building report. ${e.message}"}
            ResponseEntity.notFound().build()
        }
    }

    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()
        model.addAttribute("pageTitle", messageSource.getMessage("inventory.report.label", null, locale))
        model.addAttribute("reportTypeLabel", messageSource.getMessage("reportType.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
        model.addAttribute("loadingLabel", messageSource.getMessage("loading.button.label", null, locale))
    }
}

