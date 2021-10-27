/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { FormattedMessage } from "react-intl";
import { Select } from 'carbon-components-react';
import FactoryService 		from 'service/FactoryService';
import EventSectionHeader 	from 'component/EventSectionHeader';

// -----------------------------------------------------------
//
// EventPreferences
//
// Display one event
//
// -----------------------------------------------------------

class EventPreferences extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;
		this.state = {};
		// show : OFF, ON, COLLAPSE
		console.log("EventPreferences.constructor show="+ this.state.show+" event="+JSON.stringify(this.state.event));
		
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
			<EventSectionHeader id="eventPreferences" 
				image="img/btnPreferences.png" 
				title={<FormattedMessage id="EventPreferences.MainTitlePreferences" defaultMessage="Preferences" />}
				showPlusButton  = {false}
				userTipsText={<FormattedMessage id="EventPreferences.PreferencesTip" defaultMessage="Set up all preferences for the event" />}
				/>
				);
		return (<div>
		
					{headerSection}
					
					<table class="toghtable" style={{marginBottom:"10px"}}>
					<tr>
						<td><FormattedMessage id="EventPreferences.CurrencyOnEvent" defaultMessage="Currency used in this event" /></td>
						<td>
							<Select labelText=""
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
						</td>
					</tr>
					<tr>
						<td><FormattedMessage id="EventPreferences.EventSubscription" defaultMessage="Subscription" /></td>
						<td>{this.eventCtrl.getEvent().subscriptionEvent}<br/>
							{this.eventCtrl.getEvent().subscriptionEvent === 'FREE' 
								&& <FormattedMessage id="EventPreferences.SubscriptionFreeExplanation" 
									defaultMessage="A Free event contains all functions except the budget and is limited in the number of participants (20) and items (50)" />}
							{this.eventCtrl.getEvent().subscriptionEvent === 'PREMIUM' 
								&& <FormattedMessage id="EventPreferences.SubscriptionPremiumExplanation" 
									defaultMessage="A Premium event contains all functions, upper limitation participants (100) and items (100)" />}
							{this.eventCtrl.getEvent().subscriptionEvent === 'EXCELLENCE' 
								&& <FormattedMessage id="EventPreferences.SubscriptionExcellenceExplanation" 
									defaultMessage="A Excellence event contains all functions, and a limit of 1000 per items" />}
						</td>
					</tr>
					</table>
	
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
	