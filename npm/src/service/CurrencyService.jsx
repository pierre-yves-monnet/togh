// -----------------------------------------------------------
//
// Currency
//
// var currencyService = FactoryService.getInstance().getCurrencyService();
//
// -----------------------------------------------------------


const list_currencies = [
		{label: "US Dollar", prefix: "$", suffix: "", code: "USD"},
		{label: "Euro", prefix: "", suffix: "€", code:"EUR"},
		{label: "British Pound", prefix: "£", suffix: "", code:"GBP"},
		{label: "Moroccan Dirham", prefix: "", suffix: " DH", code:"MAD"},
		{label: "Peso", prefix: "$", suffix: "", code: "MXN"},
		{label: "Yen", prefix: "¥", suffix: "", code: "JPY"},
		{label: "Real", prefix: "R$", suffix: "", code: "BRL"},
		{label: "Won", prefix: "₩", suffix: "", code: "QRW"}
]

class CurrencyService {
	constructor(factoryService ) {
		console.log("CurrencyService: ------------ constructor ");
		this.token=null;
		this.factoryService = factoryService;
	}
	
	getCurrencyLabelList(){
		var table = [];
		list_currencies.foreach(element => table.push(element.label));
		return table;
	}
	
	getCurrencyList(){
		return list_currencies;
	}
	
	getCurrencyInfoByCode(code){
		for (var i in list_currencies){
			if (list_currencies[i].code === code){
				return list_currencies[i];
			}
		}
		return null;
	}
	
	getCurrencyInfoByLabel(label){
		for (var i in list_currencies){
			if (list_currencies[i].label === label){
				return list_currencies[i];
			}
		}
		return null;
	}
	
	
}
export default CurrencyService;