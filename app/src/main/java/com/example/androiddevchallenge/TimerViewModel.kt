package com.example.androiddevchallenge

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer

class TimerViewModel : ViewModel() {
    private var countDownState: CountDownState by mutableStateOf(CountDownState.Stop)

    val isStartVisible: Boolean
        get() = countDownState == CountDownState.Pause || countDownState == CountDownState.Stop
    val isStartEnabled: Boolean
        get() = timerSec > 0
    val isPauseVisible: Boolean
        get() = countDownState == CountDownState.Running
    val isResetVisible: Boolean
        get() = countDownState == CountDownState.Pause || countDownState == CountDownState.Completed
    val isNumPadVisible: Boolean
        get() = countDownState == CountDownState.Stop
    val remainTimeColor: Color
        get() = if (countDownState == CountDownState.Completed) {
            Color.Magenta
        } else {
            Color.Black
        }

    private val timerSec: Long
        get() = if (timerMinSecText.length > 2) {
            timerMinSecText.dropLast(2).toLong() * 60 + timerMinSecText.takeLast(2).toLong()
        } else {
            timerMinSecText.toLong()
        }

    private var capturedRemainTimeMilliSec: Long = 0
    private var timerMinSecText: String by mutableStateOf("0")
    private var passedMilliSec: Long by mutableStateOf(0)
    private val remainTimeMilliSec: Long
        get() = when (countDownState) {
            CountDownState.Pause -> capturedRemainTimeMilliSec
            CountDownState.Running, CountDownState.Completed -> capturedRemainTimeMilliSec - passedMilliSec
            else -> timerSec * 1000L
        }

    val remainTimeText
        get() = when (countDownState) {
            CountDownState.Stop -> "${timerSec / 60} m ${timerSec % 60} s"
            CountDownState.Completed -> "0 m 0 s 000"
            else -> "${remainTimeMilliSec / 60_000} m ${(remainTimeMilliSec / 1000) % 60} s ${
                "%03d".format(
                    remainTimeMilliSec % 1000
                )
            }"

        }

    private var job: Job? = null
    private var startedTimeMilliSec: Long = 0

    fun startCountDown() {
        if (countDownState == CountDownState.Stop) {
            capturedRemainTimeMilliSec = timerSec * 1000
        }
        countDownState = CountDownState.Running
        startedTimeMilliSec = System.currentTimeMillis()

        job = viewModelScope.launch {
            while (true) {
                passedMilliSec = System.currentTimeMillis() - startedTimeMilliSec
                if (remainTimeMilliSec < 0) {
                    // timer completed
                    countDownState = CountDownState.Completed
                    break
                }
                delay(100)
            }
        }
    }

    fun pauseCountingDown() {
        job?.cancel()
        capturedRemainTimeMilliSec = remainTimeMilliSec
        countDownState = CountDownState.Pause
    }

    fun reset() {
        countDownState = CountDownState.Stop
        capturedRemainTimeMilliSec = 0
    }

    /**
     * Append Number to the end of start time.
     */
    fun appendNumberToStartSec(number: Int) {
        if (number > 9 || number < 0) throw IllegalArgumentException("Bad Number")
        timerMinSecText = if (timerMinSecText == "0") {
            "$number"
        } else {
            "$timerMinSecText$number"
        }
    }

    fun clearStartSec() {
        timerMinSecText = "0"
    }

    fun backspaceStartSec() {
        timerMinSecText = if (timerMinSecText.length > 1) {
            timerMinSecText.dropLast(1)
        } else "0"
    }
}