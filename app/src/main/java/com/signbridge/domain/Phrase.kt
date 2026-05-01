package com.signbridge.domain

data class Phrase(
    val id: Int,
    val text: String,
    val category: PhraseCategory,
    val isEmergencyGridPhrase: Boolean = false,
)

enum class PhraseCategory {
    IdentityCommunication,
    RoadsideSafety,
    PharmacyClinic,
    RetailPublicService,
    EmergencyShortcut,
}
