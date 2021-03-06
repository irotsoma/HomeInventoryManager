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

package com.irotsoma.homeinventorymanager.authentication

import com.irotsoma.homeinventorymanager.data.rdbms.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

/**
 * Security configuration for REST controllers
 *
 * @author Justin Zak
 * @property rememberMeKey Key to allow remembering logins across server restarts (reduces security slightly)
 * @property userDetailsManager Autowired instance of user account manager
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Value("\${security.rememberme.key}")
    lateinit var rememberMeKey: String
    @Autowired
    lateinit var userDetailsManager: UserAccountDetailsManager
    /** Adds the user account manager to REST controllers with a password encoder for hashing passwords */
    override fun configure(auth: AuthenticationManagerBuilder){
        auth
            .userDetailsService(this.userDetailsManager)
            .passwordEncoder(User.PASSWORD_ENCODER)
    }
    /** Security configuration settings for REST controllers */
    override fun configure(http: HttpSecurity){
        http.authorizeRequests()
                .antMatchers("/scss/**").permitAll()
                .and()
            .formLogin()
                .permitAll()
                .and()
            .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
            .rememberMe()
                .key(rememberMeKey)
                .tokenValiditySeconds(86400)
                .and()
            .csrf().disable()
    }
}