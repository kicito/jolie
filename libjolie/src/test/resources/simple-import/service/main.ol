import Console from "modules/console.ol"

decl service main (){
    embed Console {
        bindIn: IP -> Console 
    }
    
    main {
        println@Console("Hello")()
    }
}
