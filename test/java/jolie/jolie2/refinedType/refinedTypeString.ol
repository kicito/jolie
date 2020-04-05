type zipCode : string( 
  minLength(4),
  maxLength(4) 
  )
type localP : string( pattern("^local://.*") )

main{
    t = "5260" instanceof zipCode
    t = "5260dd" instanceof zipCode
    t = "526" instanceof zipCode
    t = "local://consoleIn" instanceof localP
    t = "socket://localhost:8080" instanceof localP
}