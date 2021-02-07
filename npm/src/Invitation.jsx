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
import { ModalWrapper, RadioButtonGroup, RadioButton, TextInput, TextArea, Select, Checkbox, InlineLoading } from 'carbon-components-react';


import FactoryService from './service/FactoryService';

class Invitation extends React.Component {
	constructor( props ) {
		super();
		// participantInvited()
		// console.log("RegisterNewUser.constructor");
		this.state = { 'event' : props.event,
						'email' : '',
						'searchFirstName'  : '',
						'searchLastName' : '',
						'searchPhoneNumber': '',		
						'searchEmail' : '',
						'onlyNonInvitedUser': true,				
						'role': 'PARTICIPANT',
						'panelVisible': 'INVITATION',
						'message': 'Please join this event.',
						'statusinvitation' : '',
						'listSearchUsers' : [],
						'listUsersSelected':{},
						'countusers' : -1,
						'inprogresssearch' : false,
						'inprogressinvitation' : false,
						'messageServerSearch' : ''
						};

		// this is mandatory to have access to the variable in the method... thank you React!   
		this.sendInvitation = this.sendInvitation.bind(this);
		this.searchToghUser	= this.searchToghUser.bind(this);
		// this is mandatory to have access to the variable in the method... thank you React!   

	}


	render() {
		console.log("Invitation.render ");
		// console.log("Invitation.listSearch="+ JSON.stringify( this.state.listSearchUsers ));
		
		var listSearchUsersHtml = [];
		if (this.state.listSearchUser !== null) {
			// console.log("Invitation.buildListUser");
			listSearchUsersHtml = this.state.listSearchUsers.map((toghUser) =>
				<tr key={toghUser.id}>
					<td><Checkbox labelText="" 
						id={`${toghUser.id}`} 
						onChange={(value,event ) => {
							console.log("Invitation.SelectUser type="+JSON.stringify(event));
							var listUsersSelected = this.state.listUsersSelected;
							listUsersSelected[ event ]= value;
							this.setState( {"listUsersSelected": listUsersSelected})
							}
							
							}/></td>
					<td>
						{toghUser.firstName} {toghUser.lastName}   
					</td>
					<td>{toghUser.phonenumber}</td>
					<td>{toghUser.email}</td>
				</tr>
			);
		}
		// console.log("Invitation.listSearchHtml="+listSearchUsersHtml);
		
		return ( 
			 <ModalWrapper
     			 buttonTriggerText="Invitation"
     			 modalLabel="Invitation"
				primaryButtonText="Close"
				secondaryButtonText=''
				size='lg'>
					<div style={{display: "inline-block"}}>
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
					</div>
					
					<div style={{display: "inline-block", float: "right"}}>	
						<button class="btn btn-info btn-lg" 
							onClick={this.sendInvitation}
							disabled={! this.enableInvite() }
							class="btn btn-primary">
							<div class="glyphicon glyphicon-envelope"  style={{display: "inline-block"}}> </div>
							{this.state.inprogressinvitation && (<div style={{display: "inline-block"}}><InlineLoading description="inviting" status='active'/></div>) }
						    {this.state.inprogressinvitation === false && (<div style={{display: "inline-block"}}>&nbsp;Send invitation</div>)}
						</button>
						<br />
						{ this.state.statusErrorInvitation && (<div class="alert alert-warning">{this.state.statusErrorInvitation}</div>)}
						{ this.state.statusOkInvitation && (<div class="alert alert-success">{this.state.statusOkInvitation}</div>)}
					</div>


      				<br/>
      				{this.state.panelVisible === 'INVITATION' && (<div>
							<TextInput labelText="Email" value={this.state.email} onChange={(event) => this.setState({ email: event.target.value })} ></TextInput><br />
						</div>)
					}		
					{this.state.panelVisible === 'SEARCH' && (<div>
							<table>
							<tr>
								<td style={{"paddingRight": "10px"}}><TextInput labelText="First name" value={this.state.searchFirstName} onChange={(event) => this.setState( { searchFirstName: event.target.value })} ></TextInput>
								</td><td style={{ "paddingRight": "10px"}}><TextInput labelText="Last name" value={this.state.searchLastName} onChange={(event) => this.setState( { searchLastName: event.target.value })}></TextInput>
								</td><td style={{ "paddingRight": "10px"}}><TextInput labelText="Phone number" value={this.state.searchPhoneNumber} onChange={(event) => this.setState( { searchPhoneNumber: event.target.value })} ></TextInput>
								</td><td style={{ "paddingRight": "10px"}}><TextInput labelText="Email" value={this.state.searchEmail} onChange={(event) => this.setState( {searchEmail: event.target.value })} ></TextInput>
							</td></tr>
							<tr><td style={{ "paddingRight": "10px"}} colspan="4">
								<Checkbox labelText="Only users not already invited" 
									 
									onChange={(value,event ) => {
									console.log("Invitation.OnlyNonInvitedUser type="+JSON.stringify(event));
									this.setState( {onlyNonInvitedUser: value})
									}
								}/></td></tr> 
							</table>
							<br/>
							<button class="btn btn-info btn-lg" onClick={this.searchToghUser} class="btn btn-info" disabled={this.state.inprogresssearch}>	
								
									<div class="glyphicon glyphicon-search" style={{display: "inline-block"}}> </div>
									{this.state.inprogresssearch && (<div style={{display: "inline-block"}}><InlineLoading description="searching" status='active'/></div>) }
									{this.state.inprogresssearch === false && (<div style={{display: "inline-block"}}>&nbsp;Search</div>)}
								
							</button>
							<br/>
							{this.state.countusers > -1 && (<div style={{display: "inline-block"}}> {this.state.countusers} users found<br/></div> )}
							{this.state.messageServerSearch && (<div class="alert alert-danger">{this.state.messageServerSearch}</div>)}
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
					<table>
						<tr>
							<td style={{"paddingRight": "10px"}}>
								<Select labelText="Role in this event" value={this.state.role} onChange={(event) => this.setState({ role: event.target.value })}>
									<option value="ORGANIZER">Organizer</option>
									<option value="PARTICIPANT">Participant</option>
									<option value="OBSERVER">Observer</option>
								</Select>
							</td><td>
								<TextArea labelText="Message" value={this.state.message} onChange={(event) => this.setState({ message: event.target.value })} ></TextArea><br />
							</td>
						</tr>
					</table>
								
    		</ModalWrapper>
		);
	};

	
	enableInvite() {
		if (this.state.email.length>0)
			return true;
		if (this.state.listSearchUsers.length==0)
			return false;
		// check : one must be check to enable the 
		for (var i in this.state.listUsersSelected) {
			if (this.state.listUsersSelected[ i ] === true) {		
				return true;
			}
		}
		return false;
	}
	/**
	 * searchToghUser
	 */
	searchToghUser() {
		console.log("Invitation.Search: searchToghUser");
		// console.log("Invitation.Search: search by firstName="+this.state.firstName);

		this.setState( { listSearchUsers: [], 
			countusers: -1, 
			listUsersSelected: {}, 
			messageServerSearch:'', 
			statusErrorInvitation: '', 
			statusOkInvitation:'' });
			
		this.setState( {inprogresssearch: true });

		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson('/api/user/search?firstName='+this.state.searchFirstName
			+'&lastName='+this.state.searchLastName
			+'&email='+this.state.searchEmail
			+'&phoneNumber='+this.state.searchPhoneNumber
			+'&onlyNonInvitedUser='+this.state.onlyNonInvitedUser
			+'&eventid='+this.state.event.id, 
			this, 
			httpPayload => {
				console.log("Invitation payload=" + httpPayload.trace());
				this.setState( {inprogresssearch: false });
				if (httpPayload.isError()) {
					this.setState( {messageServerSearch: "Server connection error" } );
				}
				else {
					 this.setState( { listSearchUsers: httpPayload.getData().users, countusers: httpPayload.getData().countusers});
				}
			});
	}
	
	
	sendInvitation() {
		console.log("Invitation.sendInvitation: http[event/create?]");

		var param = {
			eventid : this.state.event.id,
			email : this.state.email,
			listUsersid: [],
			message : this.state.message,
			role: this.state.role,
			type: this.state.panelVisible,
		}

		// keep the ID a String 
		for( var i in this.state.listUsersSelected) {
			if (this.state.listUsersSelected[ i ] === true) {
				param.listUsersid.push(  i  );
			}
		}
		console.log("Invitation.sendInvitation: http[event/create?] param="+JSON.stringify(param));
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		this.setState( {inprogressinvitation: true, statusErrorInvitation: '', statusOkInvitation:'' });
		restCallService.postJson('/api/event/invitation', param, httpPayload => {
			console.log("Invitation.callback !!! payload=" + JSON.stringify(httpPayload.data));
			if (! httpPayload.data) {
				console.log("Invitation.callback Rebound ?");
				return;
			}
			this.setState( {inprogressinvitation: false });
			if (httpPayload.data.status === 'INVITATIONSENT') {
				this.setState({ "statusOkInvitation": "Invitation sent with success"+httpPayload.data.okMessage });
				console.log("Invitation.callback : register this new participant =" + JSON.stringify(httpPayload.data));
			} else if (httpPayload.data.status === 'ALREADYAPARTICIPANT') {
				this.setState({ "statusErrorInvitation": "This participant is already registered:"+httpPayload.data.errorMessage  });
				this.setState({ "statusOkInvitation": "Invitation sent with success"+httpPayload.data.okMessage });

			} else {
				this.setState({ "statusErrorInvitation": "An error arrived "+ httpPayload.data.status });
			}
			this.props.participantInvited(httpPayload.data.participants);

		});
	}



	
	pingParent() {
		console.log("Invitation.pingEvent child change");
	}
	

	
	
	// -------- Rest Call	
}
export default Invitation;

