decl service TwiceService(){ 
    
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