/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var shareManagerModule = angular.module('shareManager', ['ngAnimate']);
shareManagerModule.controller('shareManagerController', function ($scope,$http) {
	
		var urlBase="http://localhost:8080/BCMG_Share";
		$scope.subSectors = [{name: "Insurance - Property & Casualty", code:10325},
		                     {name:"Retail - Defensive", code:20533},{name:"Utilities - Independent Power Producers", code:20743},
		                     {name: "Education", code:20532}];

		$scope.hisfIndicators  = [{name: "Revenue(Mil)",code:"revenue", isPrecentage:false},{name: "Oper Income(Mil)",code:"operIncome", isPrecentage:false},
		                          {name: "Oper Margin(%)",code:"operMargin", isPrecentage:true},{name: "Net Income(Mil)",code:"netIncome", isPrecentage:false}]; 
		
		$http.get("http://api.fixer.io/latest?base=USD").success(function(data){
			$scope.currencyRates = data.rates;
		});
		$scope.currency = [{name:"--", from:"--",rate:1},{name:"USD",from:"BGN",rate:1.6533}];
//		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
	
		$scope.sortBy = ''; // set the default sort type
		$scope.sortReverse = true;  // set the default sort order
		
		// initialize selected subSector and indicator
		$scope.selectedSector = {name: "Education", code:20532};
		$scope.selectedHFIndicator = {name: "Revenue(Mil)",code:"revenue", isPrecentage:false};
		$http.get(urlBase+'/hisf/20532/revenue').success(function(data) {
			  $scope.hisf = data;
			  $scope.isPrecentage = false;
		    });
		//get historicalFundamental data by sector and indicator
		$scope.changeSector = function changeSector() {
			var newUrl=urlBase+'/hisf/'+$scope.selectedSector.code+'/'+$scope.selectedHFIndicator.code;
//			angular.forEach($scope.hisfIndicators, function (hfIn) {
//				//alert("in list:"+ hfIn.code +", choosen:" + $scope.selectedHFIndicator);
//				if(hfIn.code == $scope.selectedHFIndicator)
					$scope.isPrecentage = $scope.selectedHFIndicator.isPrecentage;
//			});
	   	    //alert('new url:'+newUrl);
	   	    $http.get(newUrl).success(function(data) {
				  $scope.hisf = data;			    
				  });
		  };
		  
		//change data value to US currency
		$scope.unifyToCurrency = function unifyToCurrency() {
			if($scope.selectedCurrency=="--"){
				var newUrl=urlBase+'/hisf/'+$scope.selectedSector.code+'/'+$scope.selectedHFIndicator.code;
		   	    //alert('new url:'+newUrl);
		   	    $http.get(newUrl).success(function(data) {
					  $scope.hisf = data;
				    });
			}
			else if($scope.selectedCurrency=="USD"){
			angular.forEach($scope.hisf, function (h) {
				 if(h.currencyId == "AUD"){
					 h.y1Backdata=h.y1Backdata/$scope.currencyRates.AUD;
					 h.y2Backdata=h.y2Backdata/$scope.currencyRates.AUD;
					 h.y3Backdata=h.y3Backdata/$scope.currencyRates.AUD;
					 h.y4Backdata=h.y4Backdata/$scope.currencyRates.AUD;
					 h.y5Backdata=h.y5Backdata/$scope.currencyRates.AUD;
					 h.y6Backdata=h.y6Backdata/$scope.currencyRates.AUD;
					 h.y7Backdata=h.y7Backdata/$scope.currencyRates.AUD;
				 }
				 else if(h.currencyId == "JPY"){
					 h.y1Backdata=h.y1Backdata/$scope.currencyRates.JPY;
					 h.y2Backdata=h.y2Backdata/$scope.currencyRates.JPY;
					 h.y3Backdata=h.y3Backdata/$scope.currencyRates.JPY;
					 h.y4Backdata=h.y4Backdata/$scope.currencyRates.JPY;
					 h.y5Backdata=h.y5Backdata/$scope.currencyRates.JPY;
					 h.y6Backdata=h.y6Backdata/$scope.currencyRates.JPY;
					 h.y7Backdata=h.y7Backdata/$scope.currencyRates.JPY;
				 }
				 else if(h.currencyId == "BRL"){
					 h.y1Backdata=h.y1Backdata/$scope.currencyRates.BRL;
					 h.y2Backdata=h.y2Backdata/$scope.currencyRates.BRL;
					 h.y3Backdata=h.y3Backdata/$scope.currencyRates.BRL;
					 h.y4Backdata=h.y4Backdata/$scope.currencyRates.BRL;
					 h.y5Backdata=h.y5Backdata/$scope.currencyRates.BRL;
					 h.y6Backdata=h.y6Backdata/$scope.currencyRates.BRL;
					 h.y7Backdata=h.y7Backdata/$scope.currencyRates.BRL;
				 }
				 else if(h.currencyId == "GBP"){
					 h.y1Backdata=h.y1Backdata/$scope.currencyRates.GBP;
					 h.y2Backdata=h.y2Backdata/$scope.currencyRates.GBP;
					 h.y3Backdata=h.y3Backdata/$scope.currencyRates.GBP;
					 h.y4Backdata=h.y4Backdata/$scope.currencyRates.GBP;
					 h.y5Backdata=h.y5Backdata/$scope.currencyRates.GBP;
					 h.y6Backdata=h.y6Backdata/$scope.currencyRates.GBP;
					 h.y7Backdata=h.y7Backdata/$scope.currencyRates.GBP;
				 }
				 else if(h.currencyId == "MXN"){
					 h.y1Backdata=h.y1Backdata/$scope.currencyRates.MXN;
					 h.y2Backdata=h.y2Backdata/$scope.currencyRates.MXN;
					 h.y3Backdata=h.y3Backdata/$scope.currencyRates.MXN;
					 h.y4Backdata=h.y4Backdata/$scope.currencyRates.MXN;
					 h.y5Backdata=h.y5Backdata/$scope.currencyRates.MXN;
					 h.y6Backdata=h.y6Backdata/$scope.currencyRates.MXN;
					 h.y7Backdata=h.y7Backdata/$scope.currencyRates.MXN;
				 }
			}) }
		  };

		// create the list of share companies 
			$scope.share = [
			{ name: 'Ambev SA', sector: '0C0000AO4', currency:'BRU', earn0: 1, dividend0: 3, ratio0:3, earn1: 1, dividend1: 2, ratio1:3, earn2: 1, dividend2: 2, ratio2:3, close:9 },
			{ name: 'Bnheuser-Busch Inbev SA', sector: '0C0000AO5', currency:'EUR', earn0: 4, dividend0: 9, ratio0:6, earn1: 5, dividend1: 6, ratio1:7, earn2: 8, dividend2: 5, ratio2:6, close:12  },
			{ name: 'Carlsberg A/S', sector: '0C0000AO1', currency:'DKK', earn0: 7, dividend0: 7, ratio0:8, earn1: 9, dividend1: 0, ratio1:12, earn2: 3, dividend2: 4, ratio2:7, close:16}
			];
			angular.forEach($scope.share, function (s) {
				  s.ratio0 = parseFloat(s.ratio0);
			});
			 
	}
);
