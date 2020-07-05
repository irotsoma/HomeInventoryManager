/*
 * Created by irotsoma on 7/2/2020.
 */
package com.irotsoma.homeinventorymanager.filerepository

import java.io.InputStream


class MongoAttachment (
    var attachmentId: String,
    var name: String,
    var inputStream: InputStream)
