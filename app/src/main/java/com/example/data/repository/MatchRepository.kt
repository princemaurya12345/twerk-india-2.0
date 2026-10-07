package com.example.data.repository

import com.example.data.model.PartnerProfile
import kotlin.random.Random

class MatchRepository {

    private val partnerPool = listOf(
        PartnerProfile(
            id = "p_mumbai_01",
            codename = "MumbaiRocker #42",
            location = "Mumbai, Maharashtra",
            state = "Maharashtra",
            nativeLanguage = "mr",
            interests = listOf("#Bollywood", "#StreetFood", "#Music", "#Chai"),
            avatarGradientStart = 0xFFFF5722,
            avatarGradientEnd = 0xFF9C27B0,
            bio = "Cinematographer & chai addict. Marine Drive sunsets over everything."
        ),
        PartnerProfile(
            id = "p_delhi_02",
            codename = "DelhiDilwala #99",
            location = "New Delhi",
            state = "Delhi",
            nativeLanguage = "hi",
            interests = listOf("#CricketIPL", "#CollegeLife", "#Tech", "#Gaming"),
            avatarGradientStart = 0xFF00C853,
            avatarGradientEnd = 0xFF00B0FF,
            bio = "Engineering student by day, gamer by night. Kohli fan forever!"
        ),
        PartnerProfile(
            id = "p_bengaluru_03",
            codename = "SiliconYogi #17",
            location = "Bengaluru, Karnataka",
            state = "Karnataka",
            nativeLanguage = "kn",
            interests = listOf("#TechStartup", "#AI", "#FilterCoffee", "#IndieMusic"),
            avatarGradientStart = 0xFF6200EA,
            avatarGradientEnd = 0xFFFF007F,
            bio = "Building AI startups & sipping hot filter kaapi in Koramangala."
        ),
        PartnerProfile(
            id = "p_kolkata_04",
            codename = "RoshogollaRebel #88",
            location = "Kolkata, West Bengal",
            state = "West Bengal",
            nativeLanguage = "bn",
            interests = listOf("#Literature", "#Art", "#StreetFood", "#Cinema"),
            avatarGradientStart = 0xFFFF9800,
            avatarGradientEnd = 0xFFE91E63,
            bio = "Passionate about classic Satyajit Ray cinema, poetry and puchkas."
        ),
        PartnerProfile(
            id = "p_hyderabad_05",
            codename = "BiryaniBoss #34",
            location = "Hyderabad, Telangana",
            state = "Telangana",
            nativeLanguage = "te",
            interests = listOf("#Biryani", "#TeluguCinema", "#Cricket", "#Travel"),
            avatarGradientStart = 0xFF00E5FF,
            avatarGradientEnd = 0xFF7C4DFF,
            bio = "Hyderabadi dum biryani connoisseur and Tollywood movie buff."
        ),
        PartnerProfile(
            id = "p_chennai_06",
            codename = "MarinaSurfer #07",
            location = "Chennai, Tamil Nadu",
            state = "Tamil Nadu",
            nativeLanguage = "ta",
            interests = listOf("#Music", "#ARRehman", "#Beaches", "#Gaming"),
            avatarGradientStart = 0xFFFFD600,
            avatarGradientEnd = 0xFFFF6D00,
            bio = "Keyboardist vibing to ARR chords. Sunrises at Marina beach."
        ),
        PartnerProfile(
            id = "p_chandigarh_07",
            codename = "PunjabDaSher #55",
            location = "Chandigarh / Punjab",
            state = "Punjab",
            nativeLanguage = "pa",
            interests = listOf("#Bhangra", "#GediRoute", "#Foodie", "#Fitness"),
            avatarGradientStart = 0xFFFF3D00,
            avatarGradientEnd = 0xFFFFD600,
            bio = "Lassi, gym, and upbeat Punjabi beats on full blast!"
        ),
        PartnerProfile(
            id = "p_ahmedabad_08",
            codename = "GujjuTrader #21",
            location = "Ahmedabad, Gujarat",
            state = "Gujarat",
            nativeLanguage = "gu",
            interests = listOf("#Finance", "#Garba", "#Food", "#Travel"),
            avatarGradientStart = 0xFF00E676,
            avatarGradientEnd = 0xFF1DE9B6,
            bio = "Day trader and midnight food explorer. Garba beats in my soul."
        ),
        PartnerProfile(
            id = "p_jaipur_09",
            codename = "PinkCityNomad #66",
            location = "Jaipur, Rajasthan",
            state = "Rajasthan",
            nativeLanguage = "hi",
            interests = listOf("#Photography", "#Heritage", "#Forts", "#Fashion"),
            avatarGradientStart = 0xFFD500F9,
            avatarGradientEnd = 0xFFFF1744,
            bio = "Capturing colorful Rajasthani culture and royal palace sunsets."
        ),
        PartnerProfile(
            id = "p_kochi_10",
            codename = "BackwaterVibes #12",
            location = "Kochi, Kerala",
            state = "Kerala",
            nativeLanguage = "ml",
            interests = listOf("#Nature", "#Travel", "#Coffee", "#Cinema"),
            avatarGradientStart = 0xFF18FFFF,
            avatarGradientEnd = 0xFF00B0FF,
            bio = "Monsoon lover, indie musician from the lush backwaters of Kerala."
        )
    )

    private val icebreakers = listOf(
        "Chai or Coffee? What's your daily go-to fuel?",
        "Virat Kohli or Rohit Sharma - who do you back in a clutch match?",
        "Best Indian street food: Pani Puri / Golgappa or Vada Pav?",
        "If you could travel anywhere in India tomorrow, where are you headed?",
        "What's one song on your playlist you cannot stop repeating lately?",
        "Late-night maggi at 2 AM or ordering midnight biryani?",
        "Which Bollywood or regional movie can you rewatch 100 times?",
        "What's the best spot to visit in your home city?",
        "Are you a mountain person (Himachal/Uttarakhand) or beach person (Goa/Gokarna)?",
        "What is the weirdest food combo that you secretly enjoy?"
    )

    private val partnerResponses = mapOf(
        "mr" to listOf(
            "नमस्कार! मुंबईहून बोलतोय. तुम्ही कसे आहात?",
            "वा! खूप छान! तुमच्या शहरात हवामान कसं आहे?",
            "मला स्ट्रीट फूड आणि बॉलिवूड चित्रपट खूप आवडतात!",
            "अरे वा, बरोबर बोललात तुम्ही!",
            "पुढच्या वेळी मुंबईला आलात तर नक्की सांगा!"
        ),
        "hi" to listOf(
            "नमस्ते भाई! दिल्ली से हूँ, आप कहाँ से हो?",
            "अरे वाह! चाय या कॉफ़ी - क्या पसंद है आपको?",
            "यहाँ का मौसम तो मस्त है आज, आपका दिन कैसा रहा?",
            "बिल्कुल सही कहा आपने, मज़ा आ गया बात करके!",
            "आईपीएल देखते हो क्या? कौन सी टीम सपोर्ट कर रहे हो?"
        ),
        "kn" to listOf(
            "ನಮಸ್ಕಾರ! ಬೆಂಗಳೂರಿನಿಂದ ಮಾತಾಡ್ತಾ ಇದ್ದೇನೆ.",
            "ಬೆಂಗಳೂರು ಟ್ರಾಫಿಕ್ ಬಗ್ಗೆ ಗೊತ್ತು ತಾನೇ? ಹೇಗಿದ್ದೀರಾ?",
            "ಖಂಡಿತ, ನಿಮ್ಮ ಅಭಿಪ್ರಾಯ ತುಂಬಾ ಇಷ್ಟ ಆಯ್ತು!",
            "ನಮ್ಮ ಕಡೆ ಫಿಲ್ಟರ್ ಕಾಫಿ ಸಖತ್ ಫೇಮಸ್, ನಿಮಗೆ ಇಷ್ಟಾನಾ?",
            "ತುಂಬಾ ಖುಷಿ ಆಯ್ತು ನಿಮ್ಮ ಜೊತೆ ಮಾತಾಡಿ!"
        ),
        "bn" to listOf(
            "নমস্কার! কলকাতা থেকে বলছি। কেমন আছেন?",
            "আপনার সাথে কথা বলে ভীষণ ভালো লাগছে!",
            "কলকাতার মিষ্টি আর ফুচকা খেয়েছেন কখনো?",
            "দারুণ কথা বললেন! আপনার প্রিয় শখ কি?",
            "সত্যিই খুব সুন্দর লাগলো আপনার চিন্তা!"
        ),
        "te" to listOf(
            "నమస్కారం అండీ! హైదరాబాద్ నుండి మాట్లాడుతున్నాను.",
            "హైదరాబాదీ బిర్యానీ అంటే మీకు ఇష్టమా?",
            "మీతో మాట్లాడటం చాలా బాగుంది!",
            "సినిమాలు బాగా చూస్తారా మీరు?",
            "నిజంగా చాలా మంచి విషయం చెప్పారు!"
        ),
        "ta" to listOf(
            "வணக்கம்! சென்னையில இருந்து பேசுறேன், எப்படி இருக்கீங்க?",
            "ரொம்ப சந்தோஷம் உங்களோட பேசினதுல!",
            "உங்களுக்கு ஏ.ஆர். ரஹ்மான் பாட்டு பிடிக்குமா?",
            "சென்னையோட மெரினா பீச் போனது உண்டா?",
            "சூப்பரான விஷயம் சொன்னீங்க பாஸ்!"
        ),
        "pa" to listOf(
            "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਜੀ! ਪੰਜਾਬ ਤੋਂ ਹਾਂ। ਕੀ ਹਾਲ ਚਾਲ?",
            "ਓਏ ਹੋਏ, ਗੱਲਬਾਤ ਕਰਕੇ ਪੂਰਾ ਸਵਾਦ ਆ ਗਿਆ!",
            "ਦੱਸੋ ਫਿਰ ਅੱਜ ਕੀ ਖਾਸ ਚੱਲ ਰਿਹਾ ਹੈ?",
            "ਤੁਸੀਂ ਭੰਗੜਾ ਪਾਉਂਦੇ ਹੋ ਜਾਂ ਫਿਰ ਗੇੜੀ ਰੂਟ ਦੇ ਸ਼ੌਕੀਨ ਹੋ?",
            "ਤੁਹਾਡੇ ਨਾਲ ਗੱਲ ਕਰਕੇ ਮਜ਼ਾ ਆ ਗਿਆ ਬਾਈ!"
        ),
        "gu" to listOf(
            "કેમ છો! અમદાવાદથી બોલું છું, મજામાં ને?",
            "અરે વાહ! ગરબા રમવાનો શોખ છે તમને?",
            "તમારી સાથે વાત કરીને બહુ આનંદ થયો!",
            "ગુજરાતી ખાણી-પીણી ચાખી છે ક્યારેય?",
            "એકદમ સાચી વાત કહી તમે!"
        )
    )

    fun getRandomPartner(stateFilter: String? = null): PartnerProfile {
        val filtered = if (!stateFilter.isNullOrBlank() && stateFilter != "All India") {
            partnerPool.filter { it.state.equals(stateFilter, ignoreCase = true) }
        } else {
            emptyList()
        }
        val pool = if (filtered.isNotEmpty()) filtered else partnerPool
        return pool[Random.nextInt(pool.size)]
    }

    fun getRandomIcebreaker(): String {
        return icebreakers[Random.nextInt(icebreakers.size)]
    }

    fun getPartnerResponse(nativeLanguage: String): String {
        val list = partnerResponses[nativeLanguage] ?: partnerResponses["hi"] ?: listOf(
            "Namaste! Great connecting with you on Twerk India!",
            "Where in India are you based? Love the vibes here!",
            "Tell me your top favorite song right now!"
        )
        return list[Random.nextInt(list.size)]
    }

    fun getInitialGreeting(partner: PartnerProfile): String {
        val greetings = mapOf(
            "mr" to "नमस्कार! मी ${partner.codename} मुंबईहून. काय चाललंय?",
            "hi" to "नमस्ते! मैं ${partner.codename}. कैसे हो आप?",
            "kn" to "ನಮಸ್ಕಾರ! ನಾನು ${partner.codename} ಬೆಂಗಳೂರಿನಿಂದ. ಹೇಗಿದ್ದೀರಾ?",
            "bn" to "নমস্কার! আমি ${partner.codename} কলকাতা থেকে। কেমন আছেন?",
            "te" to "నమస్కారం! నేను ${partner.codename} హైదరాబాద్ నుంచి.",
            "ta" to "வணக்கம்! நான் ${partner.codename} சென்னையில இருந்து. எப்படி இருக்கீங்க?",
            "pa" to "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਂ ${partner.codename} ਪੰਜਾਬ ਤੋਂ। ਕੀ ਹਾਲ ਨੇ?",
            "gu" to "કેમ છો! હું ${partner.codename} ગુજરાતથી."
        )
        return greetings[partner.nativeLanguage]
            ?: "Namaste! Hey there, I'm ${partner.codename} from ${partner.location}!"
    }
}
