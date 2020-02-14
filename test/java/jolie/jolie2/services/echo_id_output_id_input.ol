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


decl service echo_id_output_id_input(){ 

    inputPort echoServiceReceiver {
        location: "socket://localhost:4000"
        protocol: sodep
        OneWay: resp
    }

    outputPort req {
        location: "socket://localhost:9000"
        Protocol: sodep
        OneWay: recv
    }

    // create new output port name 'req' with ops from service
    // binding OP@echoService to echoServiceReceiver here (set location)
    // embed echoService ("req", echoServiceReceiver) 

    // set location and protocol of IP@echoService to req here
    // binding OP@echoService to echoServiceReceiver here (set location)
    embed echoService (req, echoServiceReceiver) 

    main {
        recv@req("Hello");
        resp(res)
    }
}
