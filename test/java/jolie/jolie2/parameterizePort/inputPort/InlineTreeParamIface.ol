


// inputport with inlineTree, using Iface value type
inputPort parameterizeInputPortInlineTreeParamIface ({
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


main {
    notice(n)
    rr(req)(res){
        res = "hello"
    }
}