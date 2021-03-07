// -----------------------------------------------------------
//
// Expense
//
// Describe and give an expense item : 
//  - the original budget
//  - the price
//  - then a button to split the expense between participants 
//
// -----------------------------------------------------------
//

import React from 'react';

import { FormattedMessage,FormattedDate } from "react-intl";
import {  ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';
import { TextInput,  NumberInput, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle } from 'carbon-components-react';

import SlabEvent from './../service/SlabEvent';


class Expense extends React.Component {
	
	// props.updateEvent must be defined
	constructor(props) {
		super();
		this.state = {
			'item': props.item,
			'event': props.event
		};
		
	}


	
	
	//  -------------------------------------------- Render
	render() {
		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="EventItineray.Expense" defaultMessage="Expense" />
				</div>
				<div class="card-body">
					<table><tr><td>
					<TextInput value={this.state.item.budget} onChange={(event) => this.setChildAttribut("budget", event.target.value)} 
						labelText={<FormattedMessage id="Expense.Budget" defaultMessage="Budget" />} 
						step={0.01} />
					</td><td>
					<TextInput value={this.state.item.price} onChange={(event) => this.setChildAttribut("price", event.target.value)} 
						labelText={<FormattedMessage id="Expense.Price" defaultMessage="Price" />} disable="true"/>
					</td><td>								
					<button class="btn btn-primary btn-xs"><Cash/></button>
					</td>
					</tr></table>
				</div>
			</div>
		)
	}

	setChildAttribut(name, value, item) {
		console.log("Expense.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		var item = this.state.item;
		item[name] = value;

		this.setState({ item: item });
		
		var slabEvent = SlabEvent.getUpdate(this.state.event, name, value, item);
		console.log("Expense.setChildAttribut Slab="+slabEvent.getString());
		 
		this.props.updateEvent( slabEvent );
	}

}





export default Expense;
