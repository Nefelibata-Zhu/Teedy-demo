package com.sismics.docs.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Translation utility.
 * 
 * @author jasonye
 */
public class TranslationUtil {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TranslationUtil.class);
    
    /**
     * For demonstration purposes, we use a simple map to simulate translation.
     * In a real-world application, this would be replaced with an actual translation API.
     */
    private static final Map<String, Map<String, String>> DEMO_TRANSLATIONS = new HashMap<>();
    
    static {
        // English to Chinese translations
        Map<String, String> enToZh = new HashMap<>();
        enToZh.put("Welcome", "欢迎");
        enToZh.put("Hello", "你好");
        enToZh.put("Document", "文档");
        enToZh.put("File", "文件");
        enToZh.put("Translation", "翻译");
        DEMO_TRANSLATIONS.put("en_zh_CN", enToZh);
        
        // English to French translations
        Map<String, String> enToFr = new HashMap<>();
        enToFr.put("Welcome", "Bienvenue");
        enToFr.put("Hello", "Bonjour");
        enToFr.put("Document", "Document");
        enToFr.put("File", "Fichier");
        enToFr.put("Translation", "Traduction");
        DEMO_TRANSLATIONS.put("en_fr", enToFr);
        
        // Add more languages as needed
    }
    
    /**
     * Translate text to the target language.
     * 
     * @param text Text to translate
     * @param targetLanguage Target language code
     * @return Translated text
     */
    public static String translate(String text, String targetLanguage) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // In a real implementation, this would call an external translation service
        // For demonstration, we'll use a simple approach:
        
        // 1. Determine source language (assuming English for demo)
        String sourceLanguage = "en";
        
        // 2. Create translation key
        String translationKey = sourceLanguage + "_" + targetLanguage;
        
        // 3. Check if we have demo translations for this language pair
        Map<String, String> translations = DEMO_TRANSLATIONS.get(translationKey);
        if (translations != null) {
            // Simple word-by-word translation for demo
            String[] words = text.split("\\s+");
            StringBuilder result = new StringBuilder();
            
            for (String word : words) {
                String translatedWord = translations.getOrDefault(word, word);
                result.append(translatedWord).append(" ");
            }
            
            return result.toString().trim();
        }
        
        // If no translation is available, return original text
        log.info("No translation available for {} to {}, returning original text", sourceLanguage, targetLanguage);
        return "(" + targetLanguage + ") " + text;
    }
} 