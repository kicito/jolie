// Printer.iol
interface PrinterInterface {
    OneWay: printText( string )
}

//dynamic_binding_example.ol

include "console.iol"

outputPort P {
    Location: "socket://127.0.0.1:80"
    Protocol: sodep
    Interfaces: PrinterInterface
}

main
{
    a = P.location;
    println@Console( P.location )();
    println@Console( P.protocol )()
}