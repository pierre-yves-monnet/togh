// -----------------------------------------------------------
//
// RegisterNewUser
//
// Register a new user
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput } from 'carbon-components-react';
import { FormattedMessage } from "react-intl";


import FactoryService 	from 'service/FactoryService';


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
						<button class="glyphicon glyphicon-remove" onClick={this.hideRegistration} 
							title={<FormattedMessage id="RegisterNewUser.CloseRegistration" defaultMessage="Close registration"/>} ></button>
					</div>
				</div>
				<div class="panel-body">
					<br />
					<TextInput labelText={<FormattedMessage id="RegisterNewUser.Email" defaultMessage="Email"/>} 
						type="email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><br />

					<TextInput labelText={<FormattedMessage id="RegisterNewUser.FirstName" defaultMessage="First name"/>} 
						type="string" value={this.state.firstName} onChange={(event) => this.setState({ firstName: event.target.value })}  required></TextInput><br />

					<TextInput labelText={<FormattedMessage id="RegisterNewUser.LastName" defaultMessage="Last name"/>}
						 type="string" value={this.state.lastName} onChange={(event) => this.setState({ lastName: event.target.value })}  ></TextInput><br />

					<TextInput labelText={<FormattedMessage id="RegisterNewUser.Password" defaultMessage="Password"/>} 
						type="password" value={this.state.password} onChange={(event) => this.setState({ password: event.target.value })}  maxlength="30" required></TextInput><br />

					<TextInput labelText={<FormattedMessage id="RegisterNewUser.RetypePassword" defaultMessage="Retype password"/>} 
						type="password" value={this.state.confirmPassword} onChange={(event) => this.setState({ confirmPassword: event.target.value })} maxlength="30" required></TextInput><br />
					<div dangerouslySetInnerHTML={{ __html: messageBadPassword}}></div>
					
					<button class="btn btn-info" onClick={this.registerUser} disabled={ ! this.checkPassword() || ! this.validateForm()}>
						{this.state.loading && <span class="loading">.</span>} <FormattedMessage id="RegisterNewUser.Registration" defaultMessage="Registration"/></button><p />
					<div dangerouslySetInnerHTML={{ __html: messageRegistration}}></div>
				</div>
			</div>
		)
		}
		else {
			return 	(
				<div>
				<button  class="btn btn-primary" onClick={this.showRegistration} >Register New User</button>
				
				<div style={{fontStyle:"italic", paddingTop: "20px", paddingBottom: "10px",fontSize: "18px", fontWeight:"bold"}}><FormattedMessage id="BodyTogh.whatisToghTitle" defaultMessage="What is Togh?" /></div>
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whatisToghExplanation" defaultMessage="Togh is an application to manage your event."/></div> 
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whatisToghExample" defaultMessage="Potluck with the school? Barbecue with Friends? Road trip with Family on m? multiple days? This is an event."/></div> 
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whatisToghPossibility" defaultMessage="In one event, organize participants, register tasks and shopping list, give address, specify steps your road trip. You can ask the participant any survey: do they prefer to visit Hollywood Bld, or the Griffith Observatory (Paul want to visit both!) Visualize the itinerary on the map. Calculate expense. Togh will tell who owns who." /></div>
									
				<div style={{fontStyle:"italic", paddingTop: "20px", paddingBottom: "10px",fontSize: "18px", fontWeight:"bold"}}><FormattedMessage id="BodyTogh.whyTogh" defaultMessage="Why Togh?" /></div>
				
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whyToghMotivation" defaultMessage="I wanted to learn React, Spring. Plus, I wanted to put my hand in the Cloud deployment. So, why not build an application using all these technologies, and see what's going on?"/></div>
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whyToghBorn" defaultMessage="Here Togh was born. I was thinking of this application for five years now."/></div>
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whyToghHistory" defaultMessage="Last year, when I organized a road trip for my family (which was canceled, due the Covid), I had to use Furkot to build the itinerary, make Doogle for the survey, opening a Splitwise to share the expense, a Google Doc to describe the itinerary, Facebook group to exchange idea."/></div>
				<div style={{paddingBottom: "10px"}}><FormattedMessage id="BodyTogh.whyToghConclusion" defaultMessage="So this application was really needed at this moment."/></div>
				</div>				
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
