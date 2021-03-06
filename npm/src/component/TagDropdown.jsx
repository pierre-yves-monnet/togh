// -----------------------------------------------------------
//
// TagDropDown
//
// Display a tag and a dropdown
//
// -----------------------------------------------------------

import React from 'react';


import { Tag } from 'carbon-components-react';
import { OverflowMenu } from 'carbon-components-react';
import { OverflowMenuItem } from 'carbon-components-react';

class TagDropDown extends React.Component {
	
	
	// this.props.changeState();
	// gives :
	// - listOptions[ { "label", "value", "icon", "type", "imgtitle" }] : a list of state name + type + icon
	// type is a TagHtml type: "teal", "green", "red"...
	//  see https://www.carbondesignsystem.com/components/tag/usage
	// - value : the current value
	// - readWrite[boolean]: a read/write
	// - changeState() method 
	constructor( props ) {
		super();
		
		this.state = { listOptions : props.listOptions,
					value : props.value,
					readWrite: props.readWrite}
	}

	
//----------------------------------- Render
	render() {
		console.log("TagDropDown.render value="+this.state.value);

		var tagHtml = null;
		var dropDownChangeHtml = (<div/>);
		if (this.state.readWrite) {
			var listOptions = this.state.listOptions.map( item =>
				{ return (
						<OverflowMenuItem className={item.value} 
							itemText={item.label}
							id={item.value}
							onClick={(event) => {
									console.log("TagDropdown.click new value="+item.value);
									this.setState( { value : item.value});
									this.props.changeState( item.value )}} />
					);
				}
			)
			dropDownChangeHtml = (
				<OverflowMenu selectorPrimaryFocus={'.'+ this.state.value} >
					{listOptions}
				</OverflowMenu>)
		}
		var tagHtml= (<div>{dropDownChangeHtml}</div>);
		
		for (var i in this.state.listOptions) {
			var option=this.state.listOptions[ i ];
			if (option.value === this.state.value) {				
				tagHtml = (<Tag  type={option.type} title={option.title}>
							{option.icon && <img src={option.icon} style={{width:30}} title={option.imgtitle}/>}
							{option.label && <div>{option.label}</div>}
							{dropDownChangeHtml}
						</Tag>)			
				
			}
		}
      	return tagHtml;
	};
};
export default TagDropDown;
