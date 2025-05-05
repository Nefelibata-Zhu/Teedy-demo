package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.dao.FileDao;
import com.sismics.docs.core.dao.dto.DocumentDto;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.File;
import com.sismics.docs.core.service.TranslationService;
import com.sismics.docs.core.constant.PermType;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.util.JsonUtil;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

/**
 * Translation REST resources.
 *
 * @author jasonye
 */
@Path("/translation")
public class TranslationResource extends BaseResource {
    /**
     * Returns a translated document.
     *
     * @api {get} /translation/:id/document Translate a document
     * @apiName GetDocumentTranslation
     * @apiGroup Translation
     * @apiParam {String} id Document ID
     * @apiParam {String} target Target language code
     * @apiSuccess {String} title Translated title
     * @apiSuccess {String} description Translated description
     * @apiSuccess {String} source_language Source language
     * @apiSuccess {String} target_language Target language
     * @apiError (client) NotFound Document not found
     * @apiError (client) ValidationError Validation error
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @param documentId Document ID
     * @param targetLanguage Target language code
     * @return Response
     */
    @GET
    @Path("{id: [a-z0-9\\-]+}/document")
    public Response translateDocument(
            @PathParam("id") String documentId,
            @QueryParam("target") String targetLanguage) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // Validate the input
        if (targetLanguage == null || targetLanguage.isEmpty()) {
            throw new ClientException("ValidationError", "Target language is required");
        }

        // Get the document
        DocumentDao documentDao = new DocumentDao();
        DocumentDto documentDto = documentDao.getDocument(documentId, PermType.READ, getTargetIdList(null));
        if (documentDto == null) {
            throw new NotFoundException();
        }

        // Get the original document
        Document document = documentDao.getById(documentId);
        
        // Translate the document
        TranslationService translationService = new TranslationService();
        String translatedContent = translationService.translateDocument(document, targetLanguage);
        
        // Build the response
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("title", JsonUtil.nullable(document.getTitle() + " (" + targetLanguage + ")"))
                .add("description", JsonUtil.nullable(translatedContent))
                .add("source_language", JsonUtil.nullable(documentDto.getLanguage()))
                .add("target_language", targetLanguage);
        
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Returns a translated file.
     *
     * @api {get} /translation/:id/file Translate a file
     * @apiName GetFileTranslation
     * @apiGroup Translation
     * @apiParam {String} id File ID
     * @apiParam {String} target Target language code
     * @apiSuccess {String} name Translated file name
     * @apiSuccess {String} content Translated content
     * @apiSuccess {String} target_language Target language
     * @apiError (client) NotFound File not found
     * @apiError (client) ValidationError Validation error
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @param fileId File ID
     * @param targetLanguage Target language code
     * @return Response
     */
    @GET
    @Path("{id: [a-z0-9\\-]+}/file")
    public Response translateFile(
            @PathParam("id") String fileId,
            @QueryParam("target") String targetLanguage) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // Validate the input
        if (targetLanguage == null || targetLanguage.isEmpty()) {
            throw new ClientException("ValidationError", "Target language is required");
        }

        // Get the file
        FileDao fileDao = new FileDao();
        File file = fileDao.getFile(fileId);
        if (file == null) {
            throw new NotFoundException();
        }

        // Translate the file
        TranslationService translationService = new TranslationService();
        String translatedContent = translationService.translateFile(file, targetLanguage);
        
        // Build the response
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("name", JsonUtil.nullable(file.getName() + " (" + targetLanguage + ")"))
                .add("content", JsonUtil.nullable(translatedContent))
                .add("target_language", targetLanguage);
        
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Returns supported languages.
     *
     * @api {get} /translation/languages Get supported languages
     * @apiName GetSupportedLanguages
     * @apiGroup Translation
     * @apiSuccess {Object[]} languages List of supported languages
     * @apiSuccess {String} languages.code Language code
     * @apiSuccess {String} languages.name Language name
     * @apiPermission user
     * @apiVersion 1.5.0
     *
     * @return Response
     */
    @GET
    @Path("languages")
    public Response getSupportedLanguages() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        // Build the response with supported languages
        return Response.ok().entity(
                Json.createObjectBuilder()
                    .add("languages", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("code", "en")
                            .add("name", "English"))
                        .add(Json.createObjectBuilder()
                            .add("code", "fr")
                            .add("name", "French"))
                        .add(Json.createObjectBuilder()
                            .add("code", "es")
                            .add("name", "Spanish"))
                        .add(Json.createObjectBuilder()
                            .add("code", "zh_CN")
                            .add("name", "Chinese (Simplified)"))
                        .add(Json.createObjectBuilder()
                            .add("code", "de")
                            .add("name", "German"))
                        .add(Json.createObjectBuilder()
                            .add("code", "ja")
                            .add("name", "Japanese"))
                        .add(Json.createObjectBuilder()
                            .add("code", "ru")
                            .add("name", "Russian"))
                    )
                    .build()
        ).build();
    }
} 