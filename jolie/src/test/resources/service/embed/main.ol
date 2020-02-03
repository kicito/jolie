include "console.iol"

decl service twiceService(){ 
    inputPort TwiceService {
        Location: "socket://localhost:8000"
        Protocol: sodep
        RequestResponse: twice
    }

    main
    {
        twice( number )( result ) {
            result = number * 2
        }
    } 
}


decl service main(){ 

    embed twiceService {
        bindIn: TwiceService -> tw // an outputPort tw should be created at OOITBuilder.build
    }
    
    main {
        twice@tw(5)(res);
        println@Console(res)()
    }
}
