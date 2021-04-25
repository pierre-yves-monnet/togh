// -----------------------------------------------------------
//
// AdminUsers
//
// Manage user
//
// -----------------------------------------------------------
import React from 'react';

import { injectIntl, FormattedMessage } from "react-intl";
import { TextInput,Select, TooltipIcon } from 'carbon-components-react';
import { LampFill, Lamp, PersonBadge,Bookmark, BookmarkStar,AwardFill, Fonts  } from 'react-bootstrap-icons';

import { Loading } from 'carbon-components-react';

import FactoryService from '../service/FactoryService';

import LogEvents from '../component/LogEvents';



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

		this.state = {searchUserSentence:'', listusers:[], filterevent:'all' };
		
		this.searchUsers 			= this.searchUsers.bind( this );	
		this.searchUsersCallback	= this.searchUsersCallback.bind( this);
		this.setAttributUser 		= this.setAttributUser.bind( this );		
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
							<div class="btn-group" role="group" style={{ padding: "10px 10px 10px 10px" }}>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"all"})}}><FormattedMessage id="AdminUsers.AllUsers" defaultMessage="All Users"/></button>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"connected"})}}><FormattedMessage id="AdminUsers.Connected" defaultMessage="Connected"/></button>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"block"})}}><FormattedMessage id="AdminUsers.Blocked" defaultMessage="Blocked"/></button>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"administrator"})}}><FormattedMessage id="AdminUsers.Administrator" defaultMessage="Administrator"/></button>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"Premium"})}}><FormattedMessage id="AdminUsers.Premium" defaultMessage="Premium"/></button>
								<button class="btn btn-outline-primary btn-sm" style={{ "marginLeft ": "10px" }} onClick={(event) => {this.setState({filterevent:"Illimited"})}}><FormattedMessage id="AdminUsers.Premium" defaultMessage="Illimited"/></button>
							</div>
						</div>
					</div>				
					<div class="row">
						<div class="col-6"> 
							<button class="btn btn-info btn-sm"
							 onClick={this.searchUsers}>
								<FormattedMessage id="AdminUsers.Search" defaultMessage="Search"/>
							</button>
							<div style={{color: "red"}}>{this.state.message}</div>
						</div>
					</div>
					
					
					{this.state.inprogresshtml && <Loading
      						description="Active loading indicator" withOverlay={true}
    						/>}
					<table  class="toghtable table table-stripped" style={{marginTop:"10px"}}><tr>
							<th></th>
							<th><FormattedMessage id="AdminUsers.UserName" defaultMessage="User Name"/></th>
							<th><FormattedMessage id="AdminUsers.CompleteName" defaultMessage="Complete Name"/></th>
							<th><FormattedMessage id="AdminUsers.Email" defaultMessage="Email"/></th>
							<th><FormattedMessage id="AdminUsers.PhoneNumber" defaultMessage="PhoneNumber"/></th>
							<th><FormattedMessage id="AdminUsers.LastConnectionTime" defaultMessage="LastConnectionTime"/></th>
							
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
												tooltipText={intl.formatMessage({id: "AdminUsers.StatusUserDisabledActif", defaultMessage: "User actif"})}>
												<PersonBadge style={{color:"green", fill:"green"}} />
											</TooltipIcon>}
											
											
											
											
										{item.statusUser === 'DISABLED' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.StatusUserDisabled", defaultMessage: "User disabled"})}>
												<PersonBadge style={{color:"gray", fill:"gray"}}/>
											</TooltipIcon>}
										{item.statusUser === 'BLOCK' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.StatusUserBlock", defaultMessage: "User Blocked"})}>
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
										{item.subscription === 'ILLIMITED' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.subscriptionIllimited", defaultMessage: "Illimited subscription"})}>
												<BookmarkStar style={{color:"a17f1a", backgroundColor:"a17f1a", fill:"a17f1a"}}/>
											</TooltipIcon>}



										{item.privilegeuser === 'ADMIN' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.privilegeAdmin", defaultMessage: "Administrator"})}>
												<AwardFill style={{color:"a17f1a"}}/>
											</TooltipIcon>}
										{item.privilegeuser === 'TRANS' && 
											<TooltipIcon
												tooltipText={intl.formatMessage({id: "AdminUsers.privilegeTrans", defaultMessage: "Translator"})}>
												<Fonts />
											</TooltipIcon>}
										{item.privilegeuser === 'USER' && <div />}
 
									</td>
									<td> {item.name} </td>
									<td> {item.firstname}&nbsp;{item.lastname}	</td>
									<td> {item.email}	</td>
									<td> {item.phonenumber} </td>
									<td> {item.connectionlastactivity} </td>
								</tr>
								<tr>
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
						
													<FormattedMessage id="AdminUsers.SubscriptionUserPremium" defaultMessage="Illimited">
														{(message) => <option value="ILLIMITED">{message}</option>}
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
								</tr>
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
	
	searchUsers () {	
		this.setState({ message:"", inprogress:true});
		var restCallService = FactoryService.getInstance().getRestcallService();

		restCallService.getJson('/api/user/admin/search?searchusersentence='+this.state.searchUserSentence, this, this.searchUsersCallback);

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
			debugger;
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
}

export default injectIntl(AdminUsers);

