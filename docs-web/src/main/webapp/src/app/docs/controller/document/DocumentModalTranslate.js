'use strict';

/**
 * Document translation controller.
 */
angular.module('docs').controller('DocumentModalTranslate', function($scope, $uibModalInstance, $translate, document, file, Translation) {
  $scope.document = document;
  $scope.file = file;
  $scope.languages = [];
  $scope.selectedLanguage = null;
  $scope.translation = null;
  $scope.isLoading = false;

  // Load available languages
  Translation.getLanguages().then(function(data) {
    $scope.languages = data.languages;
    
    // Automatically select the first language different from the document's language
    if ($scope.languages.length > 0) {
      for (var i = 0; i < $scope.languages.length; i++) {
        if ($scope.languages[i].code !== document.language) {
          $scope.selectedLanguage = $scope.languages[i].code;
          break;
        }
      }
      
      // If no alternate language found, select the first one
      if (!$scope.selectedLanguage && $scope.languages.length > 0) {
        $scope.selectedLanguage = $scope.languages[0].code;
      }
    }
  });

  /**
   * Close the modal.
   */
  $scope.close = function() {
    $uibModalInstance.close();
  };

  /**
   * Translate the document or file.
   */
  $scope.translate = function() {
    $scope.isLoading = true;
    
    if ($scope.file) {
      // Translate file
      Translation.translateFile($scope.file.id, $scope.selectedLanguage)
        .then(function(translation) {
          $scope.translation = translation;
          $scope.isLoading = false;
        })
        .catch(function() {
          $scope.isLoading = false;
        });
    } else {
      // Translate document
      Translation.translateDocument($scope.document.id, $scope.selectedLanguage)
        .then(function(translation) {
          $scope.translation = translation;
          $scope.isLoading = false;
        })
        .catch(function() {
          $scope.isLoading = false;
        });
    }
  };
}); 