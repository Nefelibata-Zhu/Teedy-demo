'use strict';

/**
 * Settings registration approve controller.
 */
angular.module('docs').controller('SettingsRegistrationApprove', function($scope, $uibModalInstance, Restangular, request) {
  $scope.request = request;
  $scope.quota = 1000 * 1000 * 100; // 100MB default quota
  
  // Approve button click
  $scope.approve = function() {
    Restangular.one('user').one('registration', request.id).customPOST({
      storage_quota: $scope.quota
    }, 'approve').then(function() {
      $uibModalInstance.close(true);
    }, function(e) {
      // 处理错误
      console.error('Error approving registration request', e);
      $scope.error = true;
    });
  };
  
  // Cancel button click
  $scope.cancel = function() {
    $uibModalInstance.close(false);
  };
}); 