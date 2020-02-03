include "console.iol"
decl service main(){ 
    init{ 
        a = 1 
    }
    main{
        a=2;
        println@Console(a)()
    }
}