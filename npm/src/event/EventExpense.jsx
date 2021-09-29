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


import FactoryService from 'service/FactoryService';
import EventSectionHeader from 'component/EventSectionHeader';


// -----------------------------------------------------------
//
// EventExpense
//
// Display one event
//
// -----------------------------------------------------------

class EventExpense extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {
			event: props.event

		};
		// show : OFF, ON, COLLAPSE
		console.log("EventExpense.constructor show=" + this.state.show + " event=" + JSON.stringify(this.state.event));

	}



	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		var listHtml = [];
		var currencyService = FactoryService.getInstance().getCurrencyService();

		for (var i in this.state.listexpenses) {
			var line = this.state.listexpenses[i]
			listHtml.push(<tr>
				<td>{line.name}</td>
				<td>
					<button class="btn btn-success btn-xs"
						id={line.name}
						onClick={(event) => {
							console.log("EventItinerary.add : bob id=" + event.target.id);
						}
						}>
					</button>
				</td>
			</tr>

			)
		};


		// --- Header
		var headerSection = (
			<EventSectionHeader id="task"
				image="img/btnExpense.png"
				title={<FormattedMessage id="EventExpense.MainTitleExpense" defaultMessage="Expense" />}
				showPlusButton={true}
				showPlusButtonTitle={<FormattedMessage id="EventExpense.AddExpense" defaultMessage="Add a expense" />}
				userTipsText={<FormattedMessage id="EventExpense.ExpenseTip" defaultMessage="Register all expenses in the Event. Expenses from another section (Itinerary, Shopping List) are visible here" />}
			/>
		);

		return (<div>
			{headerSection}
			{this.getCurrencySelectHtml()}

			<button onClick={this.test} > Test </button>
			<table>
				{listHtml}
			</table>
		</div>
		)
	}


	getCurrencySelectHtml() {
		//---- List Currency
		var currencyService = FactoryService.getInstance().getCurrencyService();
		/*
		return (<Select labelText={<FormattedMessage id="EventExpense.CurrencyOnEvent" defaultMessage="Currency used in this event" />}
							id="currentEvent"
							value={this.props.eventPreferences.getCurrencyCode()}
							onChange={(event) => 
									{ this.props.eventPreferences.setCurrency( event.target.value);
										this.setState( {event: this.state.event});
									}
								}>
					{currencyService.getCurrencyList().map( (item) => {
						return ( <option value={item.code}> {item.code} {item.label}</option>)
					})}
				</Select>)
				*/
	}

	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	setChildAttribut(name, value, isChild, item) {
		console.log("EventExpense.setChildAttribut: set attribut:" + name + " <= " + value);
		const currentEvent = this.state.event;
		let slabRecord;

		if (isChild) {
			item[name] = value;
			slabRecord = slabRecord.getUpdate(this.state.event, name, value, "/expense/" + item.id);
		} else {
			currentEvent[name] = value
			slabRecord = slabRecord.getUpdate(this.state.event, name, value, "");
		}

		this.setState({ "event": currentEvent });
		this.props.updateEvent(slabRecord);

	}


	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------

	

}


export default EventExpense;
