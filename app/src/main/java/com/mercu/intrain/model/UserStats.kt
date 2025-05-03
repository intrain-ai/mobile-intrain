package com.mercu.intrain.model

data class UserStats(
    var currentStreak: Int = 0,
    var longestStreak: Int = 0,
    var lastActiveDate: String = "",
    var totalXP: Int = 0,
    var currentLevel: Int = 1
)