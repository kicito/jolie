from console import Console 
from time import Time 
from monitors.prometheus_monitor import PrometheusMonitor


// Enable monitoring with the Prometheus monitor
constants {
    Location_Monitor = "local"
}

// Simple test service
type TestRequest: void {
    .value: int
}

type TestResponse: void {
    .result: int
}

interface TestInterface {
RequestResponse:
    compute(TestRequest)(TestResponse),
    divide(TestRequest)(TestResponse) throws DivisionByZero(string)
}

service TestService {
    execution: concurrent

    inputPort TestService {
        Location: "socket://localhost:8080"
        Protocol: http {
            .format = "json"
        }
        Interfaces: TestInterface
    }

    embed Console as Console
    embed Time as Time
    embed PrometheusMonitor

    init {
        println@Console("Test service with Prometheus monitoring started")();
        println@Console("Service: http://localhost:8080")();
        println@Console("Metrics: http://localhost:9400/metrics")()
    }

    main {
        [ compute(request)(response) {
            sleep@Time(1000)();  // Simulate work
            response.result = request.value * 2
        } ]
        
        [ divide(request)(response) {
            if (request.value == 0) {
                throw(DivisionByZero, "Cannot divide by zero")
            };
            sleep@Time(50)();
            response.result = 100 / request.value
        } ]
    }
}
