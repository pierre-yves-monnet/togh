// -----------------------------------------------------------
//
// RegisterNewUser
//
// Register a new user
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput } from 'carbon-components-react';


import FactoryService from './service/FactoryService';


class RegisterNewUser extends React.Component {
	
	// this.props.authCallback()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {user:'', 
			firstName:'',
			lastName:'',
			password:'', 
			confirmPassword:'',
			email: 'pierre-yves.monnet@laposte.net', 
			showRegistration:false,
			badRegistration: false, 
			registrationOk:false,
			isLog: false,
			loading : false }

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.registerUser = this.registerUser.bind(this);
		this.showRegistration = this.showRegistration.bind(this);
		this.hideRegistration = this.hideRegistration.bind(this);

	}


	render() {
		let messageRegistration='';
		if (this.state.badRegistration) {
			messageRegistration = messageRegistration.concat("<div style='color:red'>Account already exist</div>");
		}
		if (this.state.registrationOk) {
			messageRegistration = messageRegistration.concat("<div style='color:green'>Account created</div>");
		}
		// console.log("ResigerNewUser.render: badRegistration=" + this.state.badRegistration+" / message=["+messageRegistration+"]");

		let messageBadPassword='';
		if (! this.checkPassword()) {
			messageBadPassword="<div style='color:red'>Password are different</div>";
		}

		if (this.state.showRegistration) {
			return (
			<div className="App" class="panel panel-info">
				<div class="panel-heading">
					Registration 
					<div style={{float: "right"}}>
						<button class="glyphicon glyphicon-remove" onClick={this.hideRegistration} title="Close registration"></button>
					</div>
				</div>
				<div class="panel-body">
					<br />
					<TextInput labelText="Email" type="email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><br />

					<TextInput labelText="First name" type="string" value={this.state.firstName} onChange={(event) => this.setState({ firstName: event.target.value })}  required></TextInput><br />

					<TextInput labelText="Last name" type="string" value={this.state.lastName} onChange={(event) => this.setState({ lastName: event.target.value })}  ></TextInput><br />

					<TextInput labelText="Password" type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })}  required></TextInput><br />

					<TextInput labelText="Retype password" type="password" value={this.state.confirmPassword} onChange={(event) => this.setState({ confirmPassword: event.target.value })} required></TextInput><br />
					<div dangerouslySetInnerHTML={{ __html: messageBadPassword}}></div>
					
					<button class="btn btn-info" onClick={this.registerUser} disabled={ ! this.checkPassword() || ! this.validateForm()}>
						{this.state.loading && <span class="loading">.</span>} Registration</button><p />
					<div dangerouslySetInnerHTML={{ __html: messageRegistration}}></div>
				</div>
			</div>
		)
		}
		else {
			return 	(
				<button  class="btn btn-primary" onClick={this.showRegistration} >Register New User</button>
				)
		}
	}

	// -------------------------- Screen control
	showRegistration() {
		this.setState( {showRegistration: true });
	}

	hideRegistration() {
		this.setState( {showRegistration: false });
	}

	checkPassword() {
		if (this.state.password !== this.state.confirmPassword) 
			return false;
		return true;
	}
	validateForm() {
		if (this.state.email.length === 0)
			return false;
		if (this.state.firstName.length === 0)
			return false;	
		return true;		
	}
	toString() {
		return "email=[" + this.state.email+"],password=["+this.state.password+"] Connection=["+this.state.badConnection+"] isLog["+this.state.isLog+"]";
	}
	
	
	// -------- Rest Call
	registerUser() {
		this.setState( {badRegistration: false, loading:true});
		
		var param= { email: this.state.email, password: this.state.password, firstName:this.state.firstName, lastName: this.state.lastName };
		console.log("RegisterUser.registerUser: ClickRegistration, param" + JSON.stringify(param));

		var authService =FactoryService.getInstance().getAuthService();  
		authService.registerUser( param, this, this.registerUserCallback );
	}
	
	registerUserCallback( httpPayload ) {
		console.log("RegisterNew.registerStatus: registerStatus = "+JSON.stringify(httpPayload));
 		if (httpPayload.getData().isConnected) {
			console.log("RegisterNew.connectStatus : redirect then");
			this.setState( {badRegistration: false, registrationOk:true,  loading:true});
			this.props.authCallback( true );
		}
		else {
			this.setState( {badRegistration: true,  registrationOk:false,  loading:false});
		}
	} // end connectStatus
}

export default RegisterNewUser;
