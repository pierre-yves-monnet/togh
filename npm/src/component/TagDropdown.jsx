// -----------------------------------------------------------
//
// TagDropDown
//
// Display a tag and a dropdown
//
// -----------------------------------------------------------

import React from 'react';


import { Tag } from '@carbon/react';
import { OverflowMenu, OverflowMenuItem } from '@carbon/react';


class TagDropDown extends React.Component {
	
	
	// this.props.changeState();
	// gives :
	// - listOptions[ { "label", "value", "icon", "type", "imgtitle" }] : a list of state name + type + icon
	// type is a TagHtml type: "teal", "green", "red"...
	//  see https://www.carbondesignsystem.com/components/tag/usage
	// - value : the current value
	// - disabled[boolean]: a disabled
	// - changeState() method 
	constructor( props ) {
		super();
		// console.log("TagDropDown.constructor value="+props.value);

		this.state = { listOptions : props.listOptions,
					value : props.value,
					disabled: props.disabled}
	}


	componentDidUpdate(prevProps) {
        // let value=JSON.stringify(this.props);
        // console.log("TagDropDown.componentDidUpdate props=("+value+")");
   		if (prevProps.listOptions !== this.props.listOptions) {
            this.setState( {listOptions : this.props.listOptions });
        }
        if (prevProps.value !== this.props.value) {
            this.setState( {value : this.props.value });
        }
        if (prevProps.disabled !== this.props.disabled) {
            this.setState( {disabled: this.props.disabled});
        }
	}
	//----------------------------------- Render
	render() {
		// console.log("TagDropDown.render value="+this.state.value);
		let tagHtml = null;
		let dropDownChangeHtml = (<div/>);
		if (! this.state.disabled) {
			dropDownChangeHtml = (
				<OverflowMenu selectorPrimaryFocus={'.'+ this.state.value} >
					{this.state.listOptions.map( (item, index) =>
						{ return (
							<OverflowMenuItem className={item.value} 
								itemText={item.label}
								key={index}
								id={item.value}
								onClick={(event) => {
										this.setState( { value : item.value});
										this.props.changeState( item.value )}} />
							);
						}) }
				</OverflowMenu>)
				
		}
		// default value if the current option is not found: display the menu then
		tagHtml= (<div>{dropDownChangeHtml}</div>);
		
		for (let i in this.state.listOptions) {
			let option=this.state.listOptions[ i ];
			if (option.value === this.state.value) {				
				tagHtml = (<Tag  type={option.type} title={option.title}>
							{option.icon &&
							    <img src={option.icon}
							        style={{width:30}}
							        title={option.imgtitle}
							        alt={option.imgtitle}/>}
							{option.label && <div>{option.label}</div>}
							{dropDownChangeHtml}
						</Tag>)			
				
			}
		}
      	return tagHtml;
	}
}
export default TagDropDown;
