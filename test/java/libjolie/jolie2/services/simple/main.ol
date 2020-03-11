decl service main(portInfo info){ init { a = 1 } main { a=2 } }
decl service main(string a){ init { a = 1 } main { a=2 } }
decl service main(){ init { a = 1 } main { a=2 } }
decl service main{ init { a = 1 } main { a=2 } }

decl service Java("joliex.io.ConsoleService") Console { main{ a=2 } }
