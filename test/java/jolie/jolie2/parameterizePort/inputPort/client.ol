type customType: void{
    x : int
    y : int
}

interface exIface {
    oneWay: notice(string)
    requestResponse: rr(customType)(string)
}

outputPort op {
    location:"socket://localhost:5000"
    protocol:sodep
    Interfaces:exIface
}

main{
    notice@op("hello")
    rr@op(void{x=1 y=2})(res)
}