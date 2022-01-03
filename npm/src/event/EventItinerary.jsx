/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
import React from 'react';

import { injectIntl, FormattedMessage,FormattedDate } from "react-intl";
import { PlusCircle, ArrowUp, ArrowDown, DashCircle } from 'react-bootstrap-icons';

import { TextInput,  TimePicker, TextArea, Toggle } from 'carbon-components-react';

import FactoryService 				from 'service/FactoryService';

import Expense 						from 'component/Expense';
import TagDropdown 					from 'component/TagDropdown';
import EventSectionHeader 			from 'component/EventSectionHeader';
import GoogleAddressGeocode 		from 'component/GoogleAddressGeocode';
import * as GeocodeConstant 		from 'component/GoogleAddressGeocode';
import GoogleMapDisplay 			from 'component/GoogleMapDisplay';
import * as userFeedbackConstant 	from 'component/UserFeedback';
import UserFeedback  				from 'component/UserFeedback';

const ITINERARYITEM_POI 		= "POI";
const ITINERARYITEM_BEGIN		= "BEGIN";
const ITINERARYITEM_END			= "END";
const ITINERARYITEM_SHOPPING	= "SHOPPING";
const ITINERARYITEM_AIRPORT		= "AIRPORT";
const ITINERARYITEM_BUS			= "BUS";
const ITINERARYITEM_TRAIN		= "TRAIN";
const ITINERARYITEM_BOAT		= "BOAT";
const ITINERARYITEM_NIGHT		= "NIGHT";
const ITINERARYITEM_VISIT		= "VISIT";
const ITINERARYITEM_RESTAURANT	= "RESTAURANT";
const ITINERARYITEM_ENTERTAINMENT = "ENTERTAINMENT"

// -----------------------------------------------------------
//
// EventItinerary
//
// Manage Itinerary
//
// -----------------------------------------------------------

class EventItinerary extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");
		this.eventCtrl =  props.eventCtrl;
		
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
		console.log("EventItinerary.constructor event="+JSON.stringify(this.eventCtrl.getEvent()));
		
		// show : OFF, ON, COLLAPSE
		console.log("secTaskList.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.addItem 				= this.addItem.bind(this);
		this.addItemCallback		= this.addItemCallback.bind( this );

		this.removeItem				= this.removeItem.bind(this );
		this.removeItemCallback		= this.removeItemCallback.bind( this );
		this.upItem					= this.upItem.bind( this );
		this.downItem				= this.downItem.bind( this );
		this.moveStepOneDirection	= this.moveStepOneDirection.bind( this );

		this.setAttributCheckbox	 	= this.setAttributCheckbox.bind( this);
		this.reorderList				= this.reorderList.bind( this);
	}


	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// --------------------------------- render
	render() {
		console.log("EventItinerary.render:  itineraryList="+JSON.stringify(this.state.event.itinerarysteplist));

		let toolService = FactoryService.getInstance().getToolService();

		let resultHtml= [];
		resultHtml.push(
			<EventSectionHeader id="itinerary" 
				image="img/btnItinerary.png" 
				title={<FormattedMessage id="EventItinerary.MainTitleItinerary" defaultMessage="Itinerary" />}
				showPlusButton  = {false}
				userTipsText={<FormattedMessage id="EventItinerary.ItineraryTip" defaultMessage="List all places you want to visit during this event. If the event is on multiple days, then you can setup day per day your itinerary" />}
				/>
				);
		resultHtml.push(
			<UserFeedback inprogress= {this.state.operation.inprogress}
				label= {this.state.operation.label}
				status= {this.state.operation.status}
				result= {this.state.operation.result}
				listlogevents= {this.state.operation.listlogevents} />
		);

		// let's create a list of days.
		let dateStart = null;
		let dateEnd = null;
		if (this.state.event.datePolicy === 'ONEDATE') {
			console.log("EventItinerary : policy="+this.state.event.datePolicy+" dateEvent="+JSON.stringify(this.state.event.dateEvent));
			dateStart = toolService.getDateFromString( this.state.event.dateEvent);
			if ( ! dateStart ) {
				resultHtml.push(<FormattedMessage id="EventItinerary.ProvideDateInEvent" defaultMessage="Select a date for this event" />);
				return resultHtml;
			}
			dateEnd   = new Date( dateStart.getTime() ); // clone the date
			
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
			let dateSav = dateStart;
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
					<Toggle labelText="" aria-label="" 
						toggled={this.state.event.itineraryshowmap}
						selectorPrimaryFocus={this.state.event.itineraryshowmap}
						labelA={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowItineraryMap" defaultMessage="Show itinerary map"/>}
						onChange={(event) => this.setAttributCheckbox( "itineraryshowmap", event )}
						id="showitinerarymap" />
				</div>
				<div class="col">
					<Toggle labelText="" aria-label="" 
						toggled={this.state.event.itineraryshowdetails}
						selectorPrimaryFocus={this.state.event.itineraryshowdetails}
						labelA={<FormattedMessage id="EventItinerary.ShowDetail" defaultMessage="Show Detail"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowDetail" defaultMessage="Show Detail"/>}
						onChange={(event) => {this.setAttributCheckbox( "itineraryshowdetails", event )} }
						id="showDetail" />
				</div>
				<div class="col">
					<Toggle  labelText="" aria-label="" 
						toggled={this.state.event.itineraryshowexpenses}
						selectorPrimaryFocus={this.state.event.itineraryshowexpenses}
						labelA={<FormattedMessage id="EventItinerary.ShowExpense" defaultMessage="Show Expense"/>}
						labelB={<FormattedMessage id="EventItinerary.ShowExpense" defaultMessage="Show Expense"/>}
						onChange={(event) => this.setAttributCheckbox( "itineraryshowexpenses", event )}
						defaultToggled={this.state.event.itineraryshowexpenses}
						disabled={this.state.event.itineraryshowDetail === false}
						id="showexpense" />
				</div>
			</div> )
		);
		
		let nbDays = toolService.getNumberOfDaysInPeriod( dateStart, dateEnd);
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
		let toolService = FactoryService.getInstance().getToolService();

		let listItineraryListHtml = [];
		let dateIndex = new Date( dateStart.getTime() ); // clone it
		let index=-10;
		let count=0;

		// two special situation: there is steps BEFORE the dateState, and there is steps AFTER the dateStart
		// We move theses dates to the dateStart
		for (let j in this.state.event.itinerarysteplist) {
        		let stepinlist = this.state.event.itinerarysteplist[ j ];
        		if (toolService.getDayOfDate(stepinlist.dateStep) < toolService.getDayOfDate(dateStart)) {
        		    stepinlist.dateStep = new Date( dateStart.getTime())
        		}
        		if (toolService.getDayOfDate(stepinlist.dateStep) > toolService.getDayOfDate(dateEnd)) {
        		    stepinlist.dateStep = new Date( dateEnd.getTime())
        		}
        }

		// now we can loop on dates
		while (dateIndex.getTime() <= dateEnd.getTime()) {
			count++;
			// protection
			if (count > 100)
				break;			
			index = index - 10;
			// console.log("EventItinerary.renderCalendar: index="+index+", calculate date "+JSON.stringify(dateIndex));
			let dateIndexPublish = new Date( dateIndex);
			
			let line = (
				<div style={{marginBottom: "10px"}} key={index}>
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
									<PlusCircle id={dateIndexPublish.toISOString()}/>
								</button>
							</div>
						</div>
					</div>
				</div>)
			listItineraryListHtml.push( line );
			
			 
			
			
			// now attach all events on this day - ok, we parse again the list, but remember this is a 31 lines * 200 lines each so total is 6000 iteration - modern browser can handle that isn't it?
			let listMarkers = [];
			let countStepsInTheDay=0;
			for (let j in this.state.event.itinerarysteplist) {
				let stepinlist = this.state.event.itinerarysteplist[ j ];
				if (toolService.getDayOfDate(stepinlist.dateStep) === toolService.getDayOfDate(dateIndex)) {
					// console.log("EventItinerary.renderCalendar: Found line in this date "+stepinlist.rownumber);
					line = (<div class="toghBlock" style={{backgroundColor: "#fed9a691"}}>
										<div class="container">
											{this.renderOneStep( stepinlist,false, j )}
										</div>
							</div> );
					countStepsInTheDay++;
					if (stepinlist.geolat)
						listMarkers.push({ lat: stepinlist.geolat, lng: stepinlist.geolng, text:stepinlist.name, category:stepinlist.category });
					listItineraryListHtml.push( line ); 									
				}
			}
			
			// calculate the itinerary of the day
			if ( this.state.event.itineraryshowmap && countStepsInTheDay>0) {
				// console.log("EventItinerary.renderCalendar: Draw map of the day "+JSON.stringify(listMarkers));

				if (listMarkers.length === 0)
					listItineraryListHtml.push(<FormattedMessage id="EventItinerary.NoAddressProvided" defaultMessage="No address are provided for this day."/>);	
				else 
					listItineraryListHtml.push(<div><center> <GoogleMapDisplay  positions={listMarkers} /></center></div>	);
			}

			// advance one day
			dateIndex.setDate( dateIndex.getDate() + 1);
		}
		// console.log("EventItinerary.renderCalendar : end")
		return (  listItineraryListHtml );
		
		
	}

	/** ---------------------------------------------
	 * Period is too large : then give a list of value
	 */
	renderList() {
		let listItineraryListHtml = [];
		
		for (let j in this.state.event.itinerarysteplist) {
			let stepinlist = this.state.event.itinerarysteplist[ j ];
			listItineraryListHtml.push( 
				<div class="toghBlock" style={{backgrounColor: "#fed9a691"}}>
					<div class="container"> 
					{this.renderOneStep( stepinlist, true, j ) }
					</div>
				</div>);
		}
		return ( {listItineraryListHtml} );
	}


	
	
	/** ---------------------------------------------
	 * render the header
	 */
	renderOneStep( item, showDate, index ) {
		
		let listLines = [];

		const intl = this.props.intl;		
		const listOptions = [
			{ 	label: intl.formatMessage({id: "EventItineray.Begin",defaultMessage: "Begin"}),
			 	value: ITINERARYITEM_BEGIN,
				icon: "img/itineraryBegin.svg",
			 	type: "Blue" },
			{ 	label: intl.formatMessage({id: "EventItineray.PointOfInterest",defaultMessage: "Point Of Interest"}),				
			 	value: ITINERARYITEM_POI,
				icon: "img/panorama.svg", 
			 	type: "teal" },
			{ 	label: intl.formatMessage({id: "EventItineray.Visite",defaultMessage: "Visite"}),
			 	value: ITINERARYITEM_VISIT,
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
					
		// console.log("EventItinerary.renderOneStep showdetails="+this.state.event.itineraryshowdetails);	
						
		listLines.push(<div class="row">
				<div class="col-1">
					<div style={{paddingBottom: "10px"}}>
						<button class="btn btn-primary btn-xs" onClick={() => this.upItem( showDate, item )}
						        title={<FormattedMessage id="EventItineray.UpThisLine" defaultMessage="Up this line" />}
						        ><ArrowUp width="20px"/></button><br/>
					</div>
					<button class="btn btn-primary btn-xs" onClick={() => this.downItem( showDate, item )}
					        title={<FormattedMessage id="EventItineray.downThisLine" defaultMessage="Down this line" />}><ArrowDown width="20px"/></button>
					{this.rownumber}
				</div>	
				
				<div class="col-2">
					<table><tr>{showDate && (<td>Show Date</td>)}
							<td><TagDropdown listOptions={listOptions} value={item.category} readWrite={true} 
									changeState={(value) => {this.setChildAttribut("category", value, item, "");}}/><br/>
								<table><tr>
									<td><TimePicker 
										id={item.id}
										value={item.visitTime} 
										labelText={<FormattedMessage id="EventItineray.VisitTime" defaultMessage="Visit time" />}
										onChange={(event) => this.setChildAttribut( "visitTime", event.target.value,item )}	/>
									</td>
									{ (item.category === ITINERARYITEM_VISIT 
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
						value={item.name} onChange={(event) => this.setChildAttribut("name", event.target.value, item)} />
				</div>
				
				{this.state.event.itineraryshowdetails && (<div class="col-4"> 
						<TextArea
							labelText={<FormattedMessage id="EventItineray.Description" defaultMessage="Description" />} 
							value={item.description} onChange={(event) => this.setChildAttribut("description", event.target.value, item)} 
							class="toghinput" />
					</div> ) }
				
				<div class="col-1">
					<table><tr>
					<td>
					{this.isShowDelete( item ) && <button class="btn btn-danger btn-xs" onClick={() => this.removeItem(item)} 
						title={intl.formatMessage({id: "EventItineray.RemoveThisStep",defaultMessage: "Remove this step"})}>
					<DashCircle/></button>}
					</td><td>
						<button class="btn btn-primary btn-xs" onClick={() => this.addItem( item.dateStep, item.rownumber+5)} ><PlusCircle/></button>
					</td>
					</tr></table>
				</div>
			</div>);
			
			if ( this.state.event.itineraryshowdetails) {
				listLines.push(
					<div class="row">
						<div class="col-xl">
							<GoogleAddressGeocode item={item} 
								labelField={<FormattedMessage id="EventItineray.Address" defaultMessage="Address" />} 
								changeCallbackfct={(type, itemUpdated) => {
									if (type === GeocodeConstant.CHANGE_ADDRESS) {
										this.setChildAttribut("geoaddress", itemUpdated.geoaddress, item);
									}
									if (type === GeocodeConstant.CHANGE_LATLNG) {
										this.setChildAttribut("geolat", itemUpdated.geolat, item);
										this.setChildAttribut("geolng", itemUpdated.geolng, item);
										// this.forceUpdate(); // refresh the map
									}	
								}} />
			
						</div>
						<div class="col-md">
							{this.state.event.itineraryshowexpenses &&  
								<Expense item={item.expense} 
									eventCtrl={this.eventCtrl} 
									parentLocalisation={"/itinerarysteplist/"+item.id+"/expense"}/>
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
	
		
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	/**
 	*/
	setChildAttribut(name, value, item) {
		console.log("EventTasklist.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		this.eventCtrl.setAttribut(name, value, item, "/itinerarysteplist/"+item.id);
	}

	/** --------------------
 	*/
	setAttributCheckbox(name, event) {
		console.log("EventItinerary.setAttributCheckbox set " + name + "<=" + event.target.checked);
		let eventValue=this.state.event;
		if (event.target.checked)
			eventValue[name] = true;
		else
			eventValue[name] = false;

		this.eventCtrl.setAttribut(name, eventValue[name], this.state.event, "" );
		this.setState({ event: this.state.event });
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

	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	// ------------------------------------------------------------------------
	// up and down
	// basically, the rownumber will be change, and then a reorder is asking
	//  one exception : in a ! showdate, if the item is the first line in the day, then we will change the day to "date -1"
	// item become then the LAST item in the previous day. The rownumber does not change, the day change. 
	// same with the down: if this is a ! showdate policy, then the row number does not change and a day +1 is added
	upItem( showDates, item ) {
		// console.log("EventItinerary.upItem");
		this.moveStepOneDirection( item, -1);
	}
	
	
	downItem( showDates, item ) {
		// console.log("EventItinerary.downItem");
		this.moveStepOneDirection( item, 1);		
	}

	/**
	 */
	moveStepOneDirection( item, direction) {
		let toolService = FactoryService.getInstance().getToolService();

		let	dateStartEvent = toolService.getDateFromString( this.state.event.dateStartEvent );
		let dateEndEvent   = toolService.getDateFromString( this.state.event.dateEndEvent );

		let indexInList = this.getIndexInList ( item );
		if (indexInList === null) {
			return; // not normal
		}
		
		// Move top but first in the list
		if (direction === -1 && indexInList === 0 ) {
			// first in the list, maybe back for one day ?
			let newDate = new Date( this.getDateItem(item).getTime() - 86400000 );
			// something is wrong here if we are before the startDate!
			if (newDate.getTime() >= dateStartEvent.getTime() ) {
				item.dateStep = newDate;
				this.setChildAttribut( "dateStep",item.dateStep ,item );
			}
			
		// index start at 0
		} else if (direction === 1 && indexInList === this.state.event.itinerarysteplist.length-1 ) {
			// Last in the list, maybe advance for one day ?
			let newDate = new Date( this.getDateItem( item ).getTime() + 86400000 );
			// something is wrong here if we are before the startDate!
			if (newDate.getTime() <= dateEndEvent.getTime() ) {
				item.dateStep = newDate;
				this.setChildAttribut( "dateStep",item.dateStep ,item );
			}
			
		} else {
			// we have a guy before (or after). If the guy before is the same day? THen go before it
			let stepGuy = this.state.event.itinerarysteplist[ indexInList + direction ];
			if (stepGuy==null) {
				// not normal : we are not the last in the list ??
				return;
			}
			
			if ( toolService.getDayOfDate( this.getDateItem(stepGuy )) === toolService.getDayOfDate( this.getDateItem( item)  ))
			{
				// we just past before it
				item.rownumber = stepGuy.rownumber + (direction * 5);
				// row number will be recalculated and change after the reorder
			}
			else 
			{
				// move the day before, keep the same rownumber
				item.dateStep = stepGuy.dateStep;
				this.setChildAttribut( "dateStep",item.dateStep ,item );
			}
		}	
		let currentEvent = this.eventCtrl.getEvent();
		currentEvent.itinerarysteplist = this.reorderList( currentEvent.itinerarysteplist );
		this.setState({ "event": currentEvent });
		
			
	}

	getIndexInList( item ) {
		for (let i in this.state.event.itinerarysteplist) {
			if (item === this.state.event.itinerarysteplist[ i ]) {
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
		let toolService = FactoryService.getInstance().getToolService();

		let datestep = new Date( datestepSt);
		let currentEvent = this.state.event;
		
		console.log("Eventitinerary.addItem: addItem datestep=" + JSON.stringify(datestep)+" rownumber="+rownumber +" in list "+JSON.stringify(currentEvent.itinerarysteplist));

		
		if (! rownumber) {
			// then find it
			rownumber=0;
			for (let i in currentEvent.itinerarysteplist) {
				// qame date : continue to advance, idea is to be at the end of this step
				let dateStepIndex = toolService.getDateFromString( currentEvent.itinerarysteplist[ i ].dateStep);
				
				if (dateStepIndex.getTime() <= datestep.getTime() )
					rownumber=currentEvent.itinerarysteplist[ i ].rownumber;
			}
			rownumber = rownumber+5;
		}
		

		let stepToAdd ={ datestep: toolService.getDayStringFromDate( datestep ), name:"", category: "POI", rownumber: rownumber};
		// call the server to get an ID on this survey
		const intl = this.props.intl;
		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "EventItinerary.AddingStep",defaultMessage: "Adding a step"}), 
					listlogevents: [] }});


 		this.eventCtrl.addEventChildFct( "itinerarysteplist", stepToAdd, "", this.addItemCallback);
	}

	addItemCallback(httpPayload) {
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;
		
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;			
			console.log("EventItinerary.addItemCallback: ERROR ");
		} else if (httpPayload.getData().limitsubscription) {
			console.log("EventTasklist.callbackdata: Limit Subscription");
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventItinerary.LimitSubsscription",defaultMessage: "You reach the limit of steps allowed in the event. Go to your profile to see your subscription"})
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if (httpPayload.getData().status ==="ERROR") {
				console.log("EventItinerary.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
				currentOperation.status= userFeedbackConstant.ERROR;
				currentOperation.result=intl.formatMessage({id: "EventItinerary.CantaddItem",defaultMessage: "A step can't be added"});
				currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventItinerary.StepAdded",defaultMessage: "A step is added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			let stepToAdd = httpPayload.getData().childEntities[0];
			stepToAdd.expense={};
			let event = this.eventCtrl.getEvent();
			let newList= event.itinerarysteplist.concat( stepToAdd );
			event.itinerarysteplist = this.reorderList( newList );
			this.setState( { event: event});
		}
		this.setState({operation: currentOperation});
	}






	/** --------------------
 	*/
	removeItem(item) {
		const intl = this.props.intl;
		console.log("Eventitinerary.removeItem: event=" + JSON.stringify(this.state.event));

		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "Eventitinerary.RemovingStep",defaultMessage: "Removing a step"}), 
					listlogevents: [] }});
	
		let currentEvent = this.eventCtrl.getEvent();
		let listSteps = currentEvent.itinerarysteplist;
		let index = listSteps.indexOf(item);
		if (index > -1) {
			this.eventCtrl.removeEventChild("itinerarysteplist", listSteps[ index ].id, "", this.removeItemCallback);
			// listSteps.splice(index, 1);
		}
		// currentEvent.itineraryStepList = listSteps;
		console.log("Eventitinerary.removeItem: eventAfter=" + JSON.stringify(this.currentEvent));

		this.setState({ "event": currentEvent });
	}

	removeItemCallback( httpPayload) {
		const intl = this.props.intl;
		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;

		// find the task item to delete
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;			
			console.log("Eventitinerary.addTaskCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
				console.log("Eventitinerary.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
				currentOperation.status= userFeedbackConstant.ERROR;
				currentOperation.result=intl.formatMessage({id: "Eventitinerary.CantremoveItem",defaultMessage: "The step can't be removed"});
				currentOperation.listlogevent = httpPayload.getData().listLogEvents;

		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "Eventitinerary.StepRemoved",defaultMessage: "The step is removed"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			let currentEvent = this.state.event;
			let childId = httpPayload.getData().childEntitiesId[ 0 ];
			for( let i in currentEvent.itinerarysteplist) {
				if ( currentEvent.itinerarysteplist[ i ].id === childId) {
					currentEvent.itinerarysteplist.splice( currentEvent.itinerarysteplist[ i ], 1);
					break;
				}
			}
			this.setState({ event: currentEvent });
		}
		
		this.setState({ operation: currentOperation});

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
		let rownumber=10;
		for (let i in listSteps) {
			if (listSteps[ i ].rownumber !== rownumber) {
				this.setChildAttribut("rownumber", rownumber, listSteps[ i ]);
			}
			listSteps[ i ].rownumber = rownumber;
			rownumber = rownumber+10;
		}
	
		console.log("Eventitinerary.reorderList: list =" + JSON.stringify(listSteps));
		return listSteps;	
	}

	// dateState may be a date or a String
	getDateItem( item ) {
		if ( item.dateStep instanceof Date)
			return item.dateStep;
		let dateInTimeZone= new Date( item.dateStep);
		// the dateStap is something like this : "2021-08-02", meaning 08-02 IN MY TIMEZONE.
		// Buit JS think this is a UTC time, so now we need to move to the UTC date 
		let timezoneoffset = new Date().getTimezoneOffset();
		dateInTimeZone= new Date( dateInTimeZone.getTime() + timezoneoffset*60000);
		return dateInTimeZone;
	}

}
export default injectIntl(EventItinerary);
