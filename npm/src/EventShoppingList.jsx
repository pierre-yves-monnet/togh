// -----------------------------------------------------------
//
// EventShoppingList
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

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
	}


	render() {
		console.log("EventShoppinglist.render: visible="+this.state.show);
		if (this.state.show === 'OFF')
			return ( <div> </div>);
		// show the list
		var listShoppingListHtml=[];
		listShoppingListHtml= this.state.event.shoppinglist.map((item) =>
			<tr class="itemcontent">
				<td><input value={item.what} onChange={(event) => this.setChildAttribut( "what", event.target.value, item )} class="toghinput"></input></td>
				<td><input value={item.description} onChange={(event) => this.setChildAttribut( "description", event.target.value, item )} class="toghinput"></input></td>
				<td><input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input></td>
				<td><button class="btn btn-danger btn-xs glyphicon glyphicon-minus" onClick={() => this.removeItem( item )} title="Remove this item"></button></td>
			</tr>
			);
		console.log("EventShoppinglist.render: list calculated from "+JSON.stringify( this.state.event.shoppinglist ));
		console.log("EventShoppinglist.render: "+listShoppingListHtml.length);
		return ( <div>
					<div class="eventsection"> 
						<a href="secShoppinglist" href="/#"></a>
						<a onClick={this.collapse}  href="/#">
							{this.state.show === 'ON' && <span class="glyphicon glyphicon-chevron-down"></span>}
							{this.state.show === 'COLLAPSE' && <span class="glyphicon glyphicon-chevron-right"></span>}
						</a> Shopping List
						<div style={{float: "right"}}>
							<button class="btn btn-success btn-xs glyphicon glyphicon-plus" onClick={this.addItem} title="Add a new item in the list"></button>
						</div>
					</div> 
					{this.state.show ==='ON' && 	<table class="table table-striped">
											<tr class="itemheader">
											
												<th>What</th>
												<th>Description</th>
												<th>Who</th>
												<th></th>
											</tr>
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
		const newList = currentEvent.shoppinglist.concat( {"what": "HH"} );
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
}		
export default EventShoppingList;
	