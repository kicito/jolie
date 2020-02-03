
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

decl service Java Console ("joliex.io.ConsoleService") {
	inputPort IP {
		interfaces: IConsole
	}
}


decl service console (){
    embed Console {
        bindIn: IP -> Console 
    }
    
    main {
        println@Console("Hello")()
    }
}