// ********************************************************************************
//
//  Togh Project
//
//  This component is part of the Togh Project, developed by Pierre-Yves Monnet
//
//
// ********************************************************************************
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";
import { TextInput,  TooltipIcon,  Toggle, Loading, DatePicker, DatePickerInput, TimePicker } from 'carbon-components-react';
import { LampFill, Alarm,  List, LockFill,PersonXFill  } from 'react-bootstrap-icons';


import FactoryService 		from 'service/FactoryService';

import LogEvents 			from 'component/LogEvents';



// -----------------------------------------------------------
//
// AdminLogConnection
//
// Manage users
//
// -----------------------------------------------------------
class AdminLogConnection extends React.Component {

	constructor(props) {
		super();
		// console.log("AdminLogConnection.constructor");

		this.state = {searchLogConnectionSentence:'', listLogs:[], 
			filterLog: {
					all: true,
					ok:false, 
					unknownUser:false,
					badPassword:false,
					underAttack:false
			},
			show : { details: false}
			 
		};
		
		this.searchLogConnection 			= this.searchLogConnection.bind( this );	
		this.searchLogConnectionCallback	= this.searchLogConnectionCallback.bind( this);
		this.manageFilter			        = this.manageFilter.bind( this );		
	}
	
	// Calculate the state to display
	componentDidMount () {
		// call the server to get the value
		this.setState({inprogress: true });
		
		this.searchLogConnection( this.state.filterLog );
	}


	render() {
		const intl = this.props.intl;
		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="LogConnection" withOverlay={true}
    						/>);


		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminLogConnection.Title" defaultMessage="Connection" />
				</div>
				<div class="card-body">
				 	{inprogresshtml}
					<div class="row">
						<div class="col-6"> 
							<TextInput
							    id="search"
								labelText={<FormattedMessage id="AdminLogConnection.searchSentence" defaultMessage="Search"/>} 
								value={this.state.searchLogConnectionSentence}
								onChange={ (event) => this.manageFilterLogAttribut("sentence", event.target.value)}/>
						</div>

						<div class="col-6">
                            <DatePicker datePickerType="range"
                                onChange={(dates) => {
                                    console.log("AdminLogConnection: on change dates number="+dates.length);
                                    this.manageFilterLogAttribut( "dateStart", null);
                                    this.manageFilterLogAttribut( "dateEnd", null);
                                    if (dates.length > 0) {
                                        this.manageFilterLogAttribut( "dateStart", dates[0].toISOString());
                                    }
                                    if (dates.length > 1) {
                                        this.manageFilterLogAttribut( "dateEnd", dates[1].toISOString());
                                    }
                                }}
                            >
                                <DatePickerInput
                                    id="date-picker-input-id-start"
                                    placeholder="mm/dd/yyyy"
                                    labelText={<FormattedMessage id="AdminLogConnection.StartDate" defaultMessage="Start Date" />}
                                />
                                <DatePickerInput
                                    id="date-picker-input-id-finish"
                                    placeholder="mm/dd/yyyy"
                                    labelText={<FormattedMessage id="AdminLogConnection.EndDate" defaultMessage="End Date" />}
                                />
                            </DatePicker>
                        </div>
					</div>

					<div class="row">
						<div class="col-6">
							{/* role="groupstate" */}
							<div class="btn-group btn-group-sm"  
								aria-label="Basic radio toggle button group" 
								style={{ padding: "10px 10px 10px 10px" }}>
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterALL" autoComplete="off"
									checked={this.state.filterLog.all}
									onChange={() => this.manageFilter('all')}/>
							  	<label class="btn btn-outline-primary" for="filterALL">
									<List />&nbsp;<FormattedMessage id="AdminLogConnection.AllLogs" defaultMessage="All Logs"/>
								</label>
						
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterOk" autocomplete="off"
									checked={this.state.filterLog.ok}
									onChange={() => this.manageFilter('ok') }/>
							  	<label class="btn btn-outline-primary" for="filterOk">
									<LampFill style={{color:"green", fill:"green"}}/>&nbsp;<FormattedMessage id="AdminLogConnection.Ok" defaultMessage="Ok"/>
								</label>
							
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterUnknownUser" autocomplete="off"
									checked={this.state.filterLog.unknownUser}
									onChange={() => this.manageFilter('unknownUser') }/>
							  	<label class="btn btn-outline-primary" for="filterUnknownUser">
									<PersonXFill style={{color:"red", fill:"red"}}/>&nbsp;<FormattedMessage id="AdminLogConnection.UnknownUser" defaultMessage="UnknownUser"/>
								</label>
								
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterBadPassword" autocomplete="off"
									checked={this.state.filterLog.badPassword}
									onChange={() => this.manageFilter('badPassword') }/>
							  	<label class="btn btn-outline-primary" for="filterBadPassword">
									<LockFill style={{color:"a17f1a"}}/>&nbsp;<FormattedMessage id="AdminLogConnection.BadPassword" defaultMessage="Bad Password"/>
								</label>

								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterUnderAttack" autocomplete="off"
									checked={this.state.filterLog.underAttack}
									onChange={() => this.manageFilter('underAttack') }/>
							  	<label class="btn btn-outline-primary" for="filterUnderAttack">
									<Alarm style={{color:"#ff6666", fill:"#ff6666"}}/>&nbsp;<FormattedMessage id="AdminLogConnection.UnderAttack" defaultMessage="Under Attack"/>
								</label>
							</div>
							
						</div>
						<div class="col-3">
                            <TimePicker
                                    id="time-picker-input-id-start"
                                    labelText={<FormattedMessage id="AdminLogConnection.StartTime" defaultMessage="Start Time" />}
                                    onChange={(event) => this.manageFilterLogAttribut( "timeStart", event.target.value)}
                            />
                        </div>
                        <div class="col-3">
                                <TimePicker
                                    id="time-picker-input-id-end"
                                        labelText={<FormattedMessage id="AdminLogConnection.EndTime" defaultMessage="End Time" />}
                                    onChange={(event) => this.manageFilterLogAttribut( "timeEnd", event.target.value) }
                                 />
                        </div>
					</div>



					<div class="row">
						<div class="col-4"> 
							<button class="btn btn-info btn-sm"
							 onClick={() => this.searchLogConnection( this.state.filterLog )} style={{marginTop: "5px"}}>
								<FormattedMessage id="AdminLogConnection.Search" defaultMessage="Search"/>
							</button>
							<div style={{color: "red"}}>{this.state.message}</div>
						</div>
					</div>
					<div class="row">
						<div class="col-4">
						    <table>
						    <tr><td>
						        <Toggle size="sm" class="sm" labelText="" aria-label=""
                                    toggled={this.state.show.details}
                                    selectorPrimaryFocus={this.state.show.details}
                                    labelA={<FormattedMessage id="AdminLogConnection.ShowDetails" defaultMessage="Details" />}
                                    labelB={<FormattedMessage id="AdminLogConnection.ShowDetails" defaultMessage="Details" />}
                                    onChange={(event) => {
                                        this.setState( { show: { details : event.target.checked}} );
                                        }}
                                    id="showDetails" />
                            </td><td style={{padding: "10px"}}>

                                <FormattedMessage id="AdminLogConnection.ShowDetails" defaultMessage="Details"/>
                            </td></tr>
                            </table>
						</div> 
					</div>
					
					
					{this.state.inprogresshtml && <Loading
      						description="Active loading indicator" withOverlay={true}
    						/>}
					<table  class="toghtable table table-stripped" style={{marginTop:"10px"}}><tr>
							<th></th>
							<th><FormattedMessage id="AdminLogConnection.Status" defaultMessage="Status"/></th>
							<th><FormattedMessage id="AdminLogConnection.DateAndTime" defaultMessage="Date"/></th>
                            <th><FormattedMessage id="AdminLogConnection.Email" defaultMessage="Email"/></th>
							<th><FormattedMessage id="AdminLogConnection.GoogleId" defaultMessage="Google Id"/></th>
							<th><FormattedMessage id="AdminLogConnection.IpAddress" defaultMessage="Ip Address"/></th>
							<th><FormattedMessage id="AdminLogConnection.NumberOfTentatives" defaultMessage="Nb. Tentatives"/></th>
						</tr>
						{this.state.listLogs && this.state.listLogs.map( (item, index) => {
							// console.log("AdminUsers:item="+JSON.stringify(item));
							return (
								<tbody>
								<tr  key={index} style={{borderTop: "1px solid"}}>
									
									<td> 
										{item.statusConnection === 'OK' &&
											<TooltipIcon
      											tooltipText={intl.formatMessage({id: "AdminLogConnection.ConnectionOk",defaultMessage: "Connection correct"})} >
												<LampFill style={{color:"green", fill:"green"}}/>
											</TooltipIcon>}
										{item.statusConnection === 'UNKNOWUSER' &&
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminLogConnection.UnknowUser", defaultMessage: "User unknown"})}>
												<PersonXFill style={{color:"red", fill:"red"}}/>
											</TooltipIcon>}
										
										{item.statusConnection === 'BADPASSWORD' &&
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminLogConnection.BadPassword", defaultMessage: "Bad Password"})}>
												<LockFill style={{color:"red", fill:"red"}} />
											</TooltipIcon>}
										{item.numberOfTentatives > 5 && item.statusConnection !== 'OK' &&
                                            <TooltipIcon
                                                tooltipText={intl.formatMessage({id: "AdminLogConnection.UnderAttack", defaultMessage: "Under Attack (lot of tentatives)"})}
                                                style={{paddingLeft:"10px"}}>
                                                <Alarm style={{color:"red", fill:"red"}} />
                                            </TooltipIcon>}
									</td>
                                    <td style={{fontSize:"smaller"}}> {item.statusConnection} </td>
									<td> {item.connectionTimeHumanZone} </td>
                                    <td> {item.email} </td>
                                    <td> {item.googleId} </td>
                                    <td> {item.ipAddress} </td>
                                    <td> {item.numberOfTentatives} </td>
								</tr>
								</tbody>
							)
							})
						}
					</table>

					{this.state.page} / {this.state.numberOfPages} <FormattedMessage id="AdminLogConnection.Pages" defaultMessage="Pages"/>
					&nbsp;({this.state.numberOfItems}  <FormattedMessage id="AdminLogConnection.Users" defaultMessage="Users"/> )

				</div>
				<LogEvents listEvents={this.state.listEvents} />
					
			</div>
			
			);
			// 
	}

    /**
    * Set an attribut in the filterLog
    */
    manageFilterLogAttribut( attributName, attributValue ) {
        const filterLog = this.state.filterLog;
        filterLog[ attributName ] = attributValue;
        this.setState( {filterLog: filterLog});
    }
	/**
	Manage the filter button. 
	- When ALL is selected, uncheck all other
	- On opposite, when a button is clicked, uncheck all
	- Last, when all button are uncheck, click ALL
	*/	
	manageFilter( attribut) {
		let filterLog = this.state.filterLog;
		if (attribut === 'all') {
			filterLog = { all: true, ok:false, unknownUser:false,badPassword:false,underAttack:false};
		} else {
			// change the attribut 
			filterLog[ attribut ] = ! filterLog[ attribut ];
			// if all filter are uncheck, then check back all
			var oneIsTrue=false;
			for (var index in filterLog) {
				if (index !== 'all' && filterLog[index] === true)
					oneIsTrue=true;
			}
			if (oneIsTrue) {
				filterLog.all=false;
			} else {
				filterLog.all=true;
			}
		}
		this.setState({ filterLog: filterLog});
		// ask immediately
		this.searchLogConnection(filterLog);
	}
	
	
	searchLogConnection (filterLog) {
		this.setState({ message:"", inprogress:true});
		var restCallService = FactoryService.getInstance().getRestCallService();
		var filterUrl="";
		for (var index in filterLog)
			filterUrl += "&"+index+"="+filterLog[ index ];
		
		restCallService.getJson('/api/admin/users/searchloginlog?1=1'+filterUrl, this, this.searchLogConnectionCallback);

	}
	
	searchLogConnectionCallback(httpPayload ) {
        // httpPayload.trace("AdminLogConnection.getSearchUserCallback");
        this.setState({inprogress: false });
        if (httpPayload.isError()) {
            this.setState({ message: "Server connection error"});
        }
        else {
            // console.log("AdminUsers: httpPayload.getData()="+JSON.stringify(httpPayload.getData()));
            this.setState({ listLogs : httpPayload.getData().listLoginLogs,
                            countLogs: httpPayload.getData().countLogs,

                            page:httpPayload.getData().page,
                            itemsPerPage:httpPayload.getData().itemsPerPage,
                            numberOfPages:httpPayload.getData().numberOfPages,
                            numberOfItems:httpPayload.getData().numberOfItems
                            });
        }
	}

}

export default injectIntl(AdminLogConnection);

