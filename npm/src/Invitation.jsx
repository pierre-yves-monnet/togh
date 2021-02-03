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
import { ModalWrapper } from 'carbon-components-react';
import { RadioButtonGroup } from 'carbon-components-react';
import { RadioButton } from 'carbon-components-react';
import { TextInput } from 'carbon-components-react';
import { TextArea } from 'carbon-components-react';
import { Select } from 'carbon-components-react';
import { Checkbox } from 'carbon-components-react';
import { Button } from 'carbon-components-react';

import FactoryService from './service/FactoryService';

class Invitation extends React.Component {
	constructor( props ) {
		super();
		// participantInvited()
		// console.log("RegisterNewUser.constructor");
		this.state = { 'event' : props.event,
						'email' : '',
						'firstName'  : '',
						'lastName' : '',
						'phoneNumber': '',						
						'role': 'PARTICIPANT',
						'panelVisible': 'INVITATION',
						'message': 'Please join this event.',
						'statusinvitation' : '',
						'listSearchUsers' : []
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.sendInvitation = this.sendInvitation.bind(this);
		this.searchToghUser	= this.searchToghUser.bind(this);
		// this is mandatory to have access to the variable in the method... thank you React!   

	}


	render() {
		console.log("Invitation.render ");
		console.log("Invitation.listSearch="+ JSON.stringify( this.state.listSearchUsers ));
		
		var listSearchUsersHtml = [];
		if (this.state.listSearchUser !== null) {
			console.log("Invitation.buildListUser");
			listSearchUsersHtml = this.state.listSearchUsers.map((toghUser) =>
				<tr key={toghUser.id}>
					<td><Checkbox labelText="" id={toghUser.id} /></td>
					<td>
						{toghUser.firstName} {toghUser.lastName}  
					</td>
					<td>{toghUser.phonenumber}</td>
					<td>{toghUser.email}</td>
				</tr>
			);
		}
		console.log("Invitation.listSearchHtml="+listSearchUsersHtml);
		
		return ( 
			 <ModalWrapper
     			 buttonTriggerText="Invitation"
     			 modalLabel="Invitation"
				primaryButtonText="Close"
				secondaryButtonText=''
				size='lg'>
					<RadioButtonGroup
						valueSelected={this.state.panelVisible}
						legend=""
						onChange={(event) => {
							console.log("Invitation.Change type="+event);        					
							this.setState( {"panelVisible": event})}
							}
						>
						<RadioButton value="INVITATION" id="invitation_r1" labelText="Email Invitation" labelPosition="right" />
						<RadioButton value="SEARCH" id="invitation_r2"  labelText="Search a User" labelPosition="right"/>
					</RadioButtonGroup>
      				<br/>
      				{this.state.panelVisible === 'INVITATION' && (<div>
						<h2>Invitation</h2>

						<TextInput labelText="Email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><br />
						

						
						</div>)
					}		
					{this.state.panelVisible === 'SEARCH' && (<div>
							<h2>Search</h2>
							<table>
							<tr>
								<td style={{"padding-right": "10px"}}><TextInput labelText="First name" value={this.state.firstName} onChange={(event) => this.setState( { firstName: event.target.value })} ></TextInput>
								</td><td style={{ "padding-right": "10px"}}><TextInput labelText="Last name" value={this.state.lastName} onChange={(event) => this.setState( { lastName: event.target.value })}></TextInput>
								</td><td style={{ "padding-right": "10px"}}><TextInput labelText="Phone number" value={this.state.phoneNumber} onChange={(event) => this.setState( { phoneNumber: event.target.value })} ></TextInput>
								</td><td style={{ "padding-right": "10px"}}><TextInput labelText="Email" value={this.state.email} onChange={(event) => this.setState( {name: event.target.value })} ></TextInput>
							</td></tr>
							</table>
							<br/>
							<button class="btn btn-info btn-lg" onClick={this.searchToghUser} class="btn btn-info">
								<div class="glyphicon glyphicon-search"> </div>&nbsp;Search
							</button>
							<br/>
							<table class="table table-striped toghtable">
								<thead>
									<tr>
										<th></th>
										<th>Name</th>
										<th>Phone number</th>
										<th>Email</th>
									</tr>
								</thead>
								{listSearchUsersHtml}
							</table>
						</div>)
					}	
					<p/>
					<TextArea labelText="Message" value={this.state.message} onChange={(event) => this.setState({ message: event.target.value })} ></TextArea><br />
					<table>
						<tr>
							<td style={{"padding-right": "10px"}}>
								<Select labelText="Role in this event" value={this.state.role} onChange={(event) => this.setState({ role: event.target.value })}>
									<option value="ORGANIZER">Organizer</option>
									<option value="PARTICIPANT">Participant</option>
									<option value="OBSERVER">Observer</option>
								</Select>
							</td><td>
								<button class="btn btn-info btn-lg" onClick={this.sendInvitation} class="btn btn-primary">
									<div class="glyphicon glyphicon-envelope"> </div>&nbsp;Send the invitation
								</button>
							</td>
						</tr>
					</table>
					{this.state.statusinvitation}			
    		</ModalWrapper>
		);
	};

	searchToghUser() {
		console.log("Invitation.Search: searchToghUser");
		console.log("Invitation.Search: search by firstName="+this.state.firstName);

		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson('/api/user/search?firstName='+this.state.firstName+'&lastName='+this.state.lastName+'&email='+this.state.email+'&phoneNumber='+this.state.phoneNumber, 
			httpPayload => {
				console.log("Invitation payload=" + JSON.stringify(httpPayload.data));
				this.setState( { listSearchUsers: httpPayload.data.users});
			});
	}
	
	
	sendInvitation() {
		console.log("Invitation.sendInvitation: http[event/create?]");

		var param = {
			eventid : this.state.event.id,
			email : this.state.email,
			inviteduserid: this.state.inviteduserid,
			message : this.state.message,
			role: this.state.role
		}
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/event/invitation', param, httpPayload => {
			console.log("Invitation payload=" + JSON.stringify(httpPayload.data));
			if (httpPayload.data.invitationSent) {
				this.setState({ "statusinvitation": "Invitation sent with success" });
				console.log("Invitation : register this new participant =" + JSON.stringify(httpPayload.data));
				this.props.participantInvited(httpPayload.data.participant);
			} else if (httpPayload.data.status === 'ALREADYAPARTICIPANT') {
				this.setState({ "statusinvitation": "This participant is already registered" });
			} else {
				this.setState({ "statusinvitation": "An error arrived "+ httpPayload.data.status });
			}

		});
	}



	
	pingParent() {
		console.log("Invitation.pingEvent child change");
	}
	

	
	
	// -------- Rest Call	
}
export default Invitation;

