decl service twiceService( outputPort tw ){ 
    inputPort TwiceService {
        Location: "socket://localhost:8000"
        Protocol: sodep
        RequestResponse: twice
    }

    binding {
        TwiceService -> tw
    }

    main
    {
        twice( number )( result ) {
            result = number * 2
        }
    }
}


decl service twice(){ 

    embed twiceService ("tw")
    
    main {
        twice@tw(5)(res)
    }
}
