// -----------------------------------------------------------
//
// EventShoppingList
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl"; 

import { TextInput, TextArea, OverflowMenu, OverflowMenuItem, Tag, Toggle } from 'carbon-components-react';

import {  ArrowUp, ArrowDown, Cash, DashCircle, PlusCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import ChooseParticipant from './component/ChooseParticipant';
import Expense from './component/Expense';

import SlabEvent from './service/SlabEvent';
		
class EventShoppingList extends React.Component {
	// this.props.updateEvent()
	// props.eventPreferences is provide (or should be at least, I think, I don't know)
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {
			'event': props.event,
			show: {
				showDetail: true,
				showExpense : false
			}
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setChildAttribut 			= this.setChildAttribut.bind(this);
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.addItem 					= this.addItem.bind(this);
		this.changeParticipant 			= this.changeParticipant.bind(this);
	}
	
	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		console.log("EventShoppinglist.render: visible=" + this.state.show);
		
		var userParticipant = this.props.getUserParticipant();

		if (this.state.event.shoppinglist.length === 0) {
			return (
				<div>
					<FormattedMessage id="EventShoppingList.NoItem" defaultMessage="You don't have any item in the list." />
					{ userParticipant.isParticipant() && 
						<button class="btn btn-success btn-xs" 
							title={intl.formatMessage({id: "EventShoppingList.addItem",defaultMessage: "Add a new item in the list"})}
							onClick={() => this.addItem()}>
							<PlusCircle onClick={() => this.addItem()} />
							<FormattedMessage id="EventShoppingList.AddOne" defaultMessage="Add one !" />
						</button>
					}
				</div>
				)
		}

		// show the list
		var listShoppingListHtml = [];

		listShoppingListHtml = this.state.event.shoppinglist.map((item, index) =>
			<tr key={index}>
				<td> {this.getTagState(item)}</td>
				<td><TextInput value={item.what} onChange={(event) => this.setChildAttribut("what", event.target.value, item)} labelText="" ></TextInput></td>
				{this.state.show.showDetail && (<td><TextArea labelText="" value={item.description} onChange={(event) => this.setChildAttribut("description", event.target.value, item)} class="toghinput" labelText=""></TextArea></td>)}
				<td>
					<ChooseParticipant participant={item.who} event={this.state.event} modifyParticipant={true} pingChangeParticipant={this.changeParticipant} />
					<br/>
					{ this.state.show.showExpense &&  
						<Expense item={item.expense}
								 event={this.state.event}
								 eventPreferences={this.props.eventPreferences}
								 updateEvent={( slabEvent ) => 
									{ 	console.log("EventItinerary.ExpenseUpdate slab="+slabEvent.getString());
										this.props.updateEvent(slabEvent)
									} }
						/>
					}

				</td>
				<td><button class="btn btn-danger btn-xs" 					 
					title={intl.formatMessage({id: "EventShoppingList.removeItem",defaultMessage: "Remove this item"})}>
						<DashCircle onClick={() => this.removeItem(item)} />
					</button></td>
			</tr>
		);
		console.log("EventShoppinglist.render: list calculated from " + JSON.stringify(this.state.event.shoppinglist));
		console.log("EventShoppinglist.render: listsize=" + listShoppingListHtml.length);
		return (<div>
			<div class="eventsection">
				<FormattedMessage id="EventShoppingList.Title" defaultMessage="Shopping List" />
				<div style={{ float: "right" }}>
					<button class="btn btn-success btn-xs" 
						title={intl.formatMessage({id: "EventShoppingList.addItem",defaultMessage: "Add a new item in the list"})}>
						<PlusCircle onClick={() => this.addItem()} />
					</button>
				</div>
			</div>
			
			<div class="row">
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						labelA={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail"/>}
						labelB={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail"/>}
						onChange={(event) => this.setAttributeCheckbox( "showDetail", event.target.value )}
						defaultToggled={this.state.show.showDetail}
						id="showDetail" />
				</div>
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						labelA={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense"/>}
						labelB={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense"/>}
						onChange={(event) => this.setAttributeCheckbox( "showExpense", event.target.value )}
						defaultToggled={this.state.show.showExpense}
						id="showexpense" />
					</div>
			</div>
			<table class="table table-striped toghtable">
				<thead>
					<tr >
						<th><FormattedMessage id="EventShoppingList.What" defaultMessage="What" /></th>
						{this.state.show.showDetail && <th><FormattedMessage id="EventShoppingList.Description" defaultMessage="Description" /></th>}
						<th><FormattedMessage id="EventShoppingList.Who" defaultMessage="Who" /></th>
						<th></th>
					</tr>
				</thead>
				{listShoppingListHtml}
			</table>
			
		</div>
		);
	}

/** --------------------
 	*/
	setAttributeCheckbox(name, value) {
		console.log("EventTaskList.setCheckBoxValue .1");
		let showPropertiesValue = this.state.show;
		console.log("EventTaskList.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ show: showPropertiesValue })
	}
	
	setChildAttribut(name, value, item) {
		console.log("EventShoppinglist.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		const { event } = { ...this.state };
		const currentEvent = event;

		item[name] = value;

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ "event": currentEvent });
		
		var slabEvent = SlabEvent.getUpdate(this.state.event, name, value, "/shoppinglist/"+item.id);
		this.props.updateEvent( slabEvent );
	}

	addItem() {
		console.log("EventShoppinglist.setChildAttribut: addItem item=" + JSON.stringify(this.state.event));
		var currentEvent = this.state.event;
		var itemInList = { status: "TODO", what: "", expense:{} };
		
		const newList = currentEvent.shoppinglist.concat( itemInList );
		currentEvent.shoppinglist = newList;
		this.setState({ "event": currentEvent });
		
		var slabEvent = SlabEvent.getAddList(this.state.event, "shoppinglist", itemInList, "/");
		
		this.props.updateEvent( slabEvent );
	}

	removeItem(item) {
		console.log("EventShoppinglist.removeItem: event=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		var listShopping = currentEvent.shoppinglist;
		var index = listShopping.indexOf(item);
		if (index > -1) {
			listShopping.splice(index, 1);
		}
		console.log("EventShoppinglist.removeItem: " + JSON.stringify(listShopping));
		currentEvent.shoppinglist = listShopping;
		console.log("EventShoppinglist.removeItem: eventAfter=" + JSON.stringify(this.state.event));

		this.setState({ "event": currentEvent });
		this.props.updateEvent();
	}

	// Apparently that's too many nested functions for React
	//	change_item_status(item,new_state){
	//		item.status = new_state
	//		this.setState( { "event" : this.state.event});
	//	};
	//	
	getTagState(item) {
		var changeState = (
			<OverflowMenu
				selectorPrimaryFocus={item.status}
			//						onFocus={(event) => { 
			//							window.alert("you just changed this"+this.props.className)
			//							console.log("EventState: Click ");
			//							 task = this.props.className;
			//							}
			//						}
			>
				<OverflowMenuItem className="TODO" itemText={<FormattedMessage id="EventShoppingList.ToBring" defaultMessage="To bring" />}
					onClick={() => {
						item.status = "TODO"
						this.setState({ "event": this.state.event });
					}
					}
				/>
				<OverflowMenuItem className="DONE" itemText={<FormattedMessage id="EventShoppingList.Done" defaultMessage="Done" />}
					onClick={() => {
						item.status = "DONE"
						this.setState({ "event": this.state.event });
					}
					}
				/>
				<OverflowMenuItem className="CANCEL" itemText={<FormattedMessage id="EventShoppingList.Cancel" defaultMessage="Cancelled" />}
					onClick={() => {
						item.status = "CANCEL"
						this.setState({ "event": this.state.event });
					}
					}
				/>
			</OverflowMenu>
		);


		if (item.status === 'TODO')
			return (<Tag type="teal" title="Task planned">
						<FormattedMessage id="EventShoppingList.ToBring" defaultMessage="To bring" />
 						{changeState}
					</Tag>)
		if (item.status === 'DONE')
			return (<Tag type="warm-gray" title="Task is finish, well done !">
						<FormattedMessage id="EventShoppingList.Done" defaultMessage="Done" />
						{changeState}
					</Tag>);
		if (item.status === 'CANCEL')
			return (<Tag type="red" title="Oups, this task was cancelled">
						<FormattedMessage id="EventShoppingList.Cancel" defaultMessage="Cancelled" />
						{changeState}
					</Tag>);

		return (<Tag type="gray" title="Something strange arrived">{item.status} {changeState}</Tag>);
	}


	changeParticipant() {
		console.log("EventShoppinglist.cchangeParticipant");
	}
}
export default injectIntl(EventShoppingList);