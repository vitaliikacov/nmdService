'use strict';

controllers.controller('editFeedController',

    function ($scope, $rootScope, $state, $stateParams, $ionicLoading, $ionicPopup, reads, feeds) {
        $scope.showUi = false;

        $scope.utilities = AppUtilities.utilities;

        $scope.backToCategory = function () {
            $state.go('category', { id: $stateParams.categoryId });
        };

        $scope.onRenameChosen = function () {
            $scope.mode = 'rename';
        };

        $scope.onAssignChosen = function () {
        };

        $scope.onDeleteChosen = function () {
            $scope.mode = 'delete';
        };

        $scope.onCancelChosen = function () {
            $scope.mode = 'choose';
        };

        $scope.onDeleteFeed = function () {
            $ionicLoading.show({
                template: 'Deleting feed...'
            });

            feeds.delete(
                {
                    feedId: $stateParams.feedId
                },
                onDeleteFeedCompleted,
                onServerFault
            );
        };

        var loadFeedReadReport = function () {
            $ionicLoading.show({
                template: 'Loading feed...'
            });

            reads.query(
                {
                    feedId: $stateParams.feedId
                },
                onLoadFeedReportCompleted,
                onServerFault);
        };

        var onLoadFeedReportCompleted = function (response) {
            $ionicLoading.hide();

            if (response.status !== 'SUCCESS') {
                $scope.utilities.showError($ionicPopup, response);

                return;
            }

            $scope.showUi = true;
            $scope.mode = 'choose';

            $scope.feed = {
                name: response.title,
                items: response.reports.length
            };
        };

        var onDeleteFeedCompleted = function (response) {
            $ionicLoading.hide();

            if (response.status !== 'SUCCESS') {
                $scope.utilities.showError($ionicPopup, response);

                return;
            }

            $ionicPopup.alert({
                    title: 'Information',
                    template: 'Feed is deleted.'
            }).then($scope.backToCategory);
        };

        var onServerFault = function () {
            $scope.utilities.showFatalError($ionicPopup, $ionicLoading);
        };

        loadFeedReadReport();
    }
);