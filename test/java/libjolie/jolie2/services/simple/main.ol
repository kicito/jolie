decl service main(info : portInfo ){ init { a = 1 } main { a=2 } }
decl service main(a:string ){ init { a = 1 } main { a=2 } }
decl service main(){ init { a = 1 } main { a=2 } }
decl service main{ init { a = 1 } main { a=2 } }

decl service Java("joliex.io.ConsoleService") Console { main{ a=2 } }
