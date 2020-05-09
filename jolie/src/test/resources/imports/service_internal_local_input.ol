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
        location : "local"
    }

    outputPort ServiceOp {
        oneWay: notice(string)
        protocol: someparam.proto
        location: someparam.loc
    }

    main {
        println@Console("hello from someservice")()
        println@Console("receive " + someparam.loc + ", " +someparam.proto + " as parameter value")()
        twice(a)(a*2)
        notice@ServiceOp("hello from serviceNode")
    }
}