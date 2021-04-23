// -----------------------------------------------------------
//
// AdminUsers
//
// Manage user
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";
import { TextInput,Select } from 'carbon-components-react';

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

		this.state = {searchUserSentence:'', listusers:[] };
		
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
						<div class="col-6"> 
							<button class="btn btn-info btn-sm"
							 onClick={this.searchUser}>
								<FormattedMessage id="AdminUsers.Search" defaultMessage="Search"/>
							</button>
						</div>
					</div>
					<table  class="toghtable table table-stripped" style={{marginTop:"10px"}}><tr>
							
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
								<tr  key={index} stype={{borderTop: "1px solid;"}}>
									

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
													id="status"
													value={item.subscriptionUser}
													onChange={(event) => this.setAttributUser( item, "status", event.target.value)}>
		
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
												<Select labelText={<FormattedMessage id="AdminUsers.PrivilegeUser" defaultMessage="Privilege" />}
													id="TypePrivilege"
													value={item.privilegeUser}
													onChange={(event) => this.setAttributUser( item, "prilegeUser", event.target.value)}>
		
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
											</td><td>
												<Select labelText={<FormattedMessage id="AdminUsers.SubscriptionEnum" defaultMessage="Subscription" />}
													id="subscription"
													value={item.subscriptionUser}
													onChange={(event) => this.setAttributUser( item, "subscriptionUser", event.target.value)}>
		
													<FormattedMessage id="AdminUsers.SubscriptionUserFree" defaultMessage="Free">
														{(message) => <option value="FREE">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.SubscriptionUserPremium" defaultMessage="Premium">
														{(message) => <option value="PREMIUM">{message}</option>}
													</FormattedMessage>
						
													<FormattedMessage id="AdminUsers.SubscriptionUserPremium" defaultMessage="Illimited">
														{(message) => <option value="ILLIMITED">{message}</option>}
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
		console.log("AdminUsers.updateKey:");
		this.setState({inprogress: true });
		
		var restCallService = FactoryService.getInstance().getRestcallService();
		var param={userid: user.id,
					attribut: attribut,
					value: value};
		restCallService.postJson('/api/admin/apikey/update', this, param, httpPayload =>{
			httpPayload.trace("AdminAPIKey.updateKey");
			this.setState({inprogress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState({ "message": httpPayload.getData().message, listEvents: httpPayload.getData().listEvents }); 						
			}
		});
	
	}
}

export default AdminUsers;

