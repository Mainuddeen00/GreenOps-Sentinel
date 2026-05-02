package com.greenops.sentinel.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

@Service
public class CloudResourceMonitor {

    private final Ec2Client ec2Client;

    public CloudResourceMonitor() {
        // Initializes the client. It will automatically look for your local AWS credentials.
        this.ec2Client = Ec2Client.builder()
                .region(Region.AP_SOUTH_1) // Change this if your AWS resources are in ap-south-1 (Mumbai)
                .build();
    }

    // @PostConstruct tells Spring to run this method immediately after the app starts
    @PostConstruct
    public void testAwsConnection() {
        System.out.println("--- GreenOps Sentinel: Scanning AWS for EC2 Instances ---");
        try {
            DescribeInstancesResponse response = ec2Client.describeInstances();

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.println("Found Instance ID: " + instance.instanceId() +
                            " | State: " + instance.state().name());
                }
            }
            System.out.println("--- Scan Complete ---");

        } catch (Exception e) {
            System.err.println("Failed to connect to AWS: " + e.getMessage());
        }
    }
}