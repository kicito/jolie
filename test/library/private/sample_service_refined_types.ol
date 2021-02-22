from console import Console
from library.private.SampleRefinedTypesInterface import TmpInterface

service TestRefinedTypes {

  embed Console as Console

  inputPort TPort {
    Location: "socket://localhost:9000"
    Protocol: sodep
    Interfaces: TmpInterface
  }

  main {
    tmp()( response ) {
      response.field = "test";
      print@Console("")()
    }
  }
}
