'use strict';

var orb = angular.module('orb', ['ionic', 'ui.router', 'orb.services', 'orb.controllers']);

var controllers = angular.module('orb.controllers', []);

orb.config(function($stateProvider, $urlRouterProvider) {

      $urlRouterProvider.otherwise("/categories");

      $stateProvider
          .state('categories', {
            url: '/categories',
            templateUrl: 'partials/categories.html',
            controller: 'categoriesController'
          })
          .state('add-category', {
            url: '/add-category',
            templateUrl: 'partials/add-category.html',
            controller: 'addCategoryController'
          })

});