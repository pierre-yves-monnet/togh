// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import { Select, Tag } from 'carbon-components-react';

import Invitation from 'event/Invitation';


export const ROLE_OWNER = 'OWNER';
export const ROLE_ORGANIZER = 'ORGANIZER';
export const ROLE_PARTICIPANT = 'PARTICIPANT';
export const ROLE_OBSERVER = 'OBSERVER';
export const ROLE_OUTSIDE = 'OUTSIDE';

export const STATUS_LEFT = 'LEFT';
export const STATUS_ACTIF = 'ACTIF';



class EventParticipants extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("EventParticipants.constructor");

		this.state = { 'event' : props.event 
						};
		// show : OFF, ON, COLLAPSE
		// console.log("EventParticipant.constructor ");
		this.setChildAttribut		= this.setChildAttribut.bind(this);
		this.participantInvited 	= this.participantInvited.bind( this );
	}	


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	render() {
		const intl = this.props.intl;

		console.log("EventParticipant.render:  Participants:"+JSON.stringify(this.state.event.participants) );
		// show the list
		let listParticipantListHtml=[];

		//	
		if (this.state.event.participants) {
			console.log("EventParticipant.render:  Calcul the list" );
			listParticipantListHtml= this.state.event.participants.map((item,index) =>
				<tr key={index}>
					<td>
						{item.user !== '' && ( <div>{item.user.longlabel} </div>)}					
						<p/>
						{item.status === 'INVITED' && (<div class="label label-info"><FormattedMessage id="EventParticipant.InvitationInProgress" defaultMessage="Invitation in progress"/></div>)}
	
					</td>
					
					<td>
						{item.role === ROLE_OWNER && (<div class="label label-info"><FormattedMessage id="EventParticipant.Owner" defaultMessage="Owner"/></div>)}
						{item.status === STATUS_LEFT && (<div class="label label-info"><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></div>)}
						
						{ (item.role !== ROLE_OWNER && item.status !== STATUS_LEFT) && (
							<Select labelText="" disabled={item.status===STATUS_LEFT} 
								value={item.role} onChange={(event) => this.setAttribute( "role", event.target.value )}>
							 	<FormattedMessage id="EventParticipant.RoleOrganizer" defaultMessage="Organizer">
	                          		{(message) => <option value={ ROLE_ORGANIZER }>{message}</option>}
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
						{item.status==='ACTIF' && <Tag  type="green" title={intl.formatMessage({id: "EventParticipant.TitleActiveParticipant",defaultMessage: "Active participant"})}><FormattedMessage id="EventParticipant.Actif" defaultMessage="Actif"/></Tag>}			
						{item.status==='INVITED' && <Tag  type="teal" title={intl.formatMessage({id: "EventParticipant.Titleinvited",defaultMessage: "Invited participant: no confirmation is received at this moment"})}><FormattedMessage id="EventParticipant.Invited" defaultMessage="Invited"/></Tag>}			
						{item.status==='LEFT' && <Tag  type="red" title={intl.formatMessage({id: "EventParticipant.TitleLeft",defaultMessage: "The participant left the event"})}><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></Tag>}			
					</td>
				</tr>
			);
		}
		
		// console.log("EventParticipant.render: "+listParticipantListHtml.length);
	
		return ( <div>
					<div class="eventsection"> 
						<FormattedMessage id="EventParticipant.MainTitleParticipant" defaultMessage="Participants"/>
						<div style={{float: "right"}}>							
							<Invitation event={this.state.event} participantInvited={this.participantInvited}/>
						</div>
					</div> 

					<table class="table table-striped toghtable">
						<thead>
							<tr >
								<th><FormattedMessage id="EventParticipant.Person" defaultMessage="Person"/></th>
								<th><FormattedMessage id="EventParticipant.Role" defaultMessage="Role"/></th>
								<th><FormattedMessage id="EventParticipant.Status" defaultMessage="Status"/></th>
							</tr>
						</thead>
						{this.state.event.participants && this.state.event.participants.map( (item, index) => {
							return (<tr key={index}>
								<td>
									{item.user !== '' && ( <span>{item.user.longlabel} </span>)}					
									
									{item.status === 'INVITED' && (<span>
										<Tag type="teal">
										<FormattedMessage id="EventParticipant.InvitationInProgress" defaultMessage="Invitation in progress"/>
										</Tag></span>)}
								</td>
								<td>
								{item.role === ROLE_OWNER && (<div class="label label-info"><FormattedMessage id="EventParticipant.Owner" defaultMessage="Owner"/></div>)}
								{item.status === STATUS_LEFT && (<div class="label label-info"><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></div>)}
								
								{ (item.role !== ROLE_OWNER && item.status !== STATUS_LEFT) && (
									<Select labelText="" disabled={item.status===STATUS_LEFT} value={item.role} onChange={(event) => this.setAttribute( "role", event.target.value )}>
									 	<FormattedMessage id="EventParticipant.RoleOrganizer" defaultMessage="Organizer">
			                          		{(message) => <option value={ ROLE_ORGANIZER }>{message}</option>}
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
						{item.status==='ACTIF' && <Tag  type="green" title={intl.formatMessage({id: "EventParticipant.TitleActiveParticipant",defaultMessage: "Active participant"})}><FormattedMessage id="EventParticipant.Actif" defaultMessage="Actif"/></Tag>}			
						{item.status==='INVITED' && <Tag  type="teal" title={intl.formatMessage({id: "EventParticipant.Titleinvited",defaultMessage: "Invited participant: no confirmation is received at this moment"})}><FormattedMessage id="EventParticipant.Invited" defaultMessage="Invited"/></Tag>}			
						{item.status==='LEFT' && <Tag  type="red" title={intl.formatMessage({id: "EventParticipant.TitleLeft",defaultMessage: "The participant left the event"})}><FormattedMessage id="EventParticipant.Left" defaultMessage="Left"/></Tag>}			
					</td>
							</tr>
							)
					        } ) 
						}
				
								
					</table>
					
				</div>
				);
		}
		
	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	setChildAttribut( name, value, item ) {
		console.log("EventParticipant.setChildAttribut: set attribut:"+name+" <= "+value+" item="+JSON.stringify(item));
		const { event } = { ...this.state };
  		const currentEvent = event;

  		item[ name ] = value;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.updateEvent();
	}
	
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	
	participantInvited( participants ) {
		console.log("EventParticipant.participantinvited event="+JSON.stringify( this.state.event));
		var currentEvent = this.state.event;
		var newList = currentEvent.participants;
		for (var i in participants ) {		
			newList = newList.concat( participants[ i ]  );
		}
		currentEvent.participants = newList;
		this.setState( { "event" : currentEvent});
		this.props.updateEvent();
	}
	
	

}		
export default injectIntl(EventParticipants);
