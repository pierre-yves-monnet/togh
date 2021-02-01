// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput, Button } from 'carbon-components-react';

// https://www.npmjs.com/package/react-google-login
// https://dev.to/sivaneshs/add-google-login-to-your-react-apps-in-10-mins-4del
import { GoogleLogin, GoogleLogout } from 'react-google-login';


import FactoryService from './service/FactoryService';

var staticLogin=null;

class Login extends React.Component {
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.authCallback()
	constructor( props ) {
		super();
		// console.log("Login.constructor");

		this.state = { email: 'pierre-yves.monnet@laposte.net', password: 'tog', badConnection: false }

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.loginConnect = this.loginConnect.bind(this);
		this.directLogout = this.directLogout.bind( this );
		staticLogin = this;
	}

	// ------------------------------ Render
	render() {
		
		let messageConnection ="";
		
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();

		console.log("Login.render: isConnected="+authService.isConnected());
		if (authService.isConnected()) {
			if (authService.getMethodConnection() ==='DIRECT')
				return (
					<button onClick={() =>this.directLogout()} class="btn btn-warning">Logout</button>
					)
			return ( <GoogleLogout 
					    clientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com"
						buttonText="Logout"
						onLogoutSuccess={this.logoutGoogle}/>
			 		 );
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
				<br/>
				<button onClick={this.loginConnect} class="btn btn-info">Connection</button><p/>
				
				<div dangerouslySetInnerHTML={{ __html: messageConnection}}></div>
				
				<br/>
				
				<GoogleLogin
				    clientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com"
				    buttonText="Login"
				    onSuccess={this.loginGoogle}
				    
				    cookiePolicy={'single_host_origin'}
				  />,

				
								
			</div>
		)
		// <div class="g-signin2" data-onsuccess="onSignIn"></div>
	}
	// ------------------------------


	toString() {
		return "email=[" + this.state.email+"],password=["+this.state.password+"] Connection=["+this.state.badConnection+"]";
	}
	
	
	// -----------------------------------------------
	// DIRECT CONNECTION	
	// -----------------------------------------------
	
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
			// call the frame event to refresh all
			this.props.authCallback( true );
		} else {
			this.setState({ badConnection: true });
		}
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
	
	
	
	// -----------------------------------------------
	// GOOGLE CONNECTION	
	// -----------------------------------------------
	
	// when Google call me back, the this is null... it call the method outside of any object.
	// so, let's save this to "staticLogin" and use it.
	loginGoogle( googleUser) {
		// console.log("Login.loginGoogle googleInformation="+JSON.stringify(googleUser));
		console.log("Login.loginGoogle this="+this+" StaticLogin="+staticLogin);
		FactoryService.getInstance().getAuthService().loginGoogle( googleUser, staticLogin, staticLogin.loginGoogleCallBack );
	};
	loginGoogleCallBack( httpPayload ) {
		console.log("Login.loginGoogleCallBack "+JSON.stringify(httpPayload));
		if (httpPayload.isConnected) {
			this.setState({ isConnected: true });
		} else {
			this.setState({ badConnection: !httpPayload.isConnected, isLog: httpPayload.isConnected });
		}
		// call the frame event to refresh all
		this.props.authCallback( true );	
		
	}
	
	// when Google call me back, the this is null... it call the method outside of any object.
	// so, let's save this to "staticLogin" and use it.
	logoutGoogle() {
		console.log("Login.loginGoogle this="+this+" StaticLogin="+staticLogin);

		FactoryService.getInstance().getAuthService().logout(staticLogin, httpPayload => {
					staticLogin.props.authCallback( false );
			});

	}
	

}
export default Login;

