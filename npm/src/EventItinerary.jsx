// -----------------------------------------------------------
//
// EventItinerary
//
// Manage Itinerary
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage,FormattedDate } from "react-intl";
import { PlusCircle, ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import { TextInput,  TimePicker, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle } from 'carbon-components-react';

import FactoryService from './service/FactoryService';




class EventItinerary extends React.Component {

	// this.props.pingEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {
			event: props.event,
			show: props.show,
			collapse : props.collapse,
			showProperties : {
				showItineraryMap: true
				
			}
		};
		console.log("EventItinerary.constructor event="+JSON.stringify(props.event));
		
		if (!this.state.event.itinerarylist) {
			console.log("EventItinerary.constructor No tasklist defined, create an empty one")
			this.state.event.itinerarylist = [];
		}
		
		// show : OFF, ON, COLLAPSE
		console.log("secTaskList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.collapse = this.collapse.bind(this);
		this.addItem = this.addItem.bind(this);

	}






	// --------------------------------- render
	render() {
		console.log("EventItinerary.render: visible=" + this.state.show+" event="+JSON.stringify(this.state.event));

		




		var toolService = FactoryService.getInstance().getToolService();

		

		var resultHtml= [];
		resultHtml.push(
					<div class="eventsection"> 
						<a href="secItinerary"></a>
						<a onClick={this.collapse} style={{verticalAlign: "top"}}>
							{this.state.show === 'ON' && <ChevronDown width="20px"/>}
							{this.state.show === 'COLLAPSE' && <ChevronRight width="20px"/>}
						</a><FormattedMessage id="EventItinerary.MainTitleItinerary" defaultMessage="Itinerary"/>
					</div> 
					);
		if (this.state.show !=='ON')
			return resultHtml;
			
		// let's create a list of days.
		var dateStart = null;
		var dateEnd = null;
		if (this.state.event.datePolicy === 'ONEDATE') {
			console.log("EventItinerary : policy="+this.state.event.datePolicy+" dateEvent="+JSON.stringify(this.state.event.dateEvent));
			dateStart = toolService.getDateFromString( this.state.event.dateEvent);
			dateEnd   = dateStart;
			
			if ( ! dateStart ) {
				resultHtml.push(<FormattedMessage id="EventItinerary.ProvideDateInEvent" defaultMessage="Select a date for this event" />);
				return resultHtml;
			}
		} else {
			console.log("EventItinerary : policy="+this.state.event.datePolicy+" datePeriod="+JSON.stringify(this.state.event.dateStartEvent) +" =>" +JSON.stringify(this.state.event.dateEndEvent));

			dateStart = toolService.getDateFromString( this.state.event.dateStartEvent );
			dateEnd   = toolService.getDateFromString( this.state.event.dateEndEvent );
			
			if ( ! dateStart || ! dateEnd ) {
				resultHtml.push(<FormattedMessage id="EventItinerary.ProvidePeriodDateInEvent" defaultMessage="Select a start and a end date for this event" />);
				return resultHtml;
			}
		}

		if ( dateStart > dateEnd ) {
			var dateSav = dateStart;
			dateStart=dateEnd;
			dateEnd=dateSav;
		}
		// now, build a list day per days
		// two ways: the total period is over 31 days, provide a list of item
		// less than 31 days, create a day per day calendar
		// 
		// {<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
		// 
		resultHtml.push(
			(<div class="row">
				<div class="col-12">
				<Toggle  labelText="" aria-label="" 
					labelA={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
					labelB={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
					onChange={(event) => this.setAttributeCheckbox( "showItineraryMap", event.target.value )}
					defaultToggled={this.state.showProperties.showItineraryMap}
					id="showitinerarymap" />
				</div>
			</div> )
		);
		
		var nbDays = toolService.getNumberOfDaysInPeriod( dateStart, dateEnd);
		if (nbDays <= 31 )
			resultHtml.push( this.renderCalendar( dateStart, dateEnd ) );
		else
			resultHtml.push( this.renderList() );
		return resultHtml;

	}

	/** ---------------------------------------------
	* render Calendar
 	*/
	renderCalendar(dateStart, dateEnd ) {
		console.log("EventItinerary.renderCalendar ");
		
		
			
		var toolService = FactoryService.getInstance().getToolService();

		var listItineraryListHtml = [];
		var dateIndex = dateStart;
		var index=-10;
		while (dateIndex.getTime() <= dateEnd.getTime()) {			
			index = index - 10;
			console.log("EventItinerary.renderCalendar: index="+index+", calculate date "+JSON.stringify(dateIndex));
			var dateIndexPublish = new Date( dateIndex);
			
			var line = (<tr key={index }>
				<th colSpan="6" style={{verticalAlign: "middle",paddingLeft: "20px"}}>
					<FormattedDate
		           	value={dateIndexPublish}
		           	year = 'numeric'
		           	month= 'long'
		           	day = 'numeric'
		           	weekday = 'long'
		       		/>
				</th>
				<th> 
					 <div style={{ float: "right" }}>
							<button  class="btn btn-success btn-xs" >
							<PlusCircle   
								id={dateIndexPublish.toISOString()}
								onClick={(event) => {
									console.log("EventItinerary.add :  id="+event.target.id);
									this.addItem( event.target.id, null );
								}
								}/>
							</button>

							
					</div>
				</th>
				</tr>)
			listItineraryListHtml.push( line );
			
			 
			
			
			// now attach all events on this day - ok, we parse again the list, but reminber this is a 31 lines * 200 lines each so total is 6000 iteration - moden browser can hander that isn't it?
			for (var j in this.state.event.itinerarylist) {
				var stepinlist = this.state.event.itinerarylist[ j ];
				if (toolService.getDayOfDate(stepinlist.datestep) === toolService.getDayOfDate(dateIndex)) {
					console.log("EventItinerary.renderCalendar: Found line in this date "+stepinlist.rownumber);
					var blockLine = ( <div class="toghBlock">
										<table>
											{this.renderHeader( false, index ) }
											{this.renderOneStep( stepinlist,false, j )}
										</table>
									</div> );
				listItineraryListHtml.push( <tr><td colSpan="2"> {blockLine} </td></tr> );
	 
				}
			}

			// advance one day
			dateIndex.setDate( dateIndex.getDate() + 1);
		}
		
		return (  <table class="table table-striped toghtable"> 
					{listItineraryListHtml} 
				</table>);
		
		
	}

	/** ---------------------------------------------
	 * Period is too large : then give a list of value
	 */
	renderList() {
		var listItineraryListHtml = [];
		listItineraryListHtml.push( this.renderHeader( true, -1 ));
		for (var j in this.state.event.itinerarylist) {
			var stepinlist = this.state.event.itinerarylist[ j ];
			listItineraryListHtml.push( this.renderOneStep( stepinlist, true, j ) ); 
		}
		return (  <table class="table table-striped toghtable"> {listItineraryListHtml} </table>);
	}


	/** ---------------------------------------------
	 * render the header
	 */
	renderHeader( showDate, index ) {
		var indexLine = index - 1;
		return (
			<tr key={indexLine}>
				<td> {showDate && <FormattedMessage id="EventItineray.DateOfLine" defaultMessage="Date" />} </td>
				<td></td>
				<td></td>
				<td> <h6 class="card-subtitle mb-2 text-muted"><FormattedMessage id="EventItineray.Category" defaultMessage="Category" /></h6>	</td>
				<td> <FormattedMessage id="EventItineray.What" defaultMessage="What" />	</td>
				<td> <FormattedMessage id="EventItineray.Description" defaultMessage="Description" /> </td>
				<td> <FormattedMessage id="EventItineray.Budget" defaultMessage="Budget" />	</td>
				<td> <FormattedMessage id="EventItineray.Price" defaultMessage="Price" /> </td>
			</tr>
		)
	}
	
	/** ---------------------------------------------
	 * render the header
	 */
	renderOneStep( item, showDate, index ) {
		var selectDate= (null);
		var listLines = [];
		listLines.push(<tr key={index}>
				
				<td>{showDate && (<div>Show Date</div>)}</td>
				<td>
					<button class="btn btn-primary btn-xs" onClick={() => this.upItem( showDate, item )} title="Up this line"><ArrowUp width="20px"/></button>
				</td><td>
					<button class="btn btn-primary btn-xs" onClick={() => this.downItem( showDate, item )} title="Down this line"><ArrowDown width="20px"/></button>
				</td><td>
					{this.getTagCategory(item)}<br/>
					{ item.category === "VISITE" && <TimePicker value={item.durationTime} onChange={(event) => this.setChildAttribut( "durationTime", event.target.value,item )}	/>}
				</td>
				<td> <TextInput value={item.what} onChange={(event) => this.setChildAttribut("what", event.target.value, item)} labelText="" ></TextInput></td>
				<td> <TextArea  value={item.description} onChange={(event) => this.setChildAttribut("description", event.target.value, item)} class="toghinput" labelText=""></TextArea></td>
				<td>
					{this.isShowDelete( item ) && <button class="btn btn-danger btn-xs" onClick={() => this.removeItem(item)} title="Remove this item">
					<DashCircle/></button>}
				</td>
				<td>
					<button class="btn btn-primary btn-xs" onClick={() => this.addItem( item.datestep, item.rownumber+5)} title="Add a line"><PlusCircle/></button>
				</td>
			</tr>);
			listLines.push(
				<tr key={index+"-1"}>
					<td colSpan="2"></td>
					<td colSpan="4"><TextInput value={item.address} onChange={(event) => this.setChildAttribut("what", event.target.address, item)} 
							labelText={<FormattedMessage id="EventItineray.Address" defaultMessage="Address" />} />
					</td>
					<td> <TextInput value={item.budget} onChange={(event) => this.setChildAttribut("budget", event.target.value, item)} labelText="" /></td>
					<td> <TextInput value={item.price} onChange={(event) => this.setChildAttribut("price", event.target.value, item)} labelText="" disable="true"/>
						<button style={{float: "right"}} class="btn btn-primary btn-xs"><Cash/></button>
					</td>
				</tr>
			);
			listLines.push(
				<tr style={{borderBottom:"1pt solid black"}} key={index+"-2"}>
					<td colSpan="2"></td>
					<td colSpan="4"><TextInput value={item.website} onChange={(event) => this.setChildAttribut("what", event.target.website, item)} 
					labelText={<FormattedMessage id="EventItineray.WebSite" defaultMessage="WebSite" />} /></td>
				</tr>
			);
			return listLines;
	}
	/**
 	*/
	collapse() {
		console.log("EventItinerary.collapse");
		if (this.state.show === 'ON')
			this.setState({ 'show': 'COLLAPSE' });
		else
			this.setState({ 'show': 'ON' });
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
		this.props.pingEvent();
	}

	/** --------------------
 	*/
	setAttributeCheckbox(name, value) {
		console.log("EventTaskList.setCheckBoxValue .1");
		let showPropertiesValue = this.state.showProperties;
		console.log("EventTaskList.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ "showProperties": showPropertiesValue })
	}
	
	// only if the task is not empty	
	isShowDelete( item ) {
		if (item.what && item.what.length >0 )
			return false;
		if (item.description && item.description.length >0 )
			return false;
		if (item.address && item.address.length >0 )
			return false;
		if (item.website && item.website.length >0 )
			return false;
		return true;
	}


	// ------------------------------------------------------------------------
	// up and down
	// basically, the rownumber will be change, and then a reorder is asking
	//  one exception : in a ! showdate, if the item is the first line in the day, then we will change the day to "date -1"
	// item become then the LAST item in the previous day. The rownumber does not change, the day change. 
	// same with the down: if this is a ! showdate policy, then the row number does not change and a day +1 is added
	upItem( showDates, item ) {
		
	}
	downItem( showDates, item ) {
		
	}

	/** --------------------
	bob
 	*/
	addItem(datestepSt, rownumber) {
		var datestep = new Date( datestepSt);
		var currentEvent = this.state.event;
		console.log("Eventitinerary.addItem: addItem datestep=" + JSON.stringify(datestep)+" rownumber="+rownumber +" in list "+JSON.stringify(currentEvent.itinerarylist));

		if (! rownumber) {
			// then find it
			rownumber=0;
			for (var i in currentEvent.itinerarylist) {
				// qame date : continue to advance, idea is to be at the end of this step
				if (currentEvent.itinerarylist[ i ].datestep.getTime() <= datestep )
					rownumber=currentEvent.itinerarylist[ i ].rownumber;
			}
			rownumber = rownumber+5;
		}
		if (datestep instanceof Date) {
			console.log("Eventitinerary.addItem: this is a DATE");
		} else if (datestep instanceof String) {
			console.log("Eventitinerary.addItem: this is a STRING");
		} else
			console.log("Eventitinerary.addItem: DIFFERENT");

		let newList = currentEvent.itinerarylist.concat({ datestep: datestep, category: "POI", rownumber: rownumber });
		
		currentEvent.itinerarylist = this.reorderList( newList );
		this.setState({ "event": currentEvent });
		this.props.pingEvent();
	}

	/** --------------------
 	*/
	removeItem(item) {
		console.log("Eventitinerary.removeItem: event=" + JSON.stringify(this.state.event));

		var currentEvent = this.state.event;
		var listTask = currentEvent.itinerarylist;
		var index = listTask.indexOf(item);
		if (index > -1) {
			listTask.splice(index, 1);
		}
		currentEvent.itinerarylist = listTask;
		console.log("EventTasklist.removeItem: eventAfter=" + JSON.stringify(this.state.event));

		this.setState({ "event": currentEvent });
		this.props.pingEvent();
	}

	/** --------------------
 	*/
	reorderList( listSteps ) {
		// first pass, we reorder based on the index
		
		// second pass, we rename the list 10 per 10, then when an item is change, we recalculate a fresh list
		// example : add an item between 50 to 60 : range is 55. Then, reorder change nothing and after list is 50,60,79
		// up one the event 50 : 
		//     30 A
		// 	   40 B
		//     50 C ==> CHange to 35
		//     60 D 
		// it will be renamed 35. Then, reorder and recalcute by 30 A, 40 C, 50 B, 60 D
		listSteps = listSteps.sort((a, b) => a.rownumber > b.rownumber ? 1 : -1);
		var rownumber=10;
		for (var i in listSteps) {
			listSteps[ i ].rownumber = rownumber;
			rownumber = rownumber+10;
		}
	
		console.log("Eventitinerary.reorderList: list =" + JSON.stringify(listSteps));
		return listSteps;	
	}

	/**
	 *  getTagState
 	*/
	getTagCategory( item) {

		var changeState = (
			<OverflowMenu
				selectorPrimaryFocus={'.' + item.status}
			>
				<OverflowMenuItem className="POI" itemText={<FormattedMessage id="EventItineray.PointOfInterest" defaultMessage="Point Of Interest" />} 
					onClick={() => {item.category = "POI"
									this.setState( { "event" : this.state.event});
									}} 
				/>
				<OverflowMenuItem className="NIGHT" itemText={<FormattedMessage id="EventItineray.Night" defaultMessage="Night" />} 
					onClick={() => {item.category = "NIGHT"
									this.setState( { "event" : this.state.event});
									}} 
				/>
				<OverflowMenuItem className="VISITE" itemText={<FormattedMessage id="EventItineray.Visite" defaultMessage="Visit" />} 
						onClick={() => {item.category = "VISITE"
									this.setState( { "event" : this.state.event});
									}} 
				/>
				<OverflowMenuItem className="RESTAURANT" itemText={<FormattedMessage id="EventItineray.Restaurant" defaultMessage="Restaurant" />}
						onClick={() => {item.category = "RESTAURANT"
									this.setState( { "event" : this.state.event});
									}} 
				/>
			</OverflowMenu>
		);


		if (item.category === 'POI')
			return (<Tag type="teal" title="Task planned"><FormattedMessage id="EventItineray.PointOfInterest" defaultMessage="Point Of Interest" /> {changeState}</Tag>)
		if (item.category === 'NIGHT')
			return (<Tag type="warm-gray" title="Task in progress"><FormattedMessage id="EventItineray.Night" defaultMessage="Night" /> {changeState}</Tag>);
		if (item.category === 'VISITE')
			return (<Tag type="green" title="Task is finish, well done !"><FormattedMessage id="EventItineray.Visite" defaultMessage="Visit" /> {changeState}</Tag>);
		if (item.category === 'RESTAURANT')
			return (<Tag type="red" title="Oups, this task was cancelled"><FormattedMessage id="EventItineray.Restaurant" defaultMessage="Restaurant" />{changeState}</Tag>);

		return (<Tag type="gray" title="Something strange arrived">{item.category} {changeState}</Tag>);
	}

}
export default EventItinerary;
