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
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid

/**
 * Rest Controller for adding or editing inventory items
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of messages.
 * @property userRepository Autowired instance of the user JPA repository.
 * @property categoryRepository Autowired instance of the [Category] JPA repository.
 * @property inventoryItemRepository Autowired instance of the [InventoryItem] JPA repository.
 * @property attachmentRepository Autowired instance of the [Attachment] JPA repository.
 * @property propertyRepository Autowired instance of the [Property] JPA repository.
 * @property roomRepository Autowired instance of the [Room] JPA repository.
 * @property inventoryItemAttachmentLinkRepository Autowired instance of the [InventoryItemAttachmentLink] JPA repository.
 * @property attachmentService Autowired instance of the service for manipulating attachments.
 */
@Controller
@Lazy
@RequestMapping("/inventoryedit")
@Secured("ROLE_USER")
class InventoryEditController {
    //TODO: add formatting information for the purchase date field based on locale
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

    /**
     * Gets an empty copy of the edit screen for adding a new record
     *
     * @param model The Model holding attributes for the mustache templates.
     * @returns The template name to load
     */
    @GetMapping
    fun new(model: Model) : String{
        addStaticAttributes(model)
        model.addAttribute("hideAttachments", true)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id ?: return "inventoryedit"
        val properties = ArrayList<Option>()
        propertyRepository.findByUserId(userId).forEach{ properties.add(Option(it.id.toString(), it.name, false)) }
        if (properties.size > 0) {
            properties[0].selected = "selected"
        }
        model.addAttribute("properties", properties)
        val rooms = ArrayList<Option>()
        roomRepository.findByUserId(userId).forEach{ rooms.add(Option(it.id.toString(), it.name, false)) }
        if (rooms.size > 0) {
            rooms[0].selected = "selected"
        }
        model.addAttribute("rooms", rooms)
        val categories = ArrayList<Option>()
        categoryRepository.findByUserId(userId).forEach{ categories.add(Option(it.id.toString(), it.name, false))}
        if (categories.size > 0) {
            categories[0].selected = "selected"
        }
        model.addAttribute("categories", categories)
        return "inventoryedit"
    }

    /**
     * Gets a copy of the edit screen with a record already populated for editing
     *
     * @param id The ID of the record to edit
     * @param model The Model holding attributes for the mustache templates.
     * @returns The template name to load or the name of the error template
     */
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
        propertyRepository.findByUserId(userId).forEach{ if (inventoryItem.get().property?.id == it.id) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("properties", properties)
        val rooms = ArrayList<Option>()
        roomRepository.findByUserId(userId).forEach{ if (inventoryItem.get().room?.id == it.id) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("rooms", rooms)
        val categories = ArrayList<Option>()
        categoryRepository.findByUserId(userId).forEach{ if (inventoryItem.get().category?.id == it.id) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
        model.addAttribute("categories", categories)
        return "inventoryedit"
    }

    /**
     * Saves a new record
     *
     * @param inventoryItemForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @param model The Model holding attributes for the mustache templates.
     * @return A redirect back to the list page, an updated instance of the current record with validation errors, or the name of the error template
     */
    @PostMapping
    fun post(@Valid inventoryItemForm: InventoryItemForm, bindingResult: BindingResult, model: Model): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val decimalFormat= DecimalFormat.getInstance(locale) as DecimalFormat
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userRepository.findByUsername(authentication.name)
        if (user == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            model.addAttribute("error", errorMessage)
            return "error"
        }
        //if value or purchase price are not valid numbers set null and add an error
        var value = inventoryItemForm.estimatedValue?.replace(decimalFormat.decimalFormatSymbols.groupingSeparator.toString(),"")?.replace(decimalFormat.decimalFormatSymbols.decimalSeparator.toString(),".")
        try {
            BigDecimal(value)
        } catch (e:NumberFormatException) {
            value = null
        }
        var purchasePrice = inventoryItemForm.purchasePrice?.replace(decimalFormat.decimalFormatSymbols.groupingSeparator.toString(), "")?.replace(decimalFormat.decimalFormatSymbols.decimalSeparator.toString(),".")
        try {
            BigDecimal(purchasePrice)
        } catch (e:NumberFormatException) {
            purchasePrice = null
        }
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
            newInventoryItem.property = try { propertyRepository.findById(inventoryItemForm.properties!!.toInt()).get() }
                                        catch(e: NumberFormatException) { null }
                                        catch (e: NoSuchElementException) { null }
        }
        if (inventoryItemForm.rooms != null) {
            newInventoryItem.room = try { roomRepository.findById(inventoryItemForm.rooms!!.toInt()).get() }
                                    catch(e: NumberFormatException) { null }
                                    catch (e: NoSuchElementException) { null }
        }
        if (inventoryItemForm.categories != null) {
            newInventoryItem.category = try { categoryRepository.findById(inventoryItemForm.categories!!.toInt()).get() }
                                        catch(e: NumberFormatException) { null }
                                        catch (e: NoSuchElementException) { null }
        }
        if (bindingResult.hasErrors()) {
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("inventoryItem", newInventoryItem)
            val properties = ArrayList<Option>()
            propertyRepository.findByUserId(user.id).forEach{ if (newInventoryItem.property?.id == it.id) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("properties", properties)
            val rooms = ArrayList<Option>()
            roomRepository.findByUserId(user.id).forEach{ if (newInventoryItem.room?.id == it.id) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("rooms", rooms)
            val categories = ArrayList<Option>()
            categoryRepository.findByUserId(user.id).forEach{ if (newInventoryItem.category?.id == it.id) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("categories", categories)
            model.addAttribute("hideAttachments", true)
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
                propertyRepository.findByUserId(user.id).forEach{ if (inventoryItemForm.properties == it.name) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
                if (inventoryItemForm.properties == null) {
                    properties[0].selected = "selected"
                }
                model.addAttribute("properties", properties)
                val rooms = ArrayList<Option>()
                roomRepository.findByUserId(user.id).forEach{ if (inventoryItemForm.rooms == it.name) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
                if (inventoryItemForm.rooms == null) {
                    rooms[0].selected = "selected"
                }
                model.addAttribute("rooms", rooms)
                val categories = ArrayList<Option>()
                categoryRepository.findByUserId(user.id).forEach{ if (inventoryItemForm.categories == it.name) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
                if (inventoryItemForm.categories == null) {
                    categories[0].selected = "selected"
                }
                model.addAttribute("categories", categories)
                model.addAttribute("hideAttachments", true)

                return "inventoryedit"
            } else {
                throw e
            }
        }

        return "redirect:/inventory"
    }
    /**
     * Saves an existing record being edited
     *
     * @param inventoryItemForm A validated model of the form containing the record.
     * @param bindingResult Validation results for the form data.
     * @param model The Model holding attributes for the mustache templates.
     * @param id The id of the record to update.
     * @return A redirect back to the list page, an updated instance of the current record with validation errors, or the name of the error template
     */
    @PostMapping("/{id}")
    fun put(@Valid inventoryItemForm: InventoryItemForm, bindingResult: BindingResult, model: Model, @PathVariable id: Int): String{
        val locale: Locale = LocaleContextHolder.getLocale()
        val decimalFormat= DecimalFormat.getInstance(locale) as DecimalFormat
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
            val value = inventoryItemForm.estimatedValue?.replace(decimalFormat.decimalFormatSymbols.groupingSeparator.toString(),"")?.replace(decimalFormat.decimalFormatSymbols.decimalSeparator.toString(),".")
            this.estimatedValue = if (value.isNullOrBlank()) null else BigDecimal(value)
            this.purchaseDate = if (inventoryItemForm.purchaseDate.isNullOrBlank()) null else SimpleDateFormat("yyyy-MM-dd").parse(inventoryItemForm.purchaseDate)
            val purchasePrice = inventoryItemForm.purchasePrice?.replace(decimalFormat.decimalFormatSymbols.groupingSeparator.toString(),"")?.replace(decimalFormat.decimalFormatSymbols.decimalSeparator.toString(),".")
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
                    this.room = null
                } catch (e: NoSuchElementException) {
                    this.room = null
                }
            } else {
                this.room = null
            }
            if (inventoryItemForm.categories != null) {
                try {
                    this.category = categoryRepository.findById(inventoryItemForm.categories!!.toInt()).get()
                } catch (e: NumberFormatException) {
                    this.category = null
                } catch (e: NoSuchElementException) {
                    this.category = null
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
            val errors = ParseBindingResultErrors.parseBindingResultErrors(bindingResult, messageSource, locale)
            addStaticAttributes(model)
            model.addAllAttributes(errors)
            model.addAttribute("inventoryItem", updatedInventoryItem)
            val properties = ArrayList<Option>()
            propertyRepository.findByUserId(userId).forEach{ if (updatedInventoryItem.property?.id == it.id) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("properties", properties)
            val rooms = ArrayList<Option>()
            roomRepository.findByUserId(userId).forEach{ if (updatedInventoryItem.room?.id == it.id) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("rooms", rooms)
            val categories = ArrayList<Option>()
            categoryRepository.findByUserId(userId).forEach{ if (updatedInventoryItem.category?.id == it.id) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
            model.addAttribute("categories", categories)
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
                propertyRepository.findByUserId(userId).forEach{ if (inventoryItemForm.properties == it.name) {properties.add(Option(it.id.toString(), it.name, true))} else {properties.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("properties", properties)
                val rooms = ArrayList<Option>()
                roomRepository.findByUserId(userId).forEach{ if (inventoryItemForm.rooms == it.name) {rooms.add(Option(it.id.toString(), it.name, true))} else {rooms.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("rooms", rooms)
                val categories = ArrayList<Option>()
                categoryRepository.findByUserId(userId).forEach{ if (inventoryItemForm.categories == it.name) {categories.add(Option(it.id.toString(), it.name, true))} else {categories.add(Option(it.id.toString(), it.name,false)) }}
                model.addAttribute("categories", categories)
                return "inventoryedit"
            } else {
                throw e
            }
        }

        return "redirect:/inventory"
    }

    /**
     * Called to remove an attachment from an inventory item.
     *
     * @param id ID of the inventory item
     * @param attachmentId ID of the attachment
     * @param model The Model holding attributes for the mustache templates.
     * @return OK response if successful or the attachment was already not linked, not found response if item was not found or is not associated with the logged in user
     */
    @PostMapping("/{id}/remove-attachment/{attachmentId}")
    fun delete(@PathVariable id: Int, @PathVariable attachmentId: Int, model: Model): ResponseEntity<Any> {
        val locale: Locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = userRepository.findByUsername(authentication.name)?.id
        val inventoryItem = inventoryItemRepository.findById(id)
        if (userId == null || inventoryItem.isEmpty || inventoryItem.get().user?.id != userId || inventoryItem.get().id == null){
            val errorMessage = messageSource.getMessage("dataAccess.error.message", null, locale)
            logger.warn {errorMessage}
            return ResponseEntity.notFound().build()
        }
        attachmentService.detachFromInventoryItem(attachmentId, inventoryItem.get().id!!)
        return ResponseEntity.ok().build()
    }

    /**
     * Called to add an attachment and attach it to an inventory item with an ajax call.
     *
     * @param id ID of the inventory item
     * @param attachmentFile The MultipartFile sent as "attachmentFile" parameter
     * @param attachmentName The Name of the attachment sent as "attachmentName" parameter
     * @return A FormResponse that contains a boolean parameter "validated" which is true if the add was successful or false if errors, and a map of field name to message for any errors.
     */
    @PostMapping("/{id}/add-new-attachment/ajax", consumes = ["multipart/form-data"])
    @ResponseBody fun post(@PathVariable id: Int, @RequestParam("attachmentFile") attachmentFile: MultipartFile?, @RequestParam("attachmentName") attachmentName: String?): FormResponse {
        val authentication = SecurityContextHolder.getContext().authentication
        logger.debug {"Description: ${attachmentFile?.resource?.description}"}
        val userId = userRepository.findByUsername(authentication.name)?.id ?: throw UsernameNotFoundException("Unable to load user.")
        val locale: Locale = LocaleContextHolder.getLocale()
        if (attachmentFile == null || attachmentFile.isEmpty) {
            return FormResponse("attachmentFile", false, mapOf(Pair("attachmentFile", messageSource.getMessage("fileMissing.error.message", null, locale))))
        }
        if (attachmentName.isNullOrBlank()) {
            return FormResponse("attachmentName", false, mapOf(Pair("attachmentName", messageSource.getMessage("nameMissing.error.message", null, locale))))
        }
        val attachment = attachmentService.addAttachment(attachmentName, userId, attachmentFile)
        attachmentService.attachToInventoryItem(attachment.id, id)
        return FormResponse("attachment", true, null)
    }

    /**
     * Adds a series of model attributes that are required for all GETs
     *
     * @param model The Model object to add the attributes to.
     */
    fun addStaticAttributes(model: Model) {
        val locale: Locale = LocaleContextHolder.getLocale()

        val decimalFormat= DecimalFormat.getInstance(locale) as DecimalFormat

        model.addAttribute("currencySymbol", decimalFormat.decimalFormatSymbols.currencySymbol)
        model.addAttribute("decimalSeparator", decimalFormat.decimalFormatSymbols.decimalSeparator)
        model.addAttribute("numberGroupingSeparator", decimalFormat.decimalFormatSymbols.groupingSeparator)
        model.addAttribute("numberGroupingSize", decimalFormat.groupingSize.toString())
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
        model.addAttribute("deleteConfirmationMessage", messageSource.getMessage("deleteConfirmation.message", null, locale))
        model.addAttribute("fileLabel", messageSource.getMessage("file.label", null, locale))
        model.addAttribute("accessErrorMessage", messageSource.getMessage("dataAccess.error.message", null, locale))
        model.addAttribute("attachmentUnsupportedMessage", messageSource.getMessage("attachment.unsupportedFormat.error.message", null, locale))
        model.addAttribute("maximumFileSizeMessage", messageSource.getMessage("maximumFileSize.message", null, locale))
        model.addAttribute("searchLabel", messageSource.getMessage("search.label", null, locale))
        model.addAttribute("currencySymbol", decimalFormat.decimalFormatSymbols.currencySymbol)
        model.addAttribute("decimalSeparator", decimalFormat.decimalFormatSymbols.decimalSeparator)
        model.addAttribute("thousandsSeparator", decimalFormat.decimalFormatSymbols.groupingSeparator)
        model.addAttribute("previousLabel", messageSource.getMessage("previous.label", null, locale))
        model.addAttribute("nextLabel", messageSource.getMessage("next.label", null, locale))
        model.addAttribute("paginationInfoMessage", messageSource.getMessage("paginationInfo.message", null, locale))
        model.addAttribute("paginationInfoFilteredMessage", messageSource.getMessage("paginationInfoFiltered.message", null, locale))
        model.addAttribute("lengthMenuMessage", messageSource.getMessage("lengthMenu.message", null, locale))
        model.addAttribute("loadingLabel", messageSource.getMessage("loading.button.label", null, locale))
        model.addAttribute("emptyTableMessage", messageSource.getMessage("emptyTable.message", null, locale))
        model.addAttribute("zeroRecordsMessage", messageSource.getMessage("zeroRecords.message", null, locale))
    }
}