// -----------------------------------------------------------
//
// EventSectionHeader
//
// Normalize the header of a section 
//
// -----------------------------------------------------------
//

import React from 'react';


import {  PlusCircle } from 'react-bootstrap-icons';

import UserTips from './UserTips';


class EventSectionHeader extends React.Component {
	
	// props.addItemCallback must be defined if a button PLUS is displayed
	constructor(props) {
		super();
		this.state = {
			id: props.id,
			image: props.image,
			title: props.title,
			showPlusButton : props.showPlusButton,
			showPlusButtonTitle : props.showPlusButtonTitle,
			userTipsText: props.userTipsText
		};
		
	}


	render() {
		return (
			<div>
				<div class="eventsection">
					<div style={{ float: "left" }}>
						<img style={{ "float": "right" }} src={this.state.image} style={{ width: 100 }} /><br />
					</div>
					{this.state.title}
					{this.state.showPlusButton &&
						<div style={{ float: "right" }}>
							<button class="btn btn-success btn-xs " 
								 title={this.state.showPlusButtonTitle}>
								<PlusCircle onClick={this.props.addItemCallback}/>
							</button>
						</div>
					}
				</div>			
				<UserTips id={this.state.id} 
					text={this.state.userTipsText}/>
			</div>);
	}
}

export default EventSectionHeader;