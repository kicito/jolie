import Console from "modules/Console.ol"

decl service main (){
    embed Console {
        bindIn: IP -> Console 
    }
    
    main {
        println@Console("Hello")()
    }
}
