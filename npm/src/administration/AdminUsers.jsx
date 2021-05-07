// -----------------------------------------------------------
//
// AdminUsers
//
// Manage user
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";
import { TextInput,Select, TooltipIcon, Tag,Toggle,ToggleSmall } from 'carbon-components-react';
import { LampFill, Lamp, PersonBadge,Bookmark, BookmarkStar,AwardFill, Fonts, List  } from 'react-bootstrap-icons';

import { Loading } from 'carbon-components-react';

import FactoryService 		from 'service/FactoryService';

import LogEvents 			from 'component/LogEvents';



// -----------------------------------------------------------
//
// AdminUsers
//
// Manage users
//
// -----------------------------------------------------------
class AdminUsers extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {searchUserSentence:'', listusers:[], 
			filterusers: {
					all: true,
					connected:false, 
					block:false, 
					administrator:false,
					premium:false,
					excellence:false
			},
			show : { details: false}
			 
		};
		
		this.searchUsers 			= this.searchUsers.bind( this );	
		this.searchUsersCallback	= this.searchUsersCallback.bind( this);
		this.setAttributUser 		= this.setAttributUser.bind( this );
		this.managerfilter			= this.managerfilter.bind( this );
		this.disconnectUser			= this.disconnectUser.bind( this);
		
	}
	
	// Calculate the state to display
	componentDidMount () {
		// call the server to get the value
		this.setState({inprogress: true });
		
		this.searchUsers();
	}


	render() {
		const intl = this.props.intl;

		console.log("AdminAPIKey.render : inprogress="+this.state.inprogress+", listkeys="+JSON.stringify(this.state.listkeys));
		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="SearchUser" withOverlay={true}
    						/>);


		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminUsers.Title" defaultMessage="Users" />
				</div>
				<div class="card-body">
				 	{inprogresshtml}
					<div class="row">
						<div class="col-6"> 
							<TextInput
								labelText={<FormattedMessage id="AdminUsers.searchSentence" defaultMessage="Search"/>} 
								value={this.state.searchUserSentence} onChange={ (event) => this.setState({searchUserSentence: event.target.value})}/>
						</div>
						<div class="col-sm">
							<div class="btn-group btn-group-sm" role="groupstate" aria-label="Basic radio toggle button group" style={{ padding: "10px 10px 10px 10px" }}>
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterALL" autocomplete="off" 
									checked={this.state.filterusers.all}
									onChange={() => this.managerfilter('all')}/>
							  	<label class="btn btn-outline-primary" for="filterALL">
									<List />&nbsp;<FormattedMessage id="AdminUsers.AllUsers" defaultMessage="All Users"/>
								</label>
						
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterConnected" autocomplete="off" 
									checked={this.state.filterusers.connected}
									onChange={() => this.managerfilter('connected') }/>
							  	<label class="btn btn-outline-primary" for="filterConnected">
									<LampFill style={{color:"green", fill:"green"}}/>&nbsp;<FormattedMessage id="AdminUsers.Connected" defaultMessage="Connected"/>
								</label>
							
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterBlock" autocomplete="off" 
									checked={this.state.filterusers.block}
									onChange={() => this.managerfilter('block') }/>
							  	<label class="btn btn-outline-primary" for="filterBlock">
									<PersonBadge style={{color:"red", fill:"red"}}/>&nbsp;<FormattedMessage id="AdminUsers.Blocked" defaultMessage="Blocked"/>
								</label>
								
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterAdministrator" autocomplete="off" 
									checked={this.state.filterusers.administrator}
									onChange={() => this.managerfilter('administrator') }/>
							  	<label class="btn btn-outline-primary" for="filterAdministrator">
									<AwardFill style={{color:"a17f1a"}}/>&nbsp;<FormattedMessage id="AdminUsers.Administrator" defaultMessage="Administrator"/>
								</label>

								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterPremium" autocomplete="off" 
									checked={this.state.filterusers.premium}
									onChange={() => this.managerfilter('premium') }/>
							  	<label class="btn btn-outline-primary" for="filterPremium">
									<BookmarkStar style={{color:"#ff6666", fill:"#ff6666"}}/>&nbsp;<FormattedMessage id="AdminUsers.Premium" defaultMessage="Premium"/>
								</label>
								
								<input type="checkbox" class="btn-check" name="btnradiostate" id="filterExcellence" autocomplete="off" 
									checked={this.state.filterusers.excellence}
									onChange={() => this.managerfilter('excellence') }/> 
							  	<label class="btn btn-outline-primary" for="filterExcellence">
									<BookmarkStar style={{color:"a17f1a",  fill:"a17f1a"}}/>&nbsp;<FormattedMessage id="AdminUsers.Excellence" defaultMessage="Excellence"/>
								</label>
								
							</div>
							
						</div>
					</div>				
					<div class="row">
						<div class="col-4"> 
							<button class="btn btn-info btn-sm"
							 onClick={this.searchUsers} style={{marginTop: "5px"}}>
								<FormattedMessage id="AdminUsers.Search" defaultMessage="Search"/>
							</button>
							<div style={{color: "red"}}>{this.state.message}</div>
						</div>
					</div>
					<div class="row">
						<div class="col-4">
							<ToggleSmall class="sm" labelText="" aria-label="" 
							toggled={this.state.show.details}
							selectorPrimaryFocus={this.state.show.details}
							labelA={<FormattedMessage id="AdminUsers.ShowDetails" defaultMessage="Details" />}
							labelB={<FormattedMessage id="AdminUsers.ShowDetails" defaultMessage="Details" />}
							onChange={(event) => {
								this.setState( { show: { details : event.target.checked}} );
								}}
							id="showDetails" />
						</div> 
					</div>
					
					
					{this.state.inprogresshtml && <Loading
      						description="Active loading indicator" withOverlay={true}
    						/>}
					<table  class="toghtable table table-stripped" style={{marginTop:"10px"}}><tr>
							<th></th>
							<th></th>
							<th><FormattedMessage id="AdminUsers.UserName" defaultMessage="User Name"/></th>
							<th><FormattedMessage id="AdminUsers.CompleteName" defaultMessage="Complete Name"/></th>
							<th><FormattedMessage id="AdminUsers.Email" defaultMessage="Email"/></th>
							<th><FormattedMessage id="AdminUsers.PhoneNumber" defaultMessage="PhoneNumber"/></th>
							<th><FormattedMessage id="AdminUsers.LastConnectionTime" defaultMessage="Last Connection"/></th>
							<th><FormattedMessage id="AdminUsers.LastActivityTime" defaultMessage="Last Activiy"/></th>
							
						</tr>
						{this.state.listusers && this.state.listusers.map( (item, index) => {
							console.log("item="+JSON.stringify(item));
							return (
								<tbody>
								<tr  key={index} style={{borderTop: "1px solid"}}>
									
									<td> 
										{item.connected === 'ONLINE' && 
											<TooltipIcon
      											tooltipText={intl.formatMessage({id: "AdminUsers.ConnectedOnLine",defaultMessage: "User connected"})} >
												<LampFill style={{color:"green", fill:"green"}}/>
											</TooltipIcon>}
										{item.connected === 'OFFLINE' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.ConnectedOffline", defaultMessage: "User offline"})}>
												<Lamp />
											</TooltipIcon>}
										
										{item.statusUser === 'ACTIF' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.LongStatusUserActif", defaultMessage: "User actif"})}>
												<PersonBadge style={{color:"green", fill:"green"}} />
											</TooltipIcon>}
											
											
											
											
										{item.statusUser === 'DISABLED' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.LongStatusUserDisabled", defaultMessage: "User disabled"})}>
												<PersonBadge style={{color:"gray", fill:"gray"}}/>
											</TooltipIcon>}
										{item.statusUser === 'BLOCK' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.LongStatusUserBlock", defaultMessage: "User Blocked"})}>
												<PersonBadge style={{color:"red", fill:"red"}}/>
											</TooltipIcon>}
											
										
										
										
										{item.subscription === 'FREE' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.subscriptionFree", defaultMessage: "Free subscription"})}>
												<Bookmark />
											</TooltipIcon>}
										{item.subscription === 'PREMIUM' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.subscriptionPremium", defaultMessage: "Premium subscription"})}>
												<BookmarkStar style={{color:"#ff6666", fill:"#ff6666"}}/>
											</TooltipIcon>}
										{item.subscription === 'EXCELLENCE' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.subscriptionExcellence", defaultMessage: "Excellence subscription"})}>
												<BookmarkStar style={{color:"#a17f1a",  fill:"#a17f1a"}}/>
											</TooltipIcon>}



										{item.privilegeuser === 'ADMIN' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.privilegeAdmin", defaultMessage: "Administrator"})}>
												<AwardFill style={{fill:"#ff6666"}}/>
											</TooltipIcon>}
										{item.privilegeuser === 'TRANS' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.privilegeTrans", defaultMessage: "Translator"})}>
												<Fonts />
											</TooltipIcon>}
										{item.privilegeuser === 'USER' && <div />}
 
									</td>
									<td> 
										{this.canBeDisconnected( item ) =='YES' && <button class="btn btn-info btn-sm"
							 				onClick={(event) => this.disconnectUser( item)}><FormattedMessage id="AdminUsers.Disconnect" defaultMessage="Disconnect"/></button>}
										{this.canBeDisconnected( item ) =='MYSELF' && <Tag type="teal"><FormattedMessage id="AdminUsers.Myself" defaultMessage="Myself"/></Tag>}
									 </td>
									<td> {item.name} </td>
									<td> {item.firstname}&nbsp;{item.lastname}	</td>
									<td> {item.email}	</td>
									<td> {item.phonenumber} </td>
									<td> <div style={{fontSize:"12px", whiteSpace: "nowrap"}}> {item.connectiontimest}</div> </td>
									<td> <div style={{fontSize:"12px", whiteSpace: "nowrap"}}> {item.connectionlastactivityst} </div></td>
								</tr>
								{this.state.show.details && <tr>
									<td colspan="2"></td>
									<td colspan="3">
								
										<table>
											<tr>
											<td>
												<Select labelText={<FormattedMessage id="AdminUsers.Status" defaultMessage="Status" />}
													id="statusUser"
													value={item.statusUser}
													onChange={(event) => this.setAttributUser( item, "statusUser", event.target.value)}>
		
													<FormattedMessage id="AdminUsers.StatusUserActif" defaultMessage="Actif">
														{(message) => <option value="ACTIF">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.StatusUserDisabled" defaultMessage="Disabled">
														{(message) => <option value="DISABLED">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.StatusUserBlocked" defaultMessage="Blocked">
														{(message) => <option value="BLOCKED">{message}</option>}
													</FormattedMessage>
												</Select>
											</td><td>
												<Select labelText={<FormattedMessage id="AdminUsers.SubscriptionEnum" defaultMessage="Subscription" />}
													id="subscription"
													value={item.subscriptionuser}
													onChange={(event) => this.setAttributUser( item, "subscriptionuser", event.target.value)}>
		
													<FormattedMessage id="AdminUsers.SubscriptionUserFree" defaultMessage="Free">
														{(message) => <option value="FREE">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.SubscriptionUserPremium" defaultMessage="Premium">
														{(message) => <option value="PREMIUM">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.SubscriptionUserExcellence" defaultMessage="Excellence">
														{(message) => <option value="EXCELLENCE">{message}</option>}
													</FormattedMessage>
												</Select></td><td>
												<Select labelText={<FormattedMessage id="AdminUsers.PrivilegeUser" defaultMessage="Privilege" />}
													id="privilegeuser"
													value={item.privilegeuser}
													onChange={(event) => this.setAttributUser( item, "privilegeuser", event.target.value)}>
		
													<FormattedMessage id="AdminUsers.PrivilegeUserAdmin" defaultMessage="Administrator">
														{(message) => <option value="ADMIN">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.PrivilegeUserAdminTRANS" defaultMessage="Translator">
														{(message) => <option value="TRANS">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.PrivilegeUserUser" defaultMessage="User">
														{(message) => <option value="USER">{message}</option>}
													</FormattedMessage>
												</Select>
											
											</td></tr></table>
									 </td>
								</tr> }
								
								</tbody>
							)
							})
						}
					</table>
				</div>
				<LogEvents listEvents={this.state.listEvents} />
					
			</div>
			
			);
			// 
	}

	/**
	Manage the filter button. 
	- When ALL is selected, uncheck all other
	- On opposite, when a button is clicked, uncheck all
	- Last, when all button are uncheck, click ALL
	*/	
	managerfilter( attribut) {
		var filter = this.state.filterusers;
		if (attribut === 'all') {
			filter={all:true, connected:false, block:false, administrator:false,premium:false, excellence:false};
		} else {
			// change the attribut 
			filter[ attribut ] = ! filter[ attribut ];
			// if all filter are uncheck, then check back all
			var oneIsTrue=false;
			for (var index in filter) {
				if (index !== 'all' && filter[index] === true)
					oneIsTrue=true;
			}
			if (oneIsTrue) {
				filter.all=false;
			} else
				filter.all=true;
		}
		this.setState({ filterusers: filter});
	}
	
	
	searchUsers () {	
		this.setState({ message:"", inprogress:true});
		var restCallService = FactoryService.getInstance().getRestcallService();
		var filterUrl="";
		for (var index in this.state.filterusers)
			filterUrl += "&"+index+"="+this.state.filterusers[ index ];
		
		restCallService.getJson('/api/user/admin/search?searchusersentence='+this.state.searchUserSentence+filterUrl, this, this.searchUsersCallback);

	}
	
	searchUsersCallback(httpPayload ) {
			httpPayload.trace("AdminUsers.getSearchUserCallback");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				console.log("httpPayload.getData()="+JSON.stringify(httpPayload.getData()));
				this.setState({ listusers : httpPayload.getData().users,
								countusers: httpPayload.getData().countusers,
								page:httpPayload.getData().page,
								numberperpage:httpPayload.getData().numberperpage
								});
			}
		
		
	}

	setAttributUser(user, attribut, value) {
		console.log("AdminUsers.setAttributUser:");
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		var param={userid: user.id,
					attribut: attribut,
					value: value};
		restCallService.postJson('/api/user/admin/update', this, param, httpPayload =>{
			httpPayload.trace("AdminUsers.setAttributUser");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState({ "message": httpPayload.getData().message, listEvents: httpPayload.getData().listEvents }); 	
				// update the attribut now
				var listusers = this.state.listusers;
				user[ attribut ] = value;
				this.setState( { listusers : listusers});			
			}
		});
	
	}
	
	
	/**
	Is this user can be disconnected ? Yes, if it is connected and not me !
	return MYSELF, YES, NO
	 */
	canBeDisconnected( user ) {
		if (user.connected === 'ONLINE') {
			var authService = FactoryService.getInstance().getAuthService();

			if (authService.getUser().id == user.id)
				return "MYSELF"; // myself
			return "YES";
		}
		return "NO";
	}
	
	
	disconnectUser( user ) {
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		var param={userid: user.id};
		restCallService.postJson('/api/user/admin/disconnect', this, param, httpPayload =>{
			httpPayload.trace("AdminUsers.disconnectUser");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			} else {
				debugger;
				this.setState({ "message": httpPayload.getData().message, listEvents: httpPayload.getData().listEvents }); 	
				// update the attribut now
				var listusers = this.state.listusers;
				// search the user
				for( var user in listusers) {
					if (listusers[ user ].id ===  httpPayload.getData().user.id)
						listusers[ user ] = httpPayload.getData().user;
				}
				this.setState( { listusers : listusers});			
			}
		});
	}
}

export default injectIntl(AdminUsers);

