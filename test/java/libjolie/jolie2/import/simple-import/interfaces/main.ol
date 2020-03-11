from "modules/twiceInterface.ol" import TwiceInterface

inputPort TwiceService {
    Location: "socket://localhost:8000"
    Protocol: sodep
    Interfaces: TwiceInterface
}