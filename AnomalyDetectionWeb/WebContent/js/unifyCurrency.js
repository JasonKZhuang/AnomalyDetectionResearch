/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var shareManagerModule = angular.module('shareManager', ['ngAnimate']);
shareManagerModule.controller('shareManagerController', function ($scope,$http) {
	
		var urlBase="http://localhost:8080/GlobalShares";
		$scope.hisfIndicators  = [{name: "Revenue(Mil)",code:"revenue", isPrecentage:false},{name: "Oper Income(Mil)",code:"operIncome", isPrecentage:false},
		                          {name: "Oper Margin(%)",code:"operMargin", isPrecentage:true},{name: "Net Income(Mil)",code:"netIncome", isPrecentage:false}]; 
		
		// initialize selected subSector and indicator
		$scope.selectedSector = {industryGroupName: "Education", industryGroupId:20532};
		$scope.selectedHFIndicator = {name: "Revenue(Mil)",code:"revenue", isPrecentage:false};
		
		
		
		
		
		 //********************Change Currency function **************
		$scope.currency = ["--","AUD","CNY","HKD","JPY","MYR","SGP","KOR","USD"];//"TWD","THA","FRA","DEU","GBR","CHE","CAD","BRA",
		$scope.selectedCurrency = "--";
		$scope.currencyRates = {};
		//change data value to US currency
		$scope.unifyToCurrency = function unifyToCurrency() {
			if($scope.selectedCurrency=='--'){ //reset to original value
				var newUrl=urlBase+'/hisf/'+$scope.selectedSector.industryGroupId+'/'+$scope.selectedHFIndicator.code;
		   	    //alert('new url:'+newUrl);
		   	    $http.get(newUrl).then(function(data) {
					  $scope.hisf = data.data;
				    });
			}
			else { 
				//get currency rates for the selected base
				var newCurrencyUrl = "http://api.fixer.io/latest?base=" + $scope.selectedCurrency;
				$http.get(newCurrencyUrl).then(function(data){
					$scope.currencyRates = data.data.rates;//alert(JSON.stringify($scope.currencyRates));
					//reset data to original ones
					var newUrl=urlBase+'/hisf/'+$scope.selectedSector.industryGroupId+'/'+$scope.selectedHFIndicator.code;
			   	    $http.get(newUrl).then(function(data) {
						  $scope.hisf = data.data;//alert(JSON.stringify($scope.hisf));
						  //go through each line to change currency
						  angular.forEach($scope.hisf, function (h) {
							  if(h.currencyId != $scope.selectedCurrency){
									if (h.currencyId == "AUD") {
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.AUD;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.AUD;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.AUD;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.AUD;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.AUD;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.AUD;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.AUD;
									 }
									 else if (h.currencyId == "CNY") {
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.CNY;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.CNY;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.CNY;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.CNY;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.CNY;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.CNY;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.CNY;
									 } 
									 else if (h.currencyId == "HKD"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.HKD;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.HKD;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.HKD;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.HKD;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.HKD;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.HKD;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.HKD;
									 }
									 else if (h.currencyId == "JPY"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.JPY;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.JPY;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.JPY;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.JPY;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.JPY;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.JPY;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.JPY;
									 }
									 else if (h.currencyId == "MYR"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.MYR;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.MYR;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.MYR;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.MYR;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.MYR;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.MYR;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.MYR;
									 }
									 else if (h.currencyId == "SGP"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.SGP;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.SGP;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.SGP;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.SGP;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.SGP;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.SGP;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.SGP;
									 }
									 else if (h.currencyId == "KOR"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.KOR;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.KOR;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.KOR;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.KOR;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.KOR;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.KOR;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.KOR;
									 }
									 else if (h.currencyId == "USD"){
										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.USD;
										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.USD;
										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.USD;
										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.USD;
										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.USD;
										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.USD;
										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.USD;
									 }
							    }
							});//end of forEach
			   	       });
				})}
		  };
	}
);
