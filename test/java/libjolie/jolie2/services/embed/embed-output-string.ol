
decl service twiceService(outputPort fromClient){ 
    inputPort TwiceService {
        Location: "socket://localhost:8000"
        Protocol: sodep
        RequestResponse: twice
    }

    binding{
        TwiceService -> fromClient
    }

    main
    {
        twice( number )( result ) {
            result = number * 2
        }
    }
}


decl service embed(){ 

    embed twiceService ("tw")
    
    main {
        a=2
    }
}
