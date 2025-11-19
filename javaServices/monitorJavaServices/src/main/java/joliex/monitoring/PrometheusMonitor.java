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

package joliex.monitoring;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter;
import jolie.monitoring.MonitoringEvent;
import jolie.runtime.AndJarDeps;
import jolie.runtime.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.management.OperatingSystemMXBean;

/**
 * Prometheus monitor implementation that converts Jolie monitoring events into Prometheus metrics.
 * Exposes metrics in Prometheus text format for scraping.
 *
 * Tracks: - Operation counts (started, ended) with status labels - Operation duration histograms -
 * Active sessions - Outgoing call metrics
 *
 * @author Jolie Team
 */
@AndJarDeps( {
	"prometheus-metrics-config.jar",
	"prometheus-metrics-core.jar",
	"prometheus-metrics-exposition-formats.jar", "prometheus-metrics-model.jar",
	"prometheus-metrics-shaded-protobuf.jar", "prometheus-metrics-tracer-common.jar",
	"prometheus-metrics-tracer-initializer.jar", "prometheus-metrics-tracer-otel-agent.jar",
	"prometheus-metrics-tracer-otel.jar"
} )
public class PrometheusMonitor extends AbstractMonitorJavaService {

	// Configuration
	private boolean trackProcessId = false;
	private int maxTrackedOperations = 10000;

	// Metrics registry
	private final PrometheusRegistry registry;

	// Metrics
	private final Counter operationsStartedTotal;
	private final Counter operationsEndedTotal;
	private final Histogram operationDuration;
	private final Counter sessionsTotal;
	private final Gauge sessionsActive;
	private final Counter outgoingCallsTotal;
	private final Counter outgoingRepliesTotal;
	private final Histogram outgoingCallDuration;
	private final Counter protocolMessagesTotal;
	private final Histogram protocolMessageSize;
	private final Histogram sessionDuration;
	private final Counter operationFaultsByType;

	// State tracking for duration calculations
	private final Map< String, OperationStartInfo > operationStartTimes;
	private final Map< String, OutgoingCallStartInfo > outgoingCallStartTimes;
	private final Map< String, SessionStartInfo > sessionStartTimes;

	// Holds start time and metadata for correlating operation start/end events
	private static class OperationStartInfo {
		final long startTimeNanos;
		final String operationName;
		final String type;

		OperationStartInfo( long startTimeNanos, String operationName, String type ) {
			this.startTimeNanos = startTimeNanos;
			this.operationName = operationName;
			this.type = type;
		}
	}

	// Holds start time and metadata for correlating outgoing call/reply events
	private static class OutgoingCallStartInfo {
		final long startTimeNanos;
		final String operationName;
		final String outputPort;

		OutgoingCallStartInfo( long startTimeNanos, String operationName, String outputPort ) {
			this.startTimeNanos = startTimeNanos;
			this.operationName = operationName;
			this.outputPort = outputPort;
		}
	}

	// Holds start time and metadata for correlating session start/end events
	private static class SessionStartInfo {
		final long startTimeNanos;
		final String operationName;

		SessionStartInfo( long startTimeNanos, String operationName ) {
			this.startTimeNanos = startTimeNanos;
			this.operationName = operationName;
		}
	}

	public PrometheusMonitor() {
		// Use default Prometheus registry
		registry = PrometheusRegistry.defaultRegistry;

		// Initialize Prometheus metrics
		operationsStartedTotal = Counter.builder()
			.name( "jolie_operations_started_total" )
			.help( "Total number of operations started" )
			.labelNames( "operation", "type" )
			.register( registry );

		operationsEndedTotal = Counter.builder()
			.name( "jolie_operations_ended_total" )
			.help( "Total number of operations ended" )
			.labelNames( "operation", "type", "status" )
			.register( registry );

		operationDuration = Histogram.builder()
			.name( "jolie_operation_duration_seconds" )
			.help( "Operation execution duration in seconds" )
			.labelNames( "operation", "type", "status" )
			.register( registry );

		sessionsTotal = Counter.builder()
			.name( "jolie_sessions_total" )
			.help( "Total number of sessions started" )
			.labelNames( "operation" )
			.register( registry );

		sessionsActive = Gauge.builder()
			.name( "jolie_sessions_active" )
			.help( "Number of currently active sessions" )
			.register( registry );

		outgoingCallsTotal = Counter.builder()
			.name( "jolie_outgoing_calls_total" )
			.help( "Total number of outgoing operation calls" )
			.labelNames( "operation", "output_port", "status" )
			.register( registry );

		outgoingRepliesTotal = Counter.builder()
			.name( "jolie_outgoing_replies_total" )
			.help( "Total number of outgoing operation replies received" )
			.labelNames( "operation", "output_port", "status" )
			.register( registry );

		outgoingCallDuration = Histogram.builder()
			.name( "jolie_outgoing_call_duration_seconds" )
			.help( "Outgoing operation call duration in seconds" )
			.labelNames( "operation", "output_port", "status" )
			.register( registry );

		protocolMessagesTotal = Counter.builder()
			.name( "jolie_protocol_messages_total" )
			.help( "Total number of protocol-level messages" )
			.labelNames( "protocol" )
			.register( registry );

		protocolMessageSize = Histogram.builder()
			.name( "jolie_protocol_message_size_bytes" )
			.help( "Protocol message body size in bytes" )
			.labelNames( "protocol" )
			.classicUpperBounds( 1024, 10240, 102400, 1048576 ) // 1KB, 10KB, 100KB, 1MB
			.register( registry );

		sessionDuration = Histogram.builder()
			.name( "jolie_session_duration_seconds" )
			.help( "Session duration in seconds" )
			.labelNames( "operation" )
			.register( registry );

		operationFaultsByType = Counter.builder()
			.name( "jolie_operation_faults_by_type_total" )
			.help( "Total number of operation faults by fault type" )
			.labelNames( "operation", "fault_type" )
			.register( registry );

		// Initialize state tracking maps with bounded size
		operationStartTimes = new ConcurrentHashMap<>();
		outgoingCallStartTimes = new ConcurrentHashMap<>();
		sessionStartTimes = new ConcurrentHashMap<>();
	}

	@Override
	public void pushEvent( MonitoringEvent e ) {
		try {
			String eventType = e.type();
			Value data = e.data();

			switch( eventType ) {
			case "OperationStarted":
				handleOperationStarted( data );
				break;
			case "OperationEnded":
				handleOperationEnded( data );
				break;
			case "SessionStarted":
				handleSessionStarted( data );
				break;
			case "SessionEnded":
				handleSessionEnded( data );
				break;
			case "OperationCall":
				handleOperationCall( data );
				break;
			case "OperationReply":
				handleOperationReply( data );
				break;
			case "ProtocolMessage-http":
				handleProtocolMessage( data, "http" );
				break;
			case "ProtocolMessage-soap":
				handleProtocolMessage( data, "soap" );
				break;
			default:
				// Ignore other event types
				break;
			}

			// Cleanup old entries if any map grows too large
			if( operationStartTimes.size() > maxTrackedOperations
				|| outgoingCallStartTimes.size() > maxTrackedOperations
				|| sessionStartTimes.size() > maxTrackedOperations ) {
				cleanupStaleEntries();
			}
		} catch( Exception ex ) {
			// Log but don't fail on metric recording errors
			System.err.println( "Error processing monitoring event: " + ex.getMessage() );
		}
	}

	private void handleOperationStarted( Value data ) {
		String operationName = data.getFirstChild( "operationName" ).strValue();
		String processId = data.getFirstChild( "processId" ).strValue();
		String messageId = data.getFirstChild( "messageId" ).strValue();

		// Determine operation type (will be refined when we get OperationEnded)
		// For now, assume RequestResponse, will be corrected if only SessionEnded follows
		String type = "requestresponse";

		// Build correlation key
		String correlationKey = buildCorrelationKey( processId, messageId );

		// Record start time
		operationStartTimes.put( correlationKey,
			new OperationStartInfo( System.nanoTime(), operationName, type ) );

		// Increment started counter
		operationsStartedTotal.labelValues( operationName, type ).inc();
	}

	private void handleOperationEnded( Value data ) {
		String operationName = data.getFirstChild( "operationName" ).strValue();
		String processId = data.getFirstChild( "processId" ).strValue();
		String messageId = data.getFirstChild( "messageId" ).strValue();
		int statusCode = data.getFirstChild( "status" ).intValue();

		// Map status code to label
		String status = mapStatusToLabel( statusCode );

		// Track fault details if status is fault
		if( statusCode == 1 && data.getFirstChild( "details" ).isDefined() ) {
			String faultType = data.getFirstChild( "details" ).strValue();
			if( faultType != null && !faultType.isEmpty() ) {
				operationFaultsByType.labelValues( operationName, faultType ).inc();
			}
		}

		// Determine operation type
		// OperationEnded is only fired for RequestResponse operations
		String type = "requestresponse";

		// Build correlation key
		String correlationKey = buildCorrelationKey( processId, messageId );

		// Calculate duration if we have start info
		OperationStartInfo startInfo = operationStartTimes.remove( correlationKey );
		if( startInfo != null ) {
			double durationSeconds = (System.nanoTime() - startInfo.startTimeNanos) / 1_000_000_000.0;
			operationDuration.labelValues( operationName, type, status ).observe( durationSeconds );
		}

		// Increment ended counter
		operationsEndedTotal.labelValues( operationName, type, status ).inc();
	}

	private void handleSessionStarted( Value data ) {
		String operationName = data.getFirstChild( "operationName" ).strValue();
		String processId = data.getFirstChild( "processId" ).strValue();

		// Store session start time for duration tracking
		sessionStartTimes.put( processId,
			new SessionStartInfo( System.nanoTime(), operationName ) );

		// Increment session counters
		sessionsTotal.labelValues( operationName ).inc();
		sessionsActive.inc();
	}

	private void handleSessionEnded( Value data ) {
		String processId = data.getFirstChild( "processId" ).strValue();

		// Calculate session duration if we have start info
		SessionStartInfo startInfo = sessionStartTimes.remove( processId );
		if( startInfo != null ) {
			double durationSeconds = (System.nanoTime() - startInfo.startTimeNanos) / 1_000_000_000.0;
			sessionDuration.labelValues( startInfo.operationName ).observe( durationSeconds );
		}

		// Decrement active sessions
		sessionsActive.dec();
	}

	private void handleOperationCall( Value data ) {
		String operationName = data.getFirstChild( "operationName" ).strValue();
		String outputPort = data.getFirstChild( "outputPort" ).strValue();
		String messageId = data.getFirstChild( "messageId" ).strValue();
		int statusCode = data.getFirstChild( "status" ).intValue();

		// Map status code to label
		String status = (statusCode == 0) ? "success" : "fault";

		// Store call start time for duration tracking
		outgoingCallStartTimes.put( messageId,
			new OutgoingCallStartInfo( System.nanoTime(), operationName, outputPort ) );

		// Increment outgoing call counter
		outgoingCallsTotal.labelValues( operationName, outputPort, status ).inc();
	}

	private void handleOperationReply( Value data ) {
		String operationName = data.getFirstChild( "operationName" ).strValue();
		String outputPort = data.getFirstChild( "outputPort" ).strValue();
		String messageId = data.getFirstChild( "messageId" ).strValue();
		int statusCode = data.getFirstChild( "status" ).intValue();

		// Map status code to label
		String status = mapStatusToLabel( statusCode );

		// Track fault details if status is fault
		if( statusCode == 1 && data.getFirstChild( "details" ).isDefined() ) {
			String faultType = data.getFirstChild( "details" ).strValue();
			if( faultType != null && !faultType.isEmpty() ) {
				operationFaultsByType.labelValues( operationName, faultType ).inc();
			}
		}

		// Calculate call duration if we have start info
		OutgoingCallStartInfo startInfo = outgoingCallStartTimes.remove( messageId );
		if( startInfo != null ) {
			double durationSeconds = (System.nanoTime() - startInfo.startTimeNanos) / 1_000_000_000.0;
			outgoingCallDuration.labelValues( operationName, outputPort, status ).observe( durationSeconds );
		}

		// Increment reply counter
		outgoingRepliesTotal.labelValues( operationName, outputPort, status ).inc();
	}

	private void handleProtocolMessage( Value data, String protocol ) {
		// Increment protocol message counter
		protocolMessagesTotal.labelValues( protocol ).inc();

		// Track message size if body is available
		if( data.getFirstChild( "data" ).isDefined() ) {
			Value protocolData = data.getFirstChild( "data" );
			if( protocolData.getFirstChild( "body" ).isDefined() ) {
				String body = protocolData.getFirstChild( "body" ).strValue();
				if( body != null ) {
					int bodySize = body.getBytes().length;
					// Only track messages up to 1MB
					if( bodySize <= 1048576 ) {
						protocolMessageSize.labelValues( protocol ).observe( bodySize );
					}
				}
			}
		}
	}

	private String buildCorrelationKey( String processId, String messageId ) {
		if( trackProcessId ) {
			return processId + ":" + messageId;
		}
		return messageId;
	}

	private String mapStatusToLabel( int statusCode ) {
		// Based on OperationEndedEvent constants:
		// SUCCESS = 0, FAULT = 1, ERROR = 2
		switch( statusCode ) {
		case 0:
			return "success";
		case 1:
			return "fault";
		case 2:
			return "error";
		default:
			return "unknown";
		}
	}

	private void cleanupStaleEntries() {
		// Remove entries older than 5 minutes (likely stale/orphaned)
		long fiveMinutesAgo = System.nanoTime() - (5L * 60 * 1_000_000_000);

		operationStartTimes.entrySet().removeIf( entry -> entry.getValue().startTimeNanos < fiveMinutesAgo );
		outgoingCallStartTimes.entrySet().removeIf( entry -> entry.getValue().startTimeNanos < fiveMinutesAgo );
		sessionStartTimes.entrySet().removeIf( entry -> entry.getValue().startTimeNanos < fiveMinutesAgo );
	}

	/**
	 * Collect JVM and system metrics in Prometheus text format
	 *
	 * @return JVM metrics as Prometheus-formatted text
	 */
	@SuppressWarnings( "deprecation" )
	private String collectJvmMetrics() {
		StringBuilder metrics = new StringBuilder();

		// Memory metrics
		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
		MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

		metrics.append( "# HELP jvm_memory_used_bytes Used memory in bytes\n" );
		metrics.append( "# TYPE jvm_memory_used_bytes gauge\n" );
		metrics.append( String.format( "jvm_memory_used_bytes{area=\"heap\"} %d\n", heapUsage.getUsed() ) );
		metrics.append( String.format( "jvm_memory_used_bytes{area=\"nonheap\"} %d\n", nonHeapUsage.getUsed() ) );

		metrics.append( "# HELP jvm_memory_committed_bytes Committed memory in bytes\n" );
		metrics.append( "# TYPE jvm_memory_committed_bytes gauge\n" );
		metrics.append( String.format( "jvm_memory_committed_bytes{area=\"heap\"} %d\n", heapUsage.getCommitted() ) );
		metrics.append(
			String.format( "jvm_memory_committed_bytes{area=\"nonheap\"} %d\n", nonHeapUsage.getCommitted() ) );

		metrics.append( "# HELP jvm_memory_max_bytes Maximum memory in bytes\n" );
		metrics.append( "# TYPE jvm_memory_max_bytes gauge\n" );
		if( heapUsage.getMax() > 0 ) {
			metrics.append( String.format( "jvm_memory_max_bytes{area=\"heap\"} %d\n", heapUsage.getMax() ) );
		}
		if( nonHeapUsage.getMax() > 0 ) {
			metrics.append( String.format( "jvm_memory_max_bytes{area=\"nonheap\"} %d\n", nonHeapUsage.getMax() ) );
		}

		// Thread metrics
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		metrics.append( "# HELP jvm_threads_current Current number of threads\n" );
		metrics.append( "# TYPE jvm_threads_current gauge\n" );
		metrics.append( String.format( "jvm_threads_current %d\n", threadBean.getThreadCount() ) );

		metrics.append( "# HELP jvm_threads_daemon Current number of daemon threads\n" );
		metrics.append( "# TYPE jvm_threads_daemon gauge\n" );
		metrics.append( String.format( "jvm_threads_daemon %d\n", threadBean.getDaemonThreadCount() ) );

		metrics.append( "# HELP jvm_threads_peak Peak number of threads\n" );
		metrics.append( "# TYPE jvm_threads_peak gauge\n" );
		metrics.append( String.format( "jvm_threads_peak %d\n", threadBean.getPeakThreadCount() ) );

		metrics.append( "# HELP jvm_threads_started_total Total number of threads started\n" );
		metrics.append( "# TYPE jvm_threads_started_total counter\n" );
		metrics.append( String.format( "jvm_threads_started_total %d\n", threadBean.getTotalStartedThreadCount() ) );

		// Garbage collection metrics
		List< GarbageCollectorMXBean > gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
		metrics.append( "# HELP jvm_gc_collection_seconds_total Time spent in garbage collection\n" );
		metrics.append( "# TYPE jvm_gc_collection_seconds_total counter\n" );
		for( GarbageCollectorMXBean gcBean : gcBeans ) {
			String gcName = gcBean.getName().replace( " ", "_" ).toLowerCase();
			long collectionTime = gcBean.getCollectionTime();
			if( collectionTime >= 0 ) {
				metrics.append( String.format( "jvm_gc_collection_seconds_total{gc=\"%s\"} %.3f\n",
					gcName, collectionTime / 1000.0 ) );
			}
		}

		metrics.append( "# HELP jvm_gc_collection_count_total Total number of garbage collections\n" );
		metrics.append( "# TYPE jvm_gc_collection_count_total counter\n" );
		for( GarbageCollectorMXBean gcBean : gcBeans ) {
			String gcName = gcBean.getName().replace( " ", "_" ).toLowerCase();
			long collectionCount = gcBean.getCollectionCount();
			if( collectionCount >= 0 ) {
				metrics.append( String.format( "jvm_gc_collection_count_total{gc=\"%s\"} %d\n",
					gcName, collectionCount ) );
			}
		}

		// Runtime metrics
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		metrics.append( "# HELP jvm_uptime_seconds JVM uptime in seconds\n" );
		metrics.append( "# TYPE jvm_uptime_seconds gauge\n" );
		metrics.append( String.format( "jvm_uptime_seconds %.3f\n", runtimeBean.getUptime() / 1000.0 ) );

		// Class loading metrics
		ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
		metrics.append( "# HELP jvm_classes_loaded Current number of loaded classes\n" );
		metrics.append( "# TYPE jvm_classes_loaded gauge\n" );
		metrics.append( String.format( "jvm_classes_loaded %d\n", classLoadingBean.getLoadedClassCount() ) );

		metrics.append( "# HELP jvm_classes_loaded_total Total number of classes loaded since JVM start\n" );
		metrics.append( "# TYPE jvm_classes_loaded_total counter\n" );
		metrics.append( String.format( "jvm_classes_loaded_total %d\n", classLoadingBean.getTotalLoadedClassCount() ) );

		metrics.append( "# HELP jvm_classes_unloaded_total Total number of classes unloaded\n" );
		metrics.append( "# TYPE jvm_classes_unloaded_total counter\n" );
		metrics.append( String.format( "jvm_classes_unloaded_total %d\n", classLoadingBean.getUnloadedClassCount() ) );

		// System/OS metrics
		try {
			OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

			metrics.append( "# HELP system_cpu_count Number of available processors\n" );
			metrics.append( "# TYPE system_cpu_count gauge\n" );
			metrics.append( String.format( "system_cpu_count %d\n", osBean.getAvailableProcessors() ) );

			metrics.append( "# HELP system_load_average_1m System load average for the last minute\n" );
			metrics.append( "# TYPE system_load_average_1m gauge\n" );
			double loadAverage = osBean.getSystemLoadAverage();
			if( loadAverage >= 0 ) {
				metrics.append( String.format( "system_load_average_1m %.2f\n", loadAverage ) );
			}

			// Process CPU usage
			metrics.append( "# HELP process_cpu_usage Process CPU usage (0.0 to 1.0)\n" );
			metrics.append( "# TYPE process_cpu_usage gauge\n" );
			double processCpuLoad = osBean.getProcessCpuLoad();
			if( processCpuLoad >= 0 ) {
				metrics.append( String.format( "process_cpu_usage %.4f\n", processCpuLoad ) );
			}

			// System CPU usage
			metrics.append( "# HELP system_cpu_usage System CPU usage (0.0 to 1.0)\n" );
			metrics.append( "# TYPE system_cpu_usage gauge\n" );
			double systemCpuLoad = osBean.getSystemCpuLoad();
			if( systemCpuLoad >= 0 ) {
				metrics.append( String.format( "system_cpu_usage %.4f\n", systemCpuLoad ) );
			} // Memory metrics
			metrics.append( "# HELP system_memory_total_bytes Total physical memory\n" );
			metrics.append( "# TYPE system_memory_total_bytes gauge\n" );
			metrics.append( String.format( "system_memory_total_bytes %d\n", osBean.getTotalPhysicalMemorySize() ) );

			metrics.append( "# HELP system_memory_free_bytes Free physical memory\n" );
			metrics.append( "# TYPE system_memory_free_bytes gauge\n" );
			metrics.append( String.format( "system_memory_free_bytes %d\n", osBean.getFreePhysicalMemorySize() ) );

			metrics.append( "# HELP system_swap_total_bytes Total swap space\n" );
			metrics.append( "# TYPE system_swap_total_bytes gauge\n" );
			metrics.append( String.format( "system_swap_total_bytes %d\n", osBean.getTotalSwapSpaceSize() ) );

			metrics.append( "# HELP system_swap_free_bytes Free swap space\n" );
			metrics.append( "# TYPE system_swap_free_bytes gauge\n" );
			metrics.append( String.format( "system_swap_free_bytes %d\n", osBean.getFreeSwapSpaceSize() ) );
		} catch( Exception e ) {
			// OS-specific metrics might not be available on all platforms
			metrics.append( "# Warning: Some system metrics unavailable: " ).append( e.getMessage() ).append( "\n" );
		}

		return metrics.toString();
	}

	/**
	 * Get metrics in Prometheus text format
	 *
	 * @return Metrics formatted as Prometheus text format
	 */
	public Value getMetrics( Value request ) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			// Collect JVM and system metrics
			String jvmMetrics = collectJvmMetrics();

			// Scrape registered Prometheus metrics
			MetricSnapshots snapshots = registry.scrape();
			PrometheusTextFormatWriter writer = new PrometheusTextFormatWriter( false );
			writer.write( outputStream, snapshots );

			// Combine JVM metrics with Prometheus metrics
			String combinedMetrics = jvmMetrics + "\n" + outputStream.toString( "UTF-8" );

			Value response = Value.create();
			response.setValue( combinedMetrics );
			return response;
		} catch( IOException e ) {
			System.err.println( "Error formatting metrics: " + e.getMessage() );
			Value response = Value.create();
			response.setValue( "# Error formatting metrics: " + e.getMessage() );
			return response;
		}
	}

	/**
	 * Configure Prometheus monitor settings
	 *
	 * @param request Configuration request with optional parameters: - trackProcessId: boolean
	 *        (default: false) - maxTrackedOps: int (default: 10000)
	 */
	public void setMonitor( Value request ) {
		if( request.getFirstChild( "trackProcessId" ).isDefined() ) {
			trackProcessId = request.getFirstChild( "trackProcessId" ).boolValue();
		}
		if( request.getFirstChild( "maxTrackedOps" ).isDefined() ) {
			maxTrackedOperations = request.getFirstChild( "maxTrackedOps" ).intValue();
		}
	}

	/**
	 * Get current configuration
	 *
	 * @return Current configuration as Value
	 */
	public Value getMonitorConfig( Value request ) {
		Value response = Value.create();
		response.getFirstChild( "trackProcessId" ).setValue( trackProcessId );
		response.getFirstChild( "maxTrackedOps" ).setValue( maxTrackedOperations );
		response.getFirstChild( "trackedOperations" ).setValue( operationStartTimes.size() );
		response.getFirstChild( "trackedOutgoingCalls" ).setValue( outgoingCallStartTimes.size() );
		response.getFirstChild( "trackedSessions" ).setValue( sessionStartTimes.size() );
		return response;
	}
}
