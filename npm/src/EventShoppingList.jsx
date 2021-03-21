/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { TextInput, TextArea, OverflowMenu, OverflowMenuItem, Tag, Toggle } from 'carbon-components-react';

import { ArrowUp, ArrowDown, Cash, DashCircle, PlusCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import ChooseParticipant from './component/ChooseParticipant';
import Expense from './component/Expense';
import EventSectionHeader from './component/EventSectionHeader';
import TagDropdown from './component/TagDropdown';

import SlabRecord from './service/SlabRecord';


const STATUS_TODO = "TODO";
const STATUS_DONE = "DONE";
const STATUS_CANCEL = "CANCEL";

// -----------------------------------------------------------
//
// EventShoppingList
//
// Display one event
//
// -----------------------------------------------------------


class EventShoppingList extends React.Component {
	// this.props.updateEvent()
	// props.eventPreferences is provide (or should be at least, I think, I don't know)
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;

		this.state = {
			event: this.eventCtrl.getEvent(),
			showProperties: {
				showDetail: true,
				showExpense: false
			}
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttribut = this.setAttribut.bind(this);
		this.changeParticipant = this.changeParticipant.bind(this);
		this.setCheckboxValue = this.setCheckboxValue.bind(this);
		this.addItem = this.addItem.bind(this);
	}



	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		console.log("EventShoppinglist.render: visible=" + this.state.show);

		var headerSection = (
			<EventSectionHeader id="task"
				image="img/btnTask.png"
				title={<FormattedMessage id="EventShoppingList.MainTitleShoppingList" defaultMessage="Shopping List" />}
				showPlusButton={true}
				showPlusButtonTitle={<FormattedMessage id="EventShoppingList.AddItem" defaultMessage="Add a item in the list" />}
				userTipsText={<FormattedMessage id="EventShoppingList.ShoppingListTip" defaultMessage="You have a list of item to bing to the event. List them, decide who have to bought them" />}
				addItemCallback={this.addItem}
			/>
		);



		if (this.state.event.shoppinglist.length === 0) {
			return (
				<div>
					{headerSection}
					<FormattedMessage id="EventTaskList.NoItem" defaultMessage="You don't have any task in the list." />
					&nbsp;
					<button class="btn btn-success btn-xs"
						onClick={() => this.addItem()}
						title={intl.formatMessage({ id: "EventTaskList.addItem", defaultMessage: "Add a new item in the list" })}>

						<PlusCircle />&nbsp;
						<FormattedMessage id="EventTaskList.AddOne" defaultMessage="Add one !" />
					</button>
				</div>
			)
		}

		//------------------------------ show the list
		var listShoppingListHtml = [];


		console.log("EventShoppinglist.render: list calculated from " + JSON.stringify(this.state.event.shoppinglist));

		// class="table table-striped toghtable"
		return (<div>
			{headerSection}

			{this.getFilterTaskHtml()}

			<table class="toghtable">
				<thead>
					<tr >
						<th></th>
						<th><FormattedMessage id="EventShoppingList.What" defaultMessage="What" /></th>
						{this.state.show.showDetail && <th><FormattedMessage id="EventShoppingList.Description" defaultMessage="Description" /></th>}
						<th><FormattedMessage id="EventShoppingList.Who" defaultMessage="Who" /></th>
						<th></th>
					</tr>
				</thead>
				{this.state.event.shoppinglist.map((item, index) => { return (this.getLineShopping(item, index)) })}

			</table>

		</div>
		);
	}

	/**
	 * Filter the different task
	 */
	getFilterTaskHtml() {
		return (<div class="row">
			<div class="col">
				<Toggle labelText="" aria-label=""
					labelA={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					onChange={(event) => this.setCheckboxValue("showDetail", event.target.value)}
					defaultToggled={this.state.showProperties.showDetail}
					id="showDetail" />
			</div>
			<div class="col">
				<Toggle labelText="" aria-label=""
					labelA={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					onChange={(event) => this.setCheckboxValue("showExpense", event.target.value)}
					defaultToggled={this.state.showProperties.showExpense}
					id="showexpense" />
			</div>
		</div>)
	}

	/**
	* return the HTML for a line
	 */
	getLineShopping(item, index) {
		const intl = this.props.intl;

		return (
			<tr key={index}>
				<td> {this.getTagState(item)}</td>
				<td><TextInput value={item.what} onChange={(event) => this.setAttribut("what", event.target.value, item)} labelText="" ></TextInput></td>
				{this.state.show.showDetail && (<td><TextArea labelText="" value={item.description} onChange={(event) => this.setAttribut("description", event.target.value, item)} class="toghinput" labelText=""></TextArea></td>)}
				<td>
					<ChooseParticipant userid={item.who}
						event={this.state.event}
						modifyParticipant={true}
						item={item}
						onChangeParticipantfct={this.changeParticipant} />

					{this.state.show.showExpense &&
						<Expense item={item.expense}
							eventCtrl={this.eventCtrl}
							parentLocalisation={"/shoppinglist/" + item.id} />
					}

				</td>
				<td><button class="btn btn-danger btn-xs"
					title={intl.formatMessage({ id: "EventShoppingList.removeItem", defaultMessage: "Remove this item" })}>
					<DashCircle onClick={() => this.removeItem(item)} />
				</button></td>
			</tr>
		);
	}

	
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	/** --------------------
	 * Set attribut 
		  */
	setCheckboxValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue set " + name + "<=" + value.target.checked + " showProperties =" + JSON.stringify(showPropertiesValue));
		if (value.target.checked)
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ "showProperties": showPropertiesValue })
	}


	setAttribut(name, value, item) {
		console.log("EventTasklist.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));

		this.eventCtrl.setAttribut(name, value, item, "/tasklist");

	}
	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	addItem() {
		console.log("EventShoppinglist.addItem: addItem item=" + JSON.stringify(this.state.event));
		// call the server to get an ID on this taskList
		var newLine = { status: "TODO", what: "", expense: {} };;
		this.eventCtrl.addEventChildFct("tasklist", newLine, "", this.addItemlistCallback);
	}

	addItemlistCallback(httpPayload) {
		console.log("EventShoppinglist.addItemlistCallback ");
		if (httpPayload.isError()) {
			// feedback to user is required
			console.log("EventShoppinglist.addItemlistCallback: ERROR ");
		} else {
			var taskToAdd = httpPayload.getData().child;
			var event = this.eventCtrl.getEvent();
			var newList = event.shoppinglist.concat(taskToAdd);
			event.shoppinglist = newList;
			this.setState({ event: event });
		}
	}



	removeItem(item) {
		console.log("EventShoppinglist.removeItem: event=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		var listItems = currentEvent.shoppinglist;
		var index = listItems.indexOf(item);
		if (index > -1) {
			this.eventCtrl.removeEventChild("shoppinglist", listItems[index], "", this.removeStepCallback);
			listItems.splice(index, 1);
		}
		// console.log("EventTasklist.removeItem: " + JSON.stringify(listTask));
		currentEvent.shoppinglist = listItems;
		// console.log("EventTasklist.removeItem: eventAfter=" + JSON.stringify(this.state.event));

		this.setState({ "event": currentEvent });

	}
	removeStepCallback(httpPayLoad) {
		// already done
	}




	getTagState(item) {
		// console.log("EventSurvey.getTagState item.status="+survey.status);

		const intl = this.props.intl;

		const listOptions = [
			{
				label: intl.formatMessage({ id: "EventShoppingList.ToBring", defaultMessage: "To bring" }),
				value: STATUS_TODO,
				type: "teal"
			},
			{
				label: intl.formatMessage({ id: "EventShoppingList.Done", defaultMessage: "Done" }),
				value: STATUS_DONE,
				type: "green"
			},
			{
				label: intl.formatMessage({ id: "EventShoppingList.Cancelled", defaultMessage: "Cancelled" }),
				value: STATUS_CANCEL,
				type: "red"
			},
		];
		return (<TagDropdown listOptions={listOptions} value={item.status} readWrite={true}
			changeState={(value) => {
				this.setAttribut("status", value, item);
			}} />);
	}

	changeParticipant(task, userid) {
		console.log("EventShoppinglist.changeParticipant user=" + JSON.stringify(userid));
		this.eventCtrl.setAttribut("who", userid, task, "/tasklist");
		task.who = userid;
		this.setState({ event: this.state.event });
		console.log("EventShoppinglist.changeParticipant event=" + JSON.stringify(this.state.event));

	}
}
export default injectIntl(EventShoppingList);