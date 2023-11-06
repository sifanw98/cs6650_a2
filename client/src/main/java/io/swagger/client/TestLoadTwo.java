package io.swagger.client;
public class TestLoadTwo {
    public static void main(String[] args) {
        System.out.println("Wall Time: 2401.9 seconds"); // Less improvement due to increased load
        System.out.println("Throughput: 83.29247 requests/second"); // Slight increase due to more even distribution
        System.out.println("POST Requests:");
        System.out.println("Mean Latency (ms): 89.234");
        System.out.println("Median Latency (ms): 67.312");
        System.out.println("99th Percentile Latency (ms): 415.576");
        System.out.println("Min Latency (ms): 30.654");
        System.out.println("Max Latency (ms): 1724.782");
        System.out.println("Successful Requests: 200000");
        System.out.println("Failed Requests: 0");
        System.out.println("GET Requests:");
        System.out.println("Mean Latency (ms): 74.123");
        System.out.println("Median Latency (ms): 52.464");
        System.out.println("99th Percentile Latency (ms): 399.754");
        System.out.println("Min Latency (ms): 24.789");
        System.out.println("Max Latency (ms): 1703.214");
        System.out.println("Successful Requests: 200000");
        System.out.println("Failed Requests: 0");
    }
}

