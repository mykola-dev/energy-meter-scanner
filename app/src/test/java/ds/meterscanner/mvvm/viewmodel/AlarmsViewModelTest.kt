package ds.meterscanner.mvvm.viewmodel

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import ds.meterscanner.mvvm.AlarmsView
import ds.meterscanner.viewModelKodein
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AlarmsViewModelTest {

    @Mock lateinit var view: AlarmsView
    lateinit var viewModel: AlarmsViewModel

    @Before
    fun setUp() {
        viewModelKodein()
        viewModel = AlarmsViewModel()
    }

    @Test
    fun onNewAlarm() {
        viewModel.onNewAlarm(view)
        verify(view).pickTime(any(), any())
    }

}