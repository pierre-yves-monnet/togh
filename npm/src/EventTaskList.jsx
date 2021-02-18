// -----------------------------------------------------------
//
// EventTaskList
//
// Manage the task list
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";

import { TextInput, DatePicker, DatePickerInput, TextArea, Tag, OverflowMenu, OverflowMenuItem } from 'carbon-components-react';

import FactoryService from './service/FactoryService';


import ChooseParticipant from './ChooseParticipant';

class EventTaskList extends React.Component {
	
	// this.props.pingEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : props.event, 
						'show' : props.show,
						'collapse' : props.collapse
						};
		// show : OFF, ON, COLLAPSE
		console.log("secTaskList.constructor show="+ +this.state.show+" event="+JSON.stringify(this.state.event));
		this.collapse 				= this.collapse.bind(this);
		this.addItem				= this.addItem.bind(this);
		this.changeParticipant		= this.changeParticipant.bind(this);
	}

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventTasklist.render: visible="+this.state.show);
		if (this.state.show === 'OFF')
			return ( <div> </div>);
			
		var toolService = FactoryService.getInstance().getToolService();
			
		// show the list
		if (! this.state.event.tasklist) {
			console.log("No tasklist defined, reset")
			this.state.event.tasklist= [];
		}

		var listTaskListHtml=[];
		listTaskListHtml= this.state.event.tasklist.map((item) =>
			<tr key={item.id}>
				<td> {this.getTagState( item.status )}</td>
				<td>
					<DatePicker datePickerType="single"
						onChange={(dates) => {
								console.log("SingleDatePicker :"+dates.length+" is an array "+Array.isArray(dates));
 
								if (dates.length >= 1) {
									console.log("SingleDatePicker set Date");
									this.setChildAttribut( "datebegin", dates[0] , item);
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
				<td>
					<DatePicker datePickerType="single"
						onChange={(dates) => {
								console.log("SingleDatePicker :"+dates.length+" is an array "+Array.isArray(dates));
 
								if (dates.length >= 1) {
									console.log("SingleDatePicker set Date");
									this.setChildAttribut( "dateend", dates[0], item );
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
				<td><TextInput value={item.what} onChange={(event) => this.setChildAttribut( "what", event.target.value, item )}  labelText="" ></TextInput></td>
				<td><TextArea labelText="" value={item.description} onChange={(event) => this.setChildAttribut( "description", event.target.value, item )} class="toghinput" labelText=""></TextArea></td>
				<td>
					<ChooseParticipant participant={item.who} event={this.state.event} modifyParticipant={true} pingChangeParticipant={this.changeParticipant} />
				</td>
				
				
				<td><button class="btn btn-danger btn-xs glyphicon glyphicon-minus" onClick={() => this.removeItem( item )} title="Remove this item"></button></td>
			</tr>
			);
		console.log("EventTasklist.render: list calculated from "+JSON.stringify( this.state.event.tasklist ));

		return ( <div>
					<div class="eventsection"> 
						<a href="secTasklist"></a>
						<a onClick={this.collapse} style={{verticalAlign: "top"}}>
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down" style={{fontSize: "small"}}></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"  style={{fontSize: "small"}}></span>}
						</a> <FormattedMessage id="EventTaskList.MainTitleTaskList" defaultMessage="Tasks List"/>
						<div style={{float: "right"}}>
							<button class="btn btn-success btn-xs glyphicon glyphicon-plus" onClick={this.addItem} title="Add a new item in the list"></button>
						</div>
					</div> 
					{this.state.show ==='ON' && <table class="table table-striped toghtable">
							<thead>
								<tr >
									<th><FormattedMessage id="EventTaskList.State" defaultMessage="State"/></th>
									<th><FormattedMessage id="EventTaskList.Begin" defaultMessage="Begin"/></th>
									<th><FormattedMessage id="EventTaskList.End" defaultMessage="End"/></th>
									<th><FormattedMessage id="EventTaskList.Subject" defaultMessage="Subject"/></th>
									<th><FormattedMessage id="EventTaskList.Description" defaultMessage="Description"/></th>
									<th><FormattedMessage id="EventTaskList.Who" defaultMessage="Who"/></th>
									<th></th>
								</tr>
							</thead>											
							{listTaskListHtml}
						</table>
					}
				</div>
				);
		}
		
	collapse() {
		console.log("EventShoppinglist.collapse");
		if (this.state.show === 'ON')
			this.setState( { 'show' : 'COLLAPSE' });
		else
			this.setState( { 'show' : 'ON' });
	}
	
	setChildAttribut( name, value, item ) {
		console.log("EventTasklist.setChildAttribut: set attribut:"+name+" <= "+value+" item="+JSON.stringify(item));
		const { event } = { ...this.state };
  		const currentEvent = event;

  		item[ name ] = value;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	addItem() {
		console.log("EventTasklist.setChildAttribut: addItem item="+JSON.stringify(this.state.event));

		var currentEvent = this.state.event;		
		const newList = currentEvent.tasklist.concat( {"status": "PLANNED",  "what": ""} );
		currentEvent.tasklist = newList;
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	removeItem( item ) {
		console.log("EventTasklist.removeItem: event="+JSON.stringify(this.state.event));

		var currentEvent = this.state.event;	
		var listTask = 	currentEvent.tasklist;
		var index = listTask.indexOf(item);
  		if (index > -1) {
   			listTask.splice(index, 1);
  		}
		console.log("EventTasklist.removeItem: "+JSON.stringify(listTask));
		currentEvent.tasklist = listTask;
		console.log("EventTasklist.removeItem: eventAfter="+JSON.stringify(this.state.event));

		this.setState( { "event" : currentEvent });
		this.props.pingEvent();	
	} 
	
	changeParticipant() {
		console.log("EventShoppinglist.changeParticipant");
	}
	
	getTagState( task ) {
		
		var changeState= (
		<OverflowMenu
      					selectorPrimaryFocus={'.'+ task }
						onChange={(event) => { 
							console.log("EventState: Click ");
							}
						}
    				>
							<OverflowMenuItem className="PLANNED" itemText={<FormattedMessage id="EventTaskList.StatePlanned" defaultMessage="Planned"/>}/>
							<OverflowMenuItem className="ACTIVE" itemText={<FormattedMessage id="EventTaskList.InProgress" defaultMessage="In progress"/>}/>
							<OverflowMenuItem className="DONE" itemText={<FormattedMessage id="EventTaskList.Done" defaultMessage="Done"/>}/>
							<OverflowMenuItem className="CANCEL" itemText={<FormattedMessage id="EventTaskList.Cancelled" defaultMessage="Cancelled"/>}/>
					</OverflowMenu>
);


		if (task === 'PLANNED')
			return (<Tag  type="teal" title="Task planned"><FormattedMessage id="EventTaskList.StatePlanned" defaultMessage="Planned"/> {changeState}</Tag>)			
		if (task === 'ACTIVE')
			return (<Tag  type="green" title="Task in progress"><FormattedMessage id="EventTaskList.InProgress" defaultMessage="In progress"/> {changeState}</Tag>);
		if (task === 'DONE')
			return (<Tag  type="warm-gray" title="Task is finish, well done !"><FormattedMessage id="EventTaskList.Done" defaultMessage="Done"/> {changeState}</Tag>);
		if (task === 'CANCEL')
			return (<Tag  type="red" title="Oups, this task was cancelled"><FormattedMessage id="EventTaskList.Cancelled" defaultMessage="Cancelled"/>{changeState}</Tag>);
		 
		return (<Tag  type="gray" title="Something strange arrived">{task} {changeState}</Tag>);
	}
	
}		
export default EventTaskList;
	