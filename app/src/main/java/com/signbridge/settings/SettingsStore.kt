package com.signbridge.settings

interface SettingsStore {
    fun read(): AppSettings
    fun update(transform: (AppSettings) -> AppSettings): AppSettings
}

class MemorySettingsStore(
    initial: AppSettings = AppSettings.defaults(),
) : SettingsStore {
    private var current = initial.normalized()

    override fun read(): AppSettings = current

    override fun update(transform: (AppSettings) -> AppSettings): AppSettings {
        current = transform(current).normalized()
        return current
    }
}
