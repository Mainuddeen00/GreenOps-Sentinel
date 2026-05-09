package com.greenops.sentinel.service;

import com.greenops.sentinel.entity.AuditResult;
import com.greenops.sentinel.repository.AuditRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class CloudResourceMonitor {

    private final Ec2Client ec2Client;
    private final CloudWatchClient cwClient;
    private final GreenOpsAiAgent aiAgent;
    private final AuditRepository auditRepository;

    public CloudResourceMonitor(GreenOpsAiAgent aiAgent, AuditRepository auditRepository) {
        this.aiAgent = aiAgent;
        this.auditRepository = auditRepository; // Initialize it

        Region region = Region.AP_SOUTH_1; // Mumbai
        this.ec2Client = Ec2Client.builder().region(region).build();
        this.cwClient = CloudWatchClient.builder().region(region).build();
    }

    @PostConstruct
    public void scanAndAnalyze() {
        System.out.println("--- GreenOps Sentinel: Starting Infrastructure Audit ---");
        try {
            DescribeInstancesResponse response = ec2Client.describeInstances();

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.println("\nAnalyzing Instance: " + instance.instanceId());
                    System.out.println("State: " + instance.state().name());

                    // Only check CPU if the instance is actually running
                    if (instance.state().name().toString().equals("running")) {
                        checkCpuUsage(instance.instanceId());
                    }
                }
            }
            System.out.println("\n--- Audit Complete ---");

        } catch (Exception e) {
            System.err.println("Failed to audit AWS resources: " + e.getMessage());
        }
    }

    private void checkCpuUsage(String instanceId) {
        // Look at the last 1 hour of data
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(1, ChronoUnit.HOURS);

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/EC2")
                .metricName("CPUUtilization")
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .startTime(startTime)
                .endTime(endTime)
                .period(300) // Get data in 5-minute chunks
                .statistics(Statistic.AVERAGE)
                .build();

        GetMetricStatisticsResponse response = cwClient.getMetricStatistics(request);

        if (response.datapoints().isEmpty()) {
            System.out.println("   -> CPU Data: No recent data (Instance might be too new).");
        } else {
            Datapoint latestData = response.datapoints().get(0);
            double cpu = latestData.average();

            System.out.println("   -> Average CPU Usage (last 5 mins): " + String.format("%.2f", cpu) + "%");

            // --- THE NEW AI BRAIN ---
            System.out.println("   -> Asking Gemini AI for a recommendation...");
            String aiRecommendation = aiAgent.analyzeResource(instanceId, cpu);
            System.out.println("   -> [AI DECISION]: " + aiRecommendation);

            // --- SAVE TO POSTGRESQL ---
            AuditResult result = new AuditResult(instanceId, cpu, aiRecommendation);
            auditRepository.save(result);
            System.out.println("   -> [DATABASE] Audit successfully saved to PostgreSQL!");
        }
    }
}