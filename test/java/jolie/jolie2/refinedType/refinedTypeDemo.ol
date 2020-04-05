
// this approach is based on https://json-schema.org/

// Jolie adaptation: 
// syntax:
// 
// refinedType ::= type [name] : [NativeType] '(' [refinedTypeRule | ',' refinedTypeRule]* ')'
// refinedTypeRule ::= rulename '(' [ruleArgs | ',' ruleArgs]* ')'
// ruleArgs ::= expresion | refinedTypeRule
// rulename ::= ID


//----- string type -----

// check function set a default value of string if it is undefined
type defaultPort: string(
    default("3000")
)

// check function validate a string with length == 3
type stringWLength : string(
    minLength(3),
    maxLength(3)
)


// check validate string with regex pattern 
type stringWRegex: string(
   pattern("^local://.*")
)


//----- numbers type -----

// default for int
type factor: int(
    default(2)
)


// int range
// If x is the value being validated, the following must hold true:
// x ≥ minimum
// x > exclusiveMinimum
// x ≤ maximum
// x < exclusiveMaximum

// this example validate int of type portRange between 3000-3100
type portRange: int(
    minimum(3000),
    exclusiveMaximum(3101)
)

// boolean type

// default value
type defaultTrue : boolean(default(true))


// combination of rules
// rule 'or' return true when one of the rule set is valid. 
type combRule : string(
    or(
        set("socket://localhost:3001", "socket://localhost:3002"),
        foo(".....") // custom rule from external extension
    )
)



// example for Jolie 2.0's service

// twiceService.ol
interface twiceIface{
    RequestResponse: twice(int)(int)
}

type twiceParam: void{
    .location: string(
        default("socket://localhost:3000")
    )
    .protocol: string(default("sodep"))
    .interfaces: string(pattern("^twiceIface$")) // receiving interface name has to be exact "twiceIface"
}

decl service twiceService(param : twiceParam){
    inputPort myPort(param)

    main{
        twice(req)(res){
            res = req * 2
        }
    }
}


// twiceClient.ol
from "./twiceService.ol" import twiceIface, twiceService

decl service twiceClient{
    
    // parameterize port
    outputPort op ({
        location = "socket://localhost:8080"
        protocol = "http"
        interfaces << "twiceIface" 
        // during OOITbuilder.build, the visitor search for 'twiceIface' interface and assign it's operation to the OutputPort
    })

    embed twiceService({
        location = "socket://localhost:8080"
        protocol = "http"
        interfaces << "twiceIface"
    })

    main{
        twice@op(2)(res)
    }
}