// -----------------------------------------------------------
//
// Chart
//
// Display Chart
//
// -----------------------------------------------------------
import React from 'react';
import { FormattedMessage } from "react-intl";

import { Doughnut } from 'react-chartjs-2';

  const backgroundColor= [
              'rgba(255, 99, 132, 0.2)',
              'rgba(54, 162, 235, 0.2)',
              'rgba(255, 206, 86, 0.2)',
              'rgba(75, 192, 192, 0.2)',
              'rgba(153, 102, 255, 0.2)',
              'rgba(255, 159, 64, 0.2)',
            ];
    const borderColor= [
              'rgba(255, 99, 132, 1)',
              'rgba(54, 162, 235, 1)',
              'rgba(255, 206, 86, 1)',
              'rgba(75, 192, 192, 1)',
              'rgba(153, 102, 255, 1)',
              'rgba(255, 159, 64, 1)',
            ];
class Chart extends React.Component {



	/** Caller must declare 	this.props.changePasswordCallback(isCorrect, password ) */
	constructor( props ) {
		super();
		this.state={
		    type:       props.type,
		    title:      props.title,
		    labels:     props.labels,
		    data:       props.data,
		    dataMap:    props.dataMap};

        console.log("Chart.constructor: dataMap="+JSON.stringify(props.dataMap))
        this.getLabelsDataFromMap       = this.getLabelsDataFromMap.bind(this);
	}

	// Calculate the state to display
	componentDidUpdate(prevProps) {
	    console.log("Chart.componentDidUpdate: prevProps="+JSON.stringify(prevProps))
        if(prevProps.dataMap !== this.props.dataMap) {
            this.setState({dataMap: this.props.dataMap});
        }
        if(prevProps.data !== this.props.data) {
            this.setState({data: this.props.data});
        }
        if(prevProps.labels !== this.props.labels) {
            this.setState({labels: this.props.labels});
        }
    }

	//----------------------------------- Render
	render() {
        console.log("Chart.render: type="+this.state.type+" state="+JSON.stringify(this.state))
        const chartOptions= { 'plugins': { 'legend': { 'display': false}}};

		if (this.state.type==='Doughnut') {
		    // https://github.com/reactchartjs/react-chartjs-2/blob/master/example/src/charts/Doughnut.js
            const dataChart = {};

            dataChart.datasets= [];
            dataChart.datasets.label= this.state.title;

            let dataset = {};
            dataChart.datasets.push( dataset );
            dataset.backgroundColor = backgroundColor;
            dataset.borderColor     = borderColor;
            dataset.borderWidth     = 1;
            if (this.state.dataMap) {
                const resultTransformed = this.getLabelsDataFromMap( this.state.dataMap );
                dataChart.labels    = resultTransformed.labels;
                dataset.data      = resultTransformed.data;
            } else if (this.state.labels) {
                dataChart.labels    = this.state.labels;
                dataset.data      = this.state.data;
            }
            console.log("Chart: dataChart="+JSON.stringify(dataChart));
                /* target:

  labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
  datasets: [
    {
      label: '# of Votes',
      data: [12, 19, 3, 5, 2, 3],
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(255, 159, 64, 0.2)',
      ],
      borderColor: [
        'rgba(255, 99, 132, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)',
      ],
      borderWidth: 1,
    },
  ],
}; */
            return (
            <div style={{padding: "10 ps 20px", borderRadius: "10px", border: "3px solid rgb(222, 203, 228)"}}>
                <div class="h6" style={{textAlign: "center"}}>{this.state.title}</div>
                <Doughnut data={dataChart} options={chartOptions}/>
            </div>)
		}

        return (<div/>);

	}

     getLabelsDataFromMap( dataMap ) {

        const result={ 'labels': [], 'data':[]};
        for (const [key, value] of Object.entries(dataMap)) {
            result.labels.push(key)
            result.data.push(value)
        }
        console.log("Chart:getLabelsDataFromMap : "+result+" from "+dataMap);
        return result;
     }
}

export default Chart;