// -----------------------------------------------------------
//
// EventShoppingList
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { TextInput, TextArea, OverflowMenu, OverflowMenuItem, Tag  } from 'carbon-components-react';



import ChooseParticipant from './ChooseParticipant';

class EventShoppingList extends React.Component {
	
	// this.props.pingEvent()
	constructor( props ) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = { 'event' : props.event, 
						'show' : props.show,
						'collapse' : props.collapse
						};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show="+ +this.state.show+" event="+JSON.stringify(this.state.event));
		this.collapse 				= this.collapse.bind(this);
		this.setChildAttribut		= this.setChildAttribut.bind(this);
		this.addItem				= this.addItem.bind(this);
		this.changeParticipant		= this.changeParticipant.bind(this);
	}

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("EventShoppinglist.render: visible="+this.state.show);
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		if (! this.state.event.shoppinglist) {
			this.state.event.shoppinglist= [];
		}
		
		var listShoppingListHtml=[];
		listShoppingListHtml= this.state.event.shoppinglist.map((item) =>
			<tr key={item.id}>
				<td> {this.getTagState( item.status, item )} id={item.id}</td>				
				<td><TextInput value={item.what} onChange={(event) => this.setChildAttribut( "what", event.target.value, item )}  labelText="" ></TextInput></td>
				<td><TextArea labelText="" value={item.description} onChange={(event) => this.setChildAttribut( "description", event.target.value, item )} class="toghinput" labelText=""></TextArea></td>
				<td>
					<ChooseParticipant participant={item.who} event={this.state.event} modifyParticipant={true} pingChangeParticipant={this.changeParticipant} />			
				</td>
				
				
				<td><button class="btn btn-danger btn-xs glyphicon glyphicon-minus" onClick={() => this.removeItem( item )} title="Remove this item"></button></td>
			</tr>
			);
		console.log("EventShoppinglist.render: list calculated from "+JSON.stringify( this.state.event.shoppinglist ));
		console.log("EventShoppinglist.render: listsize="+listShoppingListHtml.length);
		return ( <div>
					<div class="eventsection"> 
						<a href="secShoppinglist"></a>
						<a onClick={this.collapse} style={{verticalAlign: "top"}}>
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down" style={{fontSize: "small"}}></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"  style={{fontSize: "small"}}></span>}
						</a> Shopping List
						<div style={{float: "right"}}>
							<button class="btn btn-success btn-xs glyphicon glyphicon-plus" onClick={this.addItem} title="Add a new item in the list"></button>
						</div>
					</div> 
					{this.state.show ==='ON' && <table class="table table-striped toghtable">
							<thead>
								<tr >
									<th>What</th>
									<th>Description</th>
									<th>Who</th>
									<th></th>
								</tr>
							</thead>											
							{listShoppingListHtml}
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
		console.log("EventShoppinglist.setChildAttribut: set attribut:"+name+" <= "+value+" item="+JSON.stringify(item));
		const { event } = { ...this.state };
  		const currentEvent = event;

  		item[ name ] = value;

		// currentEvent.shoppinglist[0].[name] = value;
		
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	addItem() {
		console.log("EventShoppinglist.setChildAttribut: addItem item="+JSON.stringify(this.state.event));
		var currentEvent = this.state.event;		
		const newList = currentEvent.shoppinglist.concat( {"status": "TODO", "what": ""} );
		currentEvent.shoppinglist = newList;
		this.setState( { "event" : currentEvent});
		this.props.pingEvent();
	}
	
	removeItem( item ) {
		console.log("EventShoppinglist.removeItem: event="+JSON.stringify(this.state.event));

		var currentEvent = this.state.event;	
		var listShopping = 	currentEvent.shoppinglist;
		var index = listShopping.indexOf(item);
  		if (index > -1) {
   			listShopping.splice(index, 1);
  		}
		console.log("EventShoppinglist.removeItem: "+JSON.stringify(listShopping));
		currentEvent.shoppinglist = listShopping;
		console.log("EventShoppinglist.removeItem: eventAfter="+JSON.stringify(this.state.event));

		this.setState( { "event" : currentEvent });
		this.props.pingEvent();	
	} 
	
	// Apparently that's too many nested functions for React
//	change_item_status(item,new_state){
//		item.status = new_state
//		this.setState( { "event" : this.state.event});
//	};
//	
	getTagState( task, item ) {
		var changeState= (
		<OverflowMenu
      					selectorPrimaryFocus={ task }
//						onFocus={(event) => { 
//							window.alert("you just changed this"+this.props.className)
//							console.log("EventState: Click ");
//							 task = this.props.className;
//							}
//						}
    				>
							<OverflowMenuItem className="TODO" itemText="To bring" 
								onClick={() => {item.status = "TODO"
										this.setState( { "event" : this.state.event});
									} 
								}
							/>
							<OverflowMenuItem className="DONE" itemText="Done" 
								onClick={() => {item.status = "DONE"
										this.setState( { "event" : this.state.event});										
									} 
								}
							/>
							<OverflowMenuItem className="CANCEL" itemText="Cancel"
								onClick={() => {item.status = "CANCEL"
										this.setState( { "event" : this.state.event});
									} 
								}
							/>
					</OverflowMenu>
		);

		console.log("task is "+task)

		if (task === 'TODO')
			return (<Tag  type="teal" title="Task planned">To bring {changeState}</Tag>)			
		if (task === 'DONE')
			return (<Tag  type="warm-gray" title="Task is finish, well done !">Done {changeState}</Tag>);
		if (task === 'CANCEL')
			return (<Tag  type="red" title="Oups, this task was cancelled">Cancelled{changeState}</Tag>);
		 
		return (<Tag  type="gray" title="Something strange arrived">{task} {changeState}</Tag>);
	}
	
	
	changeParticipant() {
		console.log("EventShoppinglist.cchangeParticipant");
	}
}		
export default EventShoppingList;
	