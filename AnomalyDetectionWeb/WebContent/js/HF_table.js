/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var shareManagerModule = angular.module('shareManager', ['ngAnimate']);

shareManagerModule.controller('shareManagerController', function ($scope, $http) {
	
		var today = new Date();
	    $scope.currentYear = today.getFullYear();
		var urlBase="http://localhost:8080/GlobalShares";

		$scope.showERs = false;
		$scope.showERsText = 'Efficiency Ratio';
		$scope.toggle = function(){
			$scope.showERs = !$scope.showERs;
			$scope.showERsText = $scope.showERs ? 'Hide ER' : 'Show ER';
		};
		
		$http.get(urlBase+'/sectors').success(function(data) {
			  $scope.subSectors = data;
		    });
		
		$scope.sortBy = 'companyName'; // set the default sort type
		$scope.sortReverse = false;  // set the default sort order
		
		// initialize selected subSector and indicator
		$scope.selectedSector      = {industryGroupName: "Consumer Defensive", industryGroupId:20529,type:'O'};
		$scope.selectedHFIndicator = {displayedName: "Dividend Yield(%)",columnName:"dividendYield", isPrec:1};
		$scope.selectedHEIndicator = {displayedName: "Trailing Total Return",columnName:"trailingTotalReturn"};
		$scope.selectedFEIndicator = {displayedName: "Income Value",columnName:"incomeValue"};
		
		$http.get(urlBase+'/hisf/20529/totalReturn').then(function(argData) {
			  $scope.hisf = argData.data;
			  $scope.getIndicators();
		});
		
		$http.get(urlBase+'/hisfMean/20529/totalReturn').then(function(argData) {
			$scope.meanValue = argData.data;
		});
		
		$scope.getIndicators = function getIndicators()
		{
			//1  get HF indicator list according to sector and radio button
			$http.get(urlBase+'/indicators/HF/'+$scope.selectedSector.type).success(function(data) {
				  $scope.hisFIndicators = data;
				  $scope.selectedHFIndicator = data[0];
				});
			// 2  get HE indicator list according to sector and radio button
			$http.get(urlBase+'/indicators/HE/'+$scope.selectedSector.type).success(function(data) {
				  $scope.hisEIndicators = data;
				  $scope.selectedHEIndicator = data[0];
				});
			//3 get FE indicator list according to sector and radio button
			$http.get(urlBase+'/indicators/FE').success(function(data) {
				  $scope.forEvIndicators = data;
				  $scope.selectedFEIndicator =  data[0]; 
				 
				});
			
			// 4 get AS indicator list according to sector and radio button
			$http.get(urlBase+'/indicators/AS').success(function(data) {
				$scope.attSyIndicators = data;
				$scope.selectedASIndicator =  data[0]; 
			});
		}
		
		//get historicalFundamental data by sector and indicator
		$scope.changeSector = function changeSector(data1,data2)  
		{
			//alert(data1);
			//alert(data2);
			
			//get the first group indicators list
		    $http.get(urlBase+'/indicators/HF/'+$scope.selectedSector.type).success(function(data) {
				  $scope.hisFIndicators = data;
			});
			
		    //get the second group indicators list
		    $http.get(urlBase+'/indicators/HE/'+$scope.selectedSector.type).success(function(data) {
			  $scope.hisEIndicators = data;
			});
			
		    //get the third group indicators list
		    $http.get(urlBase+'/indicators/FE').success(function(data) {
			  $scope.forEvIndicators = data;
			});
			
		    //get the forth group indicators list
		    $http.get(urlBase+'/indicators/AS').success(function(data) {
		      $scope.attSyIndicators = data;
			});
			

			var newUrl="";
			$scope.indicator = data1;
			var arg1 = $scope.selectedSector.industryGroupId;
			var arg2 = $scope.indicator;
			var arg3 = "";
			
			switch(arg2) 
			{
			    case 'hisFu':
			    	arg3 = $scope.selectedHFIndicator.columnName;
			    	$http.get(urlBase+'/indicators/HF/'+$scope.selectedSector.type).success(function(data) {
						  $scope.hisFIndicators = data;
					});
			    	 
			    	newUrl = urlBase+'/hisf/'+arg1+'/'+arg3;
			    	$scope.isPrec = $scope.selectedHFIndicator.isPrec;			   	    
					$http.get(newUrl).then(function(data) {
						  $scope.hisf = data.data;	
						  $scope.selectedCurrency = "";		    
			   	    });
			   	    
			   	    newUrl = urlBase+'/hisfMean/'+arg1+'/'+arg3;
			    	$http.get(newUrl).then(function(argData) {
			    		$scope.meanValue = argData.data;
			   	    });
			    	
			    	break;
			    case 'hisEv':
			    	arg3 = $scope.selectedHEIndicator.columnName;
			    	//alert(arg3)
			    	if (arg3.includes("summary"))
			    	{
			    		newUrl = urlBase + '/hisEvSummary/' + arg1 + '/' + arg3;
				    	$scope.isPrec = $scope.selectedHEIndicator.isPrec;//if it is percentage
				    	$http.get(newUrl).then(function(argData) {
				    		$scope.hisEv = argData.data;
				   	    });
			    	}else
			    	{
				    	newUrl = urlBase + '/hisEv/' + arg1 + '/' + arg3;
				    	$scope.isPrec = $scope.selectedHEIndicator.isPrec;//if it is percentage
				    	$http.get(newUrl).then(function(argData) {
				    		$scope.hisEv = argData.data;
				   	    });
	
				    	newUrl = urlBase + '/hisEvMean/' + arg1 + '/' + arg3;
				    	$http.get(newUrl).then(function(argData) {
				    		$scope.meanValue = argData.data;
				   	    });
			    	}
			        break;
			    case 'forEv':
			    	arg3 = $scope.selectedFEIndicator.columnName;
			    	//alert(arg3);
			    	$scope.isPrec = $scope.selectedFEIndicator.isPrec;
			    	if (arg3 == "ER_NearTermPerformance")
			    	{
			    		newUrl = urlBase + '/foveTR/' + arg1 + '/' + arg3;
			    		$http.get(newUrl).then(function(data) {
							$scope.items = data.data;	
				   	    });
			    	}else if (arg3 == "ER_RiskMeasures")
			    	{
			    		newUrl = urlBase + '/foveRM/' + arg1 + '/' + arg3;
			    		$http.get(newUrl).then(function(data) {
							$scope.items = data.data;	
				   	    });
			    	}else if (arg3 == "ER_RelativeRisk")
			    	{
			    		newUrl = urlBase + '/foveRR/' + arg1 + '/' + arg3;
			    		$http.get(newUrl).then(function(data) {
							$scope.items = data.data;	
				   	    });
			    	}else if (arg3 == "ER_ForwardEvaluationSummary")
			    	{
			    		newUrl = urlBase + '/foveSum/' + arg1 + '/' + arg3;
			    		$http.get(newUrl).then(function(data) {
							$scope.items = data.data;	
				   	    });
			    	}else{
			    		newUrl = urlBase + '/fove/' + arg1 + '/' + arg3;
			    		$http.get(newUrl).then(function(data) {
							$scope.forwardsData = data.data;	
				   	    });
			    		
			    		newUrl = urlBase + '/hisFoveMean/' + arg1 + '/' + arg3;
				    	$http.get(newUrl).then(function(argData) {
				    		$scope.meanValue = argData.data;
				   	    });
			    	}
			        break;
			    case 'attSy':
			    	arg3 = $scope.selectedASIndicator.columnName;
			    	$scope.isPrec = $scope.selectedASIndicator.isPrec;
			    	if (arg3 == "ER_HistoricalSummary")
			    	{
			    		newUrl = urlBase + '/asHisSum/' + arg1;
			    	}else if (arg3 == "ER_ForwardSummary")
			    	{
			    		newUrl = urlBase + '/asForSum/' + arg1;
			    	}else if (arg3 == "ER_CombinedSummary")
			    	{
			    		newUrl = urlBase + '/asCmbSum/' + arg1;
			    	}else if (arg3 == "TQ_RiskMeasuresSummary")
			    	{
			    		newUrl = urlBase + '/asTqRmSum/' + arg1;
			    	}else if (arg3 == "TQ_RelativeRiskSummary")
			    	{
			    		newUrl = urlBase + '/asTqRrmSum/' + arg1;
			    	}else if (arg3 == "TQ_HistoricalSummary")
			    	{
			    		newUrl = urlBase + '/asTqHisSum/' + arg1;
			    	}else if (arg3 == "TQ_ForwardSummary")
			    	{
			    		newUrl = urlBase + '/asTqForSum/' + arg1;
			    	}else if (arg3 == "TQ_CombineSummary")
			    	{
			    		newUrl = urlBase + '/asTqCmbSum/' + arg1;
			    	}
			    	//alert(newUrl);
			    	$http.get(newUrl).then(function(data) {
						$scope.items = data.data;	
			   	    });
			    	
			        break;
			    default:
			        arg3 ="";
			}
		 }
		
		$scope.onChangeCheckbox = function onChangeCheckbox(cpId,checkFlag)
		{
			var userId = "user123";
			//alert(cpId)
			if (checkFlag == true) 
			{
				$http.get(urlBase+'/pf_ck_add/'+userId+ "/" + cpId).success(function(backData){
					//alert(backData)
				});
            }else 
            {
            	$http.get(urlBase+'/pf_ck_del/'+userId+ "/" + cpId).success(function(backData){
					//alert(backData)
				});
            }
			
		}
		
		

	    //********************Change Currency function **************
//		$scope.currency = ["--","AUD","CNY","HKD","JPY","MYR","CAD","USD"];//"SGP","TWD","THA","KOR","FRA","DEU","GBR","CHE","BRA",
//		$scope.selectedCurrency = "--";
//		$scope.currencyRates = {};
//		$scope.benchmarkCurrency = "(USD)";
//		//change data value to US currency
//		$scope.unifyToCurrency = function unifyToCurrency() {
//			if($scope.selectedCurrency=='--'){ //reset to original value
//				var newUrl=urlBase+'/hisf/'+$scope.selectedSector.industryGroupId+'/'+$scope.selectedHFIndicator.columnName;
//		   	    //alert('new url:'+newUrl);
//		   	    $http.get(newUrl).then(function(data) {
//					  $scope.hisf = data.data;
//				    });
//			}
//			else {
//				$scope.curStyle = {"font-size" : "10px" };
//				//get currency rates for the selected base
//				var newCurrencyUrl = "http://api.fixer.io/latest?base=" + $scope.selectedCurrency;
//				$http.get(newCurrencyUrl).then(function(data){
//					$scope.currencyRates = data.data.rates;//alert(JSON.stringify($scope.currencyRates));
//
//					//change benchmark values to selected currency
//					$scope.benchmarkCurrency = "("+ $scope.selectedCurrency +")";
//					
//					
//					//change 'market' values to selected currency
//					var newUrl=urlBase+'/hisf/'+$scope.selectedSector.industryGroupId+'/'+$scope.selectedHFIndicator.columnName;//reset data to original ones(currency)
//			   	    $http.get(newUrl).then(function(data) {
//						  $scope.hisf = data.data;//alert(JSON.stringify($scope.hisf));
//						  //go through each line to change currency
//						  angular.forEach($scope.hisf, function (h) {
//							  if(h.currencyId != $scope.selectedCurrency){
//									if (h.currencyId == "AUD") {
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.AUD;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.AUD;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.AUD;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.AUD;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.AUD;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.AUD;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.AUD;
//										 h.currencyId = "AUD>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "CNY") {
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.CNY;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.CNY;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.CNY;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.CNY;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.CNY;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.CNY;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.CNY;
//										 h.currencyId = "CNY>"+$scope.selectedCurrency;
//									 } 
//									 else if (h.currencyId == "HKD"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.HKD;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.HKD;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.HKD;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.HKD;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.HKD;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.HKD;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.HKD;
//										 h.currencyId = "HKD>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "JPY"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.JPY;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.JPY;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.JPY;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.JPY;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.JPY;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.JPY;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.JPY;
//										 h.currencyId = "JPY>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "MYR"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.MYR;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.MYR;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.MYR;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.MYR;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.MYR;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.MYR;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.MYR;
//										 h.currencyId = "MYR>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "SGP"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.SGP;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.SGP;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.SGP;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.SGP;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.SGP;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.SGP;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.SGP;
//										 h.currencyId = "SGP>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "KRW"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.KRW;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.KRW;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.KRW;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.KRW;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.KRW;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.KRW;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.KRW;
//										 h.currencyId = "KRW>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "PHP"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.PHP;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.PHP;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.PHP;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.PHP;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.PHP;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.PHP;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.PHP;
//										 h.currencyId = "PHP>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "GBP"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.GBP;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.GBP;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.GBP;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.GBP;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.GBP;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.GBP;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.GBP;
//										 h.currencyId = "GBP>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "MXN"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.MXN;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.MXN;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.MXN;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.MXN;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.MXN;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.MXN;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.MXN;
//										 h.currencyId = "MXN>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "KOR"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.KOR;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.KOR;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.KOR;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.KOR;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.KOR;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.KOR;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.KOR;
//										 h.currencyId = "KOR>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "CAD"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.CAD;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.CAD;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.CAD;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.CAD;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.CAD;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.CAD;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.CAD;
//										 h.currencyId = "CAD>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "EUR"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.EUR;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.EUR;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.EUR;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.EUR;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.EUR;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.EUR;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.EUR;
//										 h.currencyId = "EUR>"+$scope.selectedCurrency;
//									 }
//									 else if (h.currencyId == "USD"){
//										 h.y1Backdata=h.y1Backdata/$scope.currencyRates.USD;
//										 h.y2Backdata=h.y2Backdata/$scope.currencyRates.USD;
//										 h.y3Backdata=h.y3Backdata/$scope.currencyRates.USD;
//										 h.y4Backdata=h.y4Backdata/$scope.currencyRates.USD;
//										 h.y5Backdata=h.y5Backdata/$scope.currencyRates.USD;
//										 h.y6Backdata=h.y6Backdata/$scope.currencyRates.USD;
//										 h.y7Backdata=h.y7Backdata/$scope.currencyRates.USD;
//										 h.currencyId = "USD>"+$scope.selectedCurrency;
//									 }
//							    }
//							});//end of forEach
//			   	       });
//				})}
//		  };

			
//			angular.forEach($scope.share, function (s) {
//				  s.ratio0 = parseFloat(s.ratio0);
//			});
			 
	
	}
);


function onChangeCheckbox (checkbox,data) 
{
    if (checkbox.checked) 
    {
        alert ("The check box is checked.");
    }
    else 
    {
        alert ("The check box is not checked.");
    }
    alert(data);
}
