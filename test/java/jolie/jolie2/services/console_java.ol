
type EnableTimestampRequest: bool {
	format?: string
}

type RegisterForInputRequest {
	enableSessionListener?: bool
}

type SubscribeSessionListener {
	token: string
}

type UnsubscribeSessionListener {
	token: string
}

type InRequest: string {
	token?: string
}

interface IConsole {
RequestResponse:
	print(undefined)(void), 

	println(undefined)(void), 
	
	/**!
	*	It enables timestamp inline printing for each console output operation call: print, println
	*	Parameter format allows to specifiy the timestamp output format. Bad Format will be printed out if format value is not allowed.
	*/
	enableTimestamp(EnableTimestampRequest)(void),

	/**!
	*  it enables the console for input listening
	*  parameter enableSessionListener enables console input listening for more than one service session (default=false)
	*/
	registerForInput(RegisterForInputRequest)(void),

	/**!
	* it receives a token string which identifies a service session.
	* it enables the session to receive inputs from the console
	*/
	subscribeSessionListener(SubscribeSessionListener)(void),
	
	/**!
	* it disables a session to receive inputs from the console, previously registered with subscribeSessionListener operation
	*/
	unsubscribeSessionListener(UnsubscribeSessionListener)(void)
}

interface IReceiver {
	OneWay: in(InRequest)
}

type consoleParam: void{
	fromClient : void {
		.location: string
		// .interfaces: string(pattern("^IConsole$"))
	}
	toClient : void {
		.location: string
		.interfaces: string(pattern("^IReceiver$"))
	}
}

decl service Java("joliex.io.ConsoleService") ConsoleService ( p:consoleParam ) {

	inputPort IP (p.fromClient)

	outputPort Receiver (p.toClient)

	inputPort ConsoleInputPort {
		Location: "local"
		Interfaces: IReceiver
	}

	// execution { concurrent }
	
    main{
		[in(incoming)]{
			in@Receiver(incoming)
		}
    }
}

decl service console_java() {
	
	outputPort ConsoleServicePort {
		location: "local://ConsoleJava/"
		interfaces: IConsole
	}

	inputPort ConsoleInput {
		location: "local://ConsoleJavaIn"
		interfaces: IReceiver
	}

    embed ConsoleService ( { 
        fromClient << {
			location = "local://ConsoleJava"
			// interfaces << "IConsole"
        }
        toClient << {
			location = "local://ConsoleJavaIn"
			interfaces << "IReceiver"
        }
    })

	main{
		// nullProcess
		println@ConsoleServicePort("Hello")()
		println@ConsoleServicePort("Hello2")()
		// registerForInput@ConsoleServicePort()()
		// in(req)
		// println@ConsoleServicePort("receive "+ req)()
	}
}