'use strict';

angular.module('orb.services', ['ngResource'])

    .factory('categories', function ($resource) {
        return $resource('/@security.key@/v01/categories/:categoryId/:feedId',
            {
                categoryId: '@categoryId',
                feedId: '@feedId'
            },
            {
                'query': {method: 'GET'},
                'save': {method: 'POST'},
                'update': {method: 'PUT'},
                'delete': {method: 'DELETE'}
            }
        );
    })

    .factory('feeds', function ($resource) {
        return $resource('/@security.key@/v01/feeds/:feedId',
            {
                feedId: '@feedId'
            },
            {
                'query': {method: 'GET'},
                'save': {method: 'POST'},
                'update': {method: 'PUT'},
                'delete': {method: 'DELETE'}
            }
        );
    })

    .factory('reads', function ($resource) {
        return $resource('/@security.key@/v01/reads/:feedId/:itemId',
            {
                feedId: '@feedId',
                itemId: '@itemId',
                offset: '@offset',
                size: '@size',
                filter: '@filter',
                markAs: '@markAs',
                topItemTimestamp: '@topItemTimestamp'
            },
            {
                'query': {method: 'GET'},
                'mark': {method: 'PUT'}
            }
        );
    })

    .factory('backup', function ($resource) {
        return $resource('/@security.key@/v01/backup',
            {},
            {
                'restore': {method: 'POST'}
            }
        );
    })

    .factory('lastUsedIds', function () {
        var lastUsedFeedId = '';
        var lastUsedItemId = '';

        return {

            store: function (feedId, itemId) {
                lastUsedFeedId = feedId;
                lastUsedItemId = itemId;
            },

            storeFeedId: function (feedId) {
                lastUsedFeedId = feedId;
            },

            getLastUsedFeedId: function () {
                return lastUsedFeedId;
            },

            getLastUsedItemId: function () {
                return lastUsedItemId;
            }
        }
    });
