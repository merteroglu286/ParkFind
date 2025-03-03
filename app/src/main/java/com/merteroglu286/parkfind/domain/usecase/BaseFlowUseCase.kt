package com.merteroglu286.parkfind.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class BaseFlowUseCase<T> {
    abstract fun execute(): Flow<T>
}