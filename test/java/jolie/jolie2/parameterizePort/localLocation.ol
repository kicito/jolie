include "runtime.iol"

type reqType: void{
    x: int
    y: int
}

interface LocalOperations{
    RequestResponse:
        doubleLocal( reqType )( int )
}

outputPort SelfOut {
    Interfaces: LocalOperations
}

inputPort SelfIn ({location= "local"}){
    Interfaces: LocalOperations
}

interface ExternalOperations{
    RequestResponse:
        doubleExt( reqType )( int )
}


inputPort ExternalPort {
    Location: "socket://localhost:8000"
    Protocol: http{
        .format = "html"
    }
    Interfaces: ExternalOperations
}


execution { concurrent }

init
{
    getLocalLocation@Runtime()( SelfOut.location )
}

main{
    [ doubleExt( request )( response ){
        doubleLocal@SelfOut( request )( subRes );
        response = subRes
    }]

    [ doubleLocal( request )( response) {
        response = request.x + request.y
    }]
}