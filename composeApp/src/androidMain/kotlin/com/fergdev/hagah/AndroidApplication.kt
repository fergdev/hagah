package com.fergdev.hagah

import android.app.Application
import android.content.Context
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.di.startKoin
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.storeOf
import kotlinx.io.files.Path
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin builder@{
            androidContext(this@AndroidApplication)
            modules(androidModule)
        }
    }
}

val androidModule = module {
    single {
        val context: Context = get()
        storeOf<List<DailyHagah>>(
            codec = FileCodec(Path(context.filesDir.absolutePath, HagahDb)),
            default = emptyList()
        )
    }.bind<KStore<List<DailyHagah>>>()
}
