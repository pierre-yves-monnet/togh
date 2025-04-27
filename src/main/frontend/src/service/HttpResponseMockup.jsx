// -----------------------------------------------------------
//
// HttpResponseMockup
//
// Exact signature as the HttpResponse, but mokup it
// -----------------------------------------------------------
class HttpResponseMockup {
    
    constructor( mockupData ) {
        this.mockupData = mockupData; 
    };
    
    isError() {
       return false;
    }   
    getData() {
        return this.mockupData;
    }
    getStatus() {
        return 200;
    }
    trace( label ) {
       console.log("HttpResponseMockup: "+label+" NoError");
    }
}   
export default HttpResponseMockup;

