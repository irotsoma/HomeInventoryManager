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

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

/**
 * Configuration to allow for the application to use a session based locale resolver
 */
@Configuration
class CustomWebMvcConfigurerAdapter : WebMvcConfigurer {

    /**
     * Defines a singleton that resolves the current locale using session information, defaulting to US
     *
     * @return An instance of SessionLocaleResolver with default set to en_US
     */
    @Bean
    fun localeResolver(): LocaleResolver {
        val slr = SessionLocaleResolver()
        slr.setDefaultLocale(Locale.US)
        return slr

    }

    /**
     * Adds a locale change interceptor to the registry
     *
     * @param registry The locale change interceptor registry
     */
    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry?.addInterceptor(localeChangeInterceptor())
        if (registry != null) {
            super.addInterceptors(registry)
        }
    }

    /**
     * creates a singleton instance of LocaleChangeInterceptor for the addInterceptors method
     *
     * @return a default instance of LocaleChangeInterceptor.
     */
    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        return LocaleChangeInterceptor()
    }

    /**
     * Creates a singleton instance of MessageSource setting the appropriate file name and setting encoding to UTF-8
     * so that non-ASCII characters can be used.
     *
     * @return An instance of ResourceBundleMessageSource
     */
    @Bean
    fun messageSource(): MessageSource {
        val source = ResourceBundleMessageSource()
        source.setBasenames("messages", "classpath:messages")
        source.setDefaultEncoding("UTF-8")
        return source
    }
}
