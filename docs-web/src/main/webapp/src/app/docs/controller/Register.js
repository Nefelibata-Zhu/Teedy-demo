'use strict';

/**
 * Register controller.
 */
angular.module('docs').controller('Register', function(Restangular, $scope, $rootScope, $state, $dialog, $translate, $timeout) {
  // Get the app configuration
  Restangular.one('app').get().then(function(data) {
    $rootScope.app = data;
  });
  
  // Register
  $scope.register = function() {
    // 设置加载状态，防止重复提交
    $scope.isLoading = true;
    
    Restangular.one('user').one('registration').put({
      username: $scope.user.username,
      password: $scope.user.password,
      email: $scope.user.email
    }).then(function() {
      // Success, redirect to login
      var title = "注册成功";
      var msg = "您的注册请求已提交，请等待管理员审核。";
      var btns = [{result: 'ok', label: "确定", cssClass: 'btn-primary'}];
      
      // 延迟显示对话框，避免路由冲突
      $timeout(function() {
        // 修改对话框调用方式，使用回调替代Promise
        var dialog = $dialog.messageBox(title, msg, btns);
        dialog.open().then(function() {
          // 使用replace: true避免将当前页面添加到历史记录
          $state.go('login', {}, { location: 'replace' });
        });
      }, 100);
      
    }, function(data) {
      // 清除加载状态
      $scope.isLoading = false;
      
      if (data && data.data && data.data.type === 'AlreadyExistingUsername') {
        var title = "用户名已存在";
        var msg = "该用户名已被使用，请选择其他用户名。";
        var btns = [{result: 'ok', label: "确定", cssClass: 'btn-primary'}];
        var dialog = $dialog.messageBox(title, msg, btns);
        dialog.open();
      } else if (data && data.data && data.data.type === 'AlreadyExistingRequest') {
        var title = "注册请求已存在";
        var msg = "您已经提交过注册请求，请耐心等待管理员审核。";
        var btns = [{result: 'ok', label: "确定", cssClass: 'btn-primary'}];
        var dialog = $dialog.messageBox(title, msg, btns);
        dialog.open();
      } else {
        // Unknown error
        var title = "发生错误";
        var msg = "注册过程中发生未知错误，请稍后再试。";
        var btns = [{result: 'ok', label: "确定", cssClass: 'btn-primary'}];
        var dialog = $dialog.messageBox(title, msg, btns);
        dialog.open();
      }
    });
  };
}); 