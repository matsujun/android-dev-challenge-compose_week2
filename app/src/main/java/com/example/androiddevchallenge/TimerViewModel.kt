/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var countDownState: CountDownState by mutableStateOf(CountDownState.Stop)

    val isStartVisible: Boolean
        get() = countDownState == CountDownState.Pause || countDownState == CountDownState.Stop
    val isStartEnabled: Boolean
        get() = timerSec > 0
    val isPauseVisible: Boolean
        get() = countDownState == CountDownState.Running
    val isStopVisible: Boolean
        get() = countDownState == CountDownState.Running || countDownState == CountDownState.Completed
    val isResetVisible: Boolean
        get() = countDownState == CountDownState.Pause
    val isNumPadVisible: Boolean
        get() = countDownState == CountDownState.Stop
    val isCompleted: Boolean
        get() = countDownState == CountDownState.Completed
    val isProgressVisible: Boolean
        get() = countDownState != CountDownState.Stop

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

    private val remainTimeTextParts: Triple<String, String, String>
        get() = when (countDownState) {
            CountDownState.Stop -> Triple("${timerSec / 60}", "${timerSec % 60}", "")
            CountDownState.Completed -> Triple("0", "0", "000")
            else -> Triple(
                "${remainTimeMilliSec / 60_000}",
                "${(remainTimeMilliSec / 1000) % 60}",
                "%03d".format(remainTimeMilliSec % 1000)
            )
        }
    val remainTimeTextMin
        get() = remainTimeTextParts.first
    val remainTimeTextSec
        get() = remainTimeTextParts.second
    val remainTimeTextMilliSec
        get() = remainTimeTextParts.third

    val progress
        get() = if (countDownState == CountDownState.Completed) {
            0f
        } else {
            (remainTimeMilliSec.toFloat() / (timerSec * 1000))
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
                delay(30)
            }
        }
    }

    fun pauseCountingDown() {
        job?.cancel()
        capturedRemainTimeMilliSec = remainTimeMilliSec
        countDownState = CountDownState.Pause
    }

    fun stopCountDown() {
        job?.cancel()
        countDownState = CountDownState.Stop
        capturedRemainTimeMilliSec = 0
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
