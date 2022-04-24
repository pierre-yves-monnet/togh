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

import { TextInput, TextArea, InlineLoading, Select } from 'carbon-components-react';
import { List, HandThumbsUpFill, HandThumbsDownFill, ArrowLeft, ArrowRight } from 'react-bootstrap-icons';

import UserMessage 		            from 'component/UserMessage';
import ChartTogh                    from 'component/ChartTogh';

import FactoryService 				from 'service/FactoryService';

import * as gameListConstant 		from 'event/EventGameList';
import * as gameConstant 		    from 'event/EventGame';


const CST_LIE= "LIE";
const CST_TRUTH="TRUTH";





// -----------------------------------------------------------
//
// EventGameTruthOrLie
//
// Display the Truth Or Lie game
//
// -----------------------------------------------------------

class EventGameTruthOrLie extends React.Component {


	constructor(props) {
		super();
		this.state = {
			event: props.event,
			show: {
				typeDisplay: props.typeDisplay,
				currentGameId: null,
				currentVote:0,
				tabName:'SENTENCES'
			},
			inProgress:false,
			inProgressVote: false,
			isErrorVote: false
		};

		this.eventCtrl = props.eventCtrl;
		this.forceUpdatefct =  props.forceUpdatefct;

		// show : OFF, ON, COLLAPSE
		this.renderSentences            = this.renderSentences.bind(this);
		this.renderVote                 = this.renderVote.bind(this);
		this.renderResults              = this.renderResults.bind(this);
		this.renderParameters           = this.renderParameters.bind(this);
        this.setSentence                = this.setSentence.bind(this);
		this.setAttributCheckbox		= this.setAttributCheckbox.bind( this );
		this.setAttribut		        = this.setAttribut.bind( this );
		this.setShowStateProperty       = this.setShowStateProperty.bind( this );
	}


	// Calculate the state to display
	componentDidUpdate(prevProps) {
        let prevPropsTypeDisplay='';
        if (prevProps )
            prevPropsTypeDisplay=prevProps.typeDisplay;
	    // we propose the bestDisplay only if the Game change
  		let game = this.eventCtrl.getCurrentGame();

	    // console.log("EventGameTruthOrLie.componentDidUpdate prevPropsTypeDisplay=("+prevPropsTypeDisplay+"), game.id=("+game.id+" / "+this.state.show.currentGameId+") typeDisplay=("+prevPropsTypeDisplay+" / "+this.state.show.typeDisplay+")");
	    console.log("EventGameTruthOrLie.componentDidUpdate game.id=("+game.id+" / "+this.state.show.currentGameId+")");
        let updateState=false;
        let stateShow = this.state.show;
        let inProgress = this.state.inProgress;

        // each time the user clicks on a tab to access a different game, this propertie change
	    if (this.state.show.currentGameId !== game.id) {
	        updateState=true;
	        stateShow.currentGameId = game.id;
            stateShow.currentVote=0;
            // This is time to synchronize the event with the description
            // we expect the number of sentences
            let myTruthOrLie = this.getMyTruthOrLie();
		    // console.log("EventGameTruthOrLie.componentDidUpdate Update prevProps=("+prevPropsTypeDisplay+") MySentences"+myTruthOrLie.sentencesList.length+" Expected "+game.nbSentences);
            if (game.nbSentences !== myTruthOrLie.sentencesList.length) {
                inProgress=true;
                this.synchronizeMySentences();
            }
        }
        // the display ADMIN will come after in a second call
        if (this.state.show.typeDisplay !== prevPropsTypeDisplay) {
        	updateState=true;
   	        stateShow.typeDisplay= prevPropsTypeDisplay;

        }

        if (updateState) {
            this.setState( {show : stateShow, inProgress: inProgress});
        }
	}



  // --------------------------------------------------------------
    //
    // render
    //
    // --------------------------------------------------------------

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {

		// refresh the current Game embedded
		// this.GameEmbedded = new Game( this.state.event, currentGame, this.userParticipant, this.updateEventFct);
		// console.log("EventGameTruthOrLie.render: tabName=["+this.state.show.tabName+"] gameId=["+game.id+"] !");
		let styleGameSentence={ borderBottom: "2px solid black", padding:"10px 10px 10px 10px"};
		let styleGameVote={ borderBottom: "2px solid black", padding:"10px 10px 10px 10px"};
		let styleGameResult={ borderBottom: "2px solid black", padding:"10px 10px 10px 10px"};
        let styleGameParameter={ borderBottom: "2px solid black", padding:"10px 10px 10px 10px"};
        let contentTab='';
        if (this.state.show.tabName === 'SENTENCES') {
            styleGameSentence={ borderTop: "2px solid black", borderLeft: "2px solid black", borderRight: "2px solid black", padding:"10px 10px 10px 10px"};
            contentTab = this.renderSentences();
        }
        if (this.state.show.tabName === 'VOTE') {
            styleGameVote={ borderTop: "2px solid black", borderLeft: "2px solid black", borderRight: "2px solid black", padding:"10px 10px 10px 10px"};
            contentTab=this.renderVote();
        }
        if (this.state.show.tabName === 'RESULTS') {
            styleGameResult={ borderTop: "2px solid black", borderLeft: "2px solid black", borderRight: "2px solid black", padding:"10px 10px 10px 10px"};
            contentTab=this.renderResults();
        }
        if (this.state.show.tabName === 'PARAMETERS') {
            styleGameParameter={ borderTop: "2px solid black", borderLeft: "2px solid black", borderRight: "2px solid black", padding:"10px 10px 10px 10px"};
            contentTab=this.renderParameters();
        }
        return (
            <div>
				<div class="row">
                    <nav class="navbar navbar-expand-lg navbar-light bg-light">
				        <div class="collapse navbar-collapse" id="navbarNavDropdown">
                        <ul class="navbar-nav">
                            <li class="nav-item active"
					            style={styleGameSentence}
					            key="sentences"
						        onClick={ (event) =>{
                                    console.log("EventGameList.click on "+event.target.id);
                                    this.setShowStateProperty( 'tabName', 'SENTENCES' );
								}} >
								<FormattedMessage id="EventGameTruthOrLie.sentences" defaultMessage="My Sentences" />
					        </li>
					        <li class="nav-item active"
                                style={styleGameVote}
                                key="vote"
                                onClick={ (event) =>{
                                    console.log("EventGameList.click on "+event.target.id);
                                    this.setShowStateProperty( 'tabName', 'VOTE' );
                                }} >
                                <FormattedMessage id="EventGameTruthOrLie.vote" defaultMessage="Vote" />
                            </li>
                            <li class="nav-item active"
                                style={styleGameResult}
                                key="results"
                                onClick={ (event) =>{
                                    console.log("EventGameTruthOrLie.click on "+event.target.id);
                                    this.setShowStateProperty( 'tabName', 'RESULTS' );
                                }} >
                                <FormattedMessage id="EventGameTruthOrLie.result" defaultMessage="Results" />
                            </li>
                             {(this.state.show.typeDisplay === gameConstant.DISPLAY_ADMIN) &&
                                <li class="nav-item active"
                                    style={styleGameParameter}
                                    key="parameters"
                                    onClick={ (event) =>{
                                        this.setShowStateProperty( 'tabName', 'PARAMETERS' );
                                    }} >
                                    <FormattedMessage id="EventGameTruthOrLie.parametres" defaultMessage="Parameters" />
                                </li>
                             }
					    </ul>
					    </div>
					</nav>
				</div>
				<div class="row">
					  {contentTab}
				</div>
            </div>
        )

    }

    // renderSentences
    renderSentences() {
   		let game = this.eventCtrl.getCurrentGame();
        // retrieve the sentences for the player
        let myTruthOrLie=this.getMyTruthOrLie();

        let countLie=0;
        let countTruth=0;
        let countSentences=0;

        // the number of sentences pilote the display
        for (let i=0;i<game.nbSentences;i++) {
            let currentSentence = {};
            if (i<myTruthOrLie.sentencesList.length) {
                currentSentence = myTruthOrLie.sentencesList[ i ];
            }
            // if the sentence does not exist, then this range is used to place the new sentence ah the correct place
            if (currentSentence.statusSentence===CST_LIE)
                countLie++;
            if (currentSentence.statusSentence===CST_TRUTH)
                countTruth++;
            if (currentSentence.sentence && currentSentence.sentence.length>0)
                countSentences++;
        }
        return (
            <div>
                <div class="toghBlock" style={{backgroundColor: "#fed9a691",margin: "10px 10px 10px 10px", padding: "20px 20px 20px 20px"}}>
                    <div class="row">
                        <FormattedMessage id="EventGameTruthOrLie.Explanation" defaultMessage="Give multiple sentences, and the status for each (this is a LIE, or this is a TRUTH)." />
                    </div>

                    {(game.nbTruthsRequested > 0) &&
                        <div class="row">
                             <FormattedMessage id="EventGameTruthOrLie.NumberOfTruthsUpperZero" defaultMessage="There is a number of Truths requested:" />
                            &nbsp;
                            {game.nbTruthsRequested}
                        </div>
                    }
                    {(game.nbTruthsRequested === 0) &&
                        <div class="row"> <FormattedMessage id="EventGameTruthOrLie.NoNumberOfTruthsRequested" defaultMessage="There is no requested number of truth: you decide" /></div>
                    }


                    <div class="row">
                        { (countSentences < game.nbSentences) &&
                            <div style={{paddingTop: "10px"}}>
                                <UserMessage message={<FormattedMessage id="EventGameTruthOrLie.FulfillSentences" defaultMessage="You have to give a sentence on each line." />} status="FAIL" />
                            </div>
                        }
                        { ( countLie + countTruth < game.nbSentences) &&
                            <div style={{paddingTop: "10px"}}>
                                <UserMessage message={<FormattedMessage id="EventGameTruthOrLie.GiveAStatus" defaultMessage="You have to give a statue on each sentence." />} status="FAIL" />
                            </div>
                        }
                        { (game.nbTruthsRequested > 0 && countTruth !== game.nbTruthsRequested) &&
                            <div style={{paddingTop: "10px"}}>
                                <UserMessage message={
                                    <div>
                                        <FormattedMessage id="EventGameTruthOrLie.GiveTheCorrectNumberOfTruths" defaultMessage="Give the expected number of truth." />
                                        ({game.nbTruthsRequested})
                                    </div>} status="FAIL" />

                            </div>
                        }
                    </div>
                </div>


                 {(game.nbTruthsRequested > 0) &&
                    <div class="row" style={{padding: "10px 10px 40px 20px", fontSize:"20px"}}>
                         <FormattedMessage id="EventGameTruthOrLie.NumberOfTruths" defaultMessage="Number of truths:" />
                        &nbsp;
                        {game.nbTruthsRequested}
                    </div>
                 }


                {myTruthOrLie.sentencesList.map( (currentSentence, index) => {
                    return (
                    <div class="row"  style={{borderBottom: "aliceblue", borderStyle: "solid", marginBottom: "10px", paddingBottom: "10px"}}>
                        <div class="col-8">
                            <TextArea value={currentSentence.sentence}
                                onChange={(event) => this.setSentence("sentence", event.target.value, game, myTruthOrLie, currentSentence)}
                                disabled={myTruthOrLie.validate}
                                labelText={<FormattedMessage id="EventGameTruthOrLie.Sentences" defaultMessage="Sentence" />}>
                            </TextArea>
                        </div>
                        <div class="col-4" >
                            <div class="btn-group btn-group-sm radio toggle button group Basic" role="groupstate button" >
                                <input type="radio"
                                    class="btn-check"
                                    name={"btntruthorlieradio_"+currentSentence.id}
                                    id={"btntruthorlie_1_"+currentSentence.id}
                                    autoComplete="off"
                                    checked={currentSentence.statusSentence === CST_LIE}
                                    disabled={myTruthOrLie.validate}
                                    onChange={() => this.setSentence("statusSentence",CST_LIE, game, myTruthOrLie, currentSentence)}
                                    />
                                <label class="btn btn-outline-primary" for={"btntruthorlie_1_"+currentSentence.id}>
                                    <HandThumbsDownFill />&nbsp;<FormattedMessage id="EventGameTruthOrLie.Lie" defaultMessage="Lie" />
                                </label>

                                <input type="radio"
                                    class="btn-check"
                                    name={"btntruthorlieradio_"+currentSentence.id}
                                    id={"btntruthorlie_2_"+currentSentence.id}
                                    checked={currentSentence.statusSentence === CST_TRUTH}
                                    disabled={myTruthOrLie.validate}
                                    onChange={() => this.setSentence("statusSentence",CST_TRUTH, game, myTruthOrLie, currentSentence)}
                                    />
                                <label class="btn btn-outline-primary" for={"btntruthorlie_2_"+currentSentence.id}>
                                    <HandThumbsUpFill />&nbsp;<FormattedMessage id="EventGameTruthOrLie.Truth" defaultMessage="Truth" />
                                </label>
                            </div>
                        </div>
                    </div>)
                    })
                }

                <div class="row"  style={{ marginTop: "10px"}}>
                    {! myTruthOrLie.validate &&
                    <div>
                        <FormattedMessage id="EventGameTruthOrLie.ValidateExplanation" defaultMessage="When your list is ready, validate it. Attention, it will not be possible to modify it after: players will start to vote." />
                        <br/>
                        <button class="btn btn-success btn-xs" style={{marginTop: "10px"}}
                            onClick={()=>this.seValidateMySentences("validateSentences", "true", game, myTruthOrLie ) }
                            disabled={(game.nbTruthsRequested > 0 && countTruth !== game.nbTruthsRequested)
                                    || countLie + countTruth < game.nbSentences
                                    || countSentences < game.nbSentences}
                            >
                            <FormattedMessage id="EventGameTruthOrLie.Validate" defaultMessage="Validate"/>
                        </button>
                    </div>
                    }
                    {myTruthOrLie.validate &&
                          <FormattedMessage id="EventGameTruthOrLie.SentenceValidate" defaultMessage="Your list is ready. Wait for the other players to vote!" />
                    }
                </div>

            </div>

            )

    }

    // renderVote
    renderVote() {
   		let game = this.eventCtrl.getCurrentGame();
        let myTruthOrLie=this.getMyTruthOrLie();

        // show the vote this.state.show.currentVote
        let index= this.state.show.currentVote;
        if (index<0)
            index=0;
        if (index>=myTruthOrLie.voteList.length)
            index=myTruthOrLie.voteList.length-1;

        let countTotalVote= myTruthOrLie.voteList.length;
        if (countTotalVote === 0) {
            return (<FormattedMessage id="EventGameTruthOrLie.NoVoteReady" defaultMessage="No vote at this moment" />);
        }

        let voteToDisplay= myTruthOrLie.voteList[ index ];

        let countTruth=0;
        if (voteToDisplay.voteSentenceList) {
            for (let i=0;i<voteToDisplay.voteSentenceList.length;i++) {
                if (voteToDisplay.voteSentenceList[i].statusVote===CST_TRUTH)
                    countTruth++;
            }
        }
         return (
            <div>
                <div class="row">
                    <h1>
                        <button class="btn btn-primary btn-xs"
                            style={{marginRight: "30px"}}
                            disabled={this.state.show.currentVote===0 }
                            onClick= {() => this.setShowStateProperty('currentVote', this.state.show.currentVote-1) }
						    title={<FormattedMessage id="EventItineray.UpThisLine" defaultMessage="Up this line" />}>
                             <ArrowLeft width="20px"/>
                        </button>

                        <FormattedMessage id="EventGameTruthOrLie.VotePlayer" defaultMessage="Player" />
                        &nbsp;
                        {voteToDisplay.playerName}
                        &nbsp;
                        { this.state.show.currentVote+1} / {countTotalVote}

                        <button class="btn btn-primary btn-xs"
                            style={{marginLeft: "30px"}}
                            disabled={this.state.show.currentVote>=countTotalVote-1}
                            onClick= {() => this.setShowStateProperty('currentVote', this.state.show.currentVote+1) }
						    title={<FormattedMessage id="EventItineray.UpThisLine" defaultMessage="Up this line" />}>
                            <ArrowRight width="20px"/>
                        </button>
                    </h1>

                </div>
                <div class="row">
                    <FormattedMessage id="EventGameTruthOrLie.VoteFindTheTruths" defaultMessage="In theses sentences, the number of truth sentences is:" />
                    &nbsp;
                    {game.nbTruthsRequested}
                </div>

                 {voteToDisplay.voteSentenceList.map( (currentSentence, index) => {
                        return (
                        <div class="row"  style={{borderBottom: "aliceblue", borderStyle: "solid", marginBottom: "10px", paddingBottom: "10px"}}>
                            <div class="col-8">
                                <TextArea value={currentSentence.sentence}
                                            disabled={true}
                                            labelText={<FormattedMessage id="EventGameTruthOrLie.Sentences" defaultMessage="Sentence" />}>
                                </TextArea>
                            </div>
                            <div class="col-4" >
                                <div class="btn-group btn-group-sm radio toggle button group Basic" role="groupvote" >
                                    <input type="radio"
                                        class="btn-check"
                                        name={"btnvoteradio_"+currentSentence.id}
                                        id={"btnvote_1_"+currentSentence.id}
                                        autoComplete="off"
                                        checked={currentSentence.statusVote === CST_LIE}
                                        disabled={voteToDisplay.validate}
                                        onChange={() => this.setVote("statusVote",CST_LIE, game, myTruthOrLie, voteToDisplay, currentSentence)}
                                        />
                                    <label class="btn btn-outline-primary" for={"btnvote_1_"+currentSentence.id}>
                                        <HandThumbsDownFill />&nbsp;<FormattedMessage id="EventGameTruthOrLie.ThisIsALie" defaultMessage="It' a Lie" />
                                    </label>

                                    <input type="radio"
                                        class="btn-check"
                                        name={"btnvoteradio_"+currentSentence.id}
                                        id={"btnvote_2_"+currentSentence.id}
                                        autoComplete="off"
                                        checked={currentSentence.statusVote ===CST_TRUTH}
                                        disabled={voteToDisplay.validate}
                                        onChange={() => this.setVote("statusVote",CST_TRUTH, game, myTruthOrLie, voteToDisplay, currentSentence)}
                                        />
                                    <label class="btn btn-outline-primary" for={"btnvote_2_"+currentSentence.id}>
                                        <HandThumbsUpFill />&nbsp;<FormattedMessage id="EventGameTruthOrLie.ThisIsATruth" defaultMessage="It's the truth" />
                                    </label>
                                </div>
                            </div>
                        </div>)
                        })
                    }

                    <div class="row"  style={{ marginTop: "10px"}}>
                        {! voteToDisplay.validate &&
                        <div>
                            <FormattedMessage id="EventGameTruthOrLie.voteValidate" defaultMessage="When your vote is ready, validate it. Attention, it will not be possible to modify it after." />
                            <br/>
                            <button class="btn btn-success btn-xs" style={{marginTop: "10px"}}
                                onClick={()=>this.validateMyVote(game, myTruthOrLie, voteToDisplay ) }
                                disabled={(game.nbTruthsRequested > 0 && countTruth !== game.nbTruthsRequested) || this.state.inProgress}
                                >
                                <FormattedMessage id="EventGameTruthOrLie.VoteValidate" defaultMessage="Vote"/>
                            </button>
                            {(this.state.inProgressVote) &&
                                <span style={{paddingLeft: "10px"}}>
                                  <FormattedMessage id="EventGameTruthOrLie.VoteInProgress" defaultMessage="Vote in progress"/>
                                </span>
                            }
                            {(game.nbTruthsRequested > 0 && countTruth !== game.nbTruthsRequested) &&
                                <span style={{paddingLeft: "10px"}}>
                                  <FormattedMessage id="EventGameTruthOrLie.VoteCantBeValidated" defaultMessage="Attention, the number of true sentences selected is not the expected one"/>
                                  &nbsp;
                                  <FormattedMessage id="EventGameTruthOrLie.ExpectedTruth" defaultMessage="Expected:"/>
                                  &nbsp;
                                  <span style={{color: "red", fontWeight: "bold"}}>{game.nbTruthsRequested}</span>
                                  &nbsp;
                                  <FormattedMessage id="EventGameTruthOrLie.SelectedTruth" defaultMessage="Selected:"/>
                                  &nbsp;
                                  <span style={{color: "red", fontWeight: "bold"}}>{countTruth}</span>
                                </span>
                            }
                        </div>
                        }
                    </div>
                    <div class="row"  style={{ marginTop: "10px"}}>
                        {this.state.isErrorVote &&
                              <FormattedMessage id="EventGameTruthOrLie.voteInError" defaultMessage="An error arrived during your vote - try again later." />
                        }
                        {voteToDisplay.validate &&
                              <FormattedMessage id="EventGameTruthOrLie.voteValidated" defaultMessage="Your vote is accepted." />
                        }
                    </div>
            </div>);

    }

     // renderResult
    renderResults() {
   		let game = this.eventCtrl.getCurrentGame();

        let resultToDisplay= game.result;

        return (
            <div>
                <div class="row">
                    <h1><FormattedMessage id="EventGameTruthOrLie.Result" defaultMessage="Result" /></h1>
                </div>
                {resultToDisplay.players.map( (currentPlayer, index) => {
                    return (
                       <div>
                            <div class="row" >
                                <div class="col-6">
                                    <h6>
                                     {currentPlayer.range &&
                                        <span class="badge rounded-pill bg-primary" style={{fontSize: "8px"}}>{currentPlayer.range}</span>
                                     }
                                    {currentPlayer.name}
                                    </h6>
                                </div>
                                <div class="col-2" style={{fontWeight: "bold"}}>
                                    {currentPlayer.points}
                                    &nbsp;
                                    <FormattedMessage id="EventGameTruthOrLie.points" defaultMessage="points" />
                                </div>
                                <div class="col-3" >
                                    {! currentPlayer.validatesentences &&
                                    <span class="badge bg-danger">
                                        <FormattedMessage id="EventGameTruthOrLie.sentenceNotValidate" defaultMessage="Sentences not validated" />
                                    </span>
                                    }
                                    {currentPlayer.validatesentences &&
                                        <div>
                                            {currentPlayer.numberofvotes===currentPlayer.totalvotes &&
                                            <span class="badge bg-success">
                                                <FormattedMessage id="EventGameTruthOrLie.voteComplete" defaultMessage="Vote completed" />
                                            </span>
                                            }
                                            {currentPlayer.numberofvotes!==currentPlayer.totalvotes &&
                                            <span class="badge bg-primary">
                                                <FormattedMessage id="EventGameTruthOrLie.voteInProgress" defaultMessage="Vote in progress" />
                                                &nbsp;
                                                ({currentPlayer.numberofvotes} /{currentPlayer.totalvotes})
                                            </span>
                                            }

                                        </div>
                                        }
                                </div>
                            </div>
                            {currentPlayer.vote.map( (currentVote, index) => {
                                return (
                                    <div class="row" >

                                        <div class="col-6" style={{paddingLeft:"30px"}}>
                                            <div class="toghBlock" style={{backgroundColor: "#fed9a691",padding:"10px"}}>
                                                {currentVote.sentence}<br/>
                                                <div style={{fontSize: "10px", fontStyle: "italic", textAlign: "left", marginTop: "10px"}}>
                                                    <FormattedMessage id="EventGameTruthOrLie.By" defaultMessage="By" />
                                                    &nbsp;
                                                    {currentVote.sourceplayer}
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-2">
                                            {currentVote.statussentence===CST_LIE &&
                                                <FormattedMessage id="EventGameTruthOrLie.sentenceIsLie" defaultMessage="is it a lie" />
                                            }
                                            {currentVote.statussentence===CST_TRUTH &&
                                                <FormattedMessage id="EventGameTruthOrLie.sentenceIsTruth" defaultMessage="is it a truth" />
                                            }
                                        </div>
                                        <div class="col-2">
                                            {currentVote.statussentence===currentVote.statusvote &&
                                                <span class="badge bg-success">
                                                    <FormattedMessage id="EventGameTruthOrLie.voteSuccess" defaultMessage="You find it" />
                                                    &nbsp; : &nbsp;
                                                    {currentVote.pointsforthesentence}
                                                    &nbsp;
                                                    <FormattedMessage id="EventGameTruthOrLie.points" defaultMessage="points" />
                                                </span>
                                            }
                                            {currentVote.statussentence!==currentVote.statusvote &&
                                                <span class="badge bg-danger">
                                                    <FormattedMessage id="EventGameTruthOrLie.voteError" defaultMessage="You were wrong" />
                                                </span>
                                            }

                                        </div>
                                    </div>)
                            })}
                       </div>)
                    })
                }
            </div>
        )
    }


    // Parameters
    renderParameters() {
   		let game = this.eventCtrl.getCurrentGame();
		const intl = this.props.intl;
        const colorsChart = [
                       'rgba(54, 162, 235, 0.2)',
                       'rgba(255, 99, 132, 0.2)'];

         return (<div>
                    <div class="row">
                        <h1><FormattedMessage id="EventGameTruthOrLie.Administration" defaultMessage="Manage the Truth Or Lie" /></h1>
                    </div>
                    <div class="row">
                        <div class="col-4">
                            <FormattedMessage id="EventGameTruthOrLie.Scope" defaultMessage="Players of the Truth Or Lie game" />
                        </div>
                        <div class="col-6">
                            <div class="btn-group btn-group-sm radio toggle button group Basic" role="groupstate" >
                                <input type="radio" class="btn-check" name="btnradiostate" id="filterState1" autoComplete="off"
                                    checked={game.scopeGame === "ALL"}
                                    onChange={() => this.setAttribut("scopeGame", "ALL", game)}
                                    disabled={this.state.inProgress}/>
                                <label class="btn btn-outline-primary" for="filterState1">
                                    <List />&nbsp;<FormattedMessage id="EventGameTruthOrLie.ScopeAll" defaultMessage="All participants" />
                                </label>

                                <input type="radio" class="btn-check" name="btnradiostate" id="filterState2" autocomplete="off"
                                    checked={game.scopeGame === "ACTIVE"}
                                    onChange={() => this.setAttribut("scopeGame", "ACTIVE",game)}
                                    disabled={this.state.inProgress}/>
                                <label class="btn btn-outline-primary" for="filterState2">
                                    <List />&nbsp;<FormattedMessage id="EventGameTruthOrLie.ScopeActive" defaultMessage="Active participants only" />
                                </label>
                            </div>
                        </div>
                    </div>
                     {game.numberOfPlayersWhoSentences>0 &&
                        <div class="row">
                            <div class="col-6 toghBlock"
                                style={{backgroundColor: "#fed9a691",margin: "10px 10px 10px 10px", padding: "20px 20px 20px 20px"}}>
                                <FormattedMessage id="EventGameTruthOrLie.PlayersValidatedSentences" defaultMessage="Some players validated their sentences. These settings can no longer be changed." />
                            </div>
                        </div>
                    }
                    <div class="row">
                        <div class="col-6">
                            <TextInput value={game.nbSentences}
                                onChange={(event) => this.setAttribut("nbSentences", event.target.value, game)}
                                disabled={game.numberOfPlayersWhoSentences>0}
                                labelText={<FormattedMessage id="EventGameTruthOrLie.NbSentences" defaultMessage="Number Of Sentences" />}>
                            </TextInput>
                         </div>
                         <div class="col-6" style={{paddingTop : "15px"}}>
                            <FormattedMessage id="EventGameTruthOrLie.NumberSentencesExplanation" defaultMessage="Give the number of sentences each players has to give" />
                         </div>
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <TextInput value={game.nbTruthsRequested}
                                    onChange={(event) => this.setAttribut("nbTruthsRequested", event.target.value, game)}
                                    disabled={game.numberOfPlayersWhoSentences>0}
                                    labelText={<FormattedMessage id="EventGameTruthOrLie.NumberOfTruthsRequested" defaultMessage="Number Of Truths sentences requested" />}>
                            </TextInput>
                        </div>

                        <div class="col-6" style={{paddingTop : "15px"}}>
                            <FormattedMessage id="EventGameTruthOrLie.NumberOfTruthsRequestedExplanation" defaultMessage="Give the number of true sentences each player has to give. If '0', then each player can decide." />
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <Select labelText={<FormattedMessage id="EventGameTruthOrLie.OpeningOfTheVote" defaultMessage="Opening of the vote" />}
                                    id="openingofthevote"
                                    value={game.discoverResult}
                                    onChange={(event) => this.setAttribut("openingOfTheVote", event.target.value,game)}>

                                    <FormattedMessage id="EventGameTruthOrLie.StartVoteImmediat" defaultMessage="Immediat">
                                        {(message) => <option value="IMMEDIAT">{message}</option>}
                                    </FormattedMessage>

                                    <FormattedMessage id="EventGameTruthOrLie.StartVoteOneDayBeforeEvent" defaultMessage="One day before event">
                                        {(message) => <option value="BEFOREEVENT">{message}</option>}
                                    </FormattedMessage>

                                    <FormattedMessage id="EventGameTruthOrLie.StartVoteEvent" defaultMessage="Start event">
                                        {(message) => <option value="STARTEVENT">{message}</option>}
                                    </FormattedMessage>
                            </Select>
                         </div>
                         <div class="col-6" style={{paddingTop : "15px"}}>
                            <FormattedMessage id="EventGameTruthOrLie.StartVoteExplanation" defaultMessage="With the Immediate mode, the vote is open when a player validates its sentence. Else, one day before the event starts, or when the event starts" />
                         </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <Select labelText={<FormattedMessage id="EventGameTruthOrLie.DiscoverResult" defaultMessage="Discover the result" />}
                                    id="discovertresult"
                                    value={game.discoverResult}
                                    onChange={(event) => this.setAttribut("discoverResult", event.target.value,game)}>

                                    <FormattedMessage id="EventGameTruthOrLie.DiscoverImmediat" defaultMessage="Immediat">
                                        {(message) => <option value="IMMEDIAT">{message}</option>}
                                    </FormattedMessage>

                                    <FormattedMessage id="EventGameTruthOrLie.DiscoverOnStartEvent" defaultMessage="Start event">
                                        {(message) => <option value="STARTEVENT">{message}</option>}
                                    </FormattedMessage>

                                    <FormattedMessage id="EventGameTruthOrLie.DiscoverOnEndEvent" defaultMessage="End event">
                                        {(message) => <option value="ENDEVENT">{message}</option>}
                                    </FormattedMessage>
                            </Select>
                         </div>
                         <div class="col-6" style={{paddingTop : "15px"}}>
                            <FormattedMessage id="EventGameTruthOrLie.DiscoverResultExplanation" defaultMessage="With the Immediate mode, when a player vote, result are immediately displayed. Else, result is visible only at one moment" />
                         </div>
                    </div>

                    <div class="row" style={{padding: "8px 0px 10px"}}>
                        <div class="col-2">
                            <FormattedMessage id="EventGameTruthOrLie.NumberOfPlayerInScope" defaultMessage="Potential players" />
                        </div>
                        <div class="col-2">
                            {game.numberOfParticipantsInTheScope}
                   			{this.state.inProgress && (<div style={{display: "inline-block"}}><InlineLoading description="recalculate" status='active'/></div>) }                        </div>
                    </div>


                    <div class="row" style={{padding: "8px 0px 10px"}}>
                        <div class="col-2">
                            <ChartTogh type="Doughnut"
                                dataMap={game.numberOfPlayerWhoSentencesData}
                                backgroundColor={colorsChart}
                                borderColor={colorsChart}
                                title={intl.formatMessage({id: "EventGameTruthOrLie.PlayerWhoSentences",defaultMessage: "Validate Sentences"}) } />
                            <div style={{textAlign: "center"}}>
                                {game.numberOfPlayersWhoSentences} ({game.numberOfPlayersWhoSentencesPercent} %)
                            </div>
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut"
                                dataMap={game.numberOfPlayerWhoVotedData}
                                backgroundColor={colorsChart}
                                borderColor={colorsChart}
                                title={intl.formatMessage({id: "EventGameTruthOrLie.PlayerWhoVoted",defaultMessage: "Votes"}) } />
                            <div style={{textAlign: "center"}}>
                                {game.numberOfPlayersWhoVoted} ({game.numberOfPlayersWhoVotedPercent} %)
                            </div>
                        </div>
                    </div>



               </div>
                )
    }


    /**
    * get my sentences in the list of all players
    */
    getMyTruthOrLie() {
   	    let game = this.eventCtrl.getCurrentGame();
        let mySelfUser = this.eventCtrl.getMyself();
        for (let i in game.truthOrLieList) {
            if (game.truthOrLieList[i].participantId === mySelfUser.id) {
                return game.truthOrLieList[i];
            }
        }
        let myTruthOrLie={ sentencesList: []};
        return myTruthOrLie;
    }

    /**
    * sentence list is not correct: we miss a sentence. So, let's synchronize that now
    */
    synchronizeMySentences() {
		const restCallService = FactoryService.getInstance().getRestCallService();
   		let game = this.eventCtrl.getCurrentGame();
        let mySelfUser = this.eventCtrl.getMyself();

		let param={'eventId': this.eventCtrl.event.id, 'gameId':game.id, 'playerId': mySelfUser.id};

		restCallService.postJson('/api/event/game/synchronizetruthorlie', this, param, httpPayload => {
			// update the sentences list

			if (httpPayload.getData() && httpPayload.getData().childEntities) {
			    // we update all the truthOrLie, for all players (not important to synchronize the another players)
			    let mySelfUser = this.eventCtrl.getMyself();
                for (let iteratorTruthOrLie in game.truthOrLieList) {
                    if (iteratorTruthOrLie.participantId === mySelfUser.id) {
                        game[ iteratorTruthOrLie ]= httpPayload.getData().childEntities[0];
                    }
                }
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
    setShowStateProperty( name, value ) {
        let showState=this.state.show;
        showState[ name ] = value;
        this.setState({show:showState } );
    }
    setAttribut(name, value, item) {
        console.log("EventGameTruthOrLie.setAttribut: set attribut:" + name + " <= " + value + " item=" + JSON.stringify(item));
        this.eventCtrl.registerUpdateCallback(this);
        this.setState({inProgress:true});
        this.eventCtrl.setAttribut(name, value, item, gameListConstant.NAMEENTITY+"/"+item.id);
    }

    // chang attribut, but the result impact the list of sentence to display
    setAttributChangeSentences(name, value, item) {

    }
    setAttributCheckbox(name, value, item) {
        console.log("EventGameTruthOrLie.setAttributCheckbox set " + name + "<=" + value.target.checked);
        let valueBoolean;
        if (value.target.checked)
            valueBoolean = true;
        else
            valueBoolean = false;
        this.setAttribut(name, valueBoolean, item );
    }

    // the call back. Attention, because we registered to any callback, this call maybe not for this object
    callbackUpdate( dataPayload ) {
        // console.log("EventGameTruthOrLie.callbackUpdate set " + dataPayload );
   		let game = this.eventCtrl.getCurrentGame();
        if (dataPayload.childEntities.length>0 && dataPayload.childEntities[0].id === game.id) {
            let gameOnServer = dataPayload.childEntities[0];
            game.numberOfParticipantsInTheScope     = gameOnServer.numberOfParticipantsInTheScope;
            game.numberOfPlayersWhoSentences        = gameOnServer.numberOfPlayersWhoSentences;
            game.numberOfPlayersWhoSentencesPercent = gameOnServer.numberOfPlayersWhoSentencesPercent;
            game.numberOfPlayerWhoSentencesData     = gameOnServer.numberOfPlayerWhoSentencesData;
            game.numberOfPlayers                    = gameOnServer.numberOfPlayers;
            game.nbSentences                        = gameOnServer.nbSentences;
            game.truthOrLieList                     = gameOnServer.truthOrLieList;

            // update the list of sentences: it may change if the number of sentences change
            // force the component to render again
            this.setState({'numberOfParticipantsInTheScope':game.numberOfParticipantsInTheScope,
                            'nbSentences':gameOnServer.nbSentences});
        }
        this.setState({inProgress:false});
    }

    // user give a sentence, or a status (truth, lie)
    setSentence( name, value, game, myTruthOrLie, currentSentence) {
        // console.log("EventGameTruthOrLie.setSentence value="+value);
        let path=gameListConstant.NAMEENTITY+"/"+game.id+"/truthOrLieList/"+myTruthOrLie.id+"/sentencesList/"+currentSentence.id;
        this.eventCtrl.setAttribut(name, value, currentSentence, path);
        // console.log("EventGameTruthOrLie setSentence: game="+ JSON.stringify(game));
    }

    // -- user clicks on 'validate my sentences
    seValidateMySentences( name, value, game, myTruthOrLie) {
        // console.log("EventGameTruthOrLie.setMyTruthOrLie value="+value);
        let path=gameListConstant.NAMEENTITY+"/"+game.id+"/truthOrLieList/"+myTruthOrLie.id;
        this.eventCtrl.setAttribut(name, value, myTruthOrLie, path);
        // console.log("EventGameTruthOrLie setSentence: game="+ JSON.stringify(game));
    }



    setVote( name, value, game, myTruthOrLie, voteToDisplay, currentSentence) {
        // console.log("EventGameTruthorLie.setVote value="+value);
        let path=gameListConstant.NAMEENTITY+"/"+game.id+"/truthOrLieList/"+myTruthOrLie.id+"/voteList/"+voteToDisplay.id+"/voteSentenceList/"+currentSentence.id;
        this.eventCtrl.setAttribut(name, value, currentSentence, path);
        // console.log("EventGameTruthOrLie setVote: game="+ JSON.stringify(game));
    }

    validateMyVote( game, myTruthOrLie, voteToDisplay) {
        this.setState({isErrorVote:false,inProgress:true, inProgressVote:true});
       // first, save if needed
        let basket = this.eventCtrl.getCurrentBasketSlabRecord();
        let jsonBasket = basket.getBasketJson();
		const restCallService = FactoryService.getInstance().getRestCallService();

        let param = {
            'eventId': this.eventCtrl.event.id,
            'gameId':game.id,
            "trueOrLieId":myTruthOrLie.id,
            "voteId":voteToDisplay.id,
            'listslab' : jsonBasket.listslab
        }
	    restCallService.postJson('/api/event/game/votetruthorlie?', this, param, httpPayload => {
			// update the sentences list
   		    let game = this.eventCtrl.getCurrentGame();

            if (httpPayload.isError()) {
                this.setState({isErrorVote:true});
            }
			if (httpPayload.getData() && httpPayload.getData()) {
			    // we update all the truthOrLie, for all players (not important to synchronize the another players)
			    let gameOnServer    = httpPayload.getData().childEntities[0];
                game.truthOrLieList = gameOnServer.truthOrLieList;
            }
			this.setState({inProgress:false, inProgressVote:false});
		}
		);




    }

}

export default injectIntl(EventGameTruthOrLie);