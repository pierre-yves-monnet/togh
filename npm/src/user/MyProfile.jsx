// -----------------------------------------------------------
//
// MyProfile
//
// Edit user profile
//
// -----------------------------------------------------------

import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { ModalWrapper, RadioButtonGroup, RadioButton,TextInput,Loading,Select } from 'carbon-components-react';

import AskPassword 				from 'component/AskPassword';
import UserMessage 				from 'component/UserMessage';
import FactoryService 			from 'service/FactoryService';



class MyProfile extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");
		let authService = FactoryService.getInstance().getAuthService();
		this.state = { user: authService.getUser(),
		    passwordIsCorrect: false,
			newPassword:"",
			actionPasswordMessage:"",
			errorMessage:'',
			inprogress: false};
			
		this.changePasswordCallback = this.changePasswordCallback.bind( this);
		this.actionChangePassword 	= this.actionChangePassword.bind( this );
		this.refreshUser            = this.refreshUser.bind( this );
	}

	componentDidMount() {
    	console.log("MyProfile.componentDidMount ");
        this.setState({inprogress: true});
        console.log("MyProfile.componentDidMount: ");
        const authService = FactoryService.getInstance().getAuthService();
        authService.refreshUser( this, this.refreshUser);
	}

    refreshUser( user, httpResponse ) {
        let message='';
        if (httpResponse.isError())
            message=<FormattedMessage id="MyProfile.errorServer" defaultMessage="Error connection" />
    	this.setState( { user: user, inprogress:false, errorMessage:message});
    }

	//----------------------------------- Render
	render() {
		 
		console.log("MyProfile.render loading: "+this.state.loading+", user="+JSON.stringify(this.state.user));

		// -----------------
		// <button onClick={this.changeEmail} class="btn btn-primary"><FormattedMessage id="MyProfile.ChangeEmail" defaultMessage="Change Email"/></button>
		return ( 
			<div>
				{ this.state.inprogress && <Loading
      						description="Active loading indicator" withOverlay={true}
    						/>}
                {this.state.errorMessage && <div style={{color: "red"}}>{this.state.errorMessage}</div>}

				<div class="row">
					<div class="col-sm-4" style={{textAlign: "center"}}>
						 <RadioButtonGroup
								legend={<FormattedMessage id="MyProfile.typeProfile" defaultMessage="Picture" />}
      							name="radio-Type Picture"
								onChange={(event) => {
										console.log("MyProfile.Change Type="+event);
										this.setAttribut( "typePicture", event );
									}
								}
								valueSelected={this.state.user.typePicture}
      							defaultSelected="radio-1">
      						<RadioButton value="TOGH" id="togh-1" labelText={<FormattedMessage id="MyProfile.Togh" defaultMessage="Togh" />} labelPosition="right" />
      						<RadioButton value="CYPRIS" id="Cypris-1" labelText={<FormattedMessage id="MyProfile.Cypris" defaultMessage="Cypris" />} labelPosition="right" />
      						<RadioButton value="URL" id="Url-1" labelText={<FormattedMessage id="MyProfile.URL" defaultMessage="Url" />} labelPosition="right" />
      						<RadioButton value="IMAGE" id="Url-1" labelText={<FormattedMessage id="MyProfile.Image" defaultMessage="Image" />} labelPosition="right" />
						</RadioButtonGroup><br/>
						
						{ this.state.user.typePicture=== 'TOGH' && 
										<img  src="img/togh.png" style={{width:150}} alt=""/>
						}
						{ this.state.user.typePicture=== 'CYPRIS' && 
										<img  src="img/cypris.png" style={{width:150}}  alt=""/>
						}
						{ this.state.user.typePicture=== 'URL' && 
										<img  src={this.state.user.picture} style={{width:150}}  alt=""/>
						}
					</div>
					<div class="col-sm-4">
						 <TextInput labelText={<FormattedMessage id="MyProfile.UserName" defaultMessage="User name"/>}
							id="username" 
							value={this.state.user.name} 
							class="thoginputreadonly"/><br/>
						 <TextInput labelText={<FormattedMessage id="MyProfile.firstname" defaultMessage="FirstName"/>}
							id="firstname" 
							value={this.state.user.firstName} 
							onChange={(event) => 
								{ this.setAttribut( "firstName", event.target.value ) }								
								} /><br/>
						 <TextInput labelText={<FormattedMessage id="MyProfile.lastName" defaultMessage="LastName"/>}
							id="lastname" 
							value={this.state.user.lastName} 
							onChange={(event) => { this.setAttribut( "lastName", event.target.value ) }	} /><br/>
						
 						<ModalWrapper
							passiveModal
							buttonTriggerText={<FormattedMessage id="MyProfile.ChangeMyPassword" defaultMessage="Change my password"/>}
			     			modalLabel={<FormattedMessage id="MyProfile.SetANewPassword" defaultMessage="Set a new password"/>}
							size='lg'>
								<div style={{display: "inline-block"}}>
									<AskPassword changePasswordCallback={this.changePasswordCallback} />
									
									<button onClick={this.actionChangePassword} class="btn btn-primary"
										disabled={ ! this.state.passwordIsCorrect}>
										<FormattedMessage id="MyProfile.UpdateMyPassword" defaultMessage="Update my password"/>
									</button>
									<br/>
									<UserMessage message={this.state.actionPasswordMessage} status={this.state.actionPasswordStatus} />
									
									
								</div>
						</ModalWrapper>	
					</div>
					
				</div>		
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
			
					<div class="col-sm-8">
					 	<TextInput labelText={<FormattedMessage id="MyProfile.Email" defaultMessage="Email"/>}
							id="email" 
							value={this.state.user.email} 
							class="thoginputreadonly"
							style={{width: "100%"}}
							/>
						<div class="toghTips">
							<FormattedMessage id="MyProfile.EmailExplanation" defaultMessage="Your email is important. This is the unique identifier for each user. If you lost your password, Togh will send you a new passwor on this email"/>
						</div>
					</div>
					<div class="col-sm-4">

						<br/>
					</div>
				</div>	
					
				<div class="row">
					<div class="col-sm-4">
						<Select labelText={<FormattedMessage id="MyProfile.EmailVisibility" defaultMessage="Email visibility" />}
							id="emailVisiblity"
							value={this.state.user.emailVisibility}
							onChange={(event) => this.setAttribut("emailVisibility", event.target.value)}>

							<FormattedMessage id="MyProfile.EmailVisibilityAlways" defaultMessage="Always">
								{(message) => <option value="ALWAYS">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.EmailVisibilityAlwayButSearch" defaultMessage="Everywhere except search">
								{(message) => <option value="ALWAYBUTSEARCH">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.EmailVisibilityLimitedEvent" defaultMessage="Limited Event">
								{(message) => <option value="LIMITEDEVENT">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.EmailVisibilityNever" defaultMessage="Never">
								{(message) => <option value="NEVER">{message}</option>}
							</FormattedMessage>
						</Select>
					</div>
					<div class="col-sm-8">
						<h5><FormattedMessage id="MyProfile.EmailVisibilityAlways" defaultMessage="Always"/></h5>
						<FormattedMessage id="MyProfile.EmailVisibilityAlwaysExplanation" defaultMessage="Your email is visible for any user connected to Togh"/>
						
						<h5><FormattedMessage id="MyProfile.EmailVisibilityAlwayButSearch" defaultMessage="Everywhere except search"/></h5>
						<FormattedMessage id="MyProfile.EmailVisibilityAlwayButSearchExplanation" defaultMessage="Search does not use your email, but the result display it. A user connected to an event see your email"/>
						
						<h5><FormattedMessage id="MyProfile.EmailVisibilityLimitedEvent" defaultMessage="Limited Event"/></h5>
						<FormattedMessage id="MyProfile.EmailVisibilityLimitedEventExplanation" defaultMessage="Your email is visible only on event, for all users registered in the event"/>

						<h5><FormattedMessage id="MyProfile.EmailVisibilityNever" defaultMessage="Never"/></h5>
						<FormattedMessage id="MyProfile.EmailVisibilityNeverExplanation" defaultMessage="Your email is not visible at all."/>
					</div>
				</div>
				
				
				
				
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
					<div class="col-sm-8">
						 <TextInput labelText={<FormattedMessage id="MyProfile.PhoneNumber" defaultMessage="Phone Number"/>}
							id="phone" 
							value={this.state.user.phoneNumber} 
							onChange={(event) => { this.setAttribut( "phoneNumber", event.target.value ) }	}
							/><br/>
					</div>
					<div class="toghTips">
						<FormattedMessage id="MyProfile.PhoneNumberExplanation" defaultMessage="Togh does not use it, but you can share it with users in the same event"/>
					</div>
				</div>				
				
				<div class="row">
					<div class="col-sm-4">
						<Select labelText={<FormattedMessage id="MyProfile.PhoneNumber" defaultMessage="Phone Number" />}
							id="phoneVisibility"
							value={this.state.user.phoneNumberVisibility}
							onChange={(event) => this.setAttribut("phoneNumberVisibility", event.target.value)}>

							<FormattedMessage id="MyProfile.PhoneNumberVisibilityAlways" defaultMessage="Always">
								{(message) => <option value="ALWAYS">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.PhoneNumberVisibilityAlwayButSearch" defaultMessage="Everywhere except search">
								{(message) => <option value="ALWAYBUTSEARCH">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.PhoneNumberVisibilityLimitedEvent" defaultMessage="Limited Event">
								{(message) => <option value="LIMITEDEVENT">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.PhoneNumberVisibilityNever" defaultMessage="Never">
								{(message) => <option value="NEVER">{message}</option>}
							</FormattedMessage>
						</Select>
					</div>
					<div class="col-sm-8">
						<h5><FormattedMessage id="MyProfile.PhoneNumberVisibilityAlways" defaultMessage="Always"/></h5>
						<FormattedMessage id="MyProfile.PhoneNumberVisibilityAlwaysExplanation" defaultMessage="Your phone number is visible for any user connected to Togh"/>
						
						<h5><FormattedMessage id="MyProfile.PhoneNumberVisibilityAlwayButSearch" defaultMessage="Everywhere except search"/></h5>
						<FormattedMessage id="MyProfile.PhoneNumberVisibilityAlwayButSearchExplanation" defaultMessage="Search does not use your phone number, but the result display it. A user connected to an event see your phone"/>
						
						<h5><FormattedMessage id="MyProfile.PhoneNumberVisibilityLimitedEvent" defaultMessage="Limited Event"/></h5>
						<FormattedMessage id="MyProfile.PhoneNumberVisibilityLimitedEventExplanation" defaultMessage="Your Phone number is visible only on event, for all users registered in the event"/>

						<h5><FormattedMessage id="MyProfile.PhoneNumberVisibilityNever" defaultMessage="Never"/></h5>
						<FormattedMessage id="MyProfile.PhoneNumberVisibilityNeverExplanation" defaultMessage="Your phone number is not visible at all."/>
					</div>
				</div>
				
				
				
				
				
				
				
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
					<div class="col-sm-8">				
					  	<Select labelText={<FormattedMessage id="MyProfile.searchable" defaultMessage="Searchable" />}
							id="searchable"
							value={this.state.user.searchable}
							onChange={(event) => this.setAttribut("searchable", event.target.value)}>

							<FormattedMessage id="MyProfile.searchableTrue" defaultMessage="Yes, I will appears on search">
								{(message) => <option value="TRUE">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.searchableFalse" defaultMessage="Never in the search result">
								{(message) => <option value="FALSE">{message}</option>}
							</FormattedMessage>
						</Select>
						<div class="toghTips">
							<FormattedMessage id="MyProfile.SearchableExplanation" defaultMessage="Your user can be visible in the search function. If not, the only way to invite you is to give your email explicitly."/>
						</div>
					</div>
				</div>
				
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
					<div class="col-sm-8">				
					  	<Select labelText={<FormattedMessage id="MyProfile.showtipuser" defaultMessage="ShowTipUsers" />}
							id="showtip"
							value={this.state.user.showTipsUser}
							onChange={(event) => this.setAttribut("showTipsUser", event.target.value)}>

							<FormattedMessage id="MyProfile.showTipsUser" defaultMessage="Show tips">
								{(message) => <option value="true">{message}</option>}
							</FormattedMessage>
							<FormattedMessage id="MyProfile.hideTipsUser" defaultMessage="Hide tips">
								{(message) => <option value="false">{message}</option>}
							</FormattedMessage>
						</Select>
						<div class="toghTips">
							<FormattedMessage id="MyProfile.ShowTipsExplanation" defaultMessage="Tips are visible. This help you to knows the different functions."/>
						</div>
					</div>
				</div>
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
                    <div class="col-sm-8">
                        <Select labelText={<FormattedMessage id="MyProfile.showTakeATour" defaultMessage="Show Take a tour" />}
                            id="showtakeatour"
                            value={this.state.user.showTakeATour}
                            onChange={(event) => this.setAttribut("showTakeATour", event.target.value)}>

                            <FormattedMessage id="MyProfile.showTakeATour" defaultMessage="Show take a tour">
                                {(message) => <option value="true">{message}</option>}
                            </FormattedMessage>
                            <FormattedMessage id="MyProfile.hideTakeATour" defaultMessage="Hide take a tour">
                                {(message) => <option value="false">{message}</option>}
                            </FormattedMessage>
                        </Select>
                        <div class="toghTips">
                            <FormattedMessage id="MyProfile.ShowTakeATourExplanation" defaultMessage="Where a tour is available, the icon is presented. Clicks on it to access explanations."/>
                        </div>
                    </div>
                </div>
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
					<div class="col-sm-8">				
					  	<TextInput labelText={<FormattedMessage id="MyProfile.subscriptionUser" defaultMessage="Subscription"/>}
							id="email" 
							value={this.state.user.subscriptionUser} 
							class="thoginputreadonly"
							/>
						
						
						<div class="toghTips">
						
								<FormattedMessage id="MyProfile.subscriptionUserExplanation" defaultMessage="Different level of subscription exists."/><br/>
								<ul><FormattedMessage id="MyProfile.subscriptionFreeExplanation" defaultMessage="The Free subscription contains all you need to organize your events. It's just limited to avoid the cost. You are limited in the number of events you can create per month, number of participants per event."/></ul><br/>
								<li><FormattedMessage id="MyProfile.subscriptionPremiumExplanation" defaultMessage="The Premium subscription push the limit. You can organize multiple events, manage budget. Perfect for professional who want to have a great system to track and organize everything, or for users who organize a lot of events!"/></li><br/>
								<li><FormattedMessage id="MyProfile.subscriptionExcellentExplanation" defaultMessage="The Excellence subscription reaches the sky! Large events, no real limit. And you help to maintain the application."/></li><br/>
						</div>
					</div>				
							
				</div>
						
				
			</div>)	
	} //---------------------------- end Render



		
	
	
	// -------- Rest Call
	// provide automatic save
	setAttribut(name, value) {
		this.setState( {inprogress: true, message:''});
		let restCallService = FactoryService.getInstance().getRestCallService();
		let param ={ attribut: name, value: value};
		
		
		console.log("MyProfile.setAttribute: attribut:" + name + " <= " + value );
		restCallService.postJson('/api/user/update', this, param, httpPayload =>{
			httpPayload.trace("MyProfile.update");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState( { user: httpPayload.getData().user});
				
				let authService = FactoryService.getInstance().getAuthService();
				authService.setUser( httpPayload.getData().user );
				console.log("MyProfile.setAttributeCallback user.typepicture="+authService.getUser().typePicture);

			}
		});	
	}
	
	changePasswordCallback( isCorrect, password) {
		this.setState({passwordIsCorrect: isCorrect, password:password, actionPasswordMessage:"", actionPasswordStatus:"" });
	}
	
	actionChangePassword() {

		const intl = this.props.intl;
		let restCallService = FactoryService.getInstance().getRestCallService();
		
		this.setState( {actionPasswordMessage: intl.formatMessage({id: "MyProfile.passwordChangeInProgress",defaultMessage: "In progress"}),
					actionPasswordStatus:"",
					 loading:true});

		var param= {  password: this.state.password };
		restCallService.postJson('/api/login/changePassword?', this, param, this.changePasswordRestCallback);
		
	}
	/**
	 * ChangePasswordCallback
	 */
	changePasswordRestCallback( httpPayload ) {
		const intl = this.props.intl;

		console.log("MyProfile.changePasswordCallback: registerStatus = "+JSON.stringify(httpPayload));
 		if ( ! httpPayload.isError() ) {
			console.log("MyProfile.connectStatus : password changed");
			this.setState( {actionPasswordMessage: intl.formatMessage({id: "MyProfile.passwordChangedOk",defaultMessage: "Password changed"}),
							actionPasswordStatus : "OK",
							 loading:false});
		} else {
			this.setState( {actionPasswordMessage: intl.formatMessage({id: "MyProfile.passwordChangeFailed",defaultMessage: "Change failed"}),
							actionPasswordStatus : "FAIL", 
							loading:false});
		}
	} // end changePasswordRestCallback

	
}
export default injectIntl(MyProfile);