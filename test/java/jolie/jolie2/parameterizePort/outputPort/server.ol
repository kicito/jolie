

type customType: void{
    x : int
    y : int
}

interface exIface {
    oneWay: notice(string)
    requestResponse: rr(customType)(string)
}

// jolie 1 inputport
inputPort staticInputPort {
    location: "socket://localhost:5000"
    protocol: sodep
    interfaces: exIface
}

main {
    notice(n)
    rr(req)(res){
        res = "hello"
    }
}