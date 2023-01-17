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

import { TextInput, TextArea, Toggle } from 'carbon-components-react';

import { DashCircle, PlusCircle } from 'react-bootstrap-icons';

import ChooseParticipant 			from 'component/ChooseParticipant';
import Expense 						from 'component/Expense';
import EventSectionHeader 			from 'component/EventSectionHeader';
import TagDropdown 					from 'component/TagDropdown';
import * as userFeedbackConstant 	from 'component/UserFeedback';
import UserFeedback  				from 'component/UserFeedback';


const STATUS_TODO = "TODO";
const STATUS_DONE = "DONE";
const STATUS_CANCEL = "CANCEL";

const NAMEENTITY = "shoppinglist";

// -----------------------------------------------------------
//
// EventShoppingList
//
// Display one event
//
// -----------------------------------------------------------


class EventShoppingList extends React.Component {
	// this.props.updateEvent()
	// props.manageFilter is provide (or should be at least, I think, I don't know)
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;

		this.state = {
			event: this.eventCtrl.getEvent(),
			operation: {
				inprogress: false,
				label:"",
				status:"",
				result:"",
				listlogevents: [] 
			}
			
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.setAttribut 					= this.setAttribut.bind(this);
		this.changeParticipantCallback 		= this.changeParticipantCallback.bind(this);
		this.setAttributCheckbox 				= this.setAttributCheckbox.bind(this);

		this.addItem 						= this.addItem.bind(this);
		this.addItemCallback 				= this.addItemCallback.bind( this );
		this.removeItem						= this.removeItem.bind( this );
		this.removeItemCallback				= this.removeItemCallback.bind( this );
		
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
				image="img/btnShoppingList.png"
				title={<FormattedMessage id="EventShoppingList.MainTitleBringList" defaultMessage="Bring List" />}
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
					<FormattedMessage id="EventShoppingList.NoItem" defaultMessage="You don't have any item to bring in the list." />
					&nbsp;
					<button class="btn btn-success btn-xs"
						onClick={() => this.addItem()}
						title={intl.formatMessage({ id: "EventShoppingList.addItem", defaultMessage: "Add a new item in the list" })}>

						<PlusCircle />&nbsp;
						<FormattedMessage id="EventShoppingList.AddOne" defaultMessage="Add one !" />
					</button>
				</div>
			)
		}

		//------------------------------ show the list

		console.log("EventShoppinglist.render: list calculated from " + JSON.stringify(this.state.event[ NAMEENTITY ]));

		// class="table table-striped toghtable"
		return (<div>
			{headerSection}

			<UserFeedback inprogress= {this.state.operation.inprogress}
				label= {this.state.operation.label}
				status= {this.state.operation.status}
				result= {this.state.operation.result}
				listlogevents= {this.state.operation.listlogevents} />
			
			{this.getFilterTaskHtml()}

			{this.state.event.shoppinglist.map((item, index) => { return (
					<div class="toghBlock" style={{backgroundColor: "#fed9a691"}} id={{index}}>
						<div class="container">
							{this.getLineShopping(item, index) }
						</div>
					</div>)
				})}

			
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
					toggled={this.state.event.shoppinglistshowdetails}
					selectorPrimaryFocus={this.state.event.shoppinglistshowdetails}
					labelA={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowDetails" defaultMessage="Detail" />}
					onChange={(event) => this.setAttributCheckbox("shoppinglistshowdetails", event)}
					id="showDetail" />
			</div>
			<div class="col">
				<Toggle labelText="" aria-label=""
					toggled={this.state.event.shoppinglistshowexpenses}
					selectorPrimaryFocus={this.state.event.shoppinglistshowexpenses}
					labelA={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					labelB={<FormattedMessage id="EventShoppingList.ShowExpense" defaultMessage="Show Expense" />}
					onChange={(event) => this.setAttributCheckbox("shoppinglistshowexpenses", event)}
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
			<div class="container">
			<div class="row">
				<div class="col-1">
					 {this.getTagState(item)}
				</div>
				<div class="col-2">
					<TextInput
					 labelText={<FormattedMessage id="EventShoppingList.What" defaultMessage="What" />}
						value={item.name} 
						onChange={(event) => this.setAttribut("name", event.target.value, item)} 
						class="toghInput"></TextInput>
				</div>
				{this.state.event.shoppinglistshowdetails && (
					<div class="col-4">
							<TextArea labelText={<FormattedMessage id="EventShoppingList.Description" defaultMessage="Description" />} 
								value={item.description} 
								onChange={(event) => this.setAttribut("description", event.target.value, item)} 
								class="toghInput"></TextArea>
					</div>)}
				<div class="col-4">
					<ChooseParticipant userid={item.whoid}
						event={this.state.event}
						modifyParticipant={true}
						item={item}
						label={<FormattedMessage id="EventShoppingList.Who" defaultMessage="Who" />}
						onChangeParticipantfct={this.changeParticipantCallback} />
				
					{this.state.event.shoppinglistshowexpenses &&
						<Expense item={item.expense}
							eventCtrl={this.eventCtrl}
							parentLocalisation={ NAMEENTITY+"/" + item.id+"/expense"} />
					}
				</div>
				<div class="col-1">
					<button class="btn btn-danger btn-xs"
						title={intl.formatMessage({ id: "EventShoppingList.removeItem", defaultMessage: "Remove this item" })}>
						<DashCircle onClick={() => this.removeItem(item)} />
					</button>
				</div>
			</div>
			</div>
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
	setAttributCheckbox(name, value) {		
		console.log("EventShoppinglist.setAttributCheckbox set " + name + "<=" + value.target.checked);
		let eventData=this.state.event;
		if (value.target.checked)
			eventData[name] = true;
		else
			eventData[name] = false;
		this.eventCtrl.setAttribut(name, eventData[name], eventData, "" );
		this.setState({ event: eventData });
	}


	setAttribut(name, value, item) {
		console.log("EventShoppingList.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));

		this.eventCtrl.setAttribut(name, value, item, NAMEENTITY+"/"+item.id);

	}
	changeParticipantCallback(shoppingitem, userid) {
		console.log("EventShoppinglist.changeParticipant user=" + JSON.stringify(userid));
		this.eventCtrl.setAttribut("whoid", userid, shoppingitem, NAMEENTITY+"/"+shoppingitem.id);
		shoppingitem.whoid = userid;
		this.setState({ event: this.state.event });
		console.log("EventShoppinglist.changeParticipant event=" + JSON.stringify(this.state.event));

	}
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	addItem() {
		const intl = this.props.intl;

		console.log("EventShoppinglist.addItem: item=" + JSON.stringify(this.state.event));
		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "EventShoppingList.AddingItem",defaultMessage: "Adding a item"}), 
					listlogevents: [] }});
		// call the server to get an ID on this taskList
		var newItem = { status: "TODO", name: "" };;
		this.eventCtrl.addEventChildFct( NAMEENTITY, newItem, "", this.addItemCallback);
	}

	addItemCallback(httpPayload) {
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			// feedback to user is required
			console.log("EventShoppinglist.addTaskCallback: HTTP ERROR ");
		} else if (httpPayload.getData().limitsubscription) {
			console.log("EventTasklist.callbackdata: Limit Subscription");
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventShoppingList.LimitSubsscription",defaultMessage: "You reach the limit of items allowed in the event. Go to your profile to see your subscription"})
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if (httpPayload.getData().status ==="ERROR") {
			console.log("EventShoppinglist.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventShoppingList.CantAddTask",defaultMessage: "An item can't be added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if ( ! (httpPayload.getData().childEntities && httpPayload.getData().childEntities.length>0) ) {
			currentOperation.status= userFeedbackConstant.ERRORCONTRACT;
			console.log("EventShoppingList.addTaskCallback:  BAD RECEPTION");

		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventShoppingList.TaskAdded",defaultMessage: "The item is added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			var itemToAdd = httpPayload.getData().childEntities[ 0 ];
			itemToAdd.expense={};
			var event = this.eventCtrl.getEvent();
			console.log("EventShoppingList.addItemCallback ");
			var newList = event.shoppinglist.concat(itemToAdd);
			event.shoppinglist = newList;
			this.setState({ event: event });
		}
		this.setState({operation: currentOperation});
	}



	removeItem(item) {
		const intl = this.props.intl;

		console.log("EventShoppingList.removeTask: event=" + JSON.stringify(this.state.event));

		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "EventShoppingList.RemovingItem",defaultMessage: "Removing an item"}), 
					listlogevents: [] }});
	
		var currentEvent = this.state.event;
		var listShopping = currentEvent.shoppinglist;
		var index = listShopping.indexOf(item);
		if (index > -1) {
			this.eventCtrl.removeEventChild( NAMEENTITY, listShopping[index].id, "", this.removeItemCallback);
		}
		
		this.setState({ event: currentEvent });

	}
	removeItemCallback(httpPayload) {
		const intl = this.props.intl;
		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;

		// find the task item to delete
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;			
			console.log("EventShoppingList.removeItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
				console.log("EventShoppingList.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
				currentOperation.status= userFeedbackConstant.ERROR;
				currentOperation.result=intl.formatMessage({id: "EventShoppingList.CantRemoveItem",defaultMessage: "The item can't be removed"});
				currentOperation.listlogevent = httpPayload.getData().listLogEvents;

		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventShoppingList.ItemRemoved",defaultMessage: "The item is removed"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			var currentEvent = this.state.event;
			let childId = httpPayload.getData().childEntitiesId[ 0 ];
			for( var i in currentEvent.shoppinglist) {
				if ( currentEvent.shoppinglist[ i ].id === childId) {
					currentEvent.shoppinglist.splice( currentEvent.shoppinglist[ i ], 1);
					break;
				}
			}
			this.setState({ event: currentEvent });
		}
		
		this.setState({ operation: currentOperation});

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

	
}
export default injectIntl(EventShoppingList);