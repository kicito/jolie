import twiceIface from "./twiceIface.ol"


// parameter type doesn't need to be import for running a service, since the type definition is embed to the service ast node
type twiceParam: void{
    .location: string(
        default("socket://localhost:3000")
    )
    .protocol: string(default("sodep"))
    .interfaces: string(pattern("^twiceIface$")){} // receiving interface name has to be exact "twiceIface"
}

// exporting main service become default export target
// if server.ol is the execution root, main will be the target execution service
// unexported main service may be used for declaring module specific cases
export service main( p:twiceParam ){ 
    inputPort myPort(param)

    main{
        twice(req)(res){
            res = req * 2
        }
    }
}