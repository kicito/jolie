include "console.iol"
from .service_internal_local_input import someservice, twiceIface

service main (){

    outputPort OP {
        interfaces: twiceIface
    }

    inputPort IP {
        oneWay: notice(string)
        protocol: sodep
        location: "socket://localhost:8000"
    }

    embed someservice({loc="socket://localhost:8000" proto="sodep"}) in OP

    main{
        println@Console("hello!")()
        twice@OP(2)(res)
        println@Console( "result is " + res)()
        println@Console( "waiting for embeder to call notice")()
        notice(recv)
        println@Console( "receive " + recv)()
    }
}