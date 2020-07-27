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

package com.irotsoma.homeinventorymanager.webui.controllers

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.validation.BindingResult
import java.util.*
import java.util.regex.Pattern

/**
 * Singleton for parsing errors returned by form validation
 */
object ParseBindingResultErrors {
    /**
     * Parses errors from a binding result and allows for setting up internationalization strings to translate errors
     * that are not translated by the validation library
     *
     * @param bindingResult Validation results for the form data.
     * @param messageSource MessageSource instance for internationalization of messages.
     * @param locale The locale to use for translation.
     * @return A map of key value pairs of field name to message.
     */
    fun parseBindingResultErrors(bindingResult: BindingResult, messageSource: MessageSource?, locale: Locale?) : MutableMap<String,String> {
        if (bindingResult.hasErrors()) {
            val errors: HashMap<String, String> = hashMapOf()
            for (error in bindingResult.fieldErrors) {
                val processedMessages = ArrayList<String>()
                //if the value is in the format usually returned by hibernate validation parse and make the detail message a tooltip
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
                    //otherwise translate the message if available or just add the message directly
                    for (message in error.defaultMessage!!.split("\u001E")) {
                        var messageString = message
                        if (messageSource != null && locale != null) {
                            try {
                                messageString = messageSource.getMessage("$messageString.error.message", null, locale)
                            } catch (e: NoSuchMessageException) {
                                //ignore
                            }
                        }
                        errors["${error.field}Error"] = (errors["${error.field}Error"]?:"") + messageString + "<br>"
                    }
                }
            }
            return errors
        } else {
            return hashMapOf()
        }
    }
}