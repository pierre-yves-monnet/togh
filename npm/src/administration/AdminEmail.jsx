// -----------------------------------------------------------
//
// AdminEmail
//
// Set up email parameter and test
//
// -----------------------------------------------------------
import React from 'react';

import { FormattedMessage } from "react-intl";
import { TextInput } from 'carbon-components-react';

import { Loading } from 'carbon-components-react';

import FactoryService 	from 'service/FactoryService';

import LogEvents 		from 'component/LogEvents';



// -----------------------------------------------------------
//
// AdminEmail
//
// Google access
//
// -----------------------------------------------------------
class AdminEmail extends React.Component {

	// this.props.updateEvent()
	constructor(props) {
		super();
		// console.log("RegisterNewUser.constructor");

		this.state = {listKeys : [],
		    listEvents:[],
		    listTestEvents:[],
		    sendEmailTo:'toghnow@gmail.com',
		    testEmailMessage: ''
		};

		this.updateKey 			= this.updateKey.bind( this );
		this.getApiKeyCallback 	= this.getApiKeyCallback.bind( this );
		this.testEmail          = this.testEmail.bind( this );
	}

	// Calculate the state to display
	componentDidMount () {
		// call the server to get the value
		this.setState({inProgress: true });

		let restCallService = FactoryService.getInstance().getRestCallService();

		restCallService.getJson('/api/admin/email/get?', this, this.getApiKeyCallback);

	}

	getApiKeyCallback(httpPayload ) {
        httpPayload.trace("AdminEmail.getKey");

        this.setState({inProgress: false });
        if (httpPayload.isError()) {
            this.setState({ message: "Server connection error"});
        }
        else {
            console.log("httpPayload.getData()="+JSON.stringify(httpPayload.getData()));
            this.setState({ listKeys : httpPayload.getData()});
        }
	}

	render() {
		console.log("AdminEmail.render : inProgress="+this.state.inProgress+", listKeys="+JSON.stringify(this.state.listKeys));

		return (
			<div class="card" style={{marginTop: "10px"}}>
				<div class="card-header" style={{backgroundColor:"#decbe4"}}>
				<FormattedMessage id="AdminEmail.Title" defaultMessage="Email parameters" />
				</div>
				<div class="card-body">

				    <div class="container">

                        {this.state.inProgress &&
                            <Loading
                                description="Active loading indicator" withOverlay={true}
                                />
                        }
                        {this.state.listKeys.map( (item, index) => {
                            console.log("item="+JSON.stringify(item));
                            return (

                                <div  key={index} class="row">
                                    <TextInput labelText={item.name} value={item.keyApi}
                                        onChange={(event) => {
                                                let list = this.state.listKeys;
                                                list[ index ].keyApi = event.target.value;
                                                this.setState( {listKeys: list});
                                                }} ></TextInput><br />
                                </div>
                            )
                            })
                        }
                        <div class="row align-items-end" style={{marginTop : "10px"}}>
                            <div class="col">
                                <div class="card" style={{padding:"10px 10px 10px 10px"}}>
                                    <TextInput labelText={<FormattedMessage id="AdminEmail.SendEmailTo" defaultMessage="Send Email test to"/>}
                                            value={this.state.sendEmailTo}
                                            onChange={(event) => this.setState( { sendEmailTo: event.target.value })}
                                            id="AdminEmail.SendEmailTo"/>
                                        <br />
                                        <button class="btn btn-success btn-sm" onClick={this.testEmail}
                                            style={{float:"left"}}>
                                            <FormattedMessage id="AdminEmail.testEmail" defaultMessage="Test Email"/>
                                        </button>

                                </div>
                            </div>

                            <div class="col">
                                <button class="btn btn-info btn-sm" onClick={this.updateKey}
                                        style={{float:"right"}}>
                                    <FormattedMessage id="AdminEmail.updateKey" defaultMessage="Update key"/>
                                </button>
                            </div>
                        </div>
                        <div class="row align-items-end" style={{marginTop : "10px"}}>
                            <div class="col">
                                {this.state.testEmailMessage && this.state.testEmailMessage.length>0 &&
                                    <div class="alert alert-danger" style={{marginTop: "10px"}}>
                                        {this.state.testEmailMessage}
                                    </div>
                                }
                                <LogEvents listEvents={this.state.listTestEvents} />
                                <LogEvents listEvents={this.state.listEvents} />
                            </div>
                        </div>

                    </div>

				</div>

			</div>

			);
			//
	}

	updateKey() {
		console.log("AdminEmail.updateKey:");
		this.setState({inProgress: true,
             testEmailMessage:'',
             listTestEvents:[],
             listEvents:[]});

		var restCallService = FactoryService.getInstance().getRestCallService();
		var param={listKeys: this.state.listKeys};
		restCallService.postJson('/api/admin/email/update', this, param, httpPayload =>{
			httpPayload.trace("AdminEmail.updateKey");
			this.setState({inProgress: false });
			if (httpPayload.isError()) {
				this.setState({ message: "Server connection error"});
			}
			else {
				this.setState({ "message": httpPayload.getData().message, listEvents: httpPayload.getData().listLogEvents });
			}
		});
	}

	testEmail() {
		console.log("AdminEmail.testEmail:");
		this.setState({inProgress: true,
             testEmailMessage:'',
             listTestEvents:[],
             listEvents:[]});

		var restCallService = FactoryService.getInstance().getRestCallService();
		var param={listKeys: this.state.listKeys, 'sendEmailTo': this.state.sendEmailTo};
		restCallService.postJson('/api/admin/email/test', this, param, httpPayload =>{
			httpPayload.trace("AdminEmail.test isError:"+httpPayload.isError());
			this.setState({inProgress: false });
			if (httpPayload.isError()) {
				this.setState({ testEmailMessage: "Server connection error"});
			} else {
				this.setState({ testEmailMessage: httpPayload.getData().message, listTestEvents: httpPayload.getData().listLogEvents });
			}
		});
	}
}

export default AdminEmail;

