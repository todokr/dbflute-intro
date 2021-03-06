'use strict';

/**
 * Main Controller
 */
angular.module('dbflute-intro')
        .controller('MainCtrl', function ($scope, $window, $uibModal, $state, $stateParams, ApiFactory) {

    $scope.manifest = {}; // intro manifest
    $scope.versions = []; // engine versions
    $scope.configuration = {}; // intro configuration
    $scope.classificationMap = {}; // e.g. targetDatabase
    $scope.client = null; // model of current client
    $scope.clientList = []; // existing clients
    $scope.editFlg = false;
    $scope.option = {testConnection: true};

    // ===================================================================================
    //                                                                          Basic Data
    //                                                                          ==========
    $scope.manifest = function() {
        ApiFactory.manifest().then(function(response) {
            $scope.manifest = response.data;
        });
    };

    $scope.engineVersions = function(version) {
        ApiFactory.engineVersions().then(function(response) {
            $scope.versions = response.data;
        });
    };

    $scope.configuration = function() {
        ApiFactory.configuration().then(function(response) {
            $scope.configuration = response.data;
        });
    };

    $scope.classifications = function() {
        ApiFactory.classifications().then(function(response) {
            $scope.classificationMap = response.data;
        });
    };

    // ===================================================================================
    //                                                                     Client Handling
    //                                                                     ===============
    $scope.prepareClientList = function() {
        ApiFactory.clientList().then(function(response) {
            if (response.data.length > 0) {
                $scope.clientList = response.data;
            } else {
                $state.go('welcome'); // if no client show welcome page
            }
        });
     };

    $scope.goToClient = function(client) {
        $state.go('operate', { projectName: client.projectName });
    };

    $scope.goToClientCreate = function() {
        $state.go('create');
    };

    $scope.goToSettings = function(client) {
        $state.go('settings', { client: client, projectName: client.projectName });
    };

    $scope.edit = function() {
        $scope.editFlg = true;
    };

    $scope.cancelEdit = function() {
        $scope.editFlg = false;
        if ($scope.client.create) {
            $scope.client = null;
        } else {
            for (var index in $scope.clientList) {
                var client = $scope.clientList[index];
                if ($scope.client.projectName == client.projectName) {
                    $scope.client = client;
                    break;
                }
            }
        }
    };

    $scope.create = function(client, testConnection) {
        ApiFactory.createClient(client, testConnection).then(function(response) {
            $scope.editFlg = false;
            $scope.prepareClientList();
        });
    };

    $scope.update = function(client, testConnection) {
        ApiFactory.updateClient(client, testConnection).then(function(response) {
            $scope.editFlg = false;
            $scope.prepareClientList();
        });
    };

    $scope.remove = function(client) {
        ApiFactory.removeClient(client).then(function(response) {
            $scope.editFlg = false;
            $scope.client = null;
            $scope.prepareClientList();
        });
    };

    $scope.changeDatabase = function(client) {
    	client.jdbcDriverFqcn = client.driverName;
        var database = $scope.classificationMap["targetDatabaseMap"][client.databaseCode];
        client.jdbcDriverFqcn = database.driverName;
        client.mainSchemaSettings.url = database.urlTemplate;
        client.mainSchemaSettings.schema =  database.defaultSchema;
    };

    // ===================================================================================
    //                                                                       Optional Menu
    //                                                                       =============
    $scope.dfprop = function(client) {
        ApiFactory.dfporpBeanList(client).then(function (response) {
            $scope.dfpropBeanList = response.data;
        });
    };
    $scope.playsql = function(client) {
        ApiFactory.playsqlBeanList(client).then(function (response) {
            $scope.playsqlBeanList = response.data;
        });
    };
    $scope.log = function(client) {
        ApiFactory.logBeanList(client).then(function (response) {
            $scope.logBeanList = response.data;
        });
    };

    // ===================================================================================
    //                                                                            Document
    //                                                                            ========
    $scope.openSchemaHTML = function(client) {
      $window.open($scope.configuration['serverUrl'] + '/document/' + client.projectName + '/schemahtml/');
    };

    $scope.openHistoryHTML = function(client) {
      $window.open($scope.configuration['serverUrl'] + '/document/' + client.projectName + '/historyhtml/');
    };

    // ===================================================================================
    //                                                                               Task
    //                                                                              ======
    $scope.task = function(client, task) {
        $window.open('api/task/execute/' + client.projectName + '/' + task);
    };

    // ===================================================================================
    //                                                                     SchemaSyncCheck
    //                                                                     ===============
    $scope.addSchemaSyncCheckMap = function() {
        var name = prompt("Please enter name");
        if (!name) {
            return;
        }
        if (!name) {
            return;
        }
        $scope.client.schemaSyncCheckMap[name] = {};
    };

    $scope.removeSchemaSyncCheckMap = function(name) {
        delete $scope.client.schemaSyncCheckMap[name];
    };

    // ===================================================================================
    //                                                                              Engine
    //                                                                              ======
    $scope.downloadModal = function() {
        var downloadInstance = $uibModal.open({
            templateUrl: 'app/main/download.html',
            controller: 'DownloadInstanceController',
            resolve: {
                engineLatest: function() {
                    return ApiFactory.engineLatest();
                }
            }
        });

        downloadInstance.result.then(function(versions) {
            $scope.versions = versions;
        });
    };

    $scope.removeEngine = function(version) {
        var params = {version: version};
        ApiFactory.removeEngine(params).then(function(response) {
            $scope.engineVersions();
        });
    };

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    $scope.manifest();
    $scope.engineVersions();
    $scope.configuration();
    $scope.classifications();
    $scope.prepareClientList();
});

/**
 * Download DBFlute Engine
 */
angular.module('dbflute-intro').controller('DownloadInstanceController',
        function($scope, $uibModalInstance, engineLatest, ApiFactory) {
    'use strict';

    var engineLatestData = engineLatest.data;
    var latestReleaseVersion = engineLatestData.latestReleaseVersion;
    var latestSnapshotVersion = engineLatestData.latestSnapshotVersion;

    $scope.downloading = false;
    $scope.currentBranch = 'compatible11x';
    $scope.latestVersion = latestReleaseVersion;
    $scope.specifiedVersion = latestReleaseVersion;
    $scope.dbfluteEngine = {version: latestReleaseVersion};

    $scope.version = null;

    $scope.downloadEngine = function() {
        $scope.downloading = true;

        if ($scope.specifiedVersion !== null) {
            $scope.dbfluteEngine.version = $scope.specifiedVersion;
        }
        ApiFactory.downloadEngine($scope.dbfluteEngine).then(function(response) {
            $scope.downloading = false;

            ApiFactory.engineVersions().then(function(response) {
                $uibModalInstance.close(response.data);
            });
        });
    };
});
