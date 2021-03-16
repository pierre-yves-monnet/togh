// -----------------------------------------------------------
//
// EventPreferences
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { TextInput, Select } from 'carbon-components-react';

import FactoryService from './service/FactoryService';
import EventSectionHeader from './component/EventSectionHeader';

import SlabEvent from './service/SlabEvent';


class EventPreferences extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;
		this.state = { event : props.event, 
						};
		// show : OFF, ON, COLLAPSE
		console.log("EventExpense.constructor show="+ this.state.show+" event="+JSON.stringify(this.state.event));
		
	}


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		var currencyService = FactoryService.getInstance().getCurrencyService();
		// --- Header
		var headerSection =(
			<EventSectionHeader id="task" 
				image="img/btnExpense.png" 
				title={<FormattedMessage id="EventPreferences.MainTitlePreferences" defaultMessage="Preference" />}
				showPlusButton  = {false}
				userTipsText={<FormattedMessage id="EventPreferences.ExpenseTip" defaultMessage="Set up all preferences for the event" />}
				/>
				);
	
		return (<div>
				{headerSection}
				<Select labelText={<FormattedMessage id="EventPreferences.CurrencyOnEvent" defaultMessage="Currency used in this event" />}
							id="currentEvent"
							value={this.eventCtrl.getEventPreferences().getCurrencyCode()}
							onChange={(event) => 
									{ this.eventCtrl.getEventPreferences().setCurrency( event.target.value);
										this.setState( {event: this.state.event});
									}
								}>
					{currencyService.getCurrencyList().map( (item) => {
						return ( <option value={item.code}> {item.code} {item.label}</option>)
					})}
				</Select>
	
				</div>
			)
	}
	
	
		
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	
	setAttribut(name, value, item ) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:" + name + " <= " + value );
		this.eventCtrl.setAttribut( name, value, this.state.event, "");

	}
	
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


}


export default EventPreferences;
	