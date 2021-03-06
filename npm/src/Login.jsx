// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput,  Loading } from 'carbon-components-react';
import { injectIntl, FormattedMessage } from "react-intl";

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

		this.state = { email: 'pierre-yves.monnet@laposte.net', password: 'tog', 
			badConnection: false,
			messageConnection:'',
			inprogress: false }

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.loginConnect = this.loginConnect.bind(this);
		this.directLogout = this.directLogout.bind( this );
		staticLogin = this;
	}

	// ------------------------------ Render
	render() {
		
		let messageConnectionHtml ="";
		
		var factory = FactoryService.getInstance();
		var authService = factory.getAuthService();


		console.log("Login.render: isConnected="+authService.isConnected()+" badConnection="+this.state.badConnection+" / message=["+this.state.messageConnection+"]");
		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="Active loading indicator" withOverlay={true}
    						/>);
		
		if (authService.isConnected()) {
			if (authService.getMethodConnection() ==='DIRECT')
				return (
					<button onClick={() =>this.directLogout()} class="btn btn-warning"><FormattedMessage id="Login.logout" defaultMessage="Logout" /></button>
					)
			return ( <GoogleLogout 
					    clientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com"
						buttonText={<FormattedMessage id="Login.googlelogout" defaultMessage="Login"/>}
						onLogoutSuccess={this.logoutGoogle}/>
			 		 );
		}

		// ---- not connected, give the different method
		if (this.state.badConnection) {
			messageConnectionHtml = (<div style={{color: "red"}}>{this.state.messageConnection}</div>);
		}

		return (
			<div className="App">
				 {inprogresshtml}
				 <TextInput labelText={<FormattedMessage id="Login.email" defaultMessage="Email"/>} value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><p />
				
				<TextInput labelText={<FormattedMessage id="Login.password" defaultMessage="Password"/>} type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })} ></TextInput><p />
				<br/>
				<table >
				<tr>
				<td style={{"paddingRight" : "40px", "paddingLeft" : "150px"}}>
					<button onClick={this.loginConnect} class="btn btn-info"><FormattedMessage id="Login.connection" defaultMessage="Connection"/></button><br/><br/>
					{messageConnectionHtml}
				</td>
				<td>
				<GoogleLogin
				    clientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com"
				    buttonText={<FormattedMessage id="Login.googlelogin" defaultMessage="Login"/>}
				    onSuccess={this.loginGoogle}				    
				    cookiePolicy={'single_host_origin'}
				  />
					</td>
					</tr></table>
				
								
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
		this.setState( {badConnection: false, inprogress:true, messageConnection:''});
		
		var param = { email: this.state.email, password: this.state.password };
		
		FactoryService.getInstance().getAuthService().login( 'DIRECT', param, this, httpPayload => 
			{
				const intl = this.props.intl;

				httpPayload.trace("Login.directConnectCallback");
				if (httpPayload.isError()) {
					// Server is not started
					console.log("Login.directConnectCallback  ERROR IN HTTPCALL");
					
					this.setState({ badConnection: true,inprogress:false, 
						messageConnection: intl.formatMessage({id: "Login.CantConnectToTheServer",defaultMessage: "Can't Connect To The Server"})});
				}
				else if (httpPayload.getData().isConnected) {
					// call the frame event to refresh all - the fact that the user is connected is saved in the authService, not here
					this.setState({ badConnection: false, inprogress:false });
					this.props.authCallback( true );
				} else {
					const label="Bad connection";
					this.setState({ badConnection: true,inprogress:false,messageConnection: label });
				}		
			} );
	}
	
	// ---------------------------- Logout
	logoutCallback( httpResponse ) {
		httpResponse.trace("Login.logoutCallback ");
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
	loginGoogleCallBack( httpResponse ) {
		httpResponse.trace("Login.loginGoogleCallBack");
		if (httpResponse.isError()) {
			this.setState({ badConnection: true, isLog: false });
		}
		else if (httpResponse.getData().isConnected) {
			this.setState({ isConnected: true });
		} else {
			this.setState({ badConnection: !httpResponse.getData().isConnected, isLog: httpResponse.getData().isConnected });
		}
		// call the frame event to refresh all
		this.props.authCallback( true );	
		
	}
	
	// when Google call me back, the this is null... it call the method outside of any object.
	// so, let's save this to "staticLogin" and use it.
	logoutGoogle() {
		console.log("Login.loginGoogle this="+this+" StaticLogin="+staticLogin);

		FactoryService.getInstance().getAuthService().logout(staticLogin, httpResponse => {
					staticLogin.props.authCallback( false );
			});

	}
	

}
export default injectIntl(Login);

