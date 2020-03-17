decl service print(string a){
    main {
        b = a
    }
}

decl service simple_embed(){ 

    embed print("hello world")

    main{
        a=2
    }
}