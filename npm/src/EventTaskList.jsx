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
import { PlusCircle, DashCircle} from 'react-bootstrap-icons';

import FactoryService from './service/FactoryService';

import EventSectionHeader from './component/EventSectionHeader';

import ChooseParticipant from './component/ChooseParticipant';



class EventTaskList extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {
			event: props.event,
			showProperties: {
				showdates: true,
				filterstate: "ALL",
				filterparticipant: "all"
			}
		};

		console.log("secTaskList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.addItem = this.addItem.bind(this);
		this.changeParticipant = this.changeParticipant.bind(this);
		this.isTaskHidden = this.isTaskHidden.bind(this);
	}

	// --------------------------------- render
	render() {
		const intl = this.props.intl;

		console.log("EventTasklist.render: visible=" + this.state.show);
		
		var headerSection =(
			<EventSectionHeader id="task" 
				image="img/btnTask.png" 
				title={<FormattedMessage id="EventTaskList.MainTitleTaskList" defaultMessage="Tasks List" />}
				showPlusButton  = {true}
				showPlusButtonTitle={<FormattedMessage id="EventTaskList.AddTask" defaultMessage="Add a task in the list" />}
				userTipsText={<FormattedMessage id="EventTaskList.TaskTip" defaultMessage="Use tasks to reference what you have to do for your event. You can assign participant, and mark the status of the task: planned, done.... According your preference, you may receive a notation when you have a task to realize" />}
				/>
				);
		/* old
			<div>	
				<div class="eventsection">
					<div style={{ float: "left" }}>
						<img style={{ "float": "right" }} src="img/btnTask.png" style={{ width: 100 }} /><br />
					</div>
					<FormattedMessage id="EventTaskList.MainTitleTaskList" defaultMessage="Tasks List" />
					<div style={{ float: "right" }}>
						<button class="btn btn-success btn-xs " 
							 title={<FormattedMessage id="EventTaskList.AddTask" defaultMessage="Add a task in the list" />}>
							<PlusCircle onClick={this.addItem}/>
						</button>
					</div>
				</div>			
			<UserTips id="task" text={<FormattedMessage id="EventTaskList.TaskTip" defaultMessage="Use tasks to reference what you have to do for your event. You can assign participant, and mark the status of the task: planned, done.... According your preference, you may receive a notation when you have a task to realize" />}/>
			</div> 
			)
			*/
			
		var toolService = FactoryService.getInstance().getToolService();

		if (this.state.event.tasklist.length === 0) {
			return (
				<div>
					{headerSection}
					<FormattedMessage id="EventTaskList.NoItem" defaultMessage="You don't have any item in the list. Add one !" />
					&nbsp;
					<button class="btn btn-success btn-xs"
						title={intl.formatMessage({ id: "EventTaskList.addItem", defaultMessage: "Add a new item in the list" })}>
						<PlusCircle onClick={() => this.addItem()} />
					</button>
				</div>
			)
		}


		var listTaskListHtml = [];
		var self = this.state.showProperties;
		listTaskListHtml = this.state.event.tasklist.map((item, index) => {
			console.log("EventTaskList.isTaskHidden He map---------------- filterState=[" + self.filterState + "] ");

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
										this.setChildAttribut("datebegin", dates[0], item);
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
										this.setChildAttribut("dateend", dates[0], item);
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
						<td><TextInput value={item.what} onChange={(event) => this.setChildAttribut("what", event.target.value, item)} labelText="" ></TextInput></td>
						<td><TextArea labelText="" value={item.description} onChange={(event) => this.setChildAttribut("description", event.target.value, item)} class="toghinput" labelText=""></TextArea></td>
						<td>
							<ChooseParticipant participant={item.who} event={this.state.event} modifyParticipant={true} pingChangeParticipant={this.changeParticipant} />
						</td>


						<td>
							{this.isShowDelete(item) && <button class="btn btn-danger btn-xs glyphicon glyphicon-minus" onClick={() => this.removeItem(item)} title="Remove this item"></button>}
						</td>
					</tr>
				)
		}
		);
		console.log("EventTasklist.render: list calculated from " + JSON.stringify(this.state.event.tasklist));

		// render the tab
		return (<div>
			{headerSection}
			
			<div>
				<table width="100%"><tr>
					<td style={{ paddingRight: "60px;" }}>
						<Toggle labelText="" aria-label="" size="sm" toggled={this.state.showProperties.showdates}
							selectorPrimaryFocus={this.state.showProperties.showdates}
							labelA={<FormattedMessage id="EventTaskList.ShowDate" defaultMessage="Show dates" />}
							labelB={<FormattedMessage id="EventTaskList.ShowDate" defaultMessage="Show dates" />} 
							onChange={(event) => this.setCheckboxValue("showdates", event)}
							id="showDates" />
					</td><td style={{ paddingRight: "60px;" }}>
						<ContentSwitcher size="sm" onChange={event => this.setSwitcherValue("filterstate", event)}
							labelText="Task"
							width="10px" height="small">
							<Switch name='ALL' text={<FormattedMessage id="EventTaskList.FilterAllStates" defaultMessage="All states" />} />
							<Switch name='PLANNED' text={<FormattedMessage id="EventTaskList.FilterPlanned" defaultMessage="Planned" />} />
							<Switch name='ACTIVE' text={<FormattedMessage id="EventTaskList.FilterInProgress" defaultMessage="In progress" />} />
							<Switch name='DONE' text={<FormattedMessage id="EventTaskList.FilterDone" defaultMessage="Done" />} />
						</ContentSwitcher>
					</td><td>
						<ContentSwitcher size="sm" onChange={event => this.setSwitcherValue("filterparticipant", event)} labelText="Task">
							<Switch name='ALL' text={<FormattedMessage id="EventTaskList.FilterAllParticipants" defaultMessage="All participants" />} />
							<Switch name='MYTASKS' text={<FormattedMessage id="EventTaskList.FilterMyTasks" defaultMessage="My tasks" />} />
							<Switch name='UNAFFECTED' text={<FormattedMessage id="EventTaskList.FilterUnaffected" defaultMessage="Unaffected" />} />

						</ContentSwitcher>
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

		if (this.state.showProperties.filterState === 'PLANNED' && task.status !== 'PLANNED')
			statushidden = true;
		if (this.state.showProperties.filterState === 'ACTIVE' && task.status !== 'ACTIVE')
			statushidden = true;
		if (this.state.showProperties.filterState === 'DONE' && task.status !== 'DONE')
			statushidden = true;

		// participant filter now
		if (this.state.showProperties.filterparticipant === 'MYTASKS' && task.who !== '12')
			statushidden = true;
		if (this.state.showProperties.filterparticipant === 'UNAFFECTED' && task.who !== '')
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
	setChildAttribut(name, value, item) {
		console.log("EventTasklist.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		const { event } = { ...this.state };
		const currentEvent = event;

		item[name] = value;

		// currentEvent.shoppinglist[0].[name] = value;

		this.setState({ "event": currentEvent });
		this.props.updateEvent();
	}


	setCheckboxValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue set " + name + "=" + value.target.checked + " showProperties =" + JSON.stringify(showPropertiesValue));
		if (value.target.checked)
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ "showProperties": showPropertiesValue })
	}

	setSwitcherValue(name, value) {
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue set " + name + "=" + value.name + " showProperties =" + JSON.stringify(showPropertiesValue));
		showPropertiesValue[name] = value.name;
		this.setState({ "showProperties": showPropertiesValue })
	}

	/**
   */
	addItem() {
		console.log("EventTasklist.setChildAttribut: addItem item=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		const newList = currentEvent.tasklist.concat({ "status": "PLANNED", "what": "" });
		currentEvent.tasklist = newList;
		this.setState({ "event": currentEvent });
		this.props.updateEvent();
	}

	removeItem(item) {
		console.log("EventTasklist.removeItem: event=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		var listTask = currentEvent.tasklist;
		var index = listTask.indexOf(item);
		if (index > -1) {
			listTask.splice(index, 1);
		}
		console.log("EventTasklist.removeItem: " + JSON.stringify(listTask));
		currentEvent.tasklist = listTask;
		console.log("EventTasklist.removeItem: eventAfter=" + JSON.stringify(this.state.event));

		this.setState({ "event": currentEvent });
		this.props.updateEvent();
	}

	changeParticipant() {
		console.log("EventShoppinglist.changeParticipant");
	}

	/**
	 *  getTagState
 	*/
	getTagState(item) {

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
