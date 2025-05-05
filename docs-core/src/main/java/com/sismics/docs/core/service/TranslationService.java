package com.sismics.docs.core.service;

import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.File;
import com.sismics.docs.core.util.TranslationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translation service.
 *
 * @author jasonye
 */
public class TranslationService {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    /**
     * Translate document content.
     *
     * @param document The document to translate
     * @param targetLanguage The target language code (e.g., "en", "fr", "zh_CN")
     * @return Translated document content
     */
    public String translateDocument(Document document, String targetLanguage) {
        if (document == null) {
            return null;
        }

        log.info("Translating document {} to {}", document.getId(), targetLanguage);
        
        // Get document title and description for translation
        String title = document.getTitle();
        String description = document.getDescription();
        
        // Translate the title and description
        String translatedTitle = TranslationUtil.translate(title, targetLanguage);
        String translatedDescription = TranslationUtil.translate(description, targetLanguage);
        
        // Format the translated content
        return formatTranslatedContent(translatedTitle, translatedDescription);
    }
    
    /**
     * Translate file content.
     *
     * @param file The file to translate
     * @param targetLanguage The target language code
     * @return Translated file content
     */
    public String translateFile(File file, String targetLanguage) {
        if (file == null) {
            return null;
        }
        
        log.info("Translating file {} to {}", file.getId(), targetLanguage);
        
        // Get file name and content for translation
        String fileName = file.getName();
        String content = file.getContent();
        
        // Translate the file name and content
        String translatedFileName = TranslationUtil.translate(fileName, targetLanguage);
        String translatedContent = TranslationUtil.translate(content, targetLanguage);
        
        // Format the translated content
        return formatTranslatedFileContent(translatedFileName, translatedContent);
    }
    
    /**
     * Format the translated document content.
     * 
     * @param title Translated title
     * @param description Translated description
     * @return Formatted content
     */
    private String formatTranslatedContent(String title, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(title).append("\n\n");
        sb.append(description);
        return sb.toString();
    }
    
    /**
     * Format the translated file content.
     * 
     * @param fileName Translated file name
     * @param content Translated content
     * @return Formatted content
     */
    private String formatTranslatedFileContent(String fileName, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(fileName).append("\n\n");
        sb.append(content);
        return sb.toString();
    }
} 