import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss';

import reportWebVitals from './reportWebVitals';


import BodyTogh from './BodyTogh.jsx';
import Banner from './Banner.jsx';

import FactoryService from './service/FactoryService'

var factoryService = new FactoryService();
FactoryService.setInstance( factoryService );


ReactDOM.render(<Banner />, document.getElementById('reactBanner'));
ReactDOM.render(<BodyTogh />, document.getElementById('reactBodyTogh'));





// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();



