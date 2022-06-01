package me.xhsun.gw2leo.registry

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.xhsun.gw2leo.account.datastore.AccountIDRepository
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.service.*
import me.xhsun.gw2leo.config.DB_NAME
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.http.GW2RepositoryFactory
import me.xhsun.gw2leo.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.http.interceptor.AuthorizationInterceptor
import me.xhsun.gw2leo.http.interceptor.AuthorizationStatusInterceptor
import me.xhsun.gw2leo.http.interceptor.ContentTypeInterceptor
import me.xhsun.gw2leo.storage.service.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAccountIDRepository(@ApplicationContext context: Context): IAccountIDRepository {
        return AccountIDRepository(context)
    }

    @Provides
    fun provideAccountAddService(
        gw2RepositoryFactory: IGW2RepositoryFactory,
        datastore: IDatastoreRepository,
        accountIDRepository: IAccountIDRepository,
        accountService: IAccountService
    ): IAccountAddService {
        return AccountAddService(
            gw2RepositoryFactory,
            datastore,
            accountIDRepository,
            accountService
        )
    }

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
    fun provideStorageRetrievalService(
        gw2RepositoryFactory: IGW2RepositoryFactory,
        accountService: IAccountService
    ): IStorageRetrievalService {
        return StorageRetrievalService(gw2RepositoryFactory, accountService)
    }

    @Provides
    fun provideUpdateService(
        datastore: IDatastoreRepository,
        retrievalService: IRetrievalService,
        accountService: IAccountService
    ): IUpdateService {
        return UpdateService(datastore, retrievalService, accountService)
    }

    @Provides
    fun provideStorageService(
        updateService: UpdateService,
        datastore: IDatastoreRepository,
        accountService: IAccountService,
        characterService: ICharacterService
    ): IStorageService {
        return StorageService(updateService, datastore, accountService, characterService)
    }

    @Provides
    fun provideRefreshService(
        characterService: ICharacterService,
        storageService: StorageService,
        addService: IAccountAddService
    ): IRefreshService {
        return RefreshService(characterService, storageService, addService)
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
        gw2RepositoryFactory: IGW2RepositoryFactory,
        accountService: IAccountService
    ): ICharacterService {
        return CharacterService(datastore, gw2RepositoryFactory, accountService)
    }
}