// -----------------------------------------------------------
//
// Invitation
//
// Display a invitation panel. May be used in an event, or in a "MyFriend" list
// The invitation group two main function
//   - search a toghUser. The search can be done from the email, first name, last name, phone number
//   - invite a toghUser, via a Email. Then, a "non confirmed user" is created.  
//
// -----------------------------------------------------------
import React from 'react';

// import { Button } from 'carbon-components-react';


class Invitation extends React.Component {
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'email' : '',
						'firstName'  : '',
						'lastName' : '',
						'phoneNumber': '',
						'panelVisible': 'INVITATION'
						
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.showRegistration = this.showRegistration.bind(this);
		// this is mandatory to have access to the variable in the method... thank you React!   

	}


	render() {
				
	
		console.log("Invitation.render ");

		var invitationPanelHtml= (
				<div>
					<h2>Invitation</h2>
					<div class="fieldlabel">Email</div>
					<input value={this.state.email} onChange={(event) => this.setAttribut( "name", event.target.value )} class="toghinput"></input><br />
					<button class="btn btn-info btn-lg" onClick={this.sendInvitation}>
						<div class="glyphicon glyphicon-envelope"> </div>&nbsp;Send the invitation</button>
				</div>)
		
		
		var resultSearchHtml=[];				
		var searchPanelHtml = (
				<div>
					<h2>Search</h2>
					<div class="fieldlabel">Email</div>
					<input value={this.state.email} onChange={(event) => this.setAttribut( "name", event.target.value )} class="toghinput"></input><br />
					<div class="fieldlabel">First name</div>
					<input value={this.state.firstName} onChange={(event) => this.setAttribut( "firstName", event.target.value )} class="toghinput"></input><br />
					<div class="fieldlabel">Last name</div>
					<input value={this.state.lastName} onChange={(event) => this.setAttribut( "lastName", event.target.value )} class="toghinput"></input><br />
					<div class="fieldlabel">Phone number</div>
					<input value={this.state.phoneNumber} onChange={(event) => this.setAttribut( "phoneNumber", event.target.value )} class="toghinput"></input><br />
					
					<button class="btn btn-info btn-lg" onClick={this.sendInvitation}>
						<div class="glyphicon glyphicon-search"> </div>&nbsp;Search
					</button>
					{resultSearchHtml}	
				</div>)
			
		


		return ( 
			<div>
				<div onChange={this.onChangePanel}>
        			<input type="radio" value="INVITATION" name="choiceaction" /> Invitation
        			<input type="radio" value="SEARCH" name="choiceaction" /> Search
      			</div><br/>
				{this.state.panelVisible === 'INVITATION' && {invitationPanelHtml}}
				{this.state.panelVisible === 'SEARCH' && {searchPanelHtml}}
			</div>)
	
	}

	onChangePanel(event) {
		console.log("Invitation.changePanel "+event.target.value);
		 this.setState({panelVisible: event.target.value});
	}
	

	


	// provide automatic save
	setAttribut( name, value ) {
		console.log("Invitation.setAttribut: attribut:"+name+" <= "+value+" event="+this.state.event);
		var eventValue = this.event;
		eventValue[name]= value;
		this.setState( { "event" : eventValue});
	}
	
	pingParent() {
		console.log("Invitation.pingEvent child change");
	}
	

	
	
	// -------- Rest Call	
}
export default Invitation;

