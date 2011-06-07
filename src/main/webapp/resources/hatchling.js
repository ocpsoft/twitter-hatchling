/**
 * Operations for the Hatchling Main Chart Page 
 */
// Load the Visualization API and the piechart package.

if(!window["hatchling"]) {
	var hatchling = {};
}

hatchling.setupChart = function setupChart()
{
	chart = new google.visualization.LineChart(document.getElementById('line_chart'));
	var t = setTimeout("getData();", 500);
};

hatchling.init = function init() {

	if (!window["chart"]) {
		var chart = {};
	}
	
	if (!window["busystatus"]) {
		var busystatus = {};
	}
	 
	busystatus.onStatusChange = function onStatusChange(data) {
		var status = data.status;
	 
		if (status === "begin") { // turn on busy indicator
			document.body.style.cursor = 'wait';
		} else { // turn off busy indicator, on either "complete" or "success"
			document.body.style.cursor = 'auto';
		}
	};
	 
	jsf.ajax.addOnEvent(busystatus.onStatusChange);
	
	google.load('visualization', '1', {'packages':['corechart']});

	// Set a callback to run when the Google Visualization API is loaded.
	google.setOnLoadCallback(hatchling.setupChart);
}; 

hatchling.drawChart = function drawChart(event) {
	// Everything is loaded. Lets draw our chart...

	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Date');
	data.addColumn('number', event.data.left);
	data.addColumn('number', event.data.right);

	data.addRows(event.data.rows);
	
	chart.draw(data, {
		curveType: 'none',
		legend: 'top',
        vAxis: {
            minValue: 0,
            baseline: 0
        }, 
        hAxis: {
            showTextEvery: event.data.showTextEvery,
            textStyle: {
                color: 'grey',
                fontSize: 8
            },
        	textPosition: 'in'
        },
		pointSize: 3,
		lineWidth: 3,
		colors: ["#4A91C3", "#F70"],
		width: 800, 
		height: 240, 
		is3D: true, 
		title: null
	});  

};
