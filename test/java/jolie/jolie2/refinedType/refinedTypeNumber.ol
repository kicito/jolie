// type three : int( minimum(3), maximum(3) )
// type threeD : double( minimum(3), maximum(3) )
// type threeDefault : double( default(3) )

type a : string {
    .b : string(default("b"))
}

main{
    // t = 2 instanceof three
    // t = 3 instanceof three
    // t = 4 instanceof three
    // t = 4.0 instanceof threeD
    // t = 3.0 instanceof threeD
    // t = "hello" instanceof three
    // t = tf instanceof threeDefault
    val = "aa"

    t = val instanceof a
}