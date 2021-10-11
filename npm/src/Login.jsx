// -----------------------------------------------------------
//
// Login
//
// Login page. Different button are present
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput,  Loading,ModalWrapper } from 'carbon-components-react';
import { injectIntl, FormattedMessage } from "react-intl";
import { Envelope } from 'react-bootstrap-icons';

// https://www.npmjs.com/package/react-google-login
// https://dev.to/sivaneshs/add-google-login-to-your-react-apps-in-10-mins-4del
import { GoogleLogin, GoogleLogout } from 'react-google-login';

import FactoryService from 'service/FactoryService';

const LOCALSTORAGE_REMEMBERME = "loginRememberMe";
const LOCALSTORAGE_EMAIL = "loginEmail";


var staticLogin=null;

class Login extends React.Component {
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.authCallback()
	constructor( props ) {
		super();
		// console.log("Login.constructor");

		let rememberme=localStorage.getItem( LOCALSTORAGE_REMEMBERME );
		let email=localStorage.getItem( LOCALSTORAGE_EMAIL );
		if (props.defaultLoginEmail )
			email=props.defaultLoginEmail ;
		
		this.state = { email: email, 
			password: '', 
			rememberme: rememberme,
			badConnection: false,
			messageConnection:'',
			inprogress: false,
			showLostPassword:false,
			messageLostPassword:'',
			statusresetpassord:'' }

		// get from the local storage ? 
		
		// this is mandatory to have access to the variable in the method... thank you React!   
		this.loginConnect 		= this.loginConnect.bind(this);
		this.directLogout 		= this.directLogout.bind( this );
		this.sendEmailPassword 	= this.sendEmailPassword.bind( this);
		staticLogin 			= this;
	}

	// ------------------------------ Render
	render() {
		
		let messageConnectionHtml ="";
		const intl = this.props.intl;

		let factory = FactoryService.getInstance();
		let authService = factory.getAuthService();


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
				 <TextInput labelText={<FormattedMessage id="Login.email" defaultMessage="Email"/>}
					id="loginemail" 
					value={this.state.email} 
					onChange={(event) => 
						{ this.setState({ email: event.target.value });
							if (this.state.rememberme) {
								localStorage.setItem(LOCALSTORAGE_EMAIL,  event.target.value);
							}
						}
						} /><p />
				
				<TextInput labelText={<FormattedMessage id="Login.password" defaultMessage="Password"/>} 
					type="password"
					id="loginpassword" 
					value={this.state.password} 
					onChange={(event) => this.setState({ password: event.target.value })} /><p />
				<br/>
				
				
					
				<table >
				<tr>
				<td style={{paddingLeft: "150px"}}>
					
					<button onClick={this.loginConnect} class="btn btn-info"><FormattedMessage id="Login.connection" defaultMessage="Connection"/></button><br/><br/>
					
					<input type="checkbox"
						onChange={(event) => { 
								let rememberBool = event.target.value==='on';
								this.setState( {"rememberme":  rememberBool});
								localStorage.setItem(LOCALSTORAGE_REMEMBERME, rememberBool);
								if (! event.target.value) {
									localStorage.setItem(LOCALSTORAGE_EMAIL, "");
									}
								}
						}
						defaultChecked={this.state.rememberme ? 'checked': ''} />
					&nbsp;
					<FormattedMessage id="Login.RememberMe" defaultMessage="Remember Me" />
				
					{messageConnectionHtml}
				</td>
				<td style={{paddingRight : "40px"}} >
					<GoogleLogin
					    clientId="81841339298-lh7ql69i8clqdt0p7sir8eenkk2p0hsr.apps.googleusercontent.com"
					    buttonText={<FormattedMessage id="Login.googlelogin" defaultMessage="Login"/>}
					    onSuccess={this.loginGoogle}				    
					    cookiePolicy={'single_host_origin'}
					  />
				</td>
				</tr>
				<tr>
				<td colspan="2" style={{paddingRight : "40px", paddingLeft : "150px"}}>
					
					<div style={{marginTop: "80px", marginBottom: "10px"}}>
						<FormattedMessage id="Login.LostMyPasswordExplanation" defaultMessage="You have an account, but you can't connect? Click on the 'I list my password' button to get a temporary one"/>
					</div>
					<ModalWrapper
						passiveModal
						buttonTriggerText={<FormattedMessage id="Login.LostMyPassword" defaultMessage="I lost My password"/>}
		     			modalLabel={intl.formatMessage({id: "Login.LostMyPasswordLabel", defaultMessage: "Reset my password"})}
						size='lg'>
						<div style={{display: "inline-block"}}>
							<FormattedMessage id="Login.LostPasswordExplanation" defaultMessage="A link to change your password will be send to your email"/>
							<TextInput 
									id="loginemail" 
									value={this.state.email} 
									onChange={(event) => 
										{ this.setState({ email: event.target.value });
											if (this.state.rememberme) {
												localStorage.setItem(LOCALSTORAGE_EMAIL,  event.target.value);
											}
										}
										} />
							<button class="btn btn-info"  onClick={this.sendEmailPassword}>
								<Envelope/>			
								&nbsp;					
								<FormattedMessage id="Login.SendEmail" defaultMessage="Send the email"/>
							</button>
							<br/>
							{ this.state.statusresetpassord === 'OK' && <div style={{color: "green"}}>{this.state.messageLostPassword}</div>}
							{ this.state.statusresetpassord !== '' && this.state.statusresetpassord !== 'OK'  && <div style={{color: "red"}}>{this.state.messageLostPassword}</div>}
							
						</div>
					</ModalWrapper>
					
				</td>
				</tr>
				</table>
				
								
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
					
					// setyup the API Key
					var factory = FactoryService.getInstance();
					var apiKeyService = factory.getApiKeyService();
					apiKeyService.setKeysForUser( httpPayload.getData().apikeys);

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
		console.log("Login.loginGoogle googleInformation="+JSON.stringify(googleUser));
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
		
	// -----------------------------------------------
	// LostMyPassword	
	// -----------------------------------------------
	sendEmailPassword() {
		const intl = this.props.intl;

		this.setState({ messageLostPassword :  ""});
		let restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/login/lostmypassword', this, {email: this.state.email}, httpPayload => {
				httpPayload.trace("Login.lostmypassword");
	
				if (httpPayload.isError()) {
					this.setState({ messageLostPassword: intl.formatMessage({id: "Login.ServerConnectionError",defaultMessage: "Server connection error"}) });
				} else {
					this.setState( { "statusresetpassord":httpPayload.getData().status});
					
					if (httpPayload.getData().status === "OK"){ 
						this.setState( { messageLostPassword: intl.formatMessage({id: "Login.EmailSent",defaultMessage: "An email is sent, check your mailbox"}) });
					} else if (httpPayload.getData().status === "SERVERISSUE") {
						this.setState( {messageLostPassword: intl.formatMessage({id: "Login.ServerIssue",defaultMessage: "An error arrived on the server, please try later"}) });
					} else if (httpPayload.getData().status === "BADEMAIL") {
						this.setState( {messageLostPassword: intl.formatMessage({id: "Login.EmailIncorrect",defaultMessage: "Email address is incorrect"}) });
						}		
			}
		});
	}
}
export default injectIntl(Login);

