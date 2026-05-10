package com.example.nammakelsa.utils

import com.example.nammakelsa.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

object GeminiHelper {

    // Upgraded to the Pro model
    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateWorkerBio(
        name: String,
        skill: String,
        rate: Int,
        location: String
    ): String {
        val prompt = """
            Write a professional worker bio in TWO languages:
            
            Worker details:
            - Name: $name
            - Skill: $skill
            - Daily Charges: ₹$rate for a full day's work
            - Location: $location
            
            Format your response EXACTLY like this:
            
            [English]
            Write 2-3 warm, friendly sentences in English. 
            Mention skill and location naturally.
            Describe the rate as "charges just ₹$rate for a full day" or 
            "offers services at ₹$rate per day" — make it sound like great value.
            Help a homeowner feel confident and comfortable hiring this person.
            
            [ಕನ್ನಡ]
            Write the same 2-3 sentences in simple, clear Kannada.
            Describe the rate warmly, like "ದಿನಕ್ಕೆ ಕೇವಲ ₹$rate ರಲ್ಲಿ ಸೇವೆ ನೀಡುತ್ತಾರೆ".
            
            Keep each version under 60 words.
            Do not add any extra labels or formatting beyond what is shown above.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text?.trim()
                ?: defaultBio(name, skill, location, rate)
        } catch (e: Exception) {
            defaultBio(name, skill, location, rate)
        }
    }

    private fun defaultBio(name: String, skill: String, location: String, rate: Int): String {
        return """
            [English]
            $name is a trusted and experienced $skill working in $location. 
            With a commitment to quality and punctuality, $name takes pride in 
            delivering excellent results for every household. Charges a reasonable 
            rate of ₹$rate for a full day's work.
            
            [ಕನ್ನಡ]
            $name ಅವರು $location ನಲ್ಲಿ ಕಾರ್ಯನಿರ್ವಹಿಸುವ ನಂಬಕಾರ್ಹ ಮತ್ತು 
            ಅನುಭವಿ $skill ಆಗಿದ್ದಾರೆ. ಗುಣಮಟ್ಟ ಮತ್ತು ಸಮಯಪಾಲನೆಗೆ 
            ಬದ್ಧರಾಗಿರುವ ಇವರು ದಿನಕ್ಕೆ ₹$rate ಸಮಂಜಸವಾದ ದರದಲ್ಲಿ 
            ಸೇವೆ ನೀಡುತ್ತಾರೆ.
        """.trimIndent()
    }

    suspend fun suggestSkillsFromDescription(description: String): String {
        val prompt = """
            Based on this work description: "$description"
            Suggest the most relevant skill category from this list only:
            Painter, Plumber, Electrician, Tiler, Carpenter, Gardener, Mason
            
            Reply with just ONE word — the skill name only. Nothing else.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "Painter"
        } catch (e: Exception) {
            "Painter"
        }
    }
}