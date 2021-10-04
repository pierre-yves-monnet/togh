// -----------------------------------------------------------
//
// RegisterNewUser
//
// Register a new user
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput } from 'carbon-components-react';
import { injectIntl,FormattedMessage } from "react-intl";

import { XSquare,InfoCircle } from 'react-bootstrap-icons';



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
			email: props.defaultLoginEmail, 
			showRegistration: props.showRegisterUserForm,
			badRegistration: false,
			errorNetwork: false,
			registrationOk:false,
			isLog: false,
			loading : false }

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.registerUser = this.registerUser.bind(this);
		this.showRegistration = this.showRegistration.bind(this);
		this.hideRegistration = this.hideRegistration.bind(this);

	}


	render() {


		if (this.state.showRegistration) {
			return (
			<div className="App" class="toghBlock" style={{padding:"10px 10px 20px 10px"}}>
				<div class="panel-heading">
					<div style={{float: "right"}}>						
						<XSquare onClick={this.hideRegistration} 
							title={<FormattedMessage id="RegisterNewUser.CloseRegistration" defaultMessage="Close registration"/>} width="25px" height="25px"/>
					</div>
					<center><h1>Registration</h1></center> 
				</div>
				<div class="panel-body">
					<div style={{fontStyle: "italic"}}>
						<FormattedMessage id="RegisterNewUser.Information_1" defaultMessage="Register a user in Togh to access and create events, receive notifications. Access your profile to change any settings."/>
						<br/>
						<br />
					</div>
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
					<div style={{padding:"10px 10px 10px 10px"}}>
						<div style={{color:"red"}}>{ this.validateForm() }</div>
						<div style={{color:"red"}}>
						    {!this.checkPassword() && <FormattedMessage id="RegisterUser.PasswordsAreDifferent" defaultMessage="Passwords are different"/>}
						 </div>
					</div>
					<button class="btn btn-info" onClick={this.registerUser} 
							disabled={ ! this.checkPassword() || this.validateForm().length >0}>
						{this.state.loading && <span class="loading">.</span>}
						<FormattedMessage id="RegisterNewUser.Registration" defaultMessage="Registration"/>
					</button><p />

					{this.state.badRegistration &&
					    <div class="alert alert-danger" style={{marginTop: "10px"}}>
					        <FormattedMessage id="RegisterNewUser.AccountAlreadyExist" defaultMessage="Account already exist"/>
					    </div>
					}
                    {this.state.errorNetwork &&
                        <div class="alert alert-danger" style={{marginTop: "10px"}}>
                            <FormattedMessage id="RegisterNewUser.ServerError" defaultMessage="Server error"/>
                        </div>
                    }
                    {this.state.registrationOk &&
                    	<div class="alert alert-success" style={{marginTop: "10px"}}>
                    	    <FormattedMessage id="RegisterNewUser.Account Create" defaultMessage="Account Created"/>
                    	</div>
                    }


					<div class="toghTips">
						<div class="row">
							<div class="col-1">
								<InfoCircle width="20px" height="20px" color="#1f78b4"/>
							</div>
							<div class="col-9">
								<div style={{fontWeight: "bold", padding: "5px 0px 5px 2px"}}><FormattedMessage id="RegisterNewUser.InformationLogin" defaultMessage="Login"/></div>
								<FormattedMessage id="RegisterNewUser.Information_2" defaultMessage="Your login is your email. Togh will send you a confirmation email to validate your account. This email is used when you lost your password."/>
								<br/>
								<div style={{fontWeight: "bold", padding: "5px 0px 5px 2px"}}><FormattedMessage id="RegisterNewUser.InformationVisibility" defaultMessage="Access information"/></div>
								<FormattedMessage id="RegisterNewUser.Information_3" defaultMessage="In the profile, you can change the setting to not be visible by another Togh user. By default, you are visible in search via name and email. Your friend needs to see you! But you can decide not to be visible in a general search. Then, your friend must know your email to add you as a participant."/>
							</div>
						</div>
					</div>
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
	
	/**
	 * Check if the registration form is correct
	 */
	validateForm() {
		const intl = this.props.intl;

		let messages =[];
		if (! this.state.email )
			messages.push( intl.formatMessage({id: "RegisterUser.EmailIsMandatory", defaultMessage: "Email is mandatory"})+"; ");
		if (! this.state.firstName)
			messages.push( intl.formatMessage({id: "RegisterUser.FirstNameIsMandatory", defaultMessage: "First Name is mandatory"})+"; ");
		if (!this.state.password)
			messages.push( intl.formatMessage({id: "RegisterUser.PasswordIsMandatory", defaultMessage: "Password is mandatory"}));
		else if (this.state.password.length < 4)
			messages.push( intl.formatMessage({id: "RegisterUser.PasswordSizeMandatory", defaultMessage: "Password must have minimum 4 characters"}));
		return messages;		
	}
	
	
	toString() {
		return "email=[" + this.state.email+"],password=["+this.state.password+"] Connection=["+this.state.badConnection+"] isLog["+this.state.isLog+"]";
	}
	
	
	// -------- Rest Call
	registerUser() {
		this.setState( {badRegistration: false, errorNetwork:false, registrationOk:false, loading:true});
		
		var param= { email: this.state.email, password: this.state.password, firstName:this.state.firstName, lastName: this.state.lastName };
		console.log("RegisterUser.registerUser: ClickRegistration, param" + JSON.stringify(param));

		var authService =FactoryService.getInstance().getAuthService();  
		authService.registerUser( param, this, this.registerUserCallback );
	}
	
	registerUserCallback( httpPayload ) {
		console.log("RegisterNew.registerStatus: registerStatus = "+JSON.stringify(httpPayload));
        if (httpPayload.isError()) {
        	this.setState( { errorNetwork:true, loading:false});
        }
 		else if (httpPayload.getData().isConnected) {
			console.log("RegisterNew.connectStatus : redirect then");
			this.setState( { registrationOk:true,  loading:true});
			this.props.authCallback( true );
		}
		else {
			this.setState( {badRegistration: true, loading:false});
		}
	} // end connectStatus
}

export default injectIntl(RegisterNewUser);
