package com.signbridge.llm

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GemmaCondenserTest {

    class FakeLlmClient : LlmClient {
        var lastPrompt: String = ""
        var cannedResponse: String = ""

        override suspend fun generate(prompt: String): Result<String> {
            lastPrompt = prompt
            return Result.success(cannedResponse)
        }
    }

    @Test
    fun `condenser handles empty list gracefully without calling LLM`() = runBlocking {
        val fakeClient = FakeLlmClient()
        val condenser = GemmaCondenser(fakeClient)
        
        val result = condenser.condense(emptyList())
        
        assertEquals("", result.getOrNull())
        assertEquals("", fakeClient.lastPrompt)
    }

    @Test
    fun `condenser handles single phrase directly without calling LLM`() = runBlocking {
        val fakeClient = FakeLlmClient()
        val condenser = GemmaCondenser(fakeClient)
        
        val result = condenser.condense(listOf("Help me"))
        
        assertEquals("Help me", result.getOrNull())
        assertEquals("", fakeClient.lastPrompt)
    }

    @Test
    fun `condenser builds correct prompt with strict guardrails`() = runBlocking {
        val fakeClient = FakeLlmClient()
        fakeClient.cannedResponse = "I am Deaf. Please, where is the pharmacy?"
        val condenser = GemmaCondenser(fakeClient)
        
        val phrases = listOf("I am Deaf", "where", "pharmacy")
        val result = condenser.condense(phrases)
        
        assertEquals("I am Deaf. Please, where is the pharmacy?", result.getOrNull())
        
        val prompt = fakeClient.lastPrompt
        assertTrue("Prompt should include the input phrases", prompt.contains("I am Deaf, where, pharmacy"))
        assertTrue("Prompt must include strict constraints", prompt.contains("ONLY output the natural sentence"))
        assertTrue("Prompt must prohibit extra information", prompt.contains("Do not add outside information"))
    }
}
