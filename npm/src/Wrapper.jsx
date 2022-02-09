// -----------------------------------------------------------
//
// Wrapper
//
// Wrap language to dictionnary
//
// -----------------------------------------------------------

import React, {useState} from 'react';
import {IntlProvider} from 'react-intl';

import French 		from './lang/fr.json';
import English 		from './lang/en.json';


const Context = React.createContext();
const local = navigator.language;

let lang;
if (local === 'en') {
   lang = English;
}else if (local === 'fr') {
       lang = French;
} else {
   lang = English;
}

const Wrapper = (props) => {
   const [locale, setLocale] = useState(local);
   const [messages, setMessages] = useState(lang);
   function selectLanguage(e) {
       const newLocale = e.target.value;
       setLocale(newLocale);
       if (newLocale === 'en') {
           setMessages(English);
       } else if (newLocale === 'fr'){
               setMessages(French);
       }
       
   }
   return (
       <Context.Provider value = {{locale, selectLanguage}}>
           <IntlProvider messages={messages} locale={locale}>
               {props.children}
           </IntlProvider>
       </Context.Provider>
   );
}
export default Wrapper;