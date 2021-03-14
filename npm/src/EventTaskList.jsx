// -----------------------------------------------------------
//
// EventTaskList
//
// Manage the task list
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";

import { TextInput, DatePicker, DatePickerInput, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle } from 'carbon-components-react';
import { PlusCircle, DashCircle, List, ListTask, ListUl, ListCheck } from 'react-bootstrap-icons';

import FactoryService from './service/FactoryService';

import EventSectionHeader from './component/EventSectionHeader';
import TagDropdown from './component/TagDropdown';
import ChooseParticipant from './component/ChooseParticipant';

const STATUS_PLANNED = "PLANNED";
const STATUS_ACTIVE = "ACTIVE";
const STATUS_DONE = "DONE";
const STATUS_CANCEL = "CANCEL";


class EventTaskList extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl = props.eventCtrl;
		this.state = {
			event: this.eventCtrl.getEvent(),
			showProperties: {
				showdates: true,
				filterState: "ALL",
				filterParticipant: "ALL"
			}
		};

		console.log("secTaskList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.addItem 					= this.addItem.bind(this);
		this.changeParticipant 			= this.changeParticipant.bind(this);
		this.isTaskHidden 				= this.isTaskHidden.bind(this);
		this.addTasklistCallback 		= this.addTasklistCallback.bind( this );
	}

	// --------------------------------- render
	render() {
		const intl = this.props.intl;

		console.log("EventTasklist.render: showProperties=" + JSON.stringify(this.state.showProperties)+" taslList="+JSON.stringify(this.state.event.tasklist));

		var headerSection = (
			<EventSectionHeader id="task"
				image="img/btnTask.png"
				title={<FormattedMessage id="EventTaskList.MainTitleTaskList" defaultMessage="Tasks List" />}
				showPlusButton={true}
				showPlusButtonTitle={<FormattedMessage id="EventTaskList.AddTask" defaultMessage="Add a task in the list" />}
				userTipsText={<FormattedMessage id="EventTaskList.TaskTip" defaultMessage="Use tasks to reference what you have to do for your event. You can assign participant, and mark the status of the task: planned, done.... According your preference, you may receive a notation when you have a task to realize" />}
				addItemCallback={this.addItem}
			/>
		);


		var toolService = FactoryService.getInstance().getToolService();

		if (this.state.event.tasklist.length === 0) {
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


		var listTaskListHtml = [];
		
		listTaskListHtml = this.state.event.tasklist.map((item, index) => {
			console.log("EventTaskList.isTaskHidden map---------------- filterState=[" + this.state.showProperties.filterState + "] ");

			if (this.isTaskHidden(item))
				return (null);
			else
				return (
					<tr key={index}>
						<td> {this.getTagState(item)}</td>
						{this.state.showProperties.showdates && <td>
							<DatePicker datePickerType="single"
								onChange={(dates) => {
									console.log("SingleDatePicker :" + dates.length + " is an array " + Array.isArray(dates));

									if (dates.length >= 1) {
										console.log("SingleDatePicker set Date");
										this.setAttribut("datebegin", dates[0], item);
									}
								}
								}
								value={toolService.getDateListFromDate(item.datebegin)}
							>
								<DatePickerInput
									placeholder="mm/dd/yyyy"
									labelText=""
									id="date-picker-simple"
								/>
							</DatePicker>
						</td>
						}
						{this.state.showProperties.showdates && <td>
							<DatePicker datePickerType="single"
								onChange={(dates) => {
									console.log("SingleDatePicker :" + dates.length + " is an array " + Array.isArray(dates));

									if (dates.length >= 1) {
										console.log("SingleDatePicker set Date");
										this.setAttribut("dateend", dates[0], item);
									}
								}
								}
								value={toolService.getDateListFromDate(item.dateend)}
							>
								<DatePickerInput
									placeholder="mm/dd/yyyy"
									labelText=""
									id="date-picker-simple"
								/>
							</DatePicker>
						</td>
						}
						<td><TextInput value={item.what} onChange={(event) => this.setAttribut("what", event.target.value, item)} labelText="" ></TextInput></td>
						<td><TextArea labelText="" value={item.description} onChange={(event) => this.setAttribut("description", event.target.value, item)} class="toghinput" labelText=""></TextArea></td>
						<td>
							<ChooseParticipant participant={item.who} event={this.state.event} modifyParticipant={true} pingChangeParticipant={this.changeParticipant} />
						</td>


						<td>
							{this.isShowDelete(item) && <button class="btn btn-danger btn-xs" 
															onClick={() => this.removeItem(item)} 
															title={intl.formatMessage({id: "EventTaskList.RemoveThisTask",defaultMessage: "Remove this task"})}>
															<DashCircle/>
														</button>}
														
														
						</td>
					</tr>
				)
		}
		);
		console.log("EventTasklist.render: list calculated from " + JSON.stringify(this.state.event.tasklist));

		/** 
							<ContentSwitcher size="sm" onChange={event => this.setSwitcherValue("filterState", event)}
								labelText="Task"
								width="10px" height="small">
								<Switch name='ALL' text={<FormattedMessage id="EventTaskList.FilterAllStates" defaultMessage="All states" />} />
								<Switch name='PLANNED' text={<FormattedMessage id="EventTaskList.FilterPlanned" defaultMessage="Planned" />} />
								<Switch name='ACTIVE' text={<FormattedMessage id="EventTaskList.FilterInProgress" defaultMessage="In progress" />} />
								<Switch name='DONE' text={<FormattedMessage id="EventTaskList.FilterDone" defaultMessage="Done" />} />
							</ContentSwitcher>
							
							<ContentSwitcher size="sm" onChange={event => this.setSwitcherValue("filterparticipant", event)} labelText="Task">
							<Switch name='ALL' text={<FormattedMessage id="EventTaskList.FilterAllParticipants" defaultMessage="All participants" />} />
							<Switch name='MYTASKS' text={<FormattedMessage id="EventTaskList.FilterMyTasks" defaultMessage="My tasks" />} />
							<Switch name='UNAFFECTED' text={<FormattedMessage id="EventTaskList.FilterUnaffected" defaultMessage="Unaffected" />} />

						</ContentSwitcher>
			 */

		// render the tab
		return (<div>
			{headerSection}

			<div>
				<table width="100%"><tr>
					<td style={{ paddingRight: "60px;" }}>
						<Toggle labelText="" aria-label="" toggled={this.state.showProperties.showdates}
							selectorPrimaryFocus={this.state.showProperties.showdates}
							labelA={<FormattedMessage id="EventTaskList.ShowDate" defaultMessage="Show dates" />}
							labelB={<FormattedMessage id="EventTaskList.ShowDate" defaultMessage="Show dates" />}
							onChange={(event) => {
								console.log("EventTaskList.click on showDates");
								this.setCheckboxValue("showdates", event);}}
							id="showDates" />
							
							
					</td><td style={{ paddingRight: "70px;" }}>
						<div class="btn-group btn-group-sm" role="groupstate" aria-label="Basic radio toggle button group">
							<input type="radio" class="btn-check" name="btnradiostate" id="filterState1" autocomplete="off" 
								checked={this.state.showProperties.filterState === "ALL"}
								onChange={() => this.setSwitcherValue("filterState", "ALL")}/>
						  	<label class="btn btn-outline-primary" for="filterState1">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterAllStates" defaultMessage="All states" />
							</label>
												
						  	<input type="radio" class="btn-check" name="btnradiostate" id="filterState2" autocomplete="off"
								checked={this.state.showProperties.filterState === "PLANNED"}
								onChange={() => this.setSwitcherValue("filterState", "PLANNED")}/>
						  	<label class="btn btn-outline-primary" for="filterState2">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterPlanned" defaultMessage="Planned" />
							</label>
						
						  	<input type="radio" class="btn-check" name="btnradiostate" id="filterState3" autocomplete="off"
									checked={this.state.showProperties.filterState === "ACTIVE"}
									onChange={() => this.setSwitcherValue("filterState", "ACTIVE")}/>
						 	<label class="btn btn-outline-primary" for="filterState3">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterInProgress" defaultMessage="In progress" />
							</label>
	
							<input type="radio" class="btn-check" name="btnradiostate" id="filterState4" autocomplete="off"
									checked={this.state.showProperties.filterState === "DONE"}
									onChange={() => this.setSwitcherValue("filterState", "DONE")}/>
						 	<label class="btn btn-outline-primary" for="filterState4">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterDone" defaultMessage="Done" />
							</label>
						</div>
					
					
					
					</td><td>
						<div class="btn-group btn-group-sm" role="groupparticipant" aria-label="Basic radio toggle button group">
							<input type="radio" class="btn-check" name="btnradio" id="btnradio1" autocomplete="off" 
								checked={this.state.showProperties.filterParticipant === "ALL"}
								onChange={() => this.setSwitcherValue("filterParticipant", "ALL")}/>
						  	<label class="btn btn-outline-primary" for="btnradio1">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterAllParticipants" defaultMessage="All participants" />
							</label>
						
						
						  	<input type="radio" class="btn-check" name="btnradio" id="btnradio2" autocomplete="off"
								checked={this.state.showProperties.filterParticipant === "MYTASKS"}
								onChange={() => this.setSwitcherValue("filterParticipant", "MYTASKS")}/>
						  	<label class="btn btn-outline-primary" for="btnradio2">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterMyTasks" defaultMessage="My tasks" />
							</label>
						
						  	<input type="radio" class="btn-check" name="btnradio" id="btnradio3" autocomplete="off"
									checked={this.state.showProperties.filterParticipant === "UNAFFECTED"}
									onChange={() => this.setSwitcherValue("filterParticipant", "UNAFFECTED")}/>
						 	<label class="btn btn-outline-primary" for="btnradio3">
								<List />&nbsp;<FormattedMessage id="EventTaskList.FilterUnaffected" defaultMessage="Unaffected" />
							</label>
	
						</div>
					</td>
				</tr></table>



				<p />
				<table class="table table-striped toghtable">
					<thead>
						<tr >
							<th><FormattedMessage id="EventTaskList.State" defaultMessage="State" /></th>
							{this.state.showProperties.showdates && <th> <FormattedMessage id="EventTaskList.Begin" defaultMessage="Begin" /></th>}
							{this.state.showProperties.showdates && <th> <FormattedMessage id="EventTaskList.End" defaultMessage="End" /></th>}
							<th><FormattedMessage id="EventTaskList.Subject" defaultMessage="Subject" /></th>
							<th><FormattedMessage id="EventTaskList.Description" defaultMessage="Description" /></th>
							<th><FormattedMessage id="EventTaskList.Who" defaultMessage="Who" /></th>
							<th></th>
						</tr>
					</thead>
					{listTaskListHtml}
				</table>
			</div>
		</div>
		);
	}


	/**
	* Check filter to decide if the task has to be hidden on not 
	*/
	isTaskHidden(task) {
		var statushidden = false;
		var myself = this.eventCtrl.getMyself();
		
		if (this.state.showProperties.filterState === 'PLANNED' && task.status !== 'PLANNED')
			statushidden = true;
		if (this.state.showProperties.filterState === 'ACTIVE' && task.status !== 'ACTIVE')
			statushidden = true;
		if (this.state.showProperties.filterState === 'DONE' && task.status !== 'DONE')
			statushidden = true;

		// participant filter now
		if (this.state.showProperties.filterParticipant === 'MYTASKS' && task.who !== myself.id)
			statushidden = true;
		if (this.state.showProperties.filterParticipant === 'UNAFFECTED' && task.who !== '')
			statushidden = true;
		console.log("EventTaskList.isTaskHidden: " + statushidden + " task.status=" + task.status + " filterState=[" + this.state.showProperties.filterState + "] ");
		return statushidden;
	}

	// only if the task is not empty	
	isShowDelete(task) {
		if (task.what && task.what.length > 0)
			return false;
		if (task.description && task.description.length > 0)
			return false;
		return true;
	}
	/**
 	*/
	setAttribut(name, value, item) {
		console.log("EventTasklist.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		
		this.eventCtrl.setAttribut(name, value, item, "/tasklist");
	/*
		const { event } = { ...this.state };
		const currentEvent = event;

		item[name] = value;

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ "event": currentEvent });
		this.props.updateEvent();
		*/
	}


	setCheckboxValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue2 set " + name + "<=" + value.target.checked + " showProperties =" + JSON.stringify(showPropertiesValue));
		if (value.target.checked)
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ "showProperties": showPropertiesValue })
	}

	setSwitcherValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setSwitcherValue set " + name + "<=" + value + " showProperties =" + JSON.stringify(showPropertiesValue));
		showPropertiesValue[name] = value;
		this.setState({ showProperties: showPropertiesValue })
	}

	/**
   */
	addItem() {
		console.log("EventTasklist.setAttribut: addItem item=" + JSON.stringify(this.state.event));
		/*
		var currentEvent = this.state.event;
		const newList = currentEvent.tasklist.concat({ "status": "PLANNED", "what": "" });
		currentEvent.tasklist = newList;
		this.setState({ "event": currentEvent });
				var surveyToAdd = SurveyCtrl.getDefaultSurvey();
		*/
		// call the server to get an ID on this taskList
		var newTask = { "status": "PLANNED", "what": "" };
		this.eventCtrl.addEventChildFct("tasklist", newTask, "", this.addTasklistCallback);
	}

	addTasklistCallback(httpPayload) {
		console.log("EventTasklist.addSurveyCallback ");
		if (httpPayload.isError()) {
			// feedback to user is required
			console.log("EventSurveyList.addSurveyCallback: ERROR ");
		} else {
			var taskToAdd = httpPayload.getData().child;
			var event = this.eventCtrl.getEvent();
			var newList = event.tasklist.concat(taskToAdd);
			event.tasklist = newList;
			this.setState({ event: event });
		}
	}



	removeItem(item) {
		console.log("EventTasklist.removeItem: event=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		var listTasks = currentEvent.tasklist;
		var index = listTasks.indexOf(item);
		if (index > -1) {
			this.eventCtrl.removeEventChild("tasklist", listTasks[index], "", this.removeStepCallback);
			listTasks.splice(index, 1);
		}
		// console.log("EventTasklist.removeItem: " + JSON.stringify(listTask));
		currentEvent.tasklist = listTasks;
		// console.log("EventTasklist.removeItem: eventAfter=" + JSON.stringify(this.state.event));

		this.setState({ "event": currentEvent });

	}
	removeStepCallback(httpPayLoad) {
		// already done
	}
	changeParticipant() {
		console.log("EventShoppinglist.changeParticipant");
	}


	getTagState( item ) {
		// console.log("EventSurvey.getTagState item.status="+survey.status);

		const intl = this.props.intl;
		
		const listOptions = [
			{ label: intl.formatMessage({id: "EventTaskList.StatePlanned",defaultMessage: "Planned"}),
			 value: STATUS_PLANNED,
			 type: "teal" },			
			{ label: intl.formatMessage({id: "EventTaskList.InProgress",defaultMessage: "In progress"}),
			 value:  STATUS_ACTIVE,
			 type: "blue" },
			{ label: intl.formatMessage({id: "EventTaskList.Done",defaultMessage: "Done"}),
			 value:  STATUS_DONE,
			 type: "green" },
			{ label: intl.formatMessage({id: "EventTaskList.Cancelled",defaultMessage: "Cancelled"}),
			 value:  STATUS_CANCEL,
			 type: "red" },
		];
		return (<TagDropdown listOptions={listOptions} value={item.status} readWrite={true} 
				changeState={(value) => {
					this.setAttribut("status", value, item);
					}} />);
		}		
					
	/**
	 *  getTagState
 	*/
	getTagState2(item) {

		var changeState = (
			<OverflowMenu
				selectorPrimaryFocus={'.' + item.status}
			>
				<OverflowMenuItem className="PLANNED" itemText={<FormattedMessage id="EventTaskList.StatePlanned" defaultMessage="Planned" />}
					onClick={() => {
						item.status = "PLANNED"
						this.setState({ "event": this.state.event });
					}}
				/>
				<OverflowMenuItem className="ACTIVE" itemText={<FormattedMessage id="EventTaskList.InProgress" defaultMessage="In progress" />}
					onClick={() => {
						item.status = "ACTIVE"
						this.setState({ "event": this.state.event });
					}}
				/>
				<OverflowMenuItem className="DONE" itemText={<FormattedMessage id="EventTaskList.Done" defaultMessage="Done" />}
					onClick={() => {
						item.status = "DONE"
						this.setState({ "event": this.state.event });
					}}
				/>
				<OverflowMenuItem className="CANCEL" itemText={<FormattedMessage id="EventTaskList.Cancelled" defaultMessage="Cancelled" />}
					onClick={() => {
						item.status = "CANCEL"
						this.setState({ "event": this.state.event });
					}}
				/>
			</OverflowMenu>
		);


		if (item.status === 'PLANNED')
			return (<Tag type="teal" title="Task planned"><FormattedMessage id="EventTaskList.StatePlanned" defaultMessage="Planned" /> {changeState}</Tag>)
		if (item.status === 'ACTIVE')
			return (<Tag type="green" title="Task in progress"><FormattedMessage id="EventTaskList.InProgress" defaultMessage="In progress" /> {changeState}</Tag>);
		if (item.status === 'DONE')
			return (<Tag type="warm-gray" title="Task is finish, well done !"><FormattedMessage id="EventTaskList.Done" defaultMessage="Done" /> {changeState}</Tag>);
		if (item.status === 'CANCEL')
			return (<Tag type="red" title="Oups, this task was cancelled"><FormattedMessage id="EventTaskList.Cancelled" defaultMessage="Cancelled" />{changeState}</Tag>);

		return (<Tag type="gray" title="Something strange arrived">{item.status} {changeState}</Tag>);
	}

}
export default injectIntl(EventTaskList);
