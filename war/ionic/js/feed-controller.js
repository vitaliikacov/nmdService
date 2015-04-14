'use strict';

controllers.controller('feedController',

    function ($scope, $rootScope, $state, $stateParams, $ionicLoading, $ionicPopup, reads) {
        var topItemTimestamp = 0;

        $scope.showUi = false;

        $scope.filter = $stateParams.filter;

        $scope.utilities = AppUtilities.utilities;

        $scope.backToCategory = function () {
            $state.go('category', { id: $stateParams.categoryId });
        };

        var loadFeedReport = function () {
            $ionicLoading.show({
                template: 'Loading feed...'
            });

            reads.query(
                { 
                    feedId: $stateParams.feedId,
                    filter: $stateParams.filter
                },
                onLoadFeedReportCompleted,
                onServerFault);
        };

        $scope.goToCard = function (itemId) {
            $state.go('feed-card', {
                categoryId: $stateParams.categoryId,
                feedId: $stateParams.feedId,
                itemId: itemId,
                filter: $stateParams.filter
            });
        };

        $scope.markAsRead = function (feedId, itemId) {
            $rootScope.lastItemId = itemId;

            $ionicLoading.show({
                template: 'Marking item...'
            });

            reads.mark(
                {
                    feedId: feedId,
                    itemId: itemId,
                    markAs: 'read'
                },
                onMarkAsReadCompleted,
                onServerFault
            );
        };

        $scope.markAsReadLater = function (feedId, itemId) {
            $rootScope.lastItemId = itemId;

            $ionicLoading.show({
                template: 'Marking item...'
            });

            reads.mark(
                {
                    feedId: feedId,
                    itemId: itemId,
                    markAs: 'readLater'
                },
                onMarkAsReadLaterCompleted,
                onServerFault
            );
        };

        $scope.markAllItemsRead = function () {
            $ionicLoading.show({
                template: 'Marking items...'
            });

            var feedId = $stateParams.feedId;

            $rootScope.lastFeed = feedId;

            reads.mark({
                feedId: feedId,
                topItemTimestamp: topItemTimestamp
            },
            onMarkAllItemsReadCompleted,
            onServerFault);
        };

        $scope.switchToCardView = function () {
            $state.go('feed-card', { 
                categoryId: $stateParams.categoryId, 
                feedId: $stateParams.feedId 
            });
        };

        $scope.showAll = function () {
            $state.go('feed', {
                categoryId: $stateParams.categoryId,
                feedId: $stateParams.feedId,
                filter: 'show-all'
            });
        };

        $scope.showNew = function () {
            $state.go('feed', {
                categoryId: $stateParams.categoryId,
                feedId: $stateParams.feedId,
                filter: 'show-added'
            });
        };

        $scope.showNotRead = function () {
            $state.go('feed', {
                categoryId: $stateParams.categoryId,
                feedId: $stateParams.feedId,
                filter: 'show-not-read'
            });
        };

        $scope.showReadLater = function () {
            $state.go('feed', {
                categoryId: $stateParams.categoryId,
                feedId: $stateParams.feedId,
                filter: 'show-read-later'
            });
        };

        var onMarkAllItemsReadCompleted = function (response) {
            var me = this;

            $ionicLoading.hide();

            $state.go('category', {
                id: $stateParams.categoryId
            });
        };

        var onMarkAsReadCompleted = function (response) {
            $ionicLoading.hide();

            loadFeedReport();
        };

        var onMarkAsReadLaterCompleted = function (response) {
            $ionicLoading.hide();

            loadFeedReport();
        };

        var onLoadFeedReportCompleted = function (response) {
            $ionicLoading.hide();

            if (response.status !== 'SUCCESS') {
                $scope.utilities.showError($ionicPopup, response);

                return;
            }

            topItemTimestamp = response.topItemTimestamp;

            $scope.showUi = true;
 
            $scope.feed = { 
                title: response.title,
                total: response.read + response.notRead, 
                notRead: response.notRead,
                readLater: response.readLater,
                addedSinceLastView: response.addedSinceLastView
            };

            $scope.utilities.addTimeDifference(response.reports);

            $scope.items = response.reports;
        };

        var onServerFault = function () {
            $scope.utilities.showFatalError($ionicPopup, $ionicLoading);       
        };

        loadFeedReport();
    }
);
