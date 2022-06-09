package me.xhsun.gw2leo.registry

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.core.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.core.refresh.service.IAccountRefreshService
import me.xhsun.gw2leo.core.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.service.IStorageRepository
import me.xhsun.gw2leo.storage.service.IStorageRetrievalService
import me.xhsun.gw2leo.storage.service.mediator.IStorageRemoteMediatorBuilder
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class FakeAppModule {
    @Provides
    @Singleton
    fun provideGW2RepositoryFactory(): IGW2RepositoryFactory {
        return mockk()
    }

    @Provides
    @Singleton
    fun provideDatastoreRepository(): IDatastoreRepository {
        return mockk()
    }

    @Provides
    fun provideAccountIDRepository(): IAccountIDRepository {
        return mockk()
    }

    @Provides
    @Singleton
    fun provideAccountService(
    ): IAccountService {
        return mockk()
    }

    @Provides
    @Singleton
    fun provideCharacterService(
    ): ICharacterService {
        return mockk()
    }

    @Provides
    fun provideAccountRefreshService(
    ): IAccountRefreshService {
        return mockk()
    }

    @Provides
    fun provideStorageRetrievalService(
    ): IStorageRetrievalService {
        return mockk()
    }

    @Provides
    fun provideStorageRefreshService(
    ): IStorageRefreshService {
        return mockk()
    }

    @Provides
    fun provideStorageService(
    ): IStorageRepository {
        return mockk()
    }

    @Provides
    fun provideStorageRemoteMediatorBuilder(
    ): IStorageRemoteMediatorBuilder {
        return mockk()
    }

    @Provides
    @OptIn(ExperimentalPagingApi::class)
    fun provideMaterialStorageRemoteMediator(
    ): RemoteMediator<Int, MaterialStorage> {
        return mockk()
    }
}