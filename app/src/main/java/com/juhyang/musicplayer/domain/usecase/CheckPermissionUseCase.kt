package com.juhyang.musicplayer.domain.usecase

import com.juhyang.musicplayer.domain.model.PermissionStatus
import kotlinx.coroutines.flow.Flow


interface CheckPermissionUseCase {
    suspend fun execute(): Flow<PermissionStatus>
}
