/*
 * Created by irotsoma on 7/23/2020.
 */
package com.irotsoma.homeinventorymanager.webui.controllers

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.validation.BindingResult
import java.util.*
import java.util.regex.Pattern


object ParseBindingResultErrors {
    fun parseBindingResultErrors(bindingResult: BindingResult, messageSource: MessageSource, locale: Locale) : Map<String,String> {
        if (bindingResult.hasErrors()) {
            val errors: HashMap<String, String> = hashMapOf()
            for (error in bindingResult.fieldErrors) {
                val processedMessages = ArrayList<String>()
                if (error.defaultMessage?.contains(Pattern.compile("([A-Z]|_)*:\\{").toRegex()) == true) {
                    for (message in error.defaultMessage!!.split("\u001E")) {
                        val parsedMessage = message.split(":").toMutableList()
                        val toolTip = if (parsedMessage.size == 2) parsedMessage[1].replace("\"","&quot;",false) else ""
                        processedMessages.add(
                            "<div data-toggle=\"tooltip\" data-placement=\"top\" title=\"$toolTip\">${parsedMessage[0]}</div>"
                        )
                        val messageString = processedMessages.joinToString(separator = "")
                        errors["${error.field}Error"] = messageString
                    }
                } else {
                    var messageString = error.defaultMessage ?: ""
                    try {
                        messageString = messageSource.getMessage("$messageString.error.message", null, locale)
                    } catch (e: NoSuchMessageException) {
                        //ignore
                    }
                    errors["${error.field}Error"] = messageString
                }
            }
            return errors
        } else {
            return hashMapOf()
        }
    }
}