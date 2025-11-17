/***************************************************************************
 *   Copyright (C) 2025 Jolie Team <jolie-dev@googlegroups.com>          *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Library General Public License as       *
 *   published by the Free Software Foundation; either version 2 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU Library General Public     *
 *   License along with this program; if not, write to the                 *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 *                                                                         *
 *   For details about the authors of this software, see the AUTHORS file. *
 ***************************************************************************/

from console import Console
from string_utils import StringUtils

interface MonitorInterface {
OneWay:
	pushEvent(undefined)
}

type PrometheusMonitorConfig: void {
	.trackProcessId?: bool    // Include processId in correlation key (default: false)
	.maxTrackedOps?: int      // Max number of operations to track (default: 10000)
}

type PrometheusMonitorConfigResponse: void {
	.trackProcessId: bool
	.maxTrackedOps: int
	.trackedOperations: int   // Current number of tracked operations
}

interface PrometheusMonitorInterface {
RequestResponse:
	setMonitor(PrometheusMonitorConfig)(void),
	getMonitorConfig(void)(PrometheusMonitorConfigResponse),
	getMetrics(void)(string)
}

service Monitor {
    inputPort ip {
        location:"local"
        interfaces: MonitorInterface, PrometheusMonitorInterface
    }

    foreign java {
        class: "joliex.monitoring.PrometheusMonitor"
    }
}

interface HttpInterface {
    RequestResponse: metrics(undefined)(string)
}

interface MonitorAddress {
    RequestResponse: getMonitor(void)(undefined)
}

service PrometheusMonitor {

    execution { concurrent }

    inputPort MetricsHttpPort {
        Location: "socket://localhost:9400"
        Protocol: http {
            .format = "html";
            .contentType = "text/plain; version=0.0.4; charset=utf-8";
            .statusCode -> statusCode;
            default = "metrics"
        }
        Interfaces: HttpInterface
    }

    inputPort mon {
        Location: "local"
        Interfaces: MonitorAddress
    }

    embed Console as Console
    embed Monitor as Monitor
    embed StringUtils as StringUtils

    init {
        println@Console("Prometheus Monitor started on port 9400")()
        println@Console(valueToPrettyString@StringUtils( Monitor ))()
    }

    main {
        [ metrics()(response) {
            getMetrics@Monitor()(response);
            statusCode = 200
        } ]
        [ getMonitor()(response) {
            response << Monitor
        } ]
    }
}
