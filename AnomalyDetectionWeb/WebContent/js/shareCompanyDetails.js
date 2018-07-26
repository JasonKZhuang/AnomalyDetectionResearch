angular.module('compDetails', ['angular.filter'])
.controller('compDetailsCtrl', function ($scope,$http) {

	var urlBase="http://localhost:8080/GlobalShares/";
	
	var url = window.location.href;
    var vars = url.split("?")[1];
    var companyId = vars.split("=")[1];
    
    if(!companyId){
    	companyId = "0C00000AXR";//set a default company Id
    }
    
    $scope.companyId = companyId;
    
    $http.get(urlBase+'compDetails/compProfile/'+$scope.companyId).then(function(data) {
		  $scope.compProfile = data.data;
		// get indicator list according to sector of the company
			$http.get(urlBase+'indicators/HF/'+$scope.compProfile.type).success(function(data) {
				  $scope.hfIndicators = data;
			    });
	    });
    
	// get company historical data
	$http.get(urlBase+'compDetails/hisf/'+$scope.companyId).success(function(data) {
		  $scope.compHistorical = data;
	    });
    
   //share company monthly returns

});