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
import { Loading } from '@carbon/react';


import FactoryService 		from 'service/FactoryService';

import LogEvents 			from 'component/LogEvents';
import ChartTogh            from 'component/ChartTogh';



// -----------------------------------------------------------
//
// AdminUsersStats
//
// Manage users
//
// -----------------------------------------------------------
class AdminUsersStats extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {
			users: {},
			userLastMonth: [],
			userLastYears:[]

		};
        this.usersStats = this.usersStats.bind(this);

	}

	// Calculate the state to display
	componentDidMount () {
		// call the server to get the value
		this.setState({inprogress: true });

		this.usersStats();
	}


	render() {
		const intl = this.props.intl;

		console.log("AdminUsersStats.render1: inprogress="+this.state.inprogress+" state="+JSON.stringify(this.state));
		console.log("AdminUsersStats.render2: users status = "+JSON.stringify(this.state.users.status));

		let inprogresshtml=(<div/>);
		if (this.state.inprogress )
			inprogresshtml=(<Loading
      						description="loadStats" withOverlay={true}
    						/>);


		return (
		    // AdminUsersStats
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
					<FormattedMessage id="AdminUsersStats.Title" defaultMessage="Users" />
				</div>
				<div class="card-body">
				 	{inprogresshtml}
					<div class="row">
						<div class="col-2">
						    <table class="table table-striped">
						    <tr>
                                <th scope="row"><FormattedMessage id="AdminUsersStats.nbUsers" defaultMessage="Total Users" />
                                </th><td>{this.state.users.total}</td>
						    </tr><tr>
                                <td><FormattedMessage id="AdminUsersStats.nbConnected" defaultMessage="Users connected" />
                                </td><td>{this.state.users.connected}</td>
						    </tr>
                            </table>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.status}
                                title={intl.formatMessage({id: "AdmunUsersStats.Users",defaultMessage: "Users"}) } />
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.source}
                                title={intl.formatMessage({id: "AdmunUsersStats.Source",defaultMessage: "Source"}) } />
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.privilege}
                                title={intl.formatMessage({id: "AdmunUsersStats.Privilege",defaultMessage: "Privilege"}) }/>
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.emailVisibility}
                                title={intl.formatMessage({id: "AdmunUsersStats.EmailVisibility",defaultMessage: "Email visi."}) } />
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.phoneVisibility}
                                title={intl.formatMessage({id: "AdmunUsersStats.PhoneVisibility",defaultMessage: "Phone visi."}) } />
                        </div>
                        <div class="col-2">
                            <ChartTogh type="Doughnut" dataMap={this.state.users.subscription}
                                title={intl.formatMessage({id: "AdmunUsersStats.Subscription",defaultMessage: "Subscription"}) } />
                        </div>
                    </div>
                    <div class="row" style={{ margin: "10px"}}>
                        <div class="col-6">
                            <ChartTogh type="HorizontalBar" dataList={this.state.users.connection} oneColor={true}
                                title={intl.formatMessage({id: "AdmunUsersStats.ConnectionWeek",defaultMessage: "Connection week"}) } />
                        </div>
                        <div class="col-6">
                            <ChartTogh type="HorizontalBar" dataList={this.state.users.connectionFiveYears} oneColor={true}
                                title={intl.formatMessage({id: "AdmunUsersStats.ConnectionFiveYears",defaultMessage: "Connection five years"}) } />
                        </div>
                    </div>
                    <div class="row" style={{ margin: "10px"}}>
                        <div class="col-4">
                            <ChartTogh type="HorizontalBar" dataList={this.state.users.userCreation} oneColor={true}
                                title={intl.formatMessage({id: "AdmunUsersStats.UserCreation",defaultMessage: "User creation"}) } />
                        </div>
                        <div class="col-4">
                           <ChartTogh type="HorizontalBar" dataList={this.state.users.userCreationFiveYears} oneColor={true}
                                title={intl.formatMessage({id: "AdmunUsersStats.UserCreationFiveYears",defaultMessage: "User creation five years"}) } />
                        </div>
                        <div class="col-4">
                           <ChartTogh type="HorizontalBar" dataList={this.state.users.participantCreation} oneColor={true}
                                title={intl.formatMessage({id: "AdmunUsersStats.ParticipantCreation",defaultMessage: "Participants creation"}) } />
                        </div>
                    </div>
				</div>
				<LogEvents listEvents={this.state.listEvents} />
			</div>

			);
			//
	}



	usersStats () {
		this.setState({ message:"", inprogress:true});
		let restCallService = FactoryService.getInstance().getRestCallService();

		restCallService.getJson('/api/admin/users/stats?', this, httpPayload =>{
                httpPayload.trace("AdminSyntesisUsers.usersStats");
                this.setState({inprogress: false });

                if (httpPayload.isError()) {
                    this.setState({ message: "Server connection error"});
                }
                else {
                    console.log("AdminUsersStats: httpPayload.getData()="+JSON.stringify(httpPayload.getData()));
                    this.setState({ users : httpPayload.getData()});
                }
	        });
    }
}

export default injectIntl(AdminUsersStats);

