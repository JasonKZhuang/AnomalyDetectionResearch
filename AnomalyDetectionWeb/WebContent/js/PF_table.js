/*
 * AngularJs将数值传递给后台的方式是
 * 将要传递的对象Json化之后传递给后台，
 * 这点和Ajax比较类似，
 * 当然也是属于异步提交数据的方式
 */


var potofolioApp = angular.module('pfApp',['ngAnimate','ui.grid','ui.grid.edit']);

potofolioApp.controller('pfCtrl', function ($scope,$http) {
	
    
    //var urlBase="http://localhost:8080/GlobalShares";
	//$http.get(urlBase+'/sohp/user123').then(function(argData){
	//	  $scope.hisf = argData.data;
	//});
	var urlBase="http://localhost:8080/GlobalShares";
	var userId ="user123";
	var defaultTabName = "pf_page2";
	var items_page2;
	
	var indexSubTabOfPage1 = 1;
	var prefixSubTabOfPage1 = "pf_page1_1_";
	
	$scope.pf1_1_indicators = [
         {displayName:"Total Return",value:1},
         {displayName:"Inccome Value",value:2},
         {displayName:"Growth Value 1",value:3},
         {displayName:"Growth Value 2",value:4},
         {displayName:"Price Value",value:5},
         {displayName:"Risk Value 1",value:6},
         {displayName:"Risk Value 2",value:7}
	];
	
	$scope.switchTab = function switchTab(tabName)
	{
		$scope.secondLevelTabMenu = tabName;
		
		if (tabName == "pf_page1")
		{
			$scope.initPage1(tabName);
		}
		if (tabName == "pf_page2")
		{
			$scope.initPage2(tabName);
		}
		if (tabName == "pf_page3")
		{
			$scope.initPage3(tabName);
		}
	}
	
	$scope.switchSubTab = function switchSubTab(tabName,idx)
	{
		$scope.thirdLevelTabMenu = tabName;
		var newUrl = urlBase+'/sohp/' + userId + "/" + idx;
		
		alert(newUrl);
		
		$http({
			  method: 'GET',
			  url: newUrl
			}).then(function successCallback(response) {
			    // this callback will be called asynchronously
			    // when the response is available
				$scope.items1_1 = response.data;
			  }, function errorCallback(response) {
			    // called asynchronously if an error occurs
			    // or server returns response with an error status.
			  });
	}
	
	$scope.initPage1 = function initPage1(tabName)
	{
		//$scope.myFunction("test");
		var firstSubTabName = prefixSubTabOfPage1 + indexSubTabOfPage1.toString();
		
		$scope.switchSubTab(firstSubTabName,indexSubTabOfPage1);
		$scope.pf1_1_selectedIndicator = $scope.pf1_1_indicators[0];
	}
	
	$scope.initPage2 = function initPage2(tabName)
	{
		var newUrl = urlBase+'/pfaa/' + userId;
		//alert(tabName);
		
		$scope.pf2_1_savedPortfolio = [
		  {displayName:"Select Portfolio...",value:0},
		                               
		];
		$scope.pf2_1_selectedPortfolio = $scope.pf2_1_savedPortfolio[0];
		
		//
		$scope.pf2_1_savedPfBm = [
		  {displayName:"Click here to set portfolio benchmark",value:0},
		  		                               
		];
		$scope.pf2_1_selectedPfBm = $scope.pf2_1_savedPfBm[0];
		
		//
		$http({
			  method: 'GET',
			  url: newUrl
			}).then(function successCallback(response) {
				items_page2   = response.data;
				$scope.items2 = response.data;
				$scope.pfPage2_changeCell(0);
			  }, function errorCallback(response) {
				  
			  });
	}
	
	$scope.initPage3 = function initPage3(tabName)
	{
		$scope.gridOptions = {
		  columnDefs: [
		    {field: 'id', displayName: 'Id'},
	        {field: 'name', displayName: 'Name'},
	        {name: 'edit', displayName: 'Edit', cellTemplate: '<button id="editBtn" type="button" class="btn-small" ng-click="edit(row.entity)" >Edit</button> '}
	      ],
	      enableColumnMenus : false,
	      
	      data: $scope.myData,
	      
	    };
	}

	$scope.onChangeCheckbox = function onChangeCheckbox(cpId,checkFlag)
	{
		var newUrl = "";
		if (checkFlag == true) 
		{
			newUrl = urlBase+'/pf_ck_add/'+userId+ "/" + cpId;
			$http({
				  method: 'GET',
				  url: newUrl
				}).then(function successCallback(response) {
					
				  }, function errorCallback(response) {
					  
				  });
        }else 
        {
        	newUrl = urlBase+'/pf_ck_del/'+userId+ "/" + cpId;
			$http({
				  method: 'GET',
				  url: newUrl
				}).then(function successCallback(response) {
					
				  }, function errorCallback(response) {
					  
				  });
        }
		
	}
	
	$scope.pfPage2_changeCell = function pfPage2_changeCell(statusData)
	{
		//alert(items_page2);
		var totalAllocation = 0;
		var totalAllocationMinus = 0;
		var otherValues = new Array();
		for (i=0; i<11; i++)
		{
			otherValues[i] = 0.0;
		}
		var arrayItem = new Array();
		var itemValue = 0;
		
		angular.forEach(items_page2, function(value, key) 
		{
			itemValue = parseFloat(value.factorAllocation);
			arrayItem = value.categoryValue;
			
			for (j=0;j<arrayItem.length;j++)
			{
				if (arrayItem[j] != 0)
				{
					otherValues[j] = otherValues[j] + itemValue;
				}
			}
			
			totalAllocation = totalAllocation + itemValue;
		});
		totalAllocationMinus = 100 - totalAllocation;
		$scope.pf_cpwa_tt1 = "(" + totalAllocationMinus.toFixed(2) + ")";
		$scope.pf_cpwa_tt2 = totalAllocation.toFixed(2);
		
		var pf_cpwax="";
		for (k=0;k<otherValues.length;k++)
		{
			pf_cpwax = 'pf_cpwa' + k;
			$scope[pf_cpwax] = otherValues[k].toFixed(2);
		}
		
		if (statusData == 0)
		{
			//alert("page_init");
		}
		
		if (statusData == 1)
		{
			///alert("update cell value");
			$scope.update_session($scope.items2);	
		}
		
	}
	
	$scope.update_session = function update_session(dataList)
	{
		var newUrl = urlBase+'/pfaa_up_s/' + userId;
		//alert(newUrl);
		//
		$http({
			  method: 'POST',
			  url: 	newUrl,
			  data:	dataList
		}).then(function successCallback(response) {
				
			  }, function errorCallback(response) {
				  
			  });
		
		
	}
	
	$scope.changeOptimiserOption = function changeOptimiserOption(optionValue)
	{
		var pageName = prefixSubTabOfPage1 + optionValue.toString();
		
		$scope.switchSubTab(pageName,optionValue);
		
		$scope.pf1_1_selectedIndicator = $scope.pf1_1_indicators[optionValue - 1];
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//============test data ////////////////////
	$scope.myData =[
	     	       {id:1,name:'aaaa'},
	     	       {id:2,name:'bbb'},
	];
	
	$scope.myFunction = function myMyfunction(data)
	{
		alert(data);
	}
	
	//$scope.page1   = "My first page";
    //$scope.page1_1 = "the first page of My first page";
});


