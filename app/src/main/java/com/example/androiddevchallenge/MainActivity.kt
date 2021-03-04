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

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.typography

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    Timer()
}

// Start building your app here!
@Composable
fun Timer(timerViewModel: TimerViewModel = viewModel()) {
    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center

        ) {
            if (timerViewModel.isProgressVisible) {
                CircularProgressIndicator(
                    progress = timerViewModel.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.Center
            ) {

                CountDownTimeText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    minText = timerViewModel.remainTimeTextMin,
                    secText = timerViewModel.remainTimeTextSec,
                    milliSecText = timerViewModel.remainTimeTextMilliSec,
                    isCompleted = timerViewModel.isCompleted
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (timerViewModel.isStartVisible) {
                        Button(
                            enabled = timerViewModel.isStartEnabled,
                            modifier = Modifier.padding(8.dp),
                            onClick = { timerViewModel.startCountDown() }
                        ) {
                            Text("START")
                        }
                    }

                    if (timerViewModel.isResetVisible) {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { timerViewModel.reset() }
                        ) {
                            Text("RESET")
                        }
                    }
                    if (timerViewModel.isPauseVisible) {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { timerViewModel.pauseCountingDown() }
                        ) {
                            Text("PAUSE")
                        }
                    }
                    if (timerViewModel.isStopVisible) {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { timerViewModel.stopCountDown() }
                        ) {
                            Text("STOP")
                        }
                    }
                }
                if (timerViewModel.isNumPadVisible) {
                    Spacer(modifier = Modifier.height(32.dp))
                    NumberInputButtons(
                        modifier = Modifier.fillMaxWidth(),
                        onClickNumberButton = { timerViewModel.appendNumberToStartSec(it) },
                        onClickClearButton = { timerViewModel.clearStartSec() },
                        onClickBackspaceButton = { timerViewModel.backspaceStartSec() }
                    )
                }
            }
        }
    }
}

@Composable
fun CountDownTimeText(
    modifier: Modifier,
    minText: String,
    secText: String,
    milliSecText: String,
    isCompleted: Boolean
) {
    val completedColor by rememberInfiniteTransition().animateColor(
        initialValue = Color.Black,
        targetValue = Color.Magenta,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val color = if (isCompleted) completedColor else Color.Black

    Row(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = minText,
            style = typography.h2,
            color = color
        )
        Text(
            modifier = Modifier.alignByBaseline(),
            text = "m",
            style = typography.h5,
            color = color
        )

        Text(
            modifier = Modifier.alignByBaseline().padding(start = 4.dp),
            text = secText,
            style = typography.h2,
            color = color
        )

        Text(
            modifier = Modifier.alignByBaseline(),
            text = "s",
            style = typography.h5,
            color = color
        )
        if (milliSecText.isNotEmpty()) {
            Text(
                modifier = Modifier.alignByBaseline().padding(start = 4.dp),
                text = milliSecText,
                style = typography.h4,
                color = color
            )
        }
    }
}

@Composable
fun NumberInputButtons(
    modifier: Modifier,
    onClickNumberButton: (Int) -> Unit,
    onClickClearButton: () -> Unit,
    onClickBackspaceButton: () -> Unit,
    buttonSize: Dp = 60.dp
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            for (i in 1..3) {
                NumberInputButton(i, buttonSize, onClick = onClickNumberButton)
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            for (i in 4..6) {
                NumberInputButton(i, buttonSize, onClick = onClickNumberButton)
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            for (i in 7..9) {
                NumberInputButton(i, buttonSize, onClick = onClickNumberButton)
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .size(buttonSize),
                onClick = onClickClearButton
            ) {
                Text(text = "C")
            }
            NumberInputButton(0, buttonSize, onClick = onClickNumberButton)
            val bsShape = CutCornerShape(
                topStart = buttonSize.times(0.48f),
                bottomStart = buttonSize.times(0.48f)
            )
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .size(buttonSize)
                    .clip(bsShape),
                onClick = onClickBackspaceButton
            ) {
                Text(text = "BS")
            }
        }
    }
}

@Composable
fun NumberInputButton(
    num: Int,
    buttonSize: Dp,
    onClick: (Int) -> Unit
) {
    Button(
        modifier = Modifier
            .padding(8.dp)
            .size(buttonSize)
            .clip(RoundedCornerShape(buttonSize.div(2))),
        onClick = {
            Log.d("MJ", "onClick of NumberInputButton")
            onClick.invoke(num)
        }
    ) {
        Text("$num")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
