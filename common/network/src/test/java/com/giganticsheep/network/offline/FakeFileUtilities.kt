package com.giganticsheep.network.offline

import com.giganticsheep.network.FileUtilities

class FakeFileUtilities(
    private val correctFileName: String,
) : FileUtilities {

    val fileContents = "fileContents"

    override fun stringFromFile(
        filename: String,
    ) = if (filename == correctFileName) {
        fileContents
    } else {
        error("Wrong filename")
    }
}
