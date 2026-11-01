package com.example.i18n

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    FA("fa", "فارسی", true),
    EN("en", "English", false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: FA
        }
    }
}
