type customType: void{
    x : int
    y : int
}
interface exIface {
    oneWay: notice(string)
    requestResponse: rr(customType)(string)
}

// outputport with inlineTree, using Iface value type
outputPort parameterizeInputPortInlineTreePredefinedIface ({
    location = "socket://localhost:5000"
    protocol = "sodep"
    interfaces << "exIface"
})

main{
    notice@parameterizeInputPortInlineTreePredefinedIface("hello")
    rr@parameterizeInputPortInlineTreePredefinedIface(void{x=1 y=2})(res)
}