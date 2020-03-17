


type customType: void{
    x : int
    y : int
}

// inputport with inlineTree, using Iface value type
outputPort parameterizeInputPortInlineTreeParamIface ({
    location = "socket://localhost:5000"
    protocol = "sodep"
    interfaces << "paramIface"{
        operations[0] << "notice"{
            reqType = "string"
        }
        operations[1] << "rr"{
            reqType = "customType"
            resType = "string"
        }
    }
})


main{
    notice@parameterizeInputPortInlineTreeParamIface("hello")
    rr@parameterizeInputPortInlineTreeParamIface(void{x=1 y=2})(res)
}