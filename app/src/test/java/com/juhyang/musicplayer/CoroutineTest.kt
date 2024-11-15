package com.juhyang.musicplayer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule


open class CoroutineTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // 테스트코드를 모두 한 스레드에서 실행되도록 해주는 규칙

    val testScheduler = mainDispatcherRule.testCoroutineScheduler
    val testDispatcher = mainDispatcherRule.testDispatcher
    val defaultTestDispatcher = mainDispatcherRule.defaultTestDispatcher
}
