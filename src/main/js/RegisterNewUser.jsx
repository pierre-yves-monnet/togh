// -----------------------------------------------------------
//
// RegisterNewUser
//
// Register a new user
//
// -----------------------------------------------------------
import React from 'react';

class RegisterNewUser extends React.Component {
	constructor() {
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
		console.log("ResigerNewUser.render: badRegistration=" + this.state.badRegistration+" / message=["+messageRegistration+"]");

		let messageBadPassword='';
		if (!   this.checkPassword()) {
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
					<div class="requiredField">Email address</div>
					<br />
					<input type="email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} class="toghinput requiredField" required></input><br />
					First name <span class="requiredField">.</span><br />
					<input type="string" value={this.state.firstName} onChange={(event) => this.setState({ firstName: event.target.value })} class="toghinput" required></input><br />
					Last name<br />
					<input type="string" value={this.state.lastName} onChange={(event) => this.setState({ lastName: event.target.value })} class="toghinput" ></input><br />
					Password <span class="requiredField">.</span><br />
					<input type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })} class="toghinput" required></input><br />
					Retype Password <span class="requiredField">.</span><br />
					<input type="password" value={this.state.confirmPassword} onChange={(event) => this.setState({ confirmPassword: event.target.value })} class="toghinput" required></input><br />
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
		console.log("Login.connect: ClickConnect email=" + this.toString());
		this.setState( {badRegistration: false, loading:true});
		
		const requestOptions = {
	        method: 'POST',
	        headers: { 'Content-Type': 'application/json' },
	        body: JSON.stringify({ email: this.state.email, password: this.state.password, firstName:this.state.firstName, lastName: this.state.lastName })
	    };
    	fetch('registernewuser?', requestOptions)
			.then(response => response.json())
        	.then( data => this.registerUserCallback( data ));
	}
	
	registerUserCallback( httpPayload ) {
		console.log("RegisterNew.registerStatus: registerStatus = "+JSON.stringify(httpPayload));
 		if (httpPayload.isConnected) {
			console.log("RegisterNew.connectStatus : redirect then");
			window.location.href = "homeTogh.html";
		}
		else {
			this.setState( {badConnection: true,  loading:false});
		}
	} // end connectStatus
}

export default RegisterNewUser;
