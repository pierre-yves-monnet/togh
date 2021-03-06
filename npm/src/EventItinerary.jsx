// -----------------------------------------------------------
//
// EventItinerary
//
// Manage Itinerary
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage,FormattedDate } from "react-intl";
import { PlusCircle, ArrowUp, ArrowDown, Cash, DashCircle, ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import { TextInput,  TimePicker, TextArea, Tag, OverflowMenu, OverflowMenuItem, ContentSwitcher, Switch, Toggle } from 'carbon-components-react';

import FactoryService from './service/FactoryService';

import Expense from './component/Expense';
import TagDropdown from './component/TagDropdown';
import UserTips from './component/UserTips';

import SlabEvent from './service/SlabEvent';


const ITINERARYITEM_POI 		= "POI";
const ITINERARYITEM_BEGIN		= "BEGIN";
const ITINERARYITEM_END			= "END";
const ITINERARYITEM_SHOPPING	= "SHOPPING";
const ITINERARYITEM_AIRPORT		= "AIRPORT";
const ITINERARYITEM_BUS			= "BUS";
const ITINERARYITEM_TRAIN		= "TRAIN";
const ITINERARYITEM_BOAT		= "BOAT";
const ITINERARYITEM_NIGHT		= "NIGHT";
const ITINERARYITEM_VISITE		= "VISITE";
const ITINERARYITEM_RESTAURANT	= "RESTAURANT";
const ITINERARYITEM_ENTERTAINMENT = "ENTERTAINMENT"

class EventItinerary extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.state = {
			event: props.event,
			show : {
				showItineraryMap: true,
				showExpense : false
				
			}
		};
		console.log("EventItinerary.constructor event="+JSON.stringify(props.event));
		
		if (!this.state.event.itinerarylist) {
			console.log("EventItinerary.constructor No tasklist defined, create an empty one")
			this.state.event.itinerarylist = [];
		}
		
		// show : OFF, ON, COLLAPSE
		console.log("secTaskList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.addItem 				= this.addItem.bind(this);
		this.upItem					= this.upItem.bind( this );
		this.downItem				= this.downItem.bind( this );
		this.moveItemOneDirection	= this.moveItemOneDirection.bind( this );
	}


	// --------------------------------- render
	render() {
		console.log("EventItinerary.render: visible=" + this.state.show+" event="+JSON.stringify(this.state.event));

		var toolService = FactoryService.getInstance().getToolService();

		var resultHtml= [];
		resultHtml.push(
				<div class="eventsection">
					<div style={{ float: "left" }}>
						<img style={{ "float": "right" }} src="img/btnItinerary.png" style={{ width: 100 }} /><br />
					</div>
					<FormattedMessage id="EventSurveyList.MainTitleItinerary" defaultMessage="Itinerary" />
				</div>);
							
		resultHtml.push(<UserTips id="itinerary" text={<FormattedMessage id="EventSurveyList.ItineraryTip" defaultMessage="List all place you want to visit during this event. If the event is on multiple days, then you can setup day per day your itinerary" />}/>);
			
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
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						labelA={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
						onChange={(event) => this.setAttributeCheckbox( "showItineraryMap", event.target.value )}
						defaultToggled={this.state.show.showItineraryMap}
						id="showitinerarymap" />
				</div>
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						labelA={<FormattedMessage id="EventItinerary.ShowExpense" defaultMessage="Show Expense"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowExpense" defaultMessage="Show Expense"/>}
						onChange={(event) => this.setAttributeCheckbox( "showExpense", event.target.value )}
						defaultToggled={this.state.show.showExpense}
						id="showexpense" />
				</div>
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						labelA={<FormattedMessage id="EventItinerary.ShowDetail" defaultMessage="Show Detail"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowDetail" defaultMessage="Show Detail"/>}
						onChange={(event) => this.setAttributeCheckbox( "showDetail", event.target.value )}
						defaultToggled={this.state.show.showDetail}
						id="showDetail" />
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
		// console.log("EventItinerary.renderCalendar ");
		var toolService = FactoryService.getInstance().getToolService();

		var listItineraryListHtml = [];
		var dateIndex = dateStart;
		var index=-10;
		while (dateIndex.getTime() <= dateEnd.getTime()) {			
			index = index - 10;
			console.log("EventItinerary.renderCalendar: index="+index+", calculate date "+JSON.stringify(dateIndex));
			var dateIndexPublish = new Date( dateIndex);
			
			var line = (
				<div style={{marginBottom: "10px"}}>
				<div class="row toghSectionHeader">
					<div class="col-10" style={{verticalAlign: "middle",paddingLeft: "20px"}}>
						<FormattedDate
			           	value={dateIndexPublish}
			           	year = 'numeric'
			           	month= 'long'
			           	day = 'numeric'
			           	weekday = 'long'
			       		/>
					</div>
					<div class="col-2"> 
						 <div style={{ float: "right" }}>
								<button  class="btn btn-success btn-xs" 
									id={dateIndexPublish.toISOString()}
									onClick={(event) => {
										console.log("EventItinerary.addFromButton :  id="+event.target.id);
										this.addItem( event.target.id, null );
									}}>
								<PlusCircle   
									id={dateIndexPublish.toISOString()}
									onClick={(event) => {
										console.log("EventItinerary.addFromPlusCircle :  id="+event.target.id);
										this.addItem( event.target.id, null );
									}
									}/>
								</button>
	
								
						</div>
					</div>
				</div>
				</div>)
			listItineraryListHtml.push( line );
			
			 
			
			
			// now attach all events on this day - ok, we parse again the list, but reminber this is a 31 lines * 200 lines each so total is 6000 iteration - moden browser can hander that isn't it?
			for (var j in this.state.event.itinerarylist) {
				var stepinlist = this.state.event.itinerarylist[ j ];
				if (toolService.getDayOfDate(stepinlist.datestep) === toolService.getDayOfDate(dateIndex)) {
					console.log("EventItinerary.renderCalendar: Found line in this date "+stepinlist.rownumber);
					var line = (<div class="toghBlock" style={{backgrounColor: "#fed9a691"}}>
										<div class="container">
											{this.renderOneStep( stepinlist,false, j )}
										</div>
									</div> );
					listItineraryListHtml.push( line ); 									
				}
			}

			// advance one day
			dateIndex.setDate( dateIndex.getDate() + 1);
		}
		console.log("EventItinerary.renderCalendar : end")
		return (  listItineraryListHtml );
		
		
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
	renderOneStep( item, showDate, index ) {
		var selectDate= (null);
		var listLines = [];

		const intl = this.props.intl;		
		const listOptions = [
			{ 	label: intl.formatMessage({id: "EventItineray.Begin",defaultMessage: "Begin"}),
			 	value: ITINERARYITEM_BEGIN,
				icon: null,
			 	type: "Blue" },
			{ 	label: intl.formatMessage({id: "EventItineray.PointOfInterest",defaultMessage: "Point Of Interest"}),				
			 	value: ITINERARYITEM_POI,
				icon: "img/panorama.svg", 
			 	type: "teal" },
			{ 	label: intl.formatMessage({id: "EventItineray.Visite",defaultMessage: "Visite"}),
			 	value: ITINERARYITEM_VISITE,
				icon: "img/museum.svg",
			 	type: "green" },
			{ 	label: intl.formatMessage({id: "EventItineray.Shopping",defaultMessage: "Shopping"}),
			 	value: ITINERARYITEM_SHOPPING,
				icon: null,
			 	type: "Purple" },
			{ 	label: intl.formatMessage({id: "EventItineray.Entertainment",defaultMessage: "Entertainment"}),
			 	value: ITINERARYITEM_ENTERTAINMENT,
				icon: "img/restaurant.svg",
			 	type: "Purple" },
			{ 	label: intl.formatMessage({id: "EventItineray.Airport",defaultMessage: "Airport"}),
			 	value: ITINERARYITEM_AIRPORT,
				icon: null,
			 	type: "Magenta" },
			{ 	label: intl.formatMessage({id: "EventItineray.BusStation",defaultMessage: "Bus Station"}),
			 	value: ITINERARYITEM_BUS,
			 	icon: null,
			 	type: "Magenta" },
			{ 	label: intl.formatMessage({id: "EventItineray.TrainStation",defaultMessage: "Train Station"}),
			 	value: ITINERARYITEM_TRAIN,
			 	icon: null,
			 	type: "Magenta" },
			{ 	label: intl.formatMessage({id: "EventItineray.Harbor",defaultMessage: "Harbor"}),
			 	value: ITINERARYITEM_BOAT,
			 	icon: null,
			 	type: "Magenta" },			
			{ 	label: intl.formatMessage({id: "EventItineray.Restaurant",defaultMessage: "Restaurant"}),
			 	value: ITINERARYITEM_RESTAURANT,
				icon: "img/restaurant.svg",
			 	type: "Purple" },
			{ 	label: intl.formatMessage({id: "EventItineray.Night",defaultMessage: "Night"}),
			 	value: ITINERARYITEM_NIGHT,
				icon: "img/hotel.svg",
			 	type: "warm-gray" },
			{ 	label: intl.formatMessage({id: "EventItineray.End",defaultMessage: "End"}),
			 	value: ITINERARYITEM_END,
				icon: null,
			 	type: "Blue" }

		
		];
					

				
							
					
						
		listLines.push(<div class="row">
				<div class="col-1">
					<div style={{paddingBottom: "10px"}}>
						<button class="btn btn-primary btn-xs" onClick={() => this.upItem( showDate, item )} title="Up this line"><ArrowUp width="20px"/></button><br/>
					</div>
					<button class="btn btn-primary btn-xs" onClick={() => this.downItem( showDate, item )} title="Down this line"><ArrowDown width="20px"/></button>
				</div>	
				
				<div class="col-2">
					<table><tr>{showDate && (<td>Show Date</td>)}
							<td><TagDropdown listOptions={listOptions} value={item.category} readWrite={true} 
									changeState={(value) => {this.setChildAttribut("category", value, item, "");}}/><br/>
								<table><tr>
									<td><TimePicker 
										value={item.visitTime} 
										labelText={<FormattedMessage id="EventItineray.VisitTime" defaultMessage="Visit time" />}
										onChange={(event) => this.setChildAttribut( "visitTime", event.target.value,item )}	/>
									</td>
									{ (item.category === ITINERARYITEM_VISITE 
										|| item.category === ITINERARYITEM_ENTERTAINMENT
										|| item.category === ITINERARYITEM_SHOPPING) && (
										<td style={{paddingLeft: "10px"}}>
										<TimePicker 
											value={item.durationTime} 
											labelText={<FormattedMessage id="EventItineray.Duration" defaultMessage="Duration" />}
											onChange={(event) => this.setChildAttribut( "durationTime", event.target.value,item )}	/>
										</td> )}
									</tr></table>
							</td>
							</tr>
					</table>
				</div>
				
				<div class="col-2">
					<TextInput 
						labelText={<FormattedMessage id="EventItineray.Title" defaultMessage="Title" />} 
						value={item.title} onChange={(event) => this.setChildAttribut("title", event.target.value, item)} />
				</div>
				
				{this.state.show.showDetail && (<div class="col-4"> 
						<TextArea
							labelText={<FormattedMessage id="EventItineray.Description" defaultMessage="Description" />} 
							value={item.description} onChange={(event) => this.setChildAttribut("description", event.target.value, item)} class="toghinput" />
					</div> ) }
				
				<div class="col-1">
					<table><tr>
					<td>
					{this.isShowDelete( item ) && <button class="btn btn-danger btn-xs" onClick={() => this.removeItem(item)} title="Remove this item">
					<DashCircle/></button>}
					</td><td>
						<button class="btn btn-primary btn-xs" onClick={() => this.addItem( item.datestep, item.rownumber+5)} ><PlusCircle/></button>
					</td>
					</tr></table>
				</div>
			</div>);
			
			if ( this.state.show.showDetail) {
				listLines.push(
					<div class="row">
						<div class="col-xl">
							<TextInput value={item.address} onChange={(event) => this.setChildAttribut("what", event.target.address, item)} 
								labelText={<FormattedMessage id="EventItineray.Address" defaultMessage="Address" />} />
						</div>
						<div class="col-md">
							{ this.state.show.showExpense &&  
								<Expense item={item.expense} event={this.state.event} updateEvent={( slabEvent ) => 
									{ 	console.log("EventItinerary.ExpenseUpdate slab="+slabEvent.getString());
										this.props.updateEvent(slabEvent)
									} }/>
							}
						</div>
					</div>
				);
				
				listLines.push(
					<div class="row">
						<div class="col-xs">
							<TextInput value={item.website} onChange={(event) => this.setChildAttribut("what", event.target.website, item)} 
								labelText={<FormattedMessage id="EventItineray.WebSite" defaultMessage="WebSite" />} />
						</div>
					</div>
				);
			}
			return listLines;
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
	// bob
	upItem( showDates, item ) {
		// console.log("EventItinerary.upItem");
		this.moveItemOneDirection( item, -1);
	}
	
	
	downItem( showDates, item ) {
		// console.log("EventItinerary.downItem");
		this.moveItemOneDirection( item, 1);		
	}

	/**
	 */
	moveItemOneDirection( item, direction) {
		var toolService = FactoryService.getInstance().getToolService();

		var	dateStartEvent = toolService.getDateFromString( this.state.event.dateStartEvent );
		var dateEndEvent   = toolService.getDateFromString( this.state.event.dateEndEvent );


		var indexInList = this.getIndexInList ( item );
		if (indexInList === null) {
			return; // not normal
		}
		
		// Move top but first in the list
		if (direction === -1 && indexInList === 0 ) {
			// first in the list, maybe back for one day ?
			var newDate = new Date( item.datestep.getTime() - 86400000 );			
			// something is wrong here if we are before the startDate!
			if (newDate.getTime() >= dateStartEvent.getTime() ) {
				item.datestep = newDate;
			}
			
		// index start at 0
		} else if (direction === 1 && indexInList === this.state.event.itinerarylist.length-1 ) {
			// Last in the list, maybe advance for one day ?
			var newDate = new Date( item.datestep.getTime() + 86400000 );			
			// something is wrong here if we are before the startDate!
			if (newDate.getTime() <= dateEndEvent.getTime() ) {
				item.datestep = newDate;
			}
			
		} else {
			// we have a guy before (or after). If the guy before is the same day? THen go before it
			var stepGuy = this.state.event.itinerarylist[ indexInList + direction ];
			if (stepGuy==null) {
				// not normal : we are not the last in the list ??
				return;
			}
			
			if ( toolService.getDayOfDate(stepGuy.datestep) === toolService.getDayOfDate(item.datestep  ))
			{
				// we just past before it
				item.rownumber = stepGuy.rownumber + (direction * 5);
			}
			else 
			{
				// move the day before, keep the same rownumber
				item.datestep = stepGuy.datestep;
			}
		}	
		var currentEvent = this.state.event;
		currentEvent.itinerarylist = this.reorderList( currentEvent.itinerarylist );
		this.setState({ "event": currentEvent });
		this.props.updateEvent( SlabEvent.getUpdateList( this.state.event, "/itinerarylist", this.state.event));	
	}

	getIndexInList( item ) {
		for (var i in this.state.event.itinerarylist) {
			if (item === this.state.event.itinerarylist[ i ]) {
				console.log("EventItinerary.getIndexInList found i="+i);
				return parseInt(i);
			}
			
		}
		return null;
	}
	/** --------------------
	 *
 	*/
	addItem(datestepSt, rownumber) {
		if (! datestepSt) {
			console.log("Eventitinerary.addItem: date is NULL !");
			return;
		}
		
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
		

		let newList = currentEvent.itinerarylist.concat({ datestep: datestep, category: "POI", rownumber: rownumber, expense: {} });
		
		currentEvent.itinerarylist = this.reorderList( newList );
		this.setState({ "event": currentEvent });
		this.props.updateEvent( );
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
		this.props.updateEvent();
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

	

}
export default injectIntl(EventItinerary);
