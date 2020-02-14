decl service helloService( inputPort toClient ){ 

	outputPort OP {
        oneWay: resp
	}

    binding {
        OP -> toClient
    }

    main
    {
        resp@OP("hello")
    }
}


decl service echoliteral(){ 
    
    embed helloService ({
        location: "socket://localhost:4000"
        protocol: sodep
        oneWay: resp
    })

    main {
        resp(res)
    }
}
