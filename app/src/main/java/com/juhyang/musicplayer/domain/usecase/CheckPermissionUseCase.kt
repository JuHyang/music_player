package com.juhyang.musicplayer.domain.usecase

import kotlinx.coroutines.flow.Flow


interface CheckPermissionUseCase {
    fun execute(): Flow<Boolean>
}
