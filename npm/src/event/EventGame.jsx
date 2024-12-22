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

import { TextInput } from '@carbon/react';
import {  DashCircle} from 'react-bootstrap-icons';

import TagDropdown 					from 'component/TagDropdown';
import * as userFeedbackConstant 	from 'component/UserFeedback';
import UserFeedback  				from 'component/UserFeedback';

import * as gameListConstant 		from 'event/EventGameList';

import EventGameSecretSantas        from 'event/EventGameSecretSantas';
import EventGameTruthOrLie          from 'event/EventGameTruthOrLie';




export const DISPLAY_GAME = "GAME";
export const DISPLAY_ADMIN= "ADMIN";
export const DISPLAY_NOACCESS = "NOACCESS";

export const STATUS_INPREPAR ="INPREPAR";
export const STATUS_OPEN="OPEN";
export const STATUS_CLOSE="CLOSE";


const SECRET_SANTAS = "SECRETSANTAS";
const TRUTH_OR_LIE = "TRUTHORLIE";

// -----------------------------------------------------------
//
// EventGame
//
// Display one event
//
// -----------------------------------------------------------

class EventGame extends React.Component {


	constructor(props) {
		super();
		this.state = {
			event: props.event,
			show: {
				typeDisplay: DISPLAY_GAME,
				currentGameId: null
			},
			operation: {
				inProgress: false,
				label:"",
				status:"",
				result:"",
				listlogevents: []
			}

		};
		console.log("EventGame.constructor ");
		this.eventCtrl = props.eventCtrl;
		this.forceUpdatefct =  props.forceUpdatefct;

		// show : OFF, ON, COLLAPSE
		this.setAttributCheckbox		= this.setAttributCheckbox.bind( this );
		this.getTagState				= this.getTagState.bind(this);
        this.renderHeader               = this.renderHeader.bind(this);
        this.removeGame                 = this.removeGame.bind(this);
        this.removeGameCallback         = this.removeGameCallback.bind(this);

	}
	componentDidMount() {
	    let bestDisplay= this.calculateBestDisplay();
  		let game = this.eventCtrl.getCurrentGame();
	    console.log("EventGame.componentDidMount: typeDisplay=("+bestDisplay+") GameId=("+game.id+")");

	    this.setState( { show: { typeDisplay: bestDisplay, currentGameId: game.id }});
	}

	// Calculate the state to display
	componentDidUpdate(prevProps) {

        let prevPropsTypeDisplay='';
        if (prevProps && prevProps.show )
            prevPropsTypeDisplay=prevProps.show.typeDisplay;
	    let bestDisplay= this.calculateBestDisplay();
	    // we propose the bestDisplay only if the Game change
  		let game = this.eventCtrl.getCurrentGame();

		console.log("EventGame.componentDidUpdate prevProps=("+prevPropsTypeDisplay+") typeDisplay=("+this.state.show.typeDisplay+") bestDisplay=("+bestDisplay+")");

	    if (this.state.show.currentGameId !== game.id)
	    {
            console.log("EventGame.componentDidUpdate: refresh state");
    	    let currentShow = this.state.show;
	        if (this.state.show.typeDisplay !== bestDisplay) {
                currentShow.typeDisplay= bestDisplay;
            }
            currentShow.currentGameId=game.id;
            this.setState( {show : currentShow});
        }
	}

	calculateBestDisplay() {
		// game May be completed, set it again
  		let game = this.eventCtrl.getCurrentGame();
		this.userParticipant = this.eventCtrl.getUserParticipant();
		if (this.userParticipant.isOrganizer())
            return DISPLAY_ADMIN;

		if ( ! game.status || game.status === STATUS_INPREPAR)
			return DISPLAY_NOACCESS;

    	return DISPLAY_GAME;
	}


	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
  		let game = this.eventCtrl.getCurrentGame();

		// refresh the current Game embedded
		// this.GameEmbedded = new Game( this.state.event, currentGame, this.userParticipant, this.updateEventFct);
		console.log("EventGame.render: typeDisplay=["+this.state.show.typeDisplay+"] gameId=["+game.id+"] !");
		if (this.state.show.typeDisplay === DISPLAY_NOACCESS) {
			return (
				<div>
					<div class="row">
                        <TextInput value={game.name} onChange={(event) => this.setAttribut("name", event.target.value, game)}
                          		labelText={<FormattedMessage id="EventGame.Title" defaultMessage="Title" />}
                                readOnly={true}>
                        </TextInput>
					</div>
					<div class="row">
					    <FormattedMessage id="EventGame.NoAccessForGameInPreparation" defaultMessage="This Game is in preparation. You can't access it for the moment." />
					</div>
				</div>
			);
		}
        let headerHtml= this.renderHeader( false );

		return (<div>
       		        {headerHtml}
                    {game.typeGame === SECRET_SANTAS && <EventGameSecretSantas typeDisplay={this.state.show.typeDisplay}
                                eventCtrl={this.eventCtrl}
                         />}
                    {game.typeGame === TRUTH_OR_LIE && <EventGameTruthOrLie typeDisplay={this.state.show.typeDisplay}
                                eventCtrl={this.eventCtrl}
                         />}
                </div>
                );

	}

	// --------------------------------------------------------------
	//
	// Render HTML
	//
	// --------------------------------------------------------------



    renderHeader() {
   		const intl = this.props.intl;

        let isReadOnly = this.state.show.typeDisplay !== DISPLAY_ADMIN;

        const listOptions = [
        			{ 	label: intl.formatMessage({id: "EventGame.SecretSantas",defaultMessage: "Secret Santas"}),
        			 	value: SECRET_SANTAS,
        				icon: "img/gameSecretSantas.svg",
        			 	type: "red" },
                    { 	label: intl.formatMessage({id: "EventGame.TruthOrLie",defaultMessage: "Truth or lie"}),
        			 	value: TRUTH_OR_LIE,
        				icon: "img/gameTruthOrLie.png",
        			 	type: "blue" }
        			 	];
  		let game = this.eventCtrl.getCurrentGame();

  	    // console.log("EventGame.renderHeader: isReadOnly["+isReadOnly+"] gameId=["+game.id+"], gameName=["+game.name+"] typeGame=["+game.typeGame+"]");
        if (!game.name)
            game.name="";
        return (
            <div>
                <div class="row">
                    <div class="col-2">
                        {this.getTagState( game )}
                    </div>

                    <div class="col-5">
                        <TextInput value={game.name} onChange={(event) => this.setAttribut("name", event.target.value, game)}
                          		labelText={<FormattedMessage id="EventGame.Title" defaultMessage="Title" />}
                                readOnly={isReadOnly}>
                        </TextInput>
                    </div>

                    <div class="col-4">
                        <TagDropdown listOptions={listOptions}
                                value={game.typeGame}
                                readWrite={isReadOnly}
                                changeState={(value) => {this.setAttribut("typeGame", value, game, "");}}/>
                                <br/>
                                <span style={{fontSize: "8px"}}>
                                    <a href="https://www.flaticon.com/free-icons/wrong" title="wrong icons">icons created by Freepik - Flaticon</a>
                                </span>
                    </div>

                    <div class="col-1">
                    	{this.state.show.typeDisplay === DISPLAY_ADMIN && <button class="btn btn-danger btn-xs" onClick={() => this.removeGame(game)}
                    						                                title={intl.formatMessage({id: "EventGame.RemoveThisGame",defaultMessage: "Remove this game"})}>
                    					                                    <DashCircle/></button>}
                    </div>
                </div>
            </div>);
    }

	// --------------------------------------------------------------
	//
	// Direct HTML controls
	//
	// --------------------------------------------------------------
	/**
 	*/
	setAttribut(name, value, item) {
		console.log("EventGame.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
		this.eventCtrl.setAttribut(name, value, item, gameListConstant.NAMEENTITY+"/"+item.id);

	}
	setAttributCheckbox(name, value) {
		console.log("EventGame.setAttributCheckbox set " + name + "<=" + value.target.checked);
		let eventData = this.state.event;
		if (value.target.checked)
			eventData[name] = true;
		else
			eventData[name] = false;
		this.eventCtrl.setAttribut(name, eventData[name], eventData, "" );
		this.setState({ event: eventData });
	}




	/**
	 *
	 */
	getTagState( game ) {
		// console.log("EventGame.getTagState item.status="+game.status);
		const intl = this.props.intl;

		const listOptions = [
			{ label: intl.formatMessage({id: "EventGame.InPreparation",defaultMessage: "In preparation"}),
			 value: STATUS_INPREPAR,
			 type: "teal" },
			{ label: intl.formatMessage({id: "EventGame.InProgress",defaultMessage: "In progress"}),
			 value:  STATUS_OPEN,
			 type: "blue" },
			{ label: intl.formatMessage({id: "EventGame.Closed",defaultMessage: "closed"}),
			 value:  STATUS_CLOSE,
			 type: "green" }
		];
		return (<TagDropdown listOptions={listOptions} value={game.status} readWrite={true}
				changeState={(value) => {
				  	    this.setAttribut("status", value, this.eventCtrl.getCurrentGame());
					}} />);
	}

	// --------------------------------------------------------------
	//
	// Data operation
	//
	// --------------------------------------------------------------
    removeGame (game) {
        // console.log("EventGame.removeTask: event=" + JSON.stringify(this.state.event));

        this.setState({operation:{ inprogress:true }, listlogevents: [] });

        this.eventCtrl.removeEventChild(gameListConstant.NAMEENTITY, game.id, "", this.removeGameCallback);
    }


    removeGameCallback(httpPayload) {
		const intl = this.props.intl;
		let currentOperation = this.state.operation;
		currentOperation.inprogress = false;

		// find the task item to delete
		if (httpPayload.isError()) {
			currentOperation.status= userFeedbackConstant.ERRORHTTP;
			console.log("EventTasklist.addItemCallback: HTTP ERROR ");
		} else if (httpPayload.getData().status ==="ERROR") {
            console.log("EventTasklist.callbackdata: ERROR "+JSON.stringify(httpPayload.getData().listLogEvents));
            currentOperation.status= userFeedbackConstant.ERROR;
            currentOperation.result=intl.formatMessage({id: "EventTaskList.CantRemoveTask",defaultMessage: "The task can't be removed"});
            currentOperation.listlogevent = httpPayload.getData().listLogEvents;

		} else {
			currentOperation.status= UserFeedback.OK;
			currentOperation.result=intl.formatMessage({id: "EventGame.GameRemoved",defaultMessage: "The game is removed"});
			currentOperation.listlogevent = httpPayload.getData().listLogEvents;

			var currentEvent = this.state.event;
			let childId = httpPayload.getData().childEntitiesId[ 0 ];
			for( var i in currentEvent.gamelist) {
				if ( currentEvent.gamelist[ i ].id === childId) {
					currentEvent.gamelist.splice( i , 1);
					break;
				}
			}
			this.setState({ event: currentEvent,operation:currentOperation });
		}

		this.setState({ operation: currentOperation});
        this.forceUpdatefct();
	}


	// --------------------------------------------------------------
	//
	// Add Game answer
	//
	// --------------------------------------------------------------


	// --------------------------------------------------------------
	//
	// Component controls
	//
	// --------------------------------------------------------------



}

export default injectIntl(EventGame);