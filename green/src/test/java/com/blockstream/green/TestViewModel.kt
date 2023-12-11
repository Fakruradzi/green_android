package com.blockstream.green

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.blockstream.common.CountlyBase
import com.blockstream.common.data.AppInfo
import com.blockstream.common.gdk.GdkSession
import com.blockstream.common.managers.SessionManager
import com.blockstream.common.managers.SettingsManager
import com.blockstream.common.models.GreenViewModel
import com.blockstream.green.ui.AppViewModelAndroid
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.mockito.Mock

@OptIn(ExperimentalCoroutinesApi::class)
open class TestViewModel<VM : GreenViewModel>: KoinTest {
    internal lateinit var viewModel : VM

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val schedulersRule = ImmediateSchedulersRule()

    @Mock
    protected lateinit var gdkSession: GdkSession

    @Mock
    protected lateinit var countly: CountlyBase

    private val testDispatcher = UnconfinedTestDispatcher()
    @Before
    open fun setup() {
        Dispatchers.setMain(testDispatcher)
        AppViewModelAndroid.ioDispatcher = testDispatcher

        MockProvider.register {
            // Your way to build a Mock here
            mockkClass(it)
        }

        startKoin {
            modules(
                module {
                    single { AppInfo("green_test", "1.0.0-test", true, true) }

                    declareMock<CountlyBase>{
                        every { viewModel(any()) } returns Unit
                        every { remoteConfigUpdateEvent } returns MutableSharedFlow<Unit>()
                    }

                    declareMock<SettingsManager> {
                        every { isDeviceTermsAccepted() } returns false
                    }

                    declareMock<SessionManager> {
                        every { getOnBoardingSession() } returns mockk()
                        every { getWalletSessionOrOnboarding(any()) } returns mockk()
                    }
                }
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }
}