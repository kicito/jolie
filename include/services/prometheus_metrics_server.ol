/***************************************************************************
 *   Copyright (C) 2025 Jolie Team <jolie-dev@googlegroups.com>          *
 *                                                                         *
 *   Prometheus Metrics HTTP Server                                        *
 *   Exposes Prometheus metrics at /metrics endpoint                       *
 *                                                                         *
 *   Usage:                                                                *
 *     jolie prometheus_metrics_server.ol [port]                          *
 *                                                                         *
 *   Default port: 9400                                                    *
 ***************************************************************************/

from console import Console
from monitors.prometheus_monitor import Monitor

interface MetricsInterface {
RequestResponse:
    metrics(void)(string)
}

service PrometheusMetricsServer {
    execution: concurrent
    
    inputPort MetricsPort {
        location: "socket://localhost:9400"
        protocol: http {
            format = "html"
            contentType = "text/plain; version=0.0.4; charset=utf-8"
            default = "metrics"
        }
        interfaces: MetricsInterface
    }
    
    main {
        [ metrics(request)(response) {
            // Get metrics from PrometheusMonitor in text format
            getMetrics@Monitor()(response)
        } ]
    }
    
    init {
        println@Console("Prometheus metrics server started on port 9400")()
        println@Console("Metrics available at http://localhost:9400/metrics")()
    }
}
