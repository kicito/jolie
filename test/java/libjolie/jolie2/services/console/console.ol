
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

type javaServiceParam: void{
	fromClient: portInfo
	toClient: portInfo
} 

decl service Java("joliex.io.ConsoleService") ConsoleService (outputPort fromClient, inputPort toClient) {
	inputPort IP {
		interfaces: IConsole
	}

	outputPort Receiver {
		interfaces: IReceiver
	}
}

decl service console () {
	inputPort ConsoleInput {
		location: "local:/MyConsoleInput"
		interfaces: IReceiver
	}
    embed ConsoleService ( "Console", ConsoleInput )

	main{
		println@Console("Hello")()
	}
}