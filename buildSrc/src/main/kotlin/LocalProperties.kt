@file:Suppress("MissingPackageDeclaration")

import java.util.Properties

fun Properties.openApiKey() = this["openAiApiKey"] as? String
fun Properties.storePassword() = this["storePassword"] as String
fun Properties.keyPassword() = this["keyPassword"] as String
fun Properties.keyAlias() = this["keyAlias"] as String
