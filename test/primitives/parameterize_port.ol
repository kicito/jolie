interface iface{
    RequestResponse: twice(int)(int)
}

// outputPort out {
//     location: "local://out"
//     protocol: "sodep"
//     interfaces: iface
// }

outputPort in {
    location: "local://out"
    protocol: "sodep"
    interfaces: iface
}

main{
    [twice(req)(res){
        res = req*s
    }]
    // twice@out(2)(res)
}