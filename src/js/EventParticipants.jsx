// -----------------------------------------------------------
//
// EventParticipant
//
// Display participants
//
// -----------------------------------------------------------

class EventParticipant extends React.Component {
	
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
		this.addItem				= this.addItem.bind(this);
	}


	render() {
		console.log("EventParticipant.render: visible="+this.state.show);
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		var listParticipantListHtml=[];
		listParticipantListHtml= this.state.event.participants.map((item) =>
			<tr class="itemcontent">
				<td>{item.firstName} {item.lastName}</td>
				
				<td>
					<select value={item.role} onChange={(event) => this.setAttribut( "role", event.target.value )}>
							<option value="ORGANIZER">Organizer</option>
							<option value="PARTICIPANT">Participant</option>
							<option value="OBSERVER">Observer</option>
							<option value="LEFT">Left</option>
						</select>
				</td>
			</tr>
			);
		console.log("EventParticipant.render: list calculated from "+JSON.stringify( this.state.event.participantlist ));
		console.log("EventParticipant.render: "+listParticipantListHtml.length);
		
		// invitation
		var invitationPanel = ( <div class="panel panel-info">
			<div class="panel-heading">Invitation</div>
			<div class="panel-body">
				<Invitation />
			</div>
			</div> );
		
		return ( <div>
					<div class="eventsection"> 
						<a href="secParticipantlist"></a>
						<a onClick={this.collapse}>
							{this.state.show == 'ON' && <span class="glyphicon glyphicon-chevron-down"></span>}
							{this.state.show == 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"></span>}
						</a> Participants
						<div style={{float: "right"}}>
							<button class="btn btn-success btn-xs glyphicon glyphicon-plus" onClick={this.addItem} title="Invite a new person">Invit</button>
						</div>
					</div> 
					{this.state.showInvitation && {invitationPanel} }
					{this.state.show =='ON' && 	<table class="table table-striped">
											<tr class="itemheader">
											
												<th>Person</th>
												<th>Role</th>
											</tr>
											{listParticipantListHtml}
											</table>
					}
				</div>
				);
		}
		
	collapse() {
		console.log("EventParticipant.collapse");
		if (this.state.show == 'ON')
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
	
	addItem() {
		console.log("EventParticipant.setChildAttribut: addItem item="+JSON.stringify(this.state.event));

		var currentEvent = this.state.event;		
		const newList = currentEvent.shoppinglist.concat( {}  );
		currentEvent.participantlist = newList;
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	
}		
	