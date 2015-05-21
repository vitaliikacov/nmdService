'use strict';

controllers.controller('feedController',

    function ($document, $scope, $rootScope, $state, $stateParams, $ionicLoading, $ionicPopup, $ionicPopover, reads) {
        var topItemTimestamp = 0;

        $scope.showUi = false;

        $scope.filter = $stateParams.filter;

        $scope.utilities = AppUtilities.utilities;

        $scope.backToCategory = function () {
            $state.go('category', { id: $stateParams.categoryId });
        };

        // .fromTemplate() method
        var template = '<ion-popover-view><ion-header-bar> <h1 class="title">My Popover Title</h1> </ion-header-bar> <ion-content> Hello! </ion-content></ion-popover-view>';

        $scope.popover = $ionicPopover.fromTemplate(template, {
            scope: $scope
        });

        $scope.openPopover = function($event) {
            $scope.popover.show($event);
        };

        $scope.closePopover = function() {
            $scope.popover.hide();
        };

        //Cleanup the popover when we're done with it!
        $scope.$on('$destroy', function() {
            $scope.popover.remove();
        });

        // Execute action on hide popover
        $scope.$on('popover.hidden', function() {
            // Execute action
        });

        // Execute action on remove popover
        $scope.$on('popover.removed', function() {
            // Execute action
        });

        var loadFeedReport = function () {
            $ionicLoading.show({
                template: $scope.utilities.loadingMessage('Loading feed...')
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
                template: $scope.utilities.loadingMessage('Marking item...')
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
                template: $scope.utilities.loadingMessage('Marking item...')
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
                template: $scope.utilities.loadingMessage('Marking items...')
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

            $scope.openPopover($document[0].getElementById('asd'));

        };

        var onServerFault = function () {
            $scope.utilities.showFatalError($ionicPopup, $ionicLoading);       
        };

        loadFeedReport();
    }
);
