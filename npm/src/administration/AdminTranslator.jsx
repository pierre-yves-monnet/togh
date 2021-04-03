// -----------------------------------------------------------
//
// AdminTranslator
//
// Display one event
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";
import { Loading } from 'carbon-components-react';

import { ChevronDown, ChevronRight } from 'react-bootstrap-icons';

import FactoryService from '../service/FactoryService';

import LogEvents from '../component/LogEvents';



class AdminTranslator extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {
			inprogress : false,
			message: '',
			show:'ON',
			translate : {}
		};
		// show : OFF, ON, COLLAPSE
		console.log("secShoppinglist.constructor show=" + +this.state.show + " event=" + JSON.stringify(this.state.event));
		this.collapse = this.collapse.bind(this);
		this.checkDictionary = this.checkDictionary.bind(this);
		this.completeDictionary = this.completeDictionary.bind(this);
		
		
	}
	
	componentDidMount() {
		this.checkDictionary();
	} 

	// <input value={item.who} onChange={(event) => this.setChildAttribut( "who", event.target.value, item )} class="toghinput"></input>
	render() {
		console.log("AdminTranslator.render: visible=" + this.state.show+ " listevent="+this.state.translate.listEvents);
		if (this.state.translate) {				
			console.log("AdminTranslater.render: translate="+JSON.stringify(this.state.translate) );
		}
		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="Active loading indicator" withOverlay={true}
    						/>);
		
		var listDictionaryHtml = (<div></div>);
		if (this.state.translate && this.state.translate.listLanguages ) 
			listDictionaryHtml = this.state.translate.listLanguages.map((language,index) =>
				<tr key={index}>
					<td><img src={this.getIcon( language ) }  style={{width: "30px"}}/></td>
					<td>{language.name}</td>
					<td>{language.nbMissingSentences}</td>
        			<td>{language.nbTranslatedSentences}</td>
        			<td>{language.nbTooMuchSentences}</td>
				</tr>);
		
		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminTransaltor.Title" defaultMessage="Translation" />
				</div>
				<div class="card-body">
					 {inprogresshtml}
					<div class="eventsection">
						<a onClick={this.collapse} style={{ verticalAlign: "top" }}>
							{this.state.show === 'ON' && <ChevronDown width="20px"/>}
							{this.state.show === 'COLLAPSE' && <ChevronRight width="20px"/>}
						</a><FormattedMessage id="AdminTranslator.Title" defaultMessage="Translation" />
					</div>
					{this.state.show === 'ON' && <div>
						<button class="btn btn-info btn-sm" onClick={this.checkDictionary}>
							<span class="glyphicon glyphicon-refresh"> </span>&nbsp;
							<FormattedMessage id="AdminTranslator.CheckDictionary" defaultMessage="Check Dictionary"/>
						</button>	
						<br/>
						{this.state.message}<br/> 
						
						
						
						<br/>
						<table class="table table-striped toghtable">
							<tr>
								<th></th>
								<th><FormattedMessage id="AdminTranslator.Name" defaultMessage="Name"/></th>
								<th><FormattedMessage id="AdminTranslator.MissingSentences" defaultMessage="Missing sentences"/></th>
								<th><FormattedMessage id="AdminTranslator.TranslateSentences" defaultMessage="Translated sentences"/></th>
								<th><FormattedMessage id="AdminTranslator.TooMuchSentences" defaultMessage="Overflow Sentence"/></th>
							</tr>
							{this.state.translate && this.state.translate.listLanguages && this.state.translate.listLanguages.map((language,index) =>
								<tr key={index}>
									<td><img src={this.getIcon( language ) }  style={{width: "40px", verticalAlign: "middle"}}/></td>
									<td>{language.name}</td>
									<td>{language.nbMissingSentences}</td>
				        			<td>{language.nbTranslatedSentences}</td>
				        			<td>{language.nbTooMuchSentences}</td>
								</tr>) }
								
						</table>
						<button class="btn btn-info btn-sm" onClick={this.completeDictionary}>
							<FormattedMessage id="AdminTranslator.CompleteDictionary" defaultMessage="Complete Dictionary"/>
						</button>
						
						<br/><br/>

						<LogEvents listEvents={this.state.translate.listEvents} />
						
						{this.state.translate && this.state.translate.chronometers && (<div>
							<FormattedMessage id="AdminTranslator.nbTransations" defaultMessage="Number of translation"/>&nbsp;:&nbsp;
							{this.state.translate.chronometers.translate.nbexecutions}
							&nbsp;,&nbsp;<FormattedMessage id="AdminTranslator.TimeTranslation" defaultMessage="Time of translation"/>&nbsp;:&nbsp;
							{this.state.translate.chronometers.translate.timeinms}
							&nbsp;,&nbsp;<FormattedMessage id="AdminTranslator.AverageTranslation" defaultMessage="Average (in Milliseconds)"/>&nbsp;:&nbsp;
							{this.state.translate.chronometers.translate.average}
							</div>
						)}
					</div>
					}
				</div>
			</div>
			);
	}
	
	// {this.state.translate.listEvents && <LogEvents listEvents={this.state.translate.listEvents} />}
	
	collapse() {
		console.log("EventShoppinglist.collapse");
		if (this.state.show === 'ON')
			this.setState({ 'show': 'COLLAPSE' });
		else
			this.setState({ 'show': 'ON' });
	}

	/**
	 * getIcon
     *  return the URL to access the flag for a language
	 */
	getIcon( language ) {
		return "img/flags/"+ language.name+".svg";
	}
	
	
	
	checkDictionary() {
		console.log("AdminTranslator.completeDictionary:");
		this.setState({inprogress: true });
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.getJson('/api/admin/translator/status?', this, httpPayload =>{
			httpPayload.trace("AdminTranslator.checkDictionary");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ "message": "Server connection error" });
			}
			else {
				this.setState({ message: httpPayload.getData().message,
								translate: httpPayload.getData() });
			}
		});
	}
	
	
	
	completeDictionary() {
		console.log("AdminTranslator.completeDictionary:");
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		restCallService.postJson('/api/admin/translator/complete', this, {}, httpPayload =>{
			httpPayload.trace("AdminTranslator.completeDictionary");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState({ "message": httpPayload.getData().message, 
						"translate": httpPayload.getData() });
			}
		});
	}
	
}

export default AdminTranslator;

