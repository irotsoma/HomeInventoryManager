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

package com.irotsoma.homeinventorymanager.webui

import com.irotsoma.homeinventorymanager.data.UserRoles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*


/**
 * Builds the main menu for the web ui
 *
 * @author Justin Zak
 * @property messageSource MessageSource instance for internationalization of menus.
 * @property menuLayout An array of Menu objects that hold the unprocessed menus from the configuration file
 * @property _menus Private property that holds the actual values for the processed menus
 * @property menus Read only property that triggers the populateValues method before returning the processed menus
 */
@Component
@ConfigurationProperties(prefix="webui.menus")
class MainMenu {

    @Autowired
    private lateinit var messageSource: MessageSource

    val menuLayout = ArrayList<Menu>()
    //use a separate property to store the value so that the getter isn't called to repopulate values every time it's accessed
    private var _menus = ArrayList<Menu>()
    val menus: ArrayList<Menu>
        get() {
            populateValues()
            return _menus
        }

    /**
     * Refreshes menu every time it is called to allow for dynamically enabling/disabling menu items based on role
     */
    private fun populateValues(){
        val locale = LocaleContextHolder.getLocale()
        val authentication = SecurityContextHolder.getContext().authentication
        //get a list of CloudBackEncRoles from the security context
        val authRoles = ArrayList<UserRoles>()
        if (authentication != null) {
            UserRoles.values().forEach {
                if (authentication.authorities.contains(SimpleGrantedAuthority(it.value) as GrantedAuthority)) {
                    authRoles.add(it)
                }
            }
        }
        //translate properties into localized names
        _menus.clear()
        for (menuObject in menuLayout){
            //add menu items to the menu for parsing later
            val menuItemsHolder = ArrayList<MenuItem>()
            menuObject.menuItems.mapTo(menuItemsHolder) { menuItem ->
                //determine if any of the authRoles match any of the validUserRoles or no validUserRoles were included (no restriction) for the current menu item being processed
                val enabled = menuItem.validUserRoles.size == 0 || authRoles.any{ it in menuItem.validUserRoles.map { role -> try{ UserRoles.valueOf(role) } catch (e:IllegalArgumentException){ "INVALID_ROLE" } }}
                MenuItem(menuItem.nameProperty, messageSource.getMessage(menuItem.nameProperty, null, locale), menuItem.path, menuItem.validUserRoles, !enabled)
            }
            //determine if any of the authRoles match any of the validUserRoles or no validUserRoles were included (no restriction) for the current menu object being processed
            val enabled = menuObject.validUserRoles.size == 0 || authRoles.any{it in menuObject.validUserRoles.map{role ->try{ UserRoles.valueOf(role) } catch (e:IllegalArgumentException){ "INVALID_ROLE" } }}
            _menus.add(Menu(menuObject.nameProperty, messageSource.getMessage(menuObject.nameProperty, null, locale), menuObject.path, menuObject.validUserRoles, menuItemsHolder, !enabled))
        }
    }
}