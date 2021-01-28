// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput } from 'carbon-components-react';
import FactoryService from './service/FactoryService';


class Login extends React.Component {
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.authCallback()
	constructor( props ) {
		super();
		// console.log("Login.constructor");

		this.state = { email: 'pierre-yves.monnet@laposte.net', password: 'tog', badConnection: false, isLog: false}

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.loginConnect = this.loginConnect.bind(this);
		this.directLogout = this.directLogout.bind( this );
		
	}

	// ------------------------------ Render
	render() {
		
		let messageConnection ="";
		
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();

		if (authService.isConnected()) {
			if (authService.getMethodConnection() ==='DIRECT')
				return (
					<button onClick={() =>this.directLogout()} class="btn btn-warning">Logout</button>
					)
			return ( <button onClick="signOut();">Google Sign out</button> );
		}

		// ---- not connected, give the different method
		if (this.state.badConnection) {
			messageConnection = messageConnection.concat("<div style='color:red'> Bad connection</div>");
		}
		console.log("Login.render: badConnection=" + this.state.badConnection+" / message=["+messageConnection+"]");

		return (
			<div className="App">
				
				 <TextInput labelText="Email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><p />
				
				<TextInput labelText="Password" type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })} ></TextInput><p />
				<a class="btn btn-info" onClick={this.loginConnect} href="/#">Connection</a><p />
				<div dangerouslySetInnerHTML={{ __html: messageConnection}}></div>
				<p/>
				<div class="g-signin2" data-onsuccess="onSignIn"></div>
								
			</div>
		)
	}
	// ------------------------------


	toString() {
		return "email=[" + this.state.email+"],password=["+this.state.password+"] Connection=["+this.state.badConnection+"] isLog["+this.state.isLog+"]";
	}
	
	
	// ----------------------------- Connect
	loginConnect() {
		console.log("Login.connect state="+JSON.stringify(this.state));
		this.setState( {badConnection: false});
		
		var param = { email: this.state.email, password: this.state.password };
		
		FactoryService.getInstance().getAuthService().login( 'DIRECT', param, this, this.loginConnectCallback );
	}
	
	loginConnectCallback( httpPayload ) {
		console.log("Login.directConnectCallback Result="+JSON.stringify(httpPayload));
		if (httpPayload.isConnected) {
			this.setState({ isConnected: true })
		} else {
			this.setState({ badConnection: !httpPayload.isConnected, isLog: httpPayload.isConnected });
		}
		// call the frame event to refresh all
		this.props.authCallback( true );
	}	
	
	
	
	// ---------------------------- Logout
	logoutCallback( httpPayload ) {
		console.log("Login.logoutCallback Result="+JSON.stringify(httpPayload));
		// call the frame event to refresh all
		this.props.authCallback( false );
	}	

	directLogout() {
		console.log("Login.logout");
		FactoryService.getInstance().getAuthService().logout(this, this.logoutCallback );
	}
		

		
	

}
export default Login;

