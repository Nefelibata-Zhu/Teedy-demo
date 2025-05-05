'use strict';

/**
 * File view controller.
 */
angular.module('docs').controller('FileView', function($scope, $rootScope, $state, $stateParams, Restangular, $transitions, $uibModal) {
  // Load file data
  Restangular.one('file/' + $stateParams.fileId).get().then(function(data) {
    $scope.file = data;
  });
  
  /**
   * Navigate to the next file.
   */
  $scope.nextFile = function() {
    var next = false;
    _.each($scope.files, function(file) {
      if (next) {
        $state.go('document.view.content.file', { id: $stateParams.id, fileId: file.id });
        next = false;
        return;
      }
      
      if (file.id == $stateParams.fileId) {
        next = true;
      }
    });
  };
  
  /**
   * Navigate to the previous file.
   */
  $scope.previousFile = function() {
    var previous = null;
    _.each($scope.files, function(file) {
      if (file.id == $stateParams.fileId) {
        if (previous) {
          $state.go('document.view.content.file', { id: $stateParams.id, fileId: previous.id });
        }
        return;
      }
      
      previous = file;
    });
  };

  /**
   * Open the translate dialog.
   */
  $scope.translateFile = function() {
    $uibModal.open({
      templateUrl: 'partial/docs/document.translate.html',
      controller: 'DocumentModalTranslate',
      size: 'lg',
      resolve: {
        document: function() {
          return $scope.document;
        },
        file: function() {
          return $scope.file;
        }
      }
    });

    return false;
  };
  
  // Watch for state change
  var onDestroy = $transitions.onSuccess({}, function() {
    onDestroy();
    _.each($scope.files, function(file) {
      if (file.id === $stateParams.fileId) {
        // Nothing to do
      } else if (file.thumbnailLoading) {
        // Cancel pending thumbnail fetch
        file.thumbnailLoading.resolve();
      }
    });
  });
});