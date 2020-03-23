decl service print( a : string ){
    main {
        b = a
    }
}

decl service simple_embed(){ 

    embed print("hello world")

    main{
        a = 2
    }
}