package com.fergdev.hagah.server.database

import org.ktorm.database.Database
import java.io.File

object DatabaseFactory {
    fun init(): Database {
        val dbPath = File("data/hagah.db").absolutePath
        return Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        ).apply {
            useConnection { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeUpdate(
                        """
            CREATE TABLE IF NOT EXISTS devotionals (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                verse_reference TEXT NOT NULL,
                verse_text TEXT NOT NULL,
                reflection TEXT NOT NULL,
                call_to_action TEXT NOT NULL,
                prayer TEXT NOT NULL
            );
        """.trimIndent()
                    )
                }
            }
        }
    }
}