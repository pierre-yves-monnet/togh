/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import React from 'react';

import { FormattedMessage,FormattedDate } from "react-intl";

import { ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';
import { TextInput,  NumberInput, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle, Search } from 'carbon-components-react';
import CurrencyInput from 'react-currency-input';
 

import SlabRecord from 'service/SlabRecord';
import FactoryService from 'service/FactoryService'


import * as expenseConstant from 'event/EventExpense';


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

class Expense extends React.Component {
	
	// props.updateEvent must be defined
	// props.eventPreferences
	constructor(props) {
		super();
		this.eventCtrl =  props.eventCtrl;
		this.parentLocalisation = props.parentLocalisation;
		this.state = {
			
			'item': props.item,
			'event': props.event
		};
		
	}


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
						onChangeEvent={(event) => this.setAttribut("budget", event.target.value)}
						decimalSeparator="." thousandSeparator=","
						precision="2"
						prefix={this.eventCtrl.getEventPreferences().getCurrencySymbolPrefix()}
						suffix={this.eventCtrl.getEventPreferences().getCurrencySymbolSuffix()}
					/>
					 
					</td><td style={{paddingLeft: "10px"}}>
					{<FormattedMessage id="Expense.Cost" defaultMessage="Cost" />}<br/>
					<CurrencyInput class="bx--text-input bx--text__input" value={this.state.item.cost} 
						onChangeEvent={(event) => this.setAttribut("cost", event.target.value)}
						decimalSeparator="." thousandSeparator=","
						precision="2"
						prefix={this.eventCtrl.getEventPreferences().getCurrencySymbolPrefix()}
						suffix={this.eventCtrl.getEventPreferences().getCurrencySymbolSuffix()}
						 />
					</td><td style={{paddingLeft: "10px", verticalAlign: "middle"}}>								
						<button class="btn btn-primary btn-xs"><Cash/></button>

					</td>
					</tr></table>
				</div>
			</div>
		)
	}

	setAttribut(name, value, item) {
		console.log("Expense.setAttribut: set attribut:" + name + " <= [" + value + "] item=" + JSON.stringify(item));
		var item = this.state.item;
		item[name] = value;

		console.log("EventTasklist.setAttribut: set attribut:" + name + " <= " + value + +" Localisation="+this.parentLocalisation+" item=" + JSON.stringify(item));
		
		this.eventCtrl.setAttribut(name, value, item, this.parentLocalisation);

		/*
		this.setState({ item: item });
		
		var SlabRecord = SlabRecord.getUpdate(this.state.event, name, value, item);
		console.log("Expense.setAttribut Slab="+SlabRecord.getString());
		 
		this.props.updateEvent( SlabRecord );
		*/
	}


	

}





export default Expense;
