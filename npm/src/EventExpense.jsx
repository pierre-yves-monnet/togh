// -----------------------------------------------------------
//
// EventExpense
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { TextInput, Toggle } from 'carbon-components-react';


import GoogleMapReact from 'google-map-react';
import Geocode from "react-geocode";



import { Icon } from '@iconify/react'
import locationIcon from '@iconify/icons-mdi/map-marker'


class EventExpense extends React.Component {
	
	// this.props.updateEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : props.event, 
						'show' : props.show,
						'collapse' : props.collapse,
						listexpenses : [ {
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
		console.log("EventExpense.constructor show="+ this.state.show+" event="+JSON.stringify(this.state.event));
		
	}

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		var listHtml = [];
		for (var i in this.state.listexpenses) {
			var line = this.state.listexpenses[ i ]
			listHtml.push(<tr>
						<td>{line.name}</td>
						<td>
							<button class="btn btn-success btn-xs" 
							id={line.name}
							onClick={(event) => {
								console.log("EventItinerary.add : bob dateIndexPublish="+"  id="+event.target.id);
								}
							}>
							</button>
						</td>
					</tr>)
			};
		
		
		return (
			<table>
			{listHtml}
			</table>
			)
	}
}


export default EventExpense;
	