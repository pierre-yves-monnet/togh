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


import { PlusCircle} from 'react-bootstrap-icons';


import EventSectionHeader 			from 'component/EventSectionHeader';
import * as userFeedbackConstant 	from 'component/UserFeedback';
import UserFeedback  				from 'component/UserFeedback';


import EventGame 					from 'event/EventGame';
import SlabRecord  					from 'service/SlabRecord';

import * as EventGameConstant 		from 'event/EventGame';

// -----------------------------------------------------------
//
// EventGameList
//
// Display the game list
//
// -----------------------------------------------------------


export const NAMEENTITY = "gamelist";


class EventGameList extends React.Component {
	// this.props.updateEvent()
	constructor(props) {
		super();
		console.log("EventGameList.constructor");
		this.eventCtrl =  props.eventCtrl;

		// keep the event in the state 
		this.state = {
			event: this.eventCtrl.getEvent(),
			show: {
				showAll: true,
				showGameAdmin:false
			},
			operation: {
				inprogress: false,
				label:"",
				status:"",
				result:"",
				listlogevents: [] 
			}
		};
		// show : OFF, ON, COLLAPSE
		this.setAttributeCheckbox		= this.setAttributeCheckbox.bind( this );
		this.addItem 					= this.addItem.bind(this);
		this.addItemCallback			= this.addItemCallback.bind( this );

	}
	
	// Calculate the state to display
	componentDidMount () {
		console.log("EventGameList.componentDidMount");
		if (this.eventCtrl.getGameList().length >0 ) {
			// current Game is the first one then
			let Game = this.eventCtrl.getGameList()[ 0 ];
			this.eventCtrl.setCurrentGameId( Game.id );
		}
	}
	
	
	// --------------------------------------------------------------
	// 
	// Render HTML
	// 
	// --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		const intl = this.props.intl;
		// console.log("EventGameList: render event="+JSON.stringify(this.state.event));
		let userParticipant = this.eventCtrl.getUserParticipant();
		let headerSection =(
			<EventSectionHeader id="game" 
				image="img/btnGames.png"
				title={<FormattedMessage id="EventGameList.MainTitleGameList" defaultMessage="Games" />}
				showPlusButton  = {true}
				showPlusButtonTitle={<FormattedMessage id="EventGameList.AddGame" defaultMessage="Add a Game in the list" />}
				userTipsText={<FormattedMessage id="EventGameList.GameTip" defaultMessage="Participants prefer to visit an art or an aerospace museum? Prefer Japanase, Italian or French restaurant? Create a Game and collect review." />}
				addItemCallback={this.addItem}
				/>
				);

		let contentPage = (<div/>);
							
		if (this.state.event.gamelist.length === 0) {
			contentPage= (
				<div>
					<FormattedMessage id="EventGameList.NoItem" defaultMessage="You don't have any game ready in the list." />
					{ userParticipant.isParticipant() && 
						<button class="btn btn-success btn-xs" 
							onClick={() => this.addItem()}
							title={intl.formatMessage({id: "EventGameList.addItem",defaultMessage: "Create a new game in the list"})}>
							<PlusCircle onClick={() => this.addItem()} />
							<FormattedMessage id="EventGameList.AddOne" defaultMessage="Add one !" />
						</button>
					}
				</div>
				)
		}
		else { 
			let listGamesHtml = [];
			
			listGamesHtml.push( this.state.event.gamelist.map( (item, index) => {
				let classGame = "list-group-item list-group-item-warning";
				let styleGame={};
				// color
				if (item.status === EventGameConstant.STATUS_INPREPAR)
					classGame= "list-group-item list-group-item-dark"
				else if (item.status === EventGameConstant.STATUS_OPEN)
					classGame= "list-group-item list-group-item-warning"
				else 			
					classGame= "list-group-item list-group-item-success";
				
				// Tab
				if (item.id.toString() === this.eventCtrl.getCurrentGameId().toString()) {
					styleGame = { borderTop: "2px solid black", borderLeft: "2px solid black", borderBottom: "2px solid black"};
				}
				else {
					styleGame = {borderRight:"2px solid black"};
				}
				return(
					<li class={classGame}
					    style={styleGame}
						key={index}
						id={item.id}
						onClick={ (event) =>{
								console.log("EventGameList.click on "+event.target.id);
								this.eventCtrl.setCurrentGameId(event.target.id);
								// do a setState to force to redisplay the component -- maybe use the context ?
								this.setState({currentGameId: event.target.id}  );
								}} > 
						{item.name}&nbsp;						
					</li>
					) }
					)
			);
			listGamesHtml.push(<li  style={{borderRight:"2px solid black", height: "40px"}} key="terminated"/>);
						
			
			var currentGame = this.eventCtrl.getCurrentGame();
			if (currentGame) {
				// console.log("EventGameList.currentGameId = "+currentGame.id);
			 }
			contentPage= (
				<div class="row">
					<div class="col-2">
						<ul class="list-group">
							{listGamesHtml}
						</ul>
					</div>
					<div class="col-10"> 
						<EventGame event={this.state.event}
							eventCtrl={this.eventCtrl}
							forceUpdatefct={() => {
								console.log("EventGameList.forceUpdate");
								// forceUpdate is a React function
								this.forceUpdate()}}
							 />
					</div>
				</div>
			)
		}
		return (
			<div>
				{headerSection}
				<UserFeedback inprogress= {this.state.operation.inprogress}
					label= {this.state.operation.label}
					status= {this.state.operation.status}
					result= {this.state.operation.result}
					listlogevents= {this.state.operation.listlogevents} />
			
				{contentPage}<br/><br/>
			</div>
			
		)							
	}
	
		
	// --------------------------------------------------------------
	// 
	// Direct HTML controls
	// 
	// --------------------------------------------------------------

	
	setChildAttribut(name, value, item, localisation) {
		// console.log("EventGameList.setChildAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		const currentEvent = this.state.event;

		item[name] = value;

		let game = this.state.event.gamelist[ this.state.currentGameId ];
		var completeLocalisation = "/gamelist/"+game.id+"/"+localisation;


		this.setState({ event: currentEvent });
		
		var slabRecord = SlabRecord.getUpdate(this.state.event, name, value, completeLocalisation);
		this.props.updateEvent( slabRecord );
		// TODO change to this.eventCtrl.setAttribut(name, value, item, this.parentLocalisation);

	}

	setAttributeCheckbox(name, value) {
		let showPropertiesValue = this.state.show;
		// console.log("EventGameList.setCheckBoxValue set "+name+"="+value+" showProperties =" + JSON.stringify(showPropertiesValue));
		if (value === 'on')
			showPropertiesValue[name] = true;
		else
			showPropertiesValue[name] = false;
		this.setState({ show: showPropertiesValue })
	}	
		
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------
	
	// --------------------------------------------------------------
	// 
	// Component controls
	// 
	// --------------------------------------------------------------


	/**
   */
	addItem() {
		const intl = this.props.intl;

		console.log("EventGame.addItem: addItem item=" + JSON.stringify(this.state.event));
		this.setState({operation:{
					inprogress:true,
					label: intl.formatMessage({id: "EventGameList.AddingGame",defaultMessage: "Adding a game"}),
					listlogevents: [] }});
		// call the server to get an ID on this taskList		
		let gameToAdd = {};
		this.eventCtrl.addEventChildFct(NAMEENTITY, gameToAdd, "", this.addItemCallback);
	}

	/**
	* addItemCallback
 	*/
	addItemCallback(httpPayload) {
		const intl = this.props.intl;

		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			// feedback to user is required
			console.log("EventGameList.addItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().limitsubscription) {
			console.log("EventGameList.callbackdata: Limit Subscription");
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventGameList.LimitSubsscription",defaultMessage: "You reach the limit of Game allowed in the event. Go to your profile to see your subscription"})
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if (httpPayload.getData().status ==="ERROR") {
			console.log("EventGameList.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
			currentOperation.status= userFeedbackConstant.ERROR;
			currentOperation.result=intl.formatMessage({id: "EventGameList.CantaddItem",defaultMessage: "A task can't be added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;
		} else if ( ! (httpPayload.getData().childEntities && httpPayload.getData().childEntities.length>0) ) {
			currentOperation.status= userFeedbackConstant.ERRORCONTRACT;
			console.log("EventGameList.addItemCallback:  BAD RECEPTION");

		} else {
			var gameToAdd = httpPayload.getData().childEntities[ 0 ];
			var event = this.eventCtrl.getEvent();
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventGameList.GameAdded",defaultMessage: "A Game is added"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			console.log("EventGameList.addItemCallback ");
			this.eventCtrl.addGameInEvent( gameToAdd );
			this.setState({ event: event,show: { showGameAdmin : true} });
		}
		this.setState({operation: currentOperation});
	}

	
	
	
}

export default injectIntl(EventGameList);