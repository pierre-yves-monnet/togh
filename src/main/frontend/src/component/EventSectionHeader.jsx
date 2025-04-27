/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


import React from 'react';


import { PlusCircle } from 'react-bootstrap-icons';

import UserTips 		from 'component/UserTips';

// -----------------------------------------------------------
//
// EventSectionHeader
//
// Normalize the header of a section 
//
// -----------------------------------------------------------

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
				  {this.state.image &&
                        <div style={{ float: "left" }}>
                            <img style={{ float: "right", width: "100" }} src={this.state.image} alt="State" /><br />
                        </div>
					}
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