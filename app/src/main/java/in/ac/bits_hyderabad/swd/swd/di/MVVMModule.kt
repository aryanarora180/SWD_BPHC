package `in`.ac.bits_hyderabad.swd.swd.di

import `in`.ac.bits_hyderabad.swd.swd.helper.DataStoreUtils
import `in`.ac.bits_hyderabad.swd.swd.model.AppDataSource
import `in`.ac.bits_hyderabad.swd.swd.model.AppRepository
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MVVMModule {

    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext application: Context): AppDataSource {
        return AppRepository(application)
    }

    @Provides
    @Singleton
    fun provideDataStoreUtils(@ApplicationContext application: Context): DataStoreUtils {
        return DataStoreUtils(application)
    }
}