package com.signbridge.domain

object PhraseCatalog {
    val all: List<Phrase> = listOf(
        Phrase(1, "I am Deaf", PhraseCategory.IdentityCommunication, isEmergencyGridPhrase = true),
        Phrase(2, "Please calm down", PhraseCategory.IdentityCommunication),
        Phrase(3, "Please write it down", PhraseCategory.IdentityCommunication, isEmergencyGridPhrase = true),
        Phrase(4, "Please speak slowly", PhraseCategory.IdentityCommunication),
        Phrase(5, "I do not understand", PhraseCategory.IdentityCommunication),
        Phrase(6, "It was an accident", PhraseCategory.RoadsideSafety),
        Phrase(7, "My brakes failed", PhraseCategory.RoadsideSafety),
        Phrase(8, "I have insurance", PhraseCategory.RoadsideSafety),
        Phrase(9, "I am injured", PhraseCategory.RoadsideSafety, isEmergencyGridPhrase = true),
        Phrase(10, "I need a doctor", PhraseCategory.RoadsideSafety),
        Phrase(11, "Please call emergency services", PhraseCategory.RoadsideSafety, isEmergencyGridPhrase = true),
        Phrase(12, "I am not trying to fight", PhraseCategory.RoadsideSafety),
        Phrase(13, "Please call someone for me", PhraseCategory.RoadsideSafety),
        Phrase(14, "I have pain", PhraseCategory.PharmacyClinic),
        Phrase(15, "I am allergic", PhraseCategory.PharmacyClinic),
        Phrase(16, "I need medicine", PhraseCategory.PharmacyClinic),
        Phrase(17, "Please write the dosage", PhraseCategory.PharmacyClinic),
        Phrase(18, "Please show me the price", PhraseCategory.PharmacyClinic),
        Phrase(19, "How much is this?", PhraseCategory.RetailPublicService),
        Phrase(20, "I want to pay", PhraseCategory.RetailPublicService),
        Phrase(21, "Where is the restroom?", PhraseCategory.RetailPublicService),
        Phrase(22, "Please type your reply", PhraseCategory.RetailPublicService),
        Phrase(23, "Help me", PhraseCategory.EmergencyShortcut, isEmergencyGridPhrase = true),
        Phrase(24, "I cannot hear you", PhraseCategory.EmergencyShortcut, isEmergencyGridPhrase = true),
        Phrase(25, "Use my phone", PhraseCategory.EmergencyShortcut),
    )

    val emergencyGrid: List<Phrase> = listOfNotNull(
        findById(23),
        findById(1),
        findById(11),
        findById(9),
        findById(3),
        findById(24),
    )

    fun findById(id: Int): Phrase? = all.firstOrNull { it.id == id }
}
