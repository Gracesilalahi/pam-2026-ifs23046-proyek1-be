package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.AuthService
import org.delcom.services.UserService
import org.delcom.services.WardrobeService
import org.koin.dsl.module

fun appModule(jwtSecret: String) = module {
    // 1. User & Auth (Tetap Ada)
    single<IUserRepository> { UserRepository() }
    single { UserService(get(), get()) }
    single<IRefreshTokenRepository> { RefreshTokenRepository() }
    single { AuthService(jwtSecret, get(), get()) }

    // 2. Wardrobe (GANTI dari Todo ke Wardrobe)
    single<IWardrobeRepository> { WardrobeRepository() }
    single { WardrobeService(get()) } // Sesuai constructor WardrobeService(repository)
}