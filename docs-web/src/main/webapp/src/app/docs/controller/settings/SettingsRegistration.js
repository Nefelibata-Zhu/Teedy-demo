'use strict';

/**
 * Settings registration controller.
 */
angular.module('docs').controller('SettingsRegistration', function($scope, Restangular, $uibModal, $dialog, $translate) {
  // Load registration requests
  $scope.loadRequests = function() {
    Restangular.one('user').one('registration').get().then(function(data) {
      $scope.requests = data.requests;
    }, function(e) {
      // 处理错误，例如表不存在的情况
      console.error('Error loading registration requests', e);
      $scope.requests = [];
      $scope.loadError = true;
    });
  };
  
  $scope.loadRequests();
  
  // Approve a registration request
  $scope.approve = function(request) {
    $uibModal.open({
      templateUrl: 'partial/docs/settings.registration.approve.html',
      controller: 'SettingsRegistrationApprove',
      size: 'sm',
      resolve: {
        request: function() {
          return request;
        }
      }
    }).result.then(function(result) {
      if (result) {
        $scope.loadRequests();
      }
    });
  };
  
  // Reject a registration request
  $scope.reject = function(request) {
    var title = '拒绝确认';
    var msg = '确定要拒绝用户 ' + request.username + ' 的注册请求吗？';
    var btns = [
      { result: 'cancel', label: '取消' },
      { result: 'ok', label: '确定', cssClass: 'btn-danger' }
    ];

    $dialog.messageBox(title, msg, btns)
      .then(function(result) {
        if (result === 'ok') {
          Restangular.one('user').one('registration', request.id).one('reject').post().then(function() {
            $scope.loadRequests();
          });
        }
      });
  };
}); 