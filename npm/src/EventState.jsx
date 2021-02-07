// -----------------------------------------------------------
//
// EventState
//
// Display the event state
//
// -----------------------------------------------------------

import React from 'react';


// import { Button } from 'carbon-components-react';
import { Tag } from 'carbon-components-react';
import { OverflowMenu } from 'carbon-components-react';
import { OverflowMenuItem } from 'carbon-components-react';

class EventState extends React.Component {
	// this.props.changeState();

	constructor( props ) {
		super();
		
		
		this.state = { 'statusEvent' : props.statusEvent,
						'modifyEvent' : props.modifyEvent}
	}

	
//----------------------------------- Render
	render() {
		console.log("Event.render eventId="+this.props.eventid + " event="+JSON.stringify(this.state.statusEvent));

		var tagHtml = null;
		if (this.state.statusEvent === 'INPREPAR')
			tagHtml = (<Tag  type="teal" title="Event are in preparation">In preparation</Tag>)			
		else if (this.state.statusEvent === 'INPROG')
			tagHtml = (<Tag  type="green" title="Event in progress, let's have fun! '">Actif</Tag>);
		else if (this.state.statusEvent === 'CLOSED')
			tagHtml = (<Tag  type="warm-gray" title="Event is finished, hope you had fun">Done</Tag>);
		else if (this.state.statusEvent === 'CANCEL')
			tagHtml =(<Tag  type="red" title="Oups, this event was cancelled">Cancelled</Tag>);
		else 
			tagHtml =(<Tag  type="gray" title="Something strange arrived">{this.state.statusEvent}</Tag>);

		var dropDownChangeHtml = (<div></div>);
		if (this.state.modifyEvent) {
			dropDownChangeHtml = (
				<OverflowMenu
      				selectorPrimaryFocus={'.'+ this.state.statusEvent}
					onChange={(event) => { 
						console.log("EventState: Click ");
						this.props.changeState( event );
						}
					}
    			>
      				<OverflowMenuItem className="INPREPAR" itemText="In Preparation"/>
      				<OverflowMenuItem className="INPROG" itemText="Actif"/>
      				<OverflowMenuItem className="CLOSED" itemText="Done"/>
      				<OverflowMenuItem className="CANCEL" itemText="Cancelled"/>
        		</OverflowMenu>)
		}
      
      
		return (<div>
			<table><tr>
				<td> {tagHtml}</td> 
				<td>{dropDownChangeHtml}</td>
				</tr>
			</table>
			</div>);
	};
};
export default EventState;