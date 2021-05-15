// -----------------------------------------------------------
//
// MyProfile
//
// Edit user profile
//
// -----------------------------------------------------------

import React from 'react';

import { FormattedMessage } from "react-intl";

import { RadioButtonGroup, RadioButton,TextInput,Loading,Select } from 'carbon-components-react';

import FactoryService 			from 'service/FactoryService';



class MyProfile extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		let authService = FactoryService.getInstance().getAuthService();
		console.log("MyProfile.constructor user.typepicture="+authService.getUser().typePicture);
		this.state = { user: authService.getUser() };

	}
	componentDidMount() {
		let authService = FactoryService.getInstance().getAuthService();
		console.log("MyProfile.componentDidMount user.typepicture="+authService.getUser().typePicture);
		this.state = { user: authService.getUser() };

	}
	//----------------------------------- Render
	render() {
		 
		console.log("MyProfile.render user="+JSON.stringify(this.state.user));
		
		
											
		// -----------------	 
		return ( 
			<div>
				{ this.state.inprogress && <Loading
      						description="Active loading indicator" withOverlay={true}
    						/>}

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
										<img  src="img/togh.png" style={{width:150}} />
						}
						{ this.state.user.typePicture=== 'CYPRIS' && 
										<img  src="img/cypris.png" style={{width:150}} />
						}
						{ this.state.user.typePicture=== 'URL' && 
										<img  src={this.state.user.picture} style={{width:150}} />
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
							<button onClick={this.changeEmail} class="btn btn-info"><FormattedMessage id="MyProfile.ChangeEmail" defaultMessage="Change Email"/></button>
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
							<FormattedMessage id="MyProfile.SearchableExplanation" defaultMessage="Your user can be visible in the search function. If not, the only way to invite you is to give your email explicitly."/>
						</div>
					</div>
				</div>
				<div class="row" style={{borderTop: "1px black solid",  marginTop: "40px", paddingTop: "10px"}}>
					<div class="col-sm-8">				
					  <TextInput labelText={<FormattedMessage id="MyProfile.subscriptionUser" defaultMessage="Subscription"/>}
							id="email" 
							value={this.state.user.subscriptionuser} 
							class="thoginputreadonly"
							/>
						<div class="toghTips">
								<FormattedMessage id="MyProfile.subscriptionUserExplanation" defaultMessage="Different level of subscription exists."/><br/>
								<FormattedMessage id="MyProfile.subscriptionFreeExplanation" defaultMessage="The Free subscription contains all you need to organize your events. It's just limited to avoid the cost. You are limited in the number of events you can create per month, number of participants per event."/><br/>
								<FormattedMessage id="MyProfile.subscriptionPremiumExplanation" defaultMessage="The Premium subscription push the limit. You can organize multiple events, manage budget. Perfect for professional who want to have a great system to track and organize everything, or for users who organize a lot of events!"/><br/>
								<FormattedMessage id="MyProfile.subscriptionExcellentExplanation" defaultMessage="The Excellence subscription reaches the sky! Large events, no real limit. And you help to maintain the application."/><br/>
						</div>
					</div>				
							
				</div>
						
				
			</div>)	
	} //---------------------------- end Render



		
	
	
	// -------- Rest Call
	// provide automatic save
	setAttribut(name, value) {
		this.setState( {inprogress: true, message:''});
		let restCallService = FactoryService.getInstance().getRestcallService();
		let param ={ attribut: name, value: value};
		
		
		console.log("MyProfile.setAttribute: attribut:" + name + " <= " + value );
		restCallService.postJson('/api/user/update', this, param, httpPayload =>{
			httpPayload.trace("MyProfile.update");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				debugger;
				this.setState( { user: httpPayload.getData().user});
				
				let authService = FactoryService.getInstance().getAuthService();
				authService.setUser( httpPayload.getData().user );
				console.log("MyProfile.setAttributeCallback user.typepicture="+authService.getUser().typePicture);

			}
		});	}

	
}
export default MyProfile;