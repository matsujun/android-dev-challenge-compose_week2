package com.example.androiddevchallenge

sealed class CountDownState {
    object Running: CountDownState()
    object Pause: CountDownState()
    object Stop: CountDownState()
    object Completed: CountDownState()
}