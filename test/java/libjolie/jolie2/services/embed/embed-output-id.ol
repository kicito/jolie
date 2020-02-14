
decl service twiceService(outputPort fromClient){ 
    inputPort TwiceService {
        Location: "socket://localhost:8000"
        Protocol: sodep
        RequestResponse: twice(int)(int)
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

    outputPort tw{
        Location: "socket://localhost:8000"
        Protocol: http
        RequestResponse: twice(int)(int)
    }

    embed twiceService (tw)
    
    main {
        a=2
    }
}
