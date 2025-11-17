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

from string_utils import StringUtils
from console import Console
from time import Time

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
    RequestResponse: metrics(void)(string)
}

service PrometheusMonitor {

    execution { concurrent }

    inputPort MetricsHttpPort {
        Location: "socket://localhost:9400"
        Protocol: http {
            .format = "html";
            .contentType = "text/plain; version=0.0.4; charset=utf-8";
            .statusCode -> statusCode;
            debug = true
            debug.showContent = true
            default = "metrics"
        }
        Interfaces: HttpInterface
    }

    embed Console as Console
    embed Time as Time
    embed Monitor as Monitor

    init {
    // Counters: metrics.<name>.<label_hash>.{count, labels}
    global.operationsStarted << {}
    global.operationsEnded << {}
    global.sessionCounter << {}
    global.outgoingCalls << {}

    // Gauge
    global.sessionsActive = 0L
    
    // Histogram: duration.<label_hash>.{count, sum, buckets}
    global.operationDurations << {}

    global.startTimes << {}
    
    // Config
    global.config = {
        .port = 9400
        .trackProcessId = false
        .maxTrackedOps = 10000
    }
    println@Console("Prometheus Monitor started on port " + config.port)()
    }

    main {
    [ pushEvent(event) ] {
        eventType = event.type;
        
        if (eventType == "OperationStarted") {
            with (event) {
                opName = .operationName;
                processId = .processId;
                messageId = .messageId;
                
                // Build correlation key
                if (config.trackProcessId) {
                    correlationKey = processId + ":" + messageId
                } else {
                    correlationKey = messageId
                };
                
                // Store start time
                getCurrentTimeMillis@Time()(now);
                global.startTimes.(correlationKey) << {
                    .startTimeNanos = now * 1000000L,  // Convert to nanos
                    .operationName = opName,
                    .type = "requestresponse"
                };
                
                // Increment counter
                labelKey = opName + "|requestresponse";
                synchronized(operationsStartedLock) {
                    if (!is_defined(global.operationsStarted.(labelKey))) {
                        global.operationsStarted.(labelKey) << {
                            .count = 0L,
                            .labels.operation = opName,
                            .labels.type = "requestresponse"
                        }
                    };
                    global.operationsStarted.(labelKey).count++
                }
            }
        }
        else if (eventType == "OperationEnded") {
            with (event) {
                opName = .operationName;
                processId = .processId;
                messageId = .messageId;
                statusCode = .status;
                
                // Map status
                if (statusCode == 0) { status = "success" }
                else if (statusCode == 1) { status = "fault" }
                else if (statusCode == 2) { status = "error" }
                else { status = "unknown" };
                
                // Correlation key
                if (config.trackProcessId) {
                    correlationKey = processId + ":" + messageId
                } else {
                    correlationKey = messageId
                };
                
                // Calculate duration
                if (is_defined(global.startTimes.(correlationKey))) {
                    getCurrentTimeMillis@Time()(endTime);
                    durationNanos = (endTime * 1000000L) - global.startTimes.(correlationKey).startTimeNanos;
                    durationSeconds = double(durationNanos) / 1000000000.0;
                    
                    // Record in histogram
                    labelKey = opName + "|requestresponse|" + status;
                    synchronized(operationDurationsLock) {
                        if (!is_defined(global.operationDurations.(labelKey))) {
                            global.operationDurations.(labelKey) << {
                                .count = 0L,
                                .sum = 0.0,
                                .labels.operation = opName,
                                .labels.type = "requestresponse",
                                .labels.status = status
                            }
                        };
                        global.operationDurations.(labelKey).count++;
                        global.operationDurations.(labelKey).sum = global.operationDurations.(labelKey).sum + durationSeconds
                    };
                    
                    // Remove from tracking
                    undef(global.startTimes.(correlationKey))
                };
                
                // Increment ended counter
                labelKey = opName + "|requestresponse|" + status;
                synchronized(operationsEndedLock) {
                    if (!is_defined(global.operationsEnded.(labelKey))) {
                        global.operationsEnded.(labelKey) << {
                            .count = 0L,
                            .labels.operation = opName,
                            .labels.type = "requestresponse",
                            .labels.status = status
                        }
                    };
                    global.operationsEnded.(labelKey).count++
                }
            }
        }
        else if (eventType == "SessionStarted") {
            opName = event.operationName;
            labelKey = opName;
            synchronized(sessionCounterLock) {
                if (!is_defined(global.sessionCounter.(labelKey))) {
                    global.sessionCounter.(labelKey) << {
                        .count = 0L,
                        .labels.operation = opName
                    }
                };
                global.sessionCounter.(labelKey).count++
            };
            synchronized(sessionsActiveLock) {
                global.sessionsActive++
            }
        }
        else if (eventType == "SessionEnded") {
            synchronized(sessionsActiveLock) {
                global.sessionsActive--
            }
        }
        else if (eventType == "OperationCall") {
            with (event) {
                opName = .operationName;
                outPort = .outputPort;
                statusCode = .status;
                
                if (statusCode == 0) { status = "success" }
                else { status = "fault" };
                
                labelKey = opName + "|" + outPort + "|" + status;
                synchronized(outgoingCallsLock) {
                    if (!is_defined(global.outgoingCalls.(labelKey))) {
                        global.outgoingCalls.(labelKey) << {
                            .count = 0L,
                            .labels.operation = opName,
                            .labels.output_port = outPort,
                            .labels.status = status
                        }
                    };
                    global.outgoingCalls.(labelKey).count++
                }
            }
        }
    }
    
    [ getMetrics()(response) {
        formatPrometheusMetrics
        response = output
    } ]
    
    [ metrics()(response) {
        formatPrometheusMetrics
        statusCode = 200;
        response = output
    } ]
    
    [ setMonitor(cfg)() {
        if (is_defined(cfg.port)) { config.port = cfg.port };
        if (is_defined(cfg.trackProcessId)) { config.trackProcessId = cfg.trackProcessId };
        if (is_defined(cfg.maxTrackedOps)) { config.maxTrackedOps = cfg.maxTrackedOps }
    } ]
    
    [ getMonitorConfig()(response) {
        response << config;
        response.trackedOperations = #global.startTimes
    } ]
    }

    define formatPrometheusMetrics {
    output = "";
    
    // Operations started
    output += "# HELP jolie_operations_started_total Total number of operations started\n";
    output += "# TYPE jolie_operations_started_total counter\n";
    foreach (key : global.operationsStarted) {
        m -> global.operationsStarted.(key);
        output += "jolie_operations_started_total{operation=\"" + m.labels.operation + 
                  "\",type=\"" + m.labels.type + "\"} " + m.count + "\n"
    };
    
    // Operations ended
    output += "# HELP jolie_operations_ended_total Total number of operations ended\n";
    output += "# TYPE jolie_operations_ended_total counter\n";
    foreach (key : global.operationsEnded) {
        m -> global.operationsEnded.(key);
        output += "jolie_operations_ended_total{operation=\"" + m.labels.operation + 
                  "\",type=\"" + m.labels.type + "\",status=\"" + m.labels.status + "\"} " + m.count + "\n"
    };
    
    // Operation duration
    output += "# HELP jolie_operation_duration_seconds Operation execution duration\n";
    output += "# TYPE jolie_operation_duration_seconds histogram\n";
    foreach (key : global.operationDurations) {
        m -> global.operationDurations.(key);
        output += "jolie_operation_duration_seconds_count{operation=\"" + m.labels.operation + 
                  "\",type=\"" + m.labels.type + "\",status=\"" + m.labels.status + "\"} " + m.count + "\n";
        output += "jolie_operation_duration_seconds_sum{operation=\"" + m.labels.operation + 
                  "\",type=\"" + m.labels.type + "\",status=\"" + m.labels.status + "\"} " + m.sum + "\n"
    };
    
    // Active sessions
    output += "# HELP jolie_sessions_active Number of active sessions\n";
    output += "# TYPE jolie_sessions_active gauge\n";
    output += "jolie_sessions_active " + global.sessionsActive + "\n";
    
    // Session counter
    output += "# HELP jolie_sessions_total Total sessions started\n";
    output += "# TYPE jolie_sessions_total counter\n";
    foreach (key : global.sessionCounter) {
        m -> global.sessionCounter.(key);
        output += "jolie_sessions_total{operation=\"" + m.labels.operation + "\"} " + m.count + "\n"
    };
    
    // Outgoing calls
    if (#global.outgoingCalls > 0) {
        output += "# HELP jolie_outgoing_calls_total Total outgoing operation calls\n";
        output += "# TYPE jolie_outgoing_calls_total counter\n";
        foreach (key : global.outgoingCalls) {
            m -> global.outgoingCalls.(key);
            output += "jolie_outgoing_calls_total{operation=\"" + m.labels.operation + 
                      "\",output_port=\"" + m.labels.output_port + "\",status=\"" + m.labels.status + "\"} " + m.count + "\n"
        }
    }
    }
}
