package com.boxy.authenticator.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.boxy.authenticator.db.TokenDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        NativeSqliteDriver(TokenDatabase.Schema, "TokenDatabase")
}