package com.signbridge.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhraseCatalogTest {
    @Test
    fun catalogContainsExactlyTheLockedTwentyFivePhrases() {
        val phrases = PhraseCatalog.all

        assertEquals(25, phrases.size)
        assertEquals((1..25).toList(), phrases.map { it.id })
        assertEquals("I am Deaf", phrases[0].text)
        assertEquals("Please calm down", phrases[1].text)
        assertEquals("Use my phone", phrases.last().text)
    }

    @Test
    fun catalogUsesPrdCategories() {
        val counts = PhraseCatalog.all.groupingBy { it.category }.eachCount()

        assertEquals(5, counts[PhraseCategory.IdentityCommunication])
        assertEquals(8, counts[PhraseCategory.RoadsideSafety])
        assertEquals(5, counts[PhraseCategory.PharmacyClinic])
        assertEquals(4, counts[PhraseCategory.RetailPublicService])
        assertEquals(3, counts[PhraseCategory.EmergencyShortcut])
    }

    @Test
    fun emergencyGridUsesRequiredSixPhrasesInDemoOrder() {
        assertEquals(
            listOf(
                "Help me",
                "I am Deaf",
                "Please call emergency services",
                "I am injured",
                "Please write it down",
                "I cannot hear you",
            ),
            PhraseCatalog.emergencyGrid.map { it.text },
        )

        assertTrue(PhraseCatalog.emergencyGrid.all { it.isEmergencyGridPhrase })
    }
}
