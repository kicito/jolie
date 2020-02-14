decl service echoService( outputPort fromClient, inputPort toClient ){ 

	inputPort IP {
        OneWay: recv
        location: "socket://localhost:10000"
        Protocol: sodep
	}

	outputPort OP {
        OneWay: resp
	}

    binding {
        IP -> fromClient
        OP -> toClient
    }

    main
    {
        [recv( req )] {
            resp@OP(req)
        }
    }
}


decl service echoliteral(){ 
    
    embed echoService ("req", {
        location: "socket://localhost:4000"
        protocol: sodep
        OneWay: resp
    })

    main {
        a=1
    }
}
