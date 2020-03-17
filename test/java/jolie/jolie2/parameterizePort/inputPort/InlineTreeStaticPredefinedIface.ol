type customType: void{
    x : int
    y : int
}
interface exIface {
    oneWay: notice(string)
    requestResponse: rr(customType)(string)
}

// inputport with inlineTree, using predefined interface
inputPort parameterizeInputPortInlineTreePredefinedIface ({
    location = "socket://localhost:5000"
    protocol = "sodep"
}){
    Interfaces: exIface
}


main {
    notice(n)
    rr(req)(res){
        res = "hello"
    }
}