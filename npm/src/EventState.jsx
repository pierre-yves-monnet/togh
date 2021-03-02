// -----------------------------------------------------------
//
// EventState
//
// Display the event state
//
// -----------------------------------------------------------

import React from 'react';

import { FormattedMessage } from "react-intl";


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
		console.log("EventState.render Status="+JSON.stringify(this.state.statusEvent));

		var tagHtml = null;
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
					<FormattedMessage  id="EventState.InPreparation" defaultMessage="In Preparation">
                       		{(message) => <OverflowMenuItem className="INPREPAR" itemText={message} />}
                    </FormattedMessage>
					<FormattedMessage  id="EventState.Actif" defaultMessage="Actif">
                       		{(message) => <OverflowMenuItem className="INPROG" itemText={message}/>}
                    </FormattedMessage>
					<FormattedMessage  id="EventState.Done" defaultMessage="Done">
                       		{(message) => <OverflowMenuItem className="CLOSED" itemText={message} />}
                    </FormattedMessage>
					<FormattedMessage  id="EventState.Cancelled" defaultMessage="Cancelled">
                       		{(message) => <OverflowMenuItem className="CANCEL" itemText={message}/>}
                    </FormattedMessage>
						
				</OverflowMenu>)
		}
      
      	if (this.state.statusEvent === 'INPREPAR')
			tagHtml = (<Tag  type="teal" title="Event are in preparation">
							<FormattedMessage  id="EventState.InPreparation" defaultMessage="In Preparation"/> 
							{dropDownChangeHtml}
						</Tag>)			
		else if (this.state.statusEvent === 'INPROG')
			tagHtml = (<Tag  type="green" title="Event in progress, let's have fun! '">
							<FormattedMessage  id="EventState.Actif" defaultMessage="Actif"/> 
							{dropDownChangeHtml}
						</Tag>);
		else if (this.state.statusEvent === 'CLOSED')
			tagHtml = (<Tag  type="warm-gray" title="Event is finished, hope you had fun">
							<FormattedMessage  id="EventState.Done" defaultMessage="Done"/> 
							{dropDownChangeHtml}
						</Tag>);
		else if (this.state.statusEvent === 'CANCEL')
			tagHtml =(<Tag  type="red" title="Oups, this event was cancelled">
						<FormattedMessage  id="EventState.Cancelled" defaultMessage="Cancelled"/> 
						{dropDownChangeHtml}
					</Tag>);
		else 
			tagHtml =(<Tag  type="gray" title="Something strange arrived">{this.state.statusEvent} {dropDownChangeHtml}</Tag>);


		return (<div>{tagHtml}</div>);
	};
};
export default EventState;