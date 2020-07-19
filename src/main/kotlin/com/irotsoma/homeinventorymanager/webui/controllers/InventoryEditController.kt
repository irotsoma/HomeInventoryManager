/*
 * Created by irotsoma on 7/7/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import com.irotsoma.homeinventorymanager.data.*
import com.irotsoma.homeinventorymanager.filerepository.AttachmentService
import com.irotsoma.homeinventorymanager.webui.models.FormResponse
import com.irotsoma.homeinventorymanager.webui.models.InventoryItemForm
import com.irotsoma.homeinventorymanager.webui.models.Option
import mu.KLogging
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@Controller
@Lazy
@RequestMapping("/inventoryedit")
@Secured("ROLE_USER")
class InventoryEditController {
    /** kotlin-logging implementation*/
    private companion object: KLogging()
    @Autowired
    private lateinit var messageSource: MessageSource
    @Autowired
    private lateinit var inventoryItemRepository: InventoryItemRepository
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository
    @Autowired
    private lateinit var propertyRepository: PropertyRepository
    @Autowired
    private lateinit var roomRepository: RoomRepository
    @Autowired
    private lateinit var categoryRepository: CategoryRepository
    @Autowired
    private lateinit var inventoryItemAttachmentLinkRepository: InventoryItemAttachmentLinkRepository
    @Autowired
    private lateinit var attachmentService: AttachmentService

    @GetMapping
    fun new(model: Model) : String{
        addStaticAttributes(model)
        model.addAttribute("hideAttachments", true)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "inventoryedit"
        val properties = ArrayList<Option>()
        propertyRepository.findByUserId(userId)?.forEach{ properties.add(Option(it.id.toString(), it.name, false)) }
        model.addAttribute("properties", properties)
        val rooms = ArrayList<Option>()
        roomRepository.findByUserId(userId)?.forEach{ rooms.add(Option(it.id.toString(), it.name, false)) }
        model.addAttribute("rooms", rooms)
        val categories = ArrayList<Option>()
        categoryRepository.findByUserId(userId)?.forEach{ categories.add(Option(it.id.toString(), it.name, false))}
        model.addAttribute("categories", categories)
        return "inventoryedit"
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int, model: Model) : String {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val inventoryItem = inventoryItemRepository.findById(id)
        if (userId == null || inventoryItem.isEmpty || inventoryItem.get().user?.id != userId){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        addStaticAttributes(model)
        model.addAttribute("inventoryItem", inventoryItem.get())
        val properties = ArrayList<Option>()
        propertyRepository.findByUserId(userId)?.forEach{ if (inventoryItem.get().property?.id == it.id) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("properties", properties)
        val rooms = ArrayList<Option>()
        roomRepository.findByUserId(userId)?.forEach{ if (inventoryItem.get().room?.id == it.id) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("rooms", rooms)
        val categories = ArrayList<Option>()
        categoryRepository.findByUserId(userId)?.forEach{ if (inventoryItem.get().category?.id == it.id) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("categories", categories)
        return "inventoryedit"
    }

    @PostMapping
    fun post(@Valid inventoryItemForm: InventoryItemForm, bindingResult: BindingResult, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findByUsername(authentication.name)
        if (user == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val value = inventoryItemForm.estimatedValue?.replace(",","")
        val purchasePrice = inventoryItemForm.purchasePrice?.replace(",", "")
        val newInventoryItem = InventoryItem(
            inventoryItemForm.name.trim(),
            inventoryItemForm.description?.trim(),
            if (value.isNullOrBlank()) null else BigDecimal(value),
            if (inventoryItemForm.purchaseDate.isNullOrBlank()) null else SimpleDateFormat("yyyy-MM-dd").parse(inventoryItemForm.purchaseDate),
            if (purchasePrice.isNullOrBlank()) null else BigDecimal(purchasePrice),
            inventoryItemForm.manufacturer,
            inventoryItemForm.serialNumber,
            DataState.ACTIVE
        )
        newInventoryItem.user = user
        if (inventoryItemForm.properties != null) {
            newInventoryItem.property = propertyRepository.findByNameAndUserId(inventoryItemForm.properties!!, user.id)
        }
        if (inventoryItemForm.rooms != null) {
            newInventoryItem.room = roomRepository.findByNameAndUserId(inventoryItemForm.rooms!!, user.id)
        }
        if (inventoryItemForm.categories != null) {
            newInventoryItem.category = categoryRepository.findByNameAndUserId(inventoryItemForm.categories!!, user.id)
        }
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("inventoryitem", newInventoryItem)
            return "inventoryedit"
        }
        val attachmentSet = hashSetOf<Attachment>()
        for (attachmentId in inventoryItemForm.attachments){
            val attachment = attachmentRepository.findById(attachmentId)
            if (attachment.isPresent){
                attachmentSet.add(attachment.get())
            }
        }
        if (attachmentSet.isNotEmpty()){
            newInventoryItem.attachments = attachmentSet
        }
        try {
            inventoryItemRepository.saveAndFlush(newInventoryItem)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_inventory_item_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("inventoryItem", newInventoryItem)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                val properties = ArrayList<Option>()
                propertyRepository.findByUserId(user.id)?.forEach{ if (inventoryItemForm.properties == it.name) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("properties", properties)
                val rooms = ArrayList<Option>()
                roomRepository.findByUserId(user.id)?.forEach{ if (inventoryItemForm.rooms == it.name) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("rooms", rooms)
                val categories = ArrayList<Option>()
                categoryRepository.findByUserId(user.id)?.forEach{ if (inventoryItemForm.categories == it.name) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("categories", categories)
                return "inventoryedit"
            } else {
                throw e
            }
        }

        return "redirect:/inventory"
    }

    @PostMapping("/{id}")
    fun put(@Valid inventoryItemForm: InventoryItemForm, bindingResult: BindingResult, model: Model, @PathVariable id: Int): String{
        val locale: Locale = LocaleContextHolder.getLocale()

        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val inventoryItem = inventoryItemRepository.findById(id)
        if (userId == null || (inventoryItem.isEmpty) || (inventoryItem.isPresent && inventoryItem.get().user?.id != userId)){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        val updatedInventoryItem = inventoryItem.get().apply {
            this.name = inventoryItemForm.name.trim()
            this.description = inventoryItemForm.description?.trim()
            val value = inventoryItemForm.estimatedValue?.replace(",","")
            this.estimatedValue = if (value.isNullOrBlank()) null else BigDecimal(value)
            this.purchaseDate = if (inventoryItemForm.purchaseDate.isNullOrBlank()) null else SimpleDateFormat("yyyy-MM-dd").parse(inventoryItemForm.purchaseDate)
            val purchasePrice = inventoryItemForm.purchasePrice?.replace(",","")
            this.purchasePrice = if (purchasePrice.isNullOrBlank()) null else BigDecimal(purchasePrice)
            this.manufacturer = inventoryItemForm.manufacturer
            this.serialNumber = inventoryItemForm.serialNumber
            if (inventoryItemForm.properties != null) {
                try {
                    this.property = propertyRepository.findById(inventoryItemForm.properties!!.toInt()).get()
                } catch (e: NumberFormatException) {
                    this.property = null
                } catch (e: NoSuchElementException) {
                    this.property = null
                }
            } else {
                this.property = null
            }
            if (inventoryItemForm.rooms != null) {
                try {
                    this.room = roomRepository.findById(inventoryItemForm.rooms!!.toInt()).get()
                } catch (e: NumberFormatException) {
                    this.property = null
                } catch (e: NoSuchElementException) {
                    this.property = null
                }
            } else {
                this.room = null
            }
            if (inventoryItemForm.categories != null) {
                try {
                    this.category = categoryRepository.findById(inventoryItemForm.categories!!.toInt()).get()
                } catch (e: NumberFormatException) {
                    this.property = null
                } catch (e: NoSuchElementException) {
                    this.property = null
                }
            } else {
                this.category = null
            }
        }
        val attachmentSet = hashSetOf<Attachment>()
        for (attachmentId in inventoryItemForm.attachments){
            val attachment = attachmentRepository.findById(attachmentId)
            if (attachment.isPresent){
                attachmentSet.add(attachment.get())
            }
        }
        if (attachmentSet.isNotEmpty()){
            updatedInventoryItem.attachments = attachmentSet
        }
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.stream()
                .collect(
                    Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
                )
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("inventoryitem", updatedInventoryItem)
            return "inventoryedit"
        }
        try {
            inventoryItemRepository.saveAndFlush(updatedInventoryItem)
        } catch (e: DataIntegrityViolationException){
            if (e.cause is ConstraintViolationException && (e.cause as ConstraintViolationException).constraintName == "unique_inventory_item_name_per_user"){
                addStaticAttributes(model)
                model.addAttribute("inventoryItem", updatedInventoryItem)
                model.addAttribute("nameError", messageSource.getMessage("nameUniqueness.error.message", null, locale))
                val properties = ArrayList<Option>()
                propertyRepository.findByUserId(userId)?.forEach{ if (inventoryItemForm.properties == it.name) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("properties", properties)
                val rooms = ArrayList<Option>()
                roomRepository.findByUserId(userId)?.forEach{ if (inventoryItemForm.rooms == it.name) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("rooms", rooms)
                val categories = ArrayList<Option>()
                categoryRepository.findByUserId(userId)?.forEach{ if (inventoryItemForm.categories == it.name) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("categories", categories)
                return "inventoryedit"
            } else {
                throw e
            }
        }

        return "redirect:/inventory"
    }

    @PostMapping("/{id}/remove-attachment/{attachmentId}")
    fun delete(@PathVariable id: Int, @PathVariable attachmentId: Int, model: Model): ResponseEntity<Any> {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val inventoryItem = inventoryItemRepository.findById(attachmentId)
        val link = inventoryItemAttachmentLinkRepository.findByInventoryItemIdAndAttachmentId(id,attachmentId)
        if (userId == null || link == null || inventoryItem.isEmpty || inventoryItem.get().user?.id != userId){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            return ResponseEntity.notFound().build()
        }
        inventoryItemAttachmentLinkRepository.delete(link)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{id}/add-new-attachment/ajax")
    @ResponseBody fun post(@PathVariable id: Int, @RequestParam("attachmentFile") file: MultipartFile?, @RequestParam("attachmentName") attachmentName: String?, request: HttpServletRequest): FormResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        val locale: Locale = LocaleContextHolder.getLocale()
        if (file == null || file.isEmpty) {
            return FormResponse("attachmentFile", false, mapOf(Pair("attachmentFile", messageSource.getMessage("fileMissing.error.message", null, locale))))
        }
        if (attachmentName.isNullOrBlank()) {
            return FormResponse("attachmentName", false, mapOf(Pair("attachmentName", messageSource.getMessage("nameMissing.error.message", null, locale))))
        }
        val attachment = attachmentService.addAttachment(attachmentName, userId, file)
        attachmentService.attachToInventoryItem(attachment.id, id)
        return FormResponse("attachment", true, null)
    }


    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()

        val decimalFormat= DecimalFormat.getInstance(locale) as DecimalFormat

        model.addAttribute("currencySymbol", decimalFormat.decimalFormatSymbols.currencySymbol)
        model.addAttribute("decimalSeparator", decimalFormat.decimalFormatSymbols.decimalSeparator)
        model.addAttribute("thousandsSeparator", decimalFormat.decimalFormatSymbols.groupingSeparator)
        model.addAttribute("pageTitle", messageSource.getMessage("editInventoryItem.label", null, locale))
        model.addAttribute("nameLabel", messageSource.getMessage("name.label", null, locale))
        model.addAttribute("descriptionLabel", messageSource.getMessage("description.label", null, locale))
        model.addAttribute("estimatedValueLabel", messageSource.getMessage("estimatedValue.label", null, locale))
        model.addAttribute("purchaseDateLabel", messageSource.getMessage("purchaseDate.label", null, locale))
        model.addAttribute("purchasePriceLabel", messageSource.getMessage("purchasePrice.label", null, locale))
        model.addAttribute("manufacturerLabel", messageSource.getMessage("manufacturer.label", null, locale))
        model.addAttribute("serialNumberLabel", messageSource.getMessage("serialNumber.label", null, locale))
        model.addAttribute("attachmentsLabel", messageSource.getMessage("attachments.label", null, locale))
        model.addAttribute("propertyLabel",messageSource.getMessage("property.label", null, locale))
        model.addAttribute("roomLabel",messageSource.getMessage("room.label", null, locale))
        model.addAttribute("categoryLabel",messageSource.getMessage("category.label", null, locale))
        model.addAttribute("submitButtonLabel", messageSource.getMessage("submit.label", null, locale))
        model.addAttribute("addNewLabel", messageSource.getMessage("addNew.button.label",null, locale))
        model.addAttribute("cancelLabel", messageSource.getMessage("cancel.button.label", null, locale))
        model.addAttribute("actionsLabel", messageSource.getMessage("actions.label", null, locale))
        model.addAttribute("deleteLabel", messageSource.getMessage("delete.button.label",null, locale))
        model.addAttribute("downloadLabel", messageSource.getMessage("download.button.label",null, locale))
        model.addAttribute("previewLabel", messageSource.getMessage("preview.button.label",null, locale))
        model.addAttribute("addressLabel", messageSource.getMessage("address.label", null, locale))
        model.addAttribute("streetLabel", messageSource.getMessage("address.street.label", null, locale))
        model.addAttribute("cityLabel", messageSource.getMessage("address.city.label", null, locale))
        model.addAttribute("stateLabel", messageSource.getMessage("address.state.label", null, locale))
        model.addAttribute("postalCodeLabel", messageSource.getMessage("address.postalCode.label", null, locale))
        model.addAttribute("countryLabel", messageSource.getMessage("address.country.label", null, locale))
        model.addAttribute("previewModalTitle", messageSource.getMessage("attachmentPreview.title.label", null, locale))
        model.addAttribute("newCategoryModalTitle", messageSource.getMessage("newCategory.title.label", null, locale))
        model.addAttribute("newRoomModalTitle", messageSource.getMessage("newRoom.title.label", null, locale))
        model.addAttribute("newPropertyModalTitle", messageSource.getMessage("newProperty.title.label", null, locale))
        model.addAttribute("newAttachmentModalTitle", messageSource.getMessage("newAttachment.title.label", null, locale))

        model.addAttribute("fileLabel", messageSource.getMessage("file.label", null, locale))
        model.addAttribute("accessErrorMessage", messageSource.getMessage("dataAccess.error.message", null, locale))
        model.addAttribute("attachmentUnsupportedMessage", messageSource.getMessage("attachment.unsupportedFormat.error.message", null, locale))
    }
}