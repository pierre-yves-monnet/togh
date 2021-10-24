// -----------------------------------------------------------
//
// ResetPassword
//
// Reset Password page
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput } from 'carbon-components-react';
import { injectIntl,FormattedMessage } from "react-intl";

import AskPassword 				from 'component/AskPassword' 


import FactoryService from 'service/FactoryService';


class ResetPassword extends React.Component {
	// in props, a function must be give to the call back. When we click on a line, we call
	// this.props.authCallback()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {
			uuid: props.uuid,
			inprogress: false,
			user: null,
			password: '',
			passwordIsCorrect: false,
			message:''
		}

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.getInfoCallback 			= this.getInfoCallback.bind( this );
		this.changePasswordCallback 	= this.changePasswordCallback.bind( this );
		this.changePasswordRestCallback	= this.changePasswordRestCallback.bind( this );
		this.changePassword             = this.changePassword.bind(this);
	}
	/**
	* We arrive in the window : get the information behind the uuid
	 */
	componentDidMount() {
		// call the server to get the value
		this.setState({ inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestCallService();
		let param = { uuid: this.state.uuid};
		
		restCallService.postJson('/api/login/resetPasswordInfo?', this, param, this.getInfoCallback);

	}
	getInfoCallback(httpPayload) {
		httpPayload.trace("ResetPassword.getInfo");
		
		this.setState({ inprogress: false });
		if (httpPayload.isError()) {
			this.setState({ message: "Server connection error" });
		}
		else {
			console.log("httpPayload.getData()=" + JSON.stringify(httpPayload.getData()));
			this.setState({ user: httpPayload.getData().user });
		}


	}
	render() {
		
		return (
			<div>
				<div style={{paddingTop: "10px"}}>
					<h2>{this.state.user && <div> Welcome {this.state.user.label}</div>}</h2>
				</div>
				<div style={{paddingTop: "10px"}}>{this.state.message}</div>
				
				<div style={{paddingTop: "10px"}}>
				<FormattedMessage id="ResetPassword.ResetPasswordProcedure" defaultMessage="You can now change your password. It will be changed, and you will be connected immediately" />
				</div>
				<AskPassword changePasswordCallback={this.changePasswordCallback} />
                <button class="btn btn-info"
                    onClick={ this.changePassword}
                    disabled={this.state.passwordIsCorrect===false}
                    style={{paddingTop: "10px"}}>
						<FormattedMessage id="ResetPassword.UpdateMyPassword" defaultMessage="Update my password"/>
				</button>
			</div>
			);	
	}


	
	// -------- Callback from AskPassword
	changePasswordCallback(checkOk, password) {
	    this.setState({password: password, passwordIsCorrect: checkOk});

	}


	changePassword() {

		this.setState( {badRegistration: false, loading:true});
		var restCallService = FactoryService.getInstance().getRestCallService();
		
		var param= {  password: this.state.password, uuid: this.state.uuid };
		console.log("ResetPassword.changePassword: param" + JSON.stringify(param));

		restCallService.postJson('/api/login/resetPassword?', this, param, this.changePasswordRestCallback);

	}
	
	changePasswordRestCallback( httpPayload ) {
		const intl = this.props.intl;

		console.log("RegisterNew.changePasswordCallback: registerStatus = "+JSON.stringify(httpPayload));
 		if (httpPayload.getData().isConnected) {
			console.log("RegisterNew.connectStatus : redirect then");
			this.setState( {message: intl.formatMessage({id: "ResetPassword.passwordchanged",defaultMessage: "Password changed"}), loading:false});

			let authService = FactoryService.getInstance().getAuthService();
			authService.setConnection( httpPayload.getData() );	

			this.props.authCallback( true );

			return;			
		}
		else {
			this.setState( {message:intl.formatMessage({id: "ResetPassword.passwordchangefailed",defaultMessage: "Change failed"}), loading:false});
		}
	} // end connectStatus

}
export default injectIntl(ResetPassword);
