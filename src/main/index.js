import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss';

import reportWebVitals from './reportWebVitals';
import Login from './js/Login';
import LoginGoogle from './js/LoginGoogle.jsx';
import RegisterNewUser from './js/RegisterNewUser.jsx';


ReactDOM.render(<Login />, document.getElementById('reactLogin'));
ReactDOM.render(<RegisterNewUser />, document.getElementById('reactRegisterNewUser'));

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();



