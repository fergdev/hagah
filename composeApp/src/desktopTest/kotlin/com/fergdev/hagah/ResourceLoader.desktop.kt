package com.fergdev.hagah

import java.io.File

actual fun loadTestResource(resourceName: String) = File("${RESOURCE_PATH}/$resourceName").readText()
