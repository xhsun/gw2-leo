package me.xhsun.gw2leo.registry

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.xhsun.gw2leo.account.datastore.AccountIDRepository
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.service.AccountService
import me.xhsun.gw2leo.account.service.CharacterService
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.config.DB_NAME
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.GW2RepositoryFactory
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.http.interceptor.AuthorizationInterceptor
import me.xhsun.gw2leo.http.interceptor.AuthorizationStatusInterceptor
import me.xhsun.gw2leo.http.interceptor.ContentTypeInterceptor
import me.xhsun.gw2leo.refresh.service.AccountRefreshService
import me.xhsun.gw2leo.refresh.service.IAccountRefreshService
import me.xhsun.gw2leo.refresh.service.IStorageRefreshService
import me.xhsun.gw2leo.refresh.service.StorageRefreshService
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.service.IStorageRetrievalService
import me.xhsun.gw2leo.storage.service.IStorageService
import me.xhsun.gw2leo.storage.service.StorageRetrievalService
import me.xhsun.gw2leo.storage.service.StorageService
import me.xhsun.gw2leo.storage.service.mediator.IStorageRemoteMediatorBuilder
import me.xhsun.gw2leo.storage.service.mediator.MaterialStorageRemoteMediator
import me.xhsun.gw2leo.storage.service.mediator.StorageRemoteMediatorBuilder
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideGW2RepositoryFactory(
        authInterceptor: AuthorizationInterceptor,
        contentTypeInterceptor: ContentTypeInterceptor,
        authorizationStatusInterceptor: AuthorizationStatusInterceptor
    ): IGW2RepositoryFactory {
        return GW2RepositoryFactory(
            authInterceptor,
            contentTypeInterceptor,
            authorizationStatusInterceptor
        )
    }

    @Provides
    @Singleton
    fun provideDatastoreRepository(@ApplicationContext context: Context): IDatastoreRepository {
        return Room.databaseBuilder(
            context.applicationContext,
            IDatastoreRepository::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountIDRepository(@ApplicationContext context: Context): IAccountIDRepository {
        return AccountIDRepository(context)
    }

    @Provides
    @Singleton
    fun provideAccountService(
        datastore: IDatastoreRepository,
        accountIDRepository: IAccountIDRepository,
    ): IAccountService {
        return AccountService(datastore, accountIDRepository)
    }

    @Provides
    @Singleton
    fun provideCharacterService(
        datastore: IDatastoreRepository,
        accountService: IAccountService
    ): ICharacterService {
        return CharacterService(datastore, accountService)
    }

    @Provides
    fun provideAccountRefreshService(
        gw2RepositoryFactory: IGW2RepositoryFactory,
        datastore: IDatastoreRepository,
        accountIDRepository: IAccountIDRepository,
        accountService: IAccountService,
        characterService: ICharacterService
    ): IAccountRefreshService {
        return AccountRefreshService(
            gw2RepositoryFactory, datastore, accountIDRepository, accountService, characterService
        )
    }

    @Provides
    fun provideStorageRetrievalService(
        gw2RepositoryFactory: IGW2RepositoryFactory,
        accountService: IAccountService
    ): IStorageRetrievalService {
        return StorageRetrievalService(gw2RepositoryFactory, accountService)
    }

    @Provides
    fun provideStorageRefreshService(
        accountService: IAccountService,
        datastore: IDatastoreRepository,
        characterService: ICharacterService,
        storageRetrievalService: IStorageRetrievalService
    ): IStorageRefreshService {
        return StorageRefreshService(
            accountService,
            datastore,
            characterService,
            storageRetrievalService
        )
    }

    @Provides
    @OptIn(ExperimentalPagingApi::class)
    fun provideStorageService(
        datastore: IDatastoreRepository,
        storageRemoteMediatorBuilder: IStorageRemoteMediatorBuilder,
        materialStorageRemoteMediator: RemoteMediator<Int, MaterialStorage>
    ): IStorageService {
        return StorageService(
            datastore,
            storageRemoteMediatorBuilder,
            materialStorageRemoteMediator
        )
    }

    @Provides
    fun provideStorageRemoteMediatorBuilder(
        refreshService: IStorageRefreshService
    ): IStorageRemoteMediatorBuilder {
        return StorageRemoteMediatorBuilder(refreshService)
    }

    @Provides
    @OptIn(ExperimentalPagingApi::class)
    fun provideMaterialStorageRemoteMediator(
        accountService: IAccountService,
        refreshService: IStorageRefreshService
    ): RemoteMediator<Int, MaterialStorage> {
        return MaterialStorageRemoteMediator(accountService, refreshService)
    }
}