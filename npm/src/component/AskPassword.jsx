// -----------------------------------------------------------
//
// AskPaswword
//
// Display two input field, check value are the same in both input
//
// -----------------------------------------------------------

import React from 'react';
import { FormattedMessage } from "react-intl";

import { TextInput,  } from '@carbon/react';

class AskPaswword extends React.Component {
	
	/** Caller must declare 	this.props.changePasswordCallback(isCorrect, password ) */
	constructor( props ) {
		super();
		this.state={ password : ''};
	}
		
	//----------------------------------- Render
	render() {
		let checkPassword = this.checkPassword(this.state.password,this.state.confirmPassword);
		return (
				<div>
					<TextInput labelText={<FormattedMessage id="AskPassword.NewPassword" defaultMessage="New password" />}
							type="password" 
							value={this.state.password}
							maxlength="30" 
							onChange={(event) => {
								this.changeInput(event.target.value, this.state.confirmPassword);
								}
							} 
							required></TextInput><br />

					<TextInput labelText={<FormattedMessage id="AskPassword.RetypePassword" defaultMessage="Retype password to verify it" />}
							type="password" 
							value={this.state.confirmPassword}
							maxlength="30" 
							onChange={(event) => {
								this.changeInput(this.state.password,event.target.value  );
								}
							} 
							required></TextInput><br />
					{checkPassword ===1 && <div style={{color:"red"}}><FormattedMessage id="AskPassword.DifferentPassword" defaultMessage="Password are different" /></div>}
					{checkPassword ===2 && <div style={{color:"red"}}><FormattedMessage id="AskPassword.ToSmalPassword" defaultMessage="Give minimum 3 characters for the password" /></div>}
				</div>
			);

		}
	
	changeInput(password, confirmPassword) {
		this.setState({ password: password, confirmPassword: confirmPassword })
		let check = this.checkPassword(password, confirmPassword);
		this.props.changePasswordCallback( check === 0, password);
	}


	checkPassword(password, confirmPassword) {
		// console.log("AskPassword, compare["+password+"] <> ["+confirmPassword+"]");
		if (password !== confirmPassword)
			return 1;
		if (password.length < 3)
			return 2
		return 0;
	}
	
	
};
export default AskPaswword;		