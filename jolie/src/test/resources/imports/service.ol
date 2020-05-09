include "console.iol"

interface twiceIface{
    requestResponse: twice(int)(int)
}

type serviceParam: void{
    loc: string
    proto: string
}

service someservice (someparam : serviceParam)  {
    
    inputPort IP {
        interfaces : twiceIface
        protocol: someparam.proto
        location: someparam.loc
    }

    outputPort ServiceOp {
        oneWay: notice(string)
        protocol: sodep
        location: "socket://localhost:8000"
    }

    main {
        println@Console("hello from someservice")()
        println@Console("receive " + someparam + " as parameter value")()
        twice(a)(a*2)
        notice@ServiceOp("hello from serviceNode")
    }
}