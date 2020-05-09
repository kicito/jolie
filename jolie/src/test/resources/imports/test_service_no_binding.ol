include "console.iol"
from .service import someservice, twiceIface

service main (){

    outputPort OP {
        interfaces: twiceIface
        protocol: sodep
        location: "socket://localhost:3000"
    }

    inputPort IP {
        oneWay: notice(string)
        protocol: sodep
        location: "socket://localhost:8000"
    }

    embed someservice({loc="socket://localhost:3000" proto="sodep"})

    main{
        println@Console("hello!")()
        twice@OP(2)(res)
        println@Console( "result is " + res)()
        println@Console( "waiting for embeder to call notice")()
        notice(recv)
        println@Console( "receive " + recv)()
    }
}