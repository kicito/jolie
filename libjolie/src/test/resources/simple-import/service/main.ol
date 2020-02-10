import Console from "modules/Console.ol"

decl service main (){
    embed Console ("Console")
    
    main {
        println@Console("Hello")()
    }
}
