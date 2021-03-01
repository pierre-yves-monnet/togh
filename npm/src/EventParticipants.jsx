// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { Select } from 'carbon-components-react';
import { Tag } from 'carbon-components-react';
import Invitation from './Invitation';

class EventParticipants extends React.Component {
	
	// this.props.pingEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : props.event, 
						'show' : props.show,
						'collapse' : props.collapse
						};
		// show : OFF, ON, COLLAPSE
		console.log("EventParticipant.constructor show="+ +this.state.show+" event="+JSON.stringify(this.state.event));
		this.collapse 				= this.collapse.bind(this);
		this.setChildAttribut		= this.setChildAttribut.bind(this);
		this.participantInvited 	= this.participantInvited.bind( this );
	}	


	render() {
		console.log("EventParticipant.render: visible="+this.state.show+" Participants:"+JSON.stringify(this.state.event.participants) );
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		var listParticipantListHtml=[];
		//  
		//	
		if (this.state.event.participants) {
			listParticipantListHtml= this.state.event.participants.map((item,index) =>
				<tr key={index}>
					<td>
						{item.user !== '' && ( <div>{item.user.longlabel} </div>)}					
						<p/>
						{item.status === 'INVITED' && (<div class="label label-info"><FormattedMessage id="EventParticipant.InvitationInProgress" defaultMessage="Invitation in progress"/></div>)}
	
					</td>
					
					<td>
						{item.role ==='OWNER' && (<div class="label label-info"><FormattedMessage id="EventParticipant.Owner" defaultMessage="Owner"/></div>)}
						{item.role ==='LEFT' && (<div class="label label-info"><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></div>)}
						
						{ (item.role !=='OWNER' && item.role !== 'LEFT') && (
							<Select labelText="" disabled={item.status==='LEFT'} value={item.role} onChange={(event) => this.setAttribute( "role", event.target.value )}>
							 	<FormattedMessage id="EventParticipant.RoleOrganizer" defaultMessage="Organizer">
	                          		{(message) => <option value="ORGANIZER">{message}</option>}
	                    		</FormattedMessage>
								<FormattedMessage id="EventParticipant.RoleParticipant" defaultMessage="Participant">
	                          		{(message) => <option value="PARTICIPANT">{message}</option>}
	                    		</FormattedMessage>
								<FormattedMessage id="EventParticipant.RoleObserver" defaultMessage="Observer">
									{(message) => <option value="OBSERVER">{message}</option>}
	                    		</FormattedMessage>
								
							</Select>
								)}
					</td>
					<td>
						{item.status==='ACTIF' && <Tag  type="green" title={<FormattedMessage id="EventParticipant.TitleActiveParticipant" defaultMessage="Active participant"/>}><FormattedMessage id="EventParticipant.Actif" defaultMessage="Actif"/></Tag>}			
						{item.status==='INVITED' && <Tag  type="teal" title={<FormattedMessage id="EventParticipant.Titleinvited" defaultMessage="Invited participant: no confirmation is received at this moment"/>}><FormattedMessage id="EventParticipant.Invited" defaultMessage="Invited"/></Tag>}			
						{item.status==='LEFT' && <Tag  type="red" title={<FormattedMessage id="EventParticipant.TitleLeft" defaultMessage="The participant left the event"/>}><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></Tag>}			
					</td>
				</tr>
				);
			}
		// console.log("EventParticipant.render: list calculated from "+JSON.stringify( this.state.event.participantlist ));
		// console.log("EventParticipant.render: "+listParticipantListHtml.length);
		
		
			
	
		return ( <div>
					<div class="eventsection"> 
						<a href="secParticipantlist"></a>
						<a onClick={this.collapse} style={{verticalAlign: "top"}}>
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down" style={{fontSize: "small"}}></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"  style={{fontSize: "small"}}></span>}
						</a> <FormattedMessage id="EventParticipant.MainTitleParticipant" defaultMessage="Participants"/>
						<div style={{float: "right"}}>							
							<Invitation event={this.state.event} participantInvited={this.participantInvited}/>
						</div>
					</div> 

					{this.state.show ==='ON' && 	<table class="table table-striped toghtable">
						<thead>
							<tr >
								<th><FormattedMessage id="EventParticipant.Person" defaultMessage="Person"/></th>
								<th><FormattedMessage id="EventParticipant.Role" defaultMessage="Role"/></th>
								<th><FormattedMessage id="EventParticipant.Status" defaultMessage="Status"/></th>
							</tr>
						</thead>
						{listParticipantListHtml}
						</table>
					}
				</div>
				);
		}
		
	collapse() {
		console.log("EventParticipant.collapse");
		if (this.state.show === 'ON')
			this.setState( { 'show' : 'COLLAPSE' });
		else
			this.setState( { 'show' : 'ON' });
	}
	
	setChildAttribut( name, value, item ) {
		console.log("EventParticipant.setChildAttribut: set attribut:"+name+" <= "+value+" item="+JSON.stringify(item));
		const { event } = { ...this.state };
  		const currentEvent = event;

  		item[ name ] = value;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	
	
	participantInvited( participants ) {
		console.log("EventParticipant.participantinvited event="+JSON.stringify( this.state.event));
		var currentEvent = this.state.event;
		var newList = currentEvent.participants;
		for (var i in participants ) {		
			newList = newList.concat( participants[ i ]  );
		}
		currentEvent.participants = newList;
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
}		
export default EventParticipants;
