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
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import java.io.InputStream

/**
 * A data class to hold an instance of an attachment stored in MongoDB
 *
 * @property mongoId The unique ID of the attachment in MongoDB
 * @property name The name of the attachment in MongoDB
 * @property inputStream An input stream for retrieving the attachment file from MongoDB
 */
data class MongoAttachment (
    var mongoId: String,
    var name: String,
    var inputStream: InputStream)
