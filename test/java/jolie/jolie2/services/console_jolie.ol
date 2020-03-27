

type configParam: void{
    fromClient : void {
		location: string
		interfaces: interfaces
	}
}

interface ConsoleInterface{
	RequestResponse: println(string)(void)
}

decl service Console( config: configParam ){

    inputPort myInput( config.fromClient )
	// {
	// 	interfaces:ConsoleInterface
	// }

    // outputPort RemoteConsole { ... }

    main{
        println( msg )(){
			mssage = msg
            // println@RemoteConsole( msg )()

        }
    }
}

decl service console_jolie {

    outputPort ConsolePort { 
		location: "local://Console"
		interfaces: ConsoleInterface 
	}

    embed Console( {
		fromClient << { 
			location = "local://Console"
			interfaces << "printIface" { 
				operations << "println"{
                    reqType = "string"
                    resType = "void"
				}
			}
		}
	} )

    main {
        println@ConsolePort( "hi" )()
    }
}