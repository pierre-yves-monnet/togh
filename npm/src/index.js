import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss';

import reportWebVitals from './reportWebVitals';


import BodyTogh from './BodyTogh.jsx';

import FactoryService from './service/FactoryService'


import { IntlProvider } from 'react-intl';
import {IntlShape} from 'react-intl';



import fr from "./lang/fr.json";
import en from "./lang/en.json";


var factoryService = new FactoryService();
FactoryService.setInstance( factoryService );

// see https://lokalise.com/blog/react-i18n-intl/
//default language
let language  =navigator.language.split(/[-_]/)[0];

if (language ==="en") {
	language  = "en";
} else if (language === "fr") {
	language  = "fr";
} else {
	language  = "fr";
}
const messages = {
	    'fr': fr,
	    'en': en
	};



console.log("index.js local=["+language+"]")
ReactDOM.render(
		<IntlProvider locale={language}  messages={messages[ language  ]} >
			<BodyTogh />
		</IntlProvider>
		, document.getElementById('reactBodyTogh'));





// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();



