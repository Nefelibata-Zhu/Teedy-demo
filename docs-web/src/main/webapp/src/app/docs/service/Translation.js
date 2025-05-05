'use strict';

/**
 * Translation service.
 */
angular.module('docs').service('Translation', function(Restangular, $q) {
  /**
   * Get supported languages.
   * @returns {Promise} Promise for supported languages
   */
  this.getLanguages = function() {
    return Restangular.one('translation').one('languages').get();
  };
  
  /**
   * Translate document.
   * 
   * @param {string} documentId Document ID
   * @param {string} targetLanguage Target language code
   * @returns {Promise} Promise for translated document
   */
  this.translateDocument = function(documentId, targetLanguage) {
    return Restangular.one('translation', documentId).one('document').get({
      target: targetLanguage
    });
  };
  
  /**
   * Translate file.
   * 
   * @param {string} fileId File ID
   * @param {string} targetLanguage Target language code
   * @returns {Promise} Promise for translated file
   */
  this.translateFile = function(fileId, targetLanguage) {
    return Restangular.one('translation', fileId).one('file').get({
      target: targetLanguage
    });
  };
}); 