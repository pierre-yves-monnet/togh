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

import { TextInput, TextArea, Toggle,InlineLoading } from 'carbon-components-react';
import {  List} from 'react-bootstrap-icons';


import * as gameListConstant 		from 'event/EventGameList';

import FactoryService 				from 'service/FactoryService';

import * as gameConstant 		    from 'event/EventGame';




// -----------------------------------------------------------
//
// EventGameSecretSantas
//
// Display the Secret Santas game
//
// -----------------------------------------------------------

class EventGameSecretSantas extends React.Component {


	constructor(props) {
		super();
		this.state = {
			event: props.event,
			show: {
				typeDisplay: props.typeDisplay,
				currentGameId: null
			},
			inProgress:false


		};
		console.log("EventGameSecretSantas.constructor typeDisplay="+props.typeDisplay);
		this.eventCtrl = props.eventCtrl;
		this.forceUpdatefct =  props.forceUpdatefct;

		// show : OFF, ON, COLLAPSE
		this.setAttributCheckbox		= this.setAttributCheckbox.bind( this );
		this.renderGameAdmin            = this.renderGameAdmin.bind(this);
		this.renderGame                 = this.renderGame.bind(this);
        this.synchronizePlayers         = this.synchronizePlayers.bind(this);
        this.newDrawPlayers             = this.newDrawPlayers.bind(this);

	}


	// Calculate the state to display
	componentDidUpdate(prevProps) {
        let prevPropsTypeDisplay='';
        if (prevProps )
            prevPropsTypeDisplay=prevProps.typeDisplay;
	    console.log("EventGameSecretSantas.componentDidUpdate prevPropsTypeDisplay=("+prevPropsTypeDisplay+")");
	    // we propose the bestDisplay only if the Game change
  		let game = this.eventCtrl.getCurrentGame();
	    if (this.state.show.currentGameId !== game.id
	        || this.state.show.typeDisplay !== prevPropsTypeDisplay) {
		    console.log("EventGameSecretSantas.componentDidUpdate prevProps=("+prevPropsTypeDisplay+")");
            this.setState( {show : {typeDisplay: prevPropsTypeDisplay, currentGameId: game.id}});
        }

	}



  // --------------------------------------------------------------
    //
    // render
    //
    // --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
  		let game = this.eventCtrl.getCurrentGame();

		// refresh the current Game embedded
		// this.GameEmbedded = new Game( this.state.event, currentGame, this.userParticipant, this.updateEventFct);
		console.log("EventGameSecretSantas.render: typeDisplay=["+this.state.show.typeDisplay+"] gameId=["+game.id+"] !");

        let panelHtml='';
        if (this.state.show.typeDisplay === gameConstant.DISPLAY_ADMIN) {
            panelHtml=this.renderGameAdmin();
        }
        return (<div>
                <div  class="toghBlock" style={{backgroundColor: "#fed9a691",margin: "10px 10px 10px 10px", padding: "20px 20px 20px 20px"}}>
                    <FormattedMessage id="EventGameSecretSantas.explanation" defaultMessage="Each player gets a participant name. Offer him/her/in a gift. And keep in mind you are on the list of someone." />
                </div>
                <TextArea value={game.description}
                                onChange={(event) => {
                                    this.setAttribut("description", event.target.value,game);
                                    this.setState({  event: this.eventCtrl.getEvent() });
                                }}
                                labelText={<FormattedMessage id="EventGameSecretSantas.Description" defaultMessage="Description" />}
                                readOnly={this.state.show.typeDisplay !== gameConstant.DISPLAY_ADMIN}/>

                {this.renderGame()}
                {panelHtml}
                </div>)
    }

    // administration
    renderGameAdmin() {
   		let game = this.eventCtrl.getCurrentGame();

        let listPlayersHtml=<FormattedMessage id="EventGameSecretSantas.YouDontWantToSeeTheList" defaultMessage="You don't want to see the list..." />;
        if (game.adminShowList) {
            let contentPlayersHtml = game.playersList.map((player,index) =>
            		<tr>
            		    <td>{player.userLabel}</td>
            		    <td><FormattedMessage id="EventGameSecretSantas.PrepareAGiftTo" defaultMessage="Prepare a gift to" /></td>
            		    <td>{player.giftToPlayerLabel}</td>
            		</tr>)
            listPlayersHtml = (<table class="table table-striped toghtable" style={{padding: ".5rem .5rem",
                               							borderBottomWidth: "1px",
                               							boxShadow: "inset 0 0 0 9999px var(--bs-table-accent-bg)",
                               							borderBottomColor: "currentColor"}}>
                                <thead>
                                <tr>
                               	   <th><FormattedMessage id="EventGameSecretSantas.Player" defaultMessage="Player" /></th>
                               	   <th></th>
                               	   <th><FormattedMessage id="EventGameSecretSantas.PlayerTo" defaultMessage="To Player" /></th>
                               	</tr>
                               	</thead>
                               	{contentPlayersHtml}
                               	</table>)
        }
         return (<div>
                    <div class="row">
                        <h1><FormattedMessage id="EventGameSecretSantas.Administration" defaultMessage="Manage the Secret Santas" /></h1>
                    </div>
                    <div class="row">
                        <div class="col-4">
                            <FormattedMessage id="EventGameSecretSantas.Scope" defaultMessage="Players of the Secret Santas" />
                        </div>
                        <div class="col-6">
                            <div class="btn-group btn-group-sm radio toggle button group Basic" role="group" aria-label="Status" >
                                <input type="radio" class="btn-check" name="btnradiostate" id="filterState1" autoComplete="off"
                                    checked={game.scopeGame === "ALL"}
                                    onChange={() => this.setAttribut("scopeGame", "ALL", game)}
                                    disabled={this.state.inProgress}/>
                                <label class="btn btn-outline-primary" for="filterState1">
                                    <List />&nbsp;<FormattedMessage id="EventGameSecretSantas.ScopeAll" defaultMessage="All participants" />
                                </label>

                                <input type="radio" class="btn-check" name="btnradiostate" id="filterState2" autocomplete="off"
                                    checked={game.scopeGame === "ACTIVE"}
                                    onChange={() => this.setAttribut("scopeGame", "ACTIVE",game)}
                                    disabled={this.state.inProgress}/>
                                <label class="btn btn-outline-primary" for="filterState2">
                                    <List />&nbsp;<FormattedMessage id="EventGameSecretSantas.ScopeActive" defaultMessage="Active participants only" />
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-4">
                            <FormattedMessage id="EventGameSecretSantas.ShowPlayers" defaultMessage="List of players" />
                        </div>
                        <div class="col-6">
                            <Toggle labelText=""
                                    toggled={game.adminShowList}
                                    selectorPrimaryFocus={game.adminShowList}
                                    labelA={<FormattedMessage id="EventGameSecretSantas.listVisibleToggleOff" defaultMessage="Hide the list" />}
                                    labelB={<FormattedMessage id="EventGameSecretSantas.listVisibleToggleOn" defaultMessage="Show the list" />}
                                    onChange={(event) => {
                                        this.setAttributCheckbox("adminShowList", event, game);}}
                                    id="visible"
                                    disabled={this.state.inProgress}/>
                        </div>
                    </div>
                    <div class="row" style={{padding: "8px 0px 10px"}}>
                        <div class="col-4">
                            <FormattedMessage id="EventGameSecretSantas.NumberOfPlayerInScope" defaultMessage="Potential players" />
                        </div>
                        <div class="col-8">
                            {game.numberOfParticipantsInTheScope}
                   			{this.state.inProgress && (<div style={{display: "inline-block"}}><InlineLoading description="recalculate" status='active'/></div>) }                        </div>
                    </div>
                    <div class="row" style={{padding: "8px 0px 10px"}}>
                        <div class="col-4">
                            <FormattedMessage id="EventGameSecretSantas.NumberOfPlayers" defaultMessage="Number of players" />
                        </div>
                        <div class="col-8">
                            {game.numberOfPlayers}
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-4">
                            <FormattedMessage id="EventGameSecretSantas.IncludeNewPerson" defaultMessage="Adjust players to participants" />
                        </div>
                        <div class="col-4">
                            <button class="btn btn-primary btn-sm" onClick={() => this.synchronizePlayers()}
                                title=""
                                disabled={this.state.inProgress}>
                                 <FormattedMessage id="EventGameSecretSantas.SynchronizePlayers" defaultMessage="Synchronize players" />
                                </button>
                        </div>
                        <div class="col-4">
                            {game.status === gameConstant.STATUS_INPREPAR &&
                                <button class="btn btn-primary btn-sm" onClick={() => this.newDrawPlayers()}
                                    title=""
                                    disabled={this.state.inProgress}>
                                     <FormattedMessage id="EventGameSecretSantas.NewDraw" defaultMessage="New Random Draw" />
                                    </button>
                                }
                        </div>
                    </div>
                    <div class="row" style={{marginTop:"20px"}}>
                        {listPlayersHtml}
                    </div>


               </div>
                )
    }

    // renderGame : display who is affected to you
    renderGame() {
   		let game = this.eventCtrl.getCurrentGame();
   		let panelHtml='';
   		if (game.giftedLabel) {
   		    panelHtml = (<TextInput value={game.giftedLabel}  labelText="" readOnly="true"></TextInput>)
   		} else {
   		    panelHtml = (<FormattedMessage id="EventGameSecretSantas.noAffectation" defaultMessage="No one has been assigned to you yet." />)
   		}
        return (<div>
                    <div class="row">
                        <h1><FormattedMessage id="EventGameSecretSantas.giftedForYou" defaultMessage="You have to do a secret gift to" /></h1>
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <FormattedMessage id="EventGameSecretSantas.YourSecretSantas" defaultMessage="Your Secret Santas" />
                        </div>
                        <div class="col-6">
                            {panelHtml}
                        </div>
                    </div>
            </div>);
    }


    synchronizePlayers() {
      this.setState({inProgress:true});
		const restCallService = FactoryService.getInstance().getRestCallService();
   		let game = this.eventCtrl.getCurrentGame();

		let param={'eventId': this.eventCtrl.event.id, 'gameId':game.id};

		restCallService.postJson('/api/event/game/synchronizeplayers', this, param, httpPayload => {
			// update the players list
			if (httpPayload.getData() && httpPayload.getData().childEntities && httpPayload.getData().childEntities.length>0) {
                game.playersList = httpPayload.getData().childEntities[0].playersList;
                game.numberOfParticipantsInTheScope = httpPayload.getData().childEntities[0].numberOfParticipantsInTheScope;
                game.numberOfPlayers = httpPayload.getData().childEntities[0].numberOfPlayers;
            }
			this.setState({inProgress:false});
			}
		);
    }

    newDrawPlayers() {
        this.setState({inProgress:true});
        const restCallService = FactoryService.getInstance().getRestCallService();
        let game = this.eventCtrl.getCurrentGame();

        let param={'eventId': this.eventCtrl.event.id, 'gameId':game.id, 'reset':true};

        restCallService.postJson('/api/event/game/synchronizeplayers', this, param, httpPayload => {
            // update the players list
            if (httpPayload.getData() && httpPayload.getData().childEntities && httpPayload.getData().childEntities.length>0) {
                game.playersList = httpPayload.getData().childEntities[0].playersList;
                game.numberOfParticipantsInTheScope = httpPayload.getData().childEntities[0].numberOfParticipantsInTheScope;
                game.numberOfPlayers = httpPayload.getData().childEntities[0].numberOfPlayers;
            }
            this.setState({inProgress:false});
            }
        );
    }
    // --------------------------------------------------------------
    //
    // Direct HTML controls
    //
    // --------------------------------------------------------------

    setAttribut(name, value, item) {
        console.log("EventGameSecretSantas.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
        this.eventCtrl.registerUpdateCallback(this);
        this.setState({inProgress:true});
        this.eventCtrl.setAttribut(name, value, item, gameListConstant.NAMEENTITY+"/"+item.id);
    }


    setAttributCheckbox(name, value, item) {
        console.log("EventGameSecretSantas.setAttributCheckbox set " + name + "<=" + value.target.checked);
        let valueBoolean;
        if (value.target.checked)
            valueBoolean = true;
        else
            valueBoolean = false;
        this.setAttribut(name, valueBoolean, item );
    }

    callbackUpdate( dataPayload) {
        console.log("EventGameSecretSantas.httpPayload set " + dataPayload );
   		let game = this.eventCtrl.getCurrentGame();
        if (dataPayload.childEntities.length>0) {
            game.numberOfParticipantsInTheScope = dataPayload.childEntities[0].numberOfParticipantsInTheScope;
            game.numberOfPlayers = dataPayload.childEntities[0].numberOfPlayers;

            // force the component to render again
            this.setState({'numberOfParticipantsInTheScope':game.numberOfParticipantsInTheScope});
        }
        this.setState({inProgress:false});
    }
}

export default injectIntl(EventGameSecretSantas);