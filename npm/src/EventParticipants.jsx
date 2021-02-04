// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------
import React from 'react';

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
	}


	render() {
		console.log("EventParticipant.render: visible="+this.state.show, );
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		var listParticipantListHtml=[];
		//  
		//	

		listParticipantListHtml= this.state.event.participants.map((item) =>
			<tr key={item.id}>
				<td>
					{item.user !== '' && ( <div>{item.user.firstName} {item.user.lastName} {item.user.email} </div>)}
					{item.user !== '' && item.user.phoneNumber && (<div> {item.user.phoneNumber} </div>)}
					<p/>
					{item.status === 'INVITED' && (<div class="label label-info">Invitation in progress</div>)}

				</td>
				
				<td>
					{item.role ==='OWNER' && (<div class="label label-info">Owner2</div>)}
					
					{item.role !=='OWNER' && (
						<Select labelText="" disabled={item.status==='LEFT'} value={item.role} onChange={(event) => this.setAttribut( "role", event.target.value )}>
								<option value="ORGANIZER">Organizer</option>
								<option value="PARTICIPANT">Participant</option>
								<option value="OBSERVER">Observer</option>
								<option value="LEFT">Left</option>
							</Select>
							)}
				</td>
				<td>
					{item.status==='ACTIF' && <Tag  type="green" title="Active participant">Actif</Tag>}			
					{item.status==='INVITED' && <Tag  type="teal" title="Invited participant. The participant didn't confirm yet'">Invited</Tag>}			
					{item.status==='LEFT' && <Tag  type="red" title="The participant left the event">Left</Tag>}			
				</td>
			</tr>
			);
		console.log("EventParticipant.render: list calculated from "+JSON.stringify( this.state.event.participantlist ));
		console.log("EventParticipant.render: "+listParticipantListHtml.length);
		
		
			
	
		return ( <div>
					<div class="eventsection"> 
						<a href="secParticipantlist"></a>
						<a onClick={this.collapse}>
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down"></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"></span>}
						</a> Participants
						<div style={{float: "right"}}>							
							<Invitation event={this.state.event} participantInvited={this.participantInvited}/>
						</div>
					</div> 

					{this.state.show ==='ON' && 	<table class="table table-striped toghtable">
											<thead>
												<tr >
													<th>Person</th>
													<th>Role</th>
													<th>Status</th>
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
	
	
	
	participantInvited( participant ) {
		console.log("EventParticipant.participantinvated");
		var currentEvent = this.state.event;		
		const newList = currentEvent.participants.concat( participant  );
		currentEvent.participants = newList;
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
}		
export default EventParticipants;