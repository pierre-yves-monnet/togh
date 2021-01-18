// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------
import React from 'react';


class Login extends React.Component {
	constructor() {
		super();
		// console.log("Login.constructor");

		this.state = { email: 'pierre-yves.monnet@laposte.net', password: 'tog', badConnection: false, isLog: false }

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.connect = this.connect.bind(this);

	}


	render() {
		
		let messageConnection ="";

		if (this.state.badConnection) {
			messageConnection = messageConnection.concat("<div style='color:red'> Bad connection</div>");
		}
		console.log("Login.render: badConnection=" + this.state.badConnection+" / message=["+messageConnection+"]");

		return (
			<div className="App">
				Email address<br />
				<input type="string" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} class="toghinput" ></input><p />
				Password<br />
				<input type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })} class="toghinput" ></input><p />
				<a class="btn btn-info" onClick={this.connect}>Connection</a><p />
				<div dangerouslySetInnerHTML={{ __html: messageConnection}}></div>
			</div>
		)
	}



	toString() {
		return "email=[" + this.state.email+"],password=["+this.state.password+"] Connection=["+this.state.badConnection+"] isLog["+this.state.isLog+"]";
	}
	
	connect() {
		console.log("Login.connect: ClickConnect email=" + this.toString());
		console.log("Login.connect state="+JSON.stringify(this.state));
		this.setState( {badConnection: false});
		
		const requestOptions = {
	        method: 'POST',
	        headers: { 'Content-Type': 'application/json' },
	        body: JSON.stringify({ email: this.state.email, password: this.state.password })
	    };
    	fetch('login?', requestOptions)
			.then(response => response.json())
        	.then( data => this.connectCallback( data ));
	}
	
	connectCallback( httpPayload ) {
		console.log("Login.connectCallback: connectStatus = "+JSON.stringify(httpPayload));
 		if (httpPayload.isConnected) {
			console.log("Loging.connectStatus : redirect then");
			window.location.href = "homeTogh.html";
		}
		else {
			this.setState( {badConnection: ! httpPayload.isConnected, isLog: httpPayload.isConnected});
		}
	} // end connectStatus
}
export default Login;

