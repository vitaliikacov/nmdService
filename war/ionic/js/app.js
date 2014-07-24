'use strict';

var orb = angular.module('orb', ['ionic', 'orb.services', 'orb.controllers']);

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
      
      .state('edit-category', {
        url: '/edit-category/{id}',
        templateUrl: 'partials/edit-category.html',
        controller: 'editCategoryController'
      })
      
      .state('category', {
        url: '/category/{id}',
        templateUrl: 'partials/category.html',
        controller: 'categoryController'
      })

      .state('add-feed', {
        url: '/add-feed/{id}',
        templateUrl: 'partials/add-feed.html',
        controller: 'addFeedController'
      })

      .state('feed', {
        url: '/feed/{id}',
        templateUrl: 'partials/feed.html',
        controller: 'feedController'
      })

});