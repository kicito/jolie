
interface twiceIface{
    requestResponse: twice(int)(int)
}

service someservice (someparam : string)  {
    
    inputPort IP {
        interfaces : twiceIface
        location: "socket://localhost:3000"
        protocol: sodep
    }

    main {
        twice(a)(2)
    }
}