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

import { TextInput, Select } from 'carbon-components-react';

import FactoryService from './service/FactoryService';
import EventSectionHeader from './component/EventSectionHeader';

import SlabRecord from './service/SlabRecord';

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
			event: props.event,

			listexpenses: [{
				name: 'helko',
			},
			{
				name: 'the',
			},
			{
				name: 'word',
			}]

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
		console.log("EventShoppinglist.setChildAttribut: set attribut:" + name + " <= " + value);
		const currentEvent = this.state.event;
		var SlabRecord;

		if (isChild) {
			item[name] = value;
			SlabRecord = SlabRecord.getUpdate(this.state.event, name, value, "/expense/" + item.id);
		} else {
			currentEvent[name] = value
			var SlabRecord = SlabRecord.getUpdate(this.state.event, name, value, "");
		}

		this.setState({ "event": currentEvent });
		this.props.updateEvent(SlabRecord);

	}


	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------
	test () {
		
		const solution = (tree) => {
			
			
	    	// Type your solution here
			if (tree.length==0)
				return 0; 
	    	var i =1;
			var root = {};
			var queue=[];
			queue.push(root);
			
	    	var i =1;
	    	while (i <tree.length) {
		        var node = queue[ 0 ];
			
				queue.shift();
				var left=tree[ i ];
				var right=tree[ i + 1 ];
				console.log("node="+node.value+" left="+left+" right="+right);
				if (left!=-1) {
					var nodechild = { value : left };
					node.left = nodechild;	
					queue.push( nodechild);
				}
				if (right!=-1) {
					var nodechild = { value : right };
					node.right = nodechild;	
					queue.push( nodechild);
				}
				i = i+2;
			}
			
			
			// second step, calculate the depth of the tree
			function rundepth( node ) {
				var depth= 1
				if (node.left) {
					depth = rundepth( node.left)+1;
				}
				if (node.right) {
					var depri = rundepth( node.right)+1;
					if (depri > depth)
						depth = depri;
				}
				return depth;
			}
			 var calcul = rundepth( root );
			return calcul;
			}
	


		// console.log(solution( [1, 1, -1, 1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1])); 

		console.log(solution( [1, 2, -1, 3, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1])); 
	}

	

}


export default EventExpense;
