@file:Suppress("MissingPackageDeclaration")

import java.util.Properties

fun Properties.openApiKey() = this["openApiKey"] as? String
fun Properties.storeFilePath() = "certificates/hagah.jks"
fun Properties.storePassword() = this["storePassword"] as String
fun Properties.keyPassword() = this["keyPassword"] as String
fun Properties.keyAlias() = this["keyAlias"] as String
