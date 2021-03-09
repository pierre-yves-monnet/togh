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
import { ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';
import { TextInput,  NumberInput, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle, Search } from 'carbon-components-react';
import CurrencyInput from 'react-currency-input';
 

import SlabEvent from './../service/SlabEvent';
import FactoryService from './../service/FactoryService'


import * as expenseConstant from './../EventExpense';


class Expense extends React.Component {
	
	// props.updateEvent must be defined
	// props.eventPreferences
	constructor(props) {
		super();
		this.state = {
			'item': props.item,
			'event': props.event
		};
		
	}


//	get_currency(item){
//		if (item.currency==="euro"){
//			return "suffix=\"�\""
//		}
//		if (item.currency==="dollar"){
//			return "prefix=\"$\""
//		}
//		
//	}
	
	//  -------------------------------------------- Render
	// Function to decide prefix or suffix and symbol
	// labelText={<FormattedMessage id="Expense.Budget" defaultMessage="Budget" />}
	// Divs from Search - removed because probably useless
	// <div class="bx--form-item bx--text-input-wrapper">
	// <div class="bx--text-input__field-outer-wrapper">
	// <div class="bx--text-input__field-wrapper">
	// </div></div></div>
	render() {
		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="EventItineray.Expense" defaultMessage="Expense" />
				</div>
				<div class="card-body">
					<table><tr><td>
					{<FormattedMessage id="Expense.Budget" defaultMessage="Budget" />}<br/>
					<CurrencyInput class="bx--text-input bx--text__input" 
						value={this.state.item.budget} 
						onChangeEvent={(event) => this.setChildAttribut("budget", event.target.value)}
						decimalSeparator="." thousandSeparator=","
						precision="2"
						prefix={this.props.eventPreferences.getCurrencySymbolPrefix()}
						suffix={this.props.eventPreferences.getCurrencySymbolSuffix()}
					/>
					 
					</td><td>
					{<FormattedMessage id="Expense.Cost" defaultMessage="Cost" />}<br/>
					<CurrencyInput class="bx--text-input bx--text__input" value={this.state.item.price} onChangeEvent={(event) => this.setChildAttribut("price", event.target.value)}
						decimalSeparator="." thousandSeparator=","
						precision="2"
						prefix={this.props.eventPreferences.getCurrencySymbolPrefix()}
						suffix={this.props.eventPreferences.getCurrencySymbolSuffix()}
						 />
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
