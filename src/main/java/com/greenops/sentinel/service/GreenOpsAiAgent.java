package com.greenops.sentinel.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GreenOpsAiAgent {

    private final ChatClient chatClient;

    // Spring Boot automatically injects the ChatClient.Builder because of your pom.xml and properties!
    public GreenOpsAiAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeResource(String instanceId, double cpuUsage) {
        // This is the Prompt Engineering part. We give the AI a persona and the live data.
        String promptText = String.format(
                "You are an expert Cloud FinOps AI. An AWS EC2 instance (%s) is currently running with an average " +
                        "CPU utilization of %.2f%% over the last 5 minutes. Based on this metric, is this instance likely wasting money? " +
                        "Provide a short, one-sentence recommendation on whether to keep it running, monitor it, or shut it down.",
                instanceId, cpuUsage
        );

        // Send the prompt to Gemini and return the text response
        return chatClient.prompt()
                .user(promptText)
                .call()
                .content();
    }
}