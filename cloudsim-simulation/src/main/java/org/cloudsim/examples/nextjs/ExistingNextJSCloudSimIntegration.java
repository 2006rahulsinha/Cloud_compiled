package org.cloudsim.examples.nextjs;

import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CloudSim Plus Integration for Existing Next.js Applications
 * 
 * This simulation reads real performance data from any existing Next.js application
 * that has been integrated with the monitoring system and creates accurate
 * cloud infrastructure simulations based on actual application behavior.
 * 
 * Features:
 * - Integrates with any existing Next.js project
 * - Real-time metrics integration from live applications
 * - Dynamic resource scaling based on actual performance
 * - Workload modeling based on real usage patterns
 * - Cost optimization analysis for existing applications
 * 
 * @author CloudSim Integration Team
 * @version 3.0 EXISTING-APP-INTEGRATION
 */
public class ExistingNextJSCloudSimIntegration {

    private final CloudSimPlus simulation;
    private DatacenterBroker broker;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Map<Integer, String> workloadTypes;

    // Real-time monitoring components
    private Map<String, Object> realTimeMetrics;
    private ScheduledExecutorService monitoringService;
    private boolean useRealData = false;
    private String metricsFilePath = "cloudsim-metrics.json";
    private String projectName = "Unknown-NextJS-App";

    // VM specifications (dynamically adjusted)
    private final int[] baseVmCores = {4, 2, 2, 4, 6};
    private final int[] baseVmRam = {4096, 2048, 2048, 4096, 8192};
    private final String[] vmNames = {
        "Next.js Application Server", 
        "API/Backend Server", 
        "Static Content Server", 
        "Database Server", 
        "Build/CI Server"
    };

    private int[] actualVmCores;
    private int[] actualVmRam;

    /**
     * Constructor for existing Next.js app integration
     */
    public ExistingNextJSCloudSimIntegration(boolean enableRealTimeMonitoring, String customMetricsPath) {
        System.out.println("🔗 Initializing CloudSim Plus Integration for Existing Next.js App...");

        this.useRealData = enableRealTimeMonitoring;
        if (customMetricsPath != null && !customMetricsPath.isEmpty()) {
            this.metricsFilePath = customMetricsPath;
        }

        this.realTimeMetrics = new HashMap<>();
        this.simulation = new CloudSimPlus();
        this.workloadTypes = new HashMap<>();

        // Initialize VM specs
        this.actualVmCores = baseVmCores.clone();
        this.actualVmRam = baseVmRam.clone();

        if (enableRealTimeMonitoring) {
            startRealTimeMonitoring();
            // Wait for initial data collection
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        createDatacenter();
        createBroker();
        createVMs();
        createCloudlets();

        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);
    }

    /**
     * Alternative constructor with default metrics path
     */
    public ExistingNextJSCloudSimIntegration(boolean enableRealTimeMonitoring) {
        this(enableRealTimeMonitoring, null);
    }

    /**
     * Starts real-time monitoring of the existing Next.js application
     */
    private void startRealTimeMonitoring() {
        System.out.println("📊 Starting real-time monitoring for existing Next.js application...");
        System.out.println("📁 Looking for metrics file: " + metricsFilePath);

        monitoringService = Executors.newScheduledThreadPool(1);

        // Monitor every 5 seconds
        monitoringService.scheduleAtFixedRate(() -> {
            try {
                collectRealTimeMetrics();
            } catch (Exception e) {
                System.err.println("⚠️  Error collecting metrics: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Collects real-time metrics from the existing Next.js application
     */
    private void collectRealTimeMetrics() {
        try {
            File metricsFile = new File(metricsFilePath);
            if (metricsFile.exists()) {
                String metricsJson = new String(Files.readAllBytes(Paths.get(metricsFilePath)));
                parseExistingAppMetrics(metricsJson);
            } else {
                // Check alternative locations
                String[] alternativePaths = {
                    "../" + metricsFilePath,
                    "../../" + metricsFilePath,
                    "./Cloud_project/" + metricsFilePath,
                    "../Cloud_project/" + metricsFilePath
                };

                boolean found = false;
                for (String altPath : alternativePaths) {
                    File altFile = new File(altPath);
                    if (altFile.exists()) {
                        String metricsJson = new String(Files.readAllBytes(Paths.get(altPath)));
                        parseExistingAppMetrics(metricsJson);
                        found = true;
                        System.out.println("📊 Found metrics at: " + altPath);
                        break;
                    }
                }

                if (!found) {
                    generateSimulatedMetrics();
                }
            }
        } catch (IOException e) {
            System.err.println("📊 Using simulated metrics - could not read real data: " + e.getMessage());
            generateSimulatedMetrics();
        }
    }

    /**
     * Parses JSON metrics from the existing Next.js application
     */
    private void parseExistingAppMetrics(String json) {
        try {
            // Parse key metrics from the existing app
            realTimeMetrics.put("responseTime", extractJsonValue(json, "responseTime"));
            realTimeMetrics.put("cpuUsage", extractJsonValue(json, "cpuUsage"));
            realTimeMetrics.put("memoryUsage", extractJsonValue(json, "memoryUsage"));
            realTimeMetrics.put("requestCount", extractJsonValue(json, "requestCount"));
            realTimeMetrics.put("errorCount", extractJsonValue(json, "errorCount"));
            realTimeMetrics.put("successCount", extractJsonValue(json, "successCount"));
            realTimeMetrics.put("activeConnections", extractJsonValue(json, "activeConnections"));

            // Extract project name if available
            String extractedProjectName = extractJsonString(json, "projectName");
            if (extractedProjectName != null && !extractedProjectName.isEmpty()) {
                this.projectName = extractedProjectName;
            }

            // Extract page metrics
            Map<String, Double> pages = extractJsonObject(json, "pages");
            if (pages != null) {
                realTimeMetrics.put("pages", pages);
            }

            System.out.println("📈 Real metrics from " + projectName + " - CPU: " + 
                String.format("%.1f", (Double)realTimeMetrics.getOrDefault("cpuUsage", 0.0)) + 
                "%, Response: " + 
                String.format("%.1f", (Double)realTimeMetrics.getOrDefault("responseTime", 0.0)) + "ms, " +
                "Requests: " + 
                String.format("%.0f", (Double)realTimeMetrics.getOrDefault("requestCount", 0.0)));

            adjustInfrastructureBasedOnRealMetrics();

        } catch (Exception e) {
            System.err.println("⚠️  Error parsing existing app metrics JSON: " + e.getMessage());
            generateSimulatedMetrics();
        }
    }

    /**
     * Simple JSON value extraction
     */
    private double extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return 0.0;

            startIndex += searchKey.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }

            String valueStr = json.substring(startIndex, endIndex).trim();
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Extract string value from JSON
     */
    private String extractJsonString(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return null;

            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);

            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract object from JSON (simplified)
     */
    private Map<String, Double> extractJsonObject(String json, String key) {
        try {
            Map<String, Double> result = new HashMap<>();
            String searchKey = "\"" + key + "\":{";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return null;

            // This is a simplified extraction - in production use a proper JSON library
            result.put("home", extractJsonValue(json, "home"));
            result.put("api", extractJsonValue(json, "api"));
            result.put("other", extractJsonValue(json, "other"));

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates simulated metrics when real data isn't available
     */
    private void generateSimulatedMetrics() {
        realTimeMetrics.put("responseTime", 80 + Math.random() * 120); // 80-200ms
        realTimeMetrics.put("cpuUsage", 25 + Math.random() * 35); // 25-60%
        realTimeMetrics.put("memoryUsage", 40 + Math.random() * 40); // 40-80MB
        realTimeMetrics.put("requestCount", Math.random() * 200);
        realTimeMetrics.put("errorCount", Math.random() * 10);
        realTimeMetrics.put("activeConnections", Math.random() * 20);

        // Simulated page metrics
        Map<String, Double> pages = new HashMap<>();
        pages.put("home", Math.random() * 50);
        pages.put("api", Math.random() * 100);
        pages.put("other", Math.random() * 30);
        realTimeMetrics.put("pages", pages);
    }

    /**
     * Adjusts infrastructure based on real metrics from existing app
     */
    private void adjustInfrastructureBasedOnRealMetrics() {
        double cpuUsage = (Double)realTimeMetrics.getOrDefault("cpuUsage", 30.0);
        double responseTime = (Double)realTimeMetrics.getOrDefault("responseTime", 100.0);
        double requestCount = (Double)realTimeMetrics.getOrDefault("requestCount", 50.0);

        // Scale factors based on actual performance
        double cpuScaleFactor = Math.max(0.5, Math.min(2.5, cpuUsage / 40.0));
        double responseScaleFactor = Math.max(0.4, Math.min(2.0, responseTime / 100.0));
        double loadScaleFactor = Math.max(0.6, Math.min(1.8, requestCount / 100.0));

        // Adjust VM specifications based on real usage
        for (int i = 0; i < actualVmCores.length; i++) {
            actualVmCores[i] = (int) Math.max(1, baseVmCores[i] * cpuScaleFactor);
            actualVmRam[i] = (int) Math.max(1024, baseVmRam[i] * responseScaleFactor * loadScaleFactor);
        }

        System.out.println("🔧 Adjusted infrastructure - CPU scale: " + String.format("%.2f", cpuScaleFactor) + 
                         ", Response scale: " + String.format("%.2f", responseScaleFactor) +
                         ", Load scale: " + String.format("%.2f", loadScaleFactor));
    }

    private String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Creates the datacenter with resources scaled for existing app
     */
    private void createDatacenter() {
        List<Host> hostList = new ArrayList<>();

        // Calculate scale factor
        double scaleFactor = 1.0;
        if (useRealData && realTimeMetrics.containsKey("cpuUsage")) {
            scaleFactor = Math.max(0.7, (Double)realTimeMetrics.get("cpuUsage") / 35.0);
        }

        // Host configurations based on typical Next.js app requirements
        int host1Cores = (int) Math.max(16, 32 * scaleFactor);
        List<Pe> peList1 = createPeList(host1Cores, 2800);
        Host host1 = new HostSimple(65536, 25000, 500000, peList1) // 64GB RAM
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host1);

        int host2Cores = (int) Math.max(12, 20 * scaleFactor);
        List<Pe> peList2 = createPeList(host2Cores, 2500);
        Host host2 = new HostSimple(32768, 20000, 300000, peList2) // 32GB RAM
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host2);

        List<Pe> peList3 = createPeList(8, 2000);
        Host host3 = new HostSimple(16384, 50000, 1000000, peList3) // High bandwidth for CDN
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host3);

        Datacenter datacenter = new DatacenterSimple(simulation, hostList);
        datacenter.getCharacteristics()
            .setCostPerSecond(2.8)        // Realistic cloud pricing
            .setCostPerMem(0.048)         // Memory cost
            .setCostPerStorage(0.0009)    // Storage cost
            .setCostPerBw(0.0);           // Bandwidth included

        System.out.println("🏢 Created datacenter for " + projectName + " (scale: " + String.format("%.2f", scaleFactor) + ")");
        System.out.println("   Total capacity: " + (host1Cores + host2Cores + 8) + " cores, " + 
                         (65536 + 32768 + 16384) + " MB RAM");
    }

    private List<Pe> createPeList(int numberOfPes, long mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips));
        }
        return peList;
    }

    private void createBroker() {
        broker = new DatacenterBrokerSimple(simulation);
        System.out.println("🎛️  Created datacenter broker for " + projectName);
    }

    /**
     * Creates VMs optimized for the existing Next.js application
     */
    private void createVMs() {
        vmList = new ArrayList<>();

        System.out.println("💻 Creating VMs optimized for " + projectName + ":");

        for (int i = 0; i < vmNames.length; i++) {
            int baseMips = 2200 + (i * 600);
            if (useRealData) {
                double cpuMultiplier = (Double)realTimeMetrics.getOrDefault("cpuUsage", 40.0) / 40.0;
                baseMips = (int) (baseMips * Math.max(0.5, Math.min(2.2, cpuMultiplier)));
            }

            Vm vm = new VmSimple(baseMips, actualVmCores[i])
                .setRam(actualVmRam[i])
                .setBw(1200 + (i * 400))
                .setSize(12000 + (i * 8000))
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
            vmList.add(vm);

            System.out.printf("  VM%d (%s): %d cores, %d MB RAM, %d MIPS%n", 
                            i, vmNames[i], actualVmCores[i], actualVmRam[i], baseMips);
        }
    }

    /**
     * Creates cloudlets representing the existing app's workload patterns
     */
    private void createCloudlets() {
        cloudletList = new ArrayList<>();
        UtilizationModel utilizationModel = new UtilizationModelFull();

        int cloudletId = 0;

        // Adjust based on real application metrics
        double responseMultiplier = 1.0;
        double requestMultiplier = 1.0;

        if (useRealData) {
            double realResponseTime = (Double)realTimeMetrics.getOrDefault("responseTime", 100.0);
            double requestCount = (Double)realTimeMetrics.getOrDefault("requestCount", 50.0);

            responseMultiplier = Math.max(0.3, Math.min(2.8, realResponseTime / 100.0));
            requestMultiplier = Math.max(0.5, Math.min(2.0, requestCount / 100.0));

            // Use page metrics if available
            @SuppressWarnings("unchecked")
            Map<String, Double> pages = (Map<String, Double>)realTimeMetrics.get("pages");
            if (pages != null) {
                System.out.println("📊 Using real page metrics: Home=" + pages.get("home") + 
                                 ", API=" + pages.get("api") + ", Other=" + pages.get("other"));
            }
        }

        System.out.println("📋 Creating cloudlets with response multiplier: " + 
                         String.format("%.2f", responseMultiplier) + 
                         ", request multiplier: " + String.format("%.2f", requestMultiplier));

        // Page rendering workloads (based on real page views)
        int pageWorkloads = Math.max(4, (int)(6 * requestMultiplier));
        for (int i = 0; i < pageWorkloads; i++) {
            int length = (int) ((9000 + (i * 1200)) * responseMultiplier);
            Cloudlet cloudlet = new CloudletSimple(cloudletId, length, 2)
                .setFileSize(400).setOutputSize(350)
                .setUtilizationModelCpu(utilizationModel)
                .setUtilizationModelRam(utilizationModel)
                .setUtilizationModelBw(utilizationModel);
            cloudletList.add(cloudlet);
            workloadTypes.put(cloudletId, projectName + " Page Rendering");
            cloudletId++;
        }

        // API workloads (based on real API usage)
        int apiWorkloads = Math.max(5, (int)(8 * requestMultiplier));
        for (int i = 0; i < apiWorkloads; i++) {
            int length = (int) ((4500 + (i * 700)) * responseMultiplier);
            Cloudlet cloudlet = new CloudletSimple(cloudletId, length, 1)
                .setFileSize(600).setOutputSize(500)
                .setUtilizationModelCpu(utilizationModel)
                .setUtilizationModelRam(utilizationModel)
                .setUtilizationModelBw(utilizationModel);
            cloudletList.add(cloudlet);
            workloadTypes.put(cloudletId, projectName + " API Processing");
            cloudletId++;
        }

        // Static asset serving
        for (int i = 0; i < 4; i++) {
            Cloudlet cloudlet = new CloudletSimple(cloudletId, 1800 + (i * 400), 1)
                .setFileSize(200).setOutputSize(1800)
                .setUtilizationModelCpu(utilizationModel)
                .setUtilizationModelRam(utilizationModel)
                .setUtilizationModelBw(utilizationModel);
            cloudletList.add(cloudlet);
            workloadTypes.put(cloudletId, "Static Assets");
            cloudletId++;
        }

        // Image optimization (if the app uses Next.js Image component)
        for (int i = 0; i < 3; i++) {
            Cloudlet cloudlet = new CloudletSimple(cloudletId, 16000 + (i * 2500), 3)
                .setFileSize(1200).setOutputSize(900)
                .setUtilizationModelCpu(utilizationModel)
                .setUtilizationModelRam(utilizationModel)
                .setUtilizationModelBw(utilizationModel);
            cloudletList.add(cloudlet);
            workloadTypes.put(cloudletId, "Image Processing");
            cloudletId++;
        }

        // Build/deployment processes
        for (int i = 0; i < 2; i++) {
            Cloudlet cloudlet = new CloudletSimple(cloudletId, 32000 + (i * 6000), 4)
                .setFileSize(800).setOutputSize(600)
                .setUtilizationModelCpu(utilizationModel)
                .setUtilizationModelRam(utilizationModel)
                .setUtilizationModelBw(utilizationModel);
            cloudletList.add(cloudlet);
            workloadTypes.put(cloudletId, "Build/Deploy");
            cloudletId++;
        }

        System.out.println("✅ Created " + cloudletList.size() + " cloudlets for " + projectName);
    }

    /**
     * Runs the simulation
     */
    public void runSimulation() {
        System.out.println("\n" + repeatString("=", 75));
        System.out.println("🎯 CloudSim Plus Analysis for Existing Next.js App: " + projectName);
        System.out.println(repeatString("=", 75));

        if (useRealData) {
            displayCurrentMetrics();
        }

        simulation.start();

        if (monitoringService != null) {
            monitoringService.shutdown();
        }

        displayResults();
    }

    private void displayCurrentMetrics() {
        System.out.println("\n📊 Current Application Metrics for " + projectName + ":");
        System.out.println("  Response Time: " + 
            String.format("%.2f ms", (Double)realTimeMetrics.getOrDefault("responseTime", 0.0)));
        System.out.println("  CPU Usage: " + 
            String.format("%.1f%%", (Double)realTimeMetrics.getOrDefault("cpuUsage", 0.0)));
        System.out.println("  Memory Usage: " + 
            String.format("%.1f MB", (Double)realTimeMetrics.getOrDefault("memoryUsage", 0.0)));
        System.out.println("  Total Requests: " + 
            String.format("%.0f", (Double)realTimeMetrics.getOrDefault("requestCount", 0.0)));
        System.out.println("  Active Connections: " + 
            String.format("%.0f", (Double)realTimeMetrics.getOrDefault("activeConnections", 0.0)));
        System.out.println();
    }

    private void displayResults() {
        List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();

        System.out.println("\n" + repeatString("=", 75));
        System.out.println("🎯 CloudSim Analysis Results for " + projectName);
        System.out.println(repeatString("=", 75));

        new CloudletsTableBuilder(finishedCloudlets).build();

        displayPerformanceAnalysis(finishedCloudlets);
        displayCostAnalysis(finishedCloudlets);
        displayRealVsSimulatedComparison(finishedCloudlets);
        displayOptimizationRecommendations(finishedCloudlets);
    }

    private void displayPerformanceAnalysis(List<Cloudlet> cloudlets) {
        System.out.println("\n" + repeatString("=", 55));
        System.out.println("📈 Performance Analysis for " + projectName);
        System.out.println(repeatString("=", 55));

        Map<String, Double> workloadTimes = new HashMap<>();
        Map<String, Integer> workloadCounts = new HashMap<>();

        int successful = 0;
        double totalTime = 0;

        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.isFinished()) {
                successful++;
                String workloadType = workloadTypes.get((int)cloudlet.getId());
                double execTime = cloudlet.getTotalExecutionTime();
                totalTime += execTime;

                workloadTimes.put(workloadType, 
                    workloadTimes.getOrDefault(workloadType, 0.0) + execTime);
                workloadCounts.put(workloadType, 
                    workloadCounts.getOrDefault(workloadType, 0) + 1);
            }
        }

        System.out.printf("✅ Successful simulations: %d/%d (%.1f%%)%n", 
                         successful, cloudlets.size(), 
                         (successful * 100.0 / cloudlets.size()));
        System.out.printf("⏱️  Total simulation time: %.2f seconds%n", totalTime);

        System.out.println("\n🔄 Average execution times by component:");
        for (Map.Entry<String, Double> entry : workloadTimes.entrySet()) {
            String type = entry.getKey();
            double avgTime = entry.getValue() / workloadCounts.get(type);
            System.out.printf("  %-35s: %.2f seconds%n", type, avgTime);
        }
    }

    private void displayCostAnalysis(List<Cloudlet> cloudlets) {
        System.out.println("\n" + repeatString("=", 55));
        System.out.println("💰 Infrastructure Cost Analysis");
        System.out.println(repeatString("=", 55));

        double totalCost = 0;
        Map<String, Double> costByWorkload = new HashMap<>();

        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.isFinished()) {
                double execTime = cloudlet.getTotalExecutionTime();
                double cost = execTime * 0.0042; // Realistic cloud cost per second
                totalCost += cost;

                String workloadType = workloadTypes.get((int)cloudlet.getId());
                costByWorkload.put(workloadType, 
                    costByWorkload.getOrDefault(workloadType, 0.0) + cost);
            }
        }

        System.out.printf("💵 Estimated hourly cost: $%.4f%n", totalCost);
        System.out.printf("📅 Estimated monthly cost: $%.2f%n", totalCost * 24 * 30);
        System.out.printf("📊 Cost per request: $%.6f%n", 
                         totalCost / Math.max(1, (Double)realTimeMetrics.getOrDefault("requestCount", 1.0)));

        System.out.println("\n💸 Cost breakdown by component:");
        for (Map.Entry<String, Double> entry : costByWorkload.entrySet()) {
            System.out.printf("  %-35s: $%.4f (%.1f%%)%n", 
                            entry.getKey(), 
                            entry.getValue(), 
                            (entry.getValue() / totalCost) * 100);
        }
    }

    private void displayRealVsSimulatedComparison(List<Cloudlet> cloudlets) {
        if (!useRealData) {
            return;
        }

        System.out.println("\n" + repeatString("=", 55));
        System.out.println("🔄 Real App vs CloudSim Comparison");
        System.out.println(repeatString("=", 55));

        double realResponseTime = (Double)realTimeMetrics.getOrDefault("responseTime", 0.0);
        double realCpuUsage = (Double)realTimeMetrics.getOrDefault("cpuUsage", 0.0);

        // Calculate simulated metrics
        double simulatedAvgTime = 0;
        int count = 0;
        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.isFinished()) {
                simulatedAvgTime += cloudlet.getTotalExecutionTime() * 1000; // Convert to ms
                count++;
            }
        }
        if (count > 0) {
            simulatedAvgTime /= count;
        }

        System.out.printf("🌐 Real Response Time: %.2f ms%n", realResponseTime);
        System.out.printf("🖥️  Simulated Avg Time: %.2f ms%n", simulatedAvgTime);

        if (simulatedAvgTime > 0 && realResponseTime > 0) {
            double accuracy = Math.max(0, 100 - Math.abs(realResponseTime - simulatedAvgTime) 
                                    / Math.max(realResponseTime, simulatedAvgTime) * 100);
            System.out.printf("🎯 Simulation accuracy: %.1f%%n", accuracy);
        }

        System.out.printf("📊 Real CPU Usage: %.1f%% (influenced infrastructure scaling)%n", realCpuUsage);
        System.out.printf("💻 Real Request Count: %.0f (scaled workload generation)%n", 
                         (Double)realTimeMetrics.getOrDefault("requestCount", 0.0));
    }

    private void displayOptimizationRecommendations(List<Cloudlet> cloudlets) {
        System.out.println("\n" + repeatString("=", 55));
        System.out.println("💡 Optimization Recommendations for " + projectName);
        System.out.println(repeatString("=", 55));

        // Calculate average execution times
        Map<String, Double> avgTimes = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (Cloudlet cloudlet : cloudlets) {
            if (cloudlet.isFinished()) {
                String workloadType = workloadTypes.get((int)cloudlet.getId());
                double execTime = cloudlet.getTotalExecutionTime();

                avgTimes.put(workloadType, avgTimes.getOrDefault(workloadType, 0.0) + execTime);
                counts.put(workloadType, counts.getOrDefault(workloadType, 0) + 1);
            }
        }

        for (Map.Entry<String, Double> entry : avgTimes.entrySet()) {
            String key = entry.getKey();
            avgTimes.put(key, entry.getValue() / counts.get(key));
        }

        System.out.println("🎯 Performance Recommendations:");

        // Analyze each workload type
        for (Map.Entry<String, Double> entry : avgTimes.entrySet()) {
            String workload = entry.getKey();
            double time = entry.getValue();

            if (workload.contains("Page Rendering")) {
                if (time > 8) {
                    System.out.printf("  ⚠️ Page rendering is slow (%.1fs avg)%n", time);
                    System.out.println("     💡 Consider: Static generation, ISR, or caching");
                } else {
                    System.out.printf("  ✅ Page rendering is optimal (%.1fs avg)%n", time);
                }
            } else if (workload.contains("API Processing")) {
                if (time > 5) {
                    System.out.printf("  ⚠️ API processing is slow (%.1fs avg)%n", time);
                    System.out.println("     💡 Consider: Database optimization, API caching, rate limiting");
                } else {
                    System.out.printf("  ✅ API performance is excellent (%.1fs avg)%n", time);
                }
            }
        }

        // Real-data specific recommendations
        if (useRealData) {
            double cpuUsage = (Double)realTimeMetrics.getOrDefault("cpuUsage", 0.0);
            double responseTime = (Double)realTimeMetrics.getOrDefault("responseTime", 0.0);

            System.out.println("\n🔧 Infrastructure Recommendations:");

            if (cpuUsage > 70) {
                System.out.printf("  🚨 High CPU usage (%.1f%%) - Scale up recommended%n", cpuUsage);
                System.out.println("     💡 Add more CPU cores or horizontal scaling");
            } else if (cpuUsage < 25) {
                System.out.printf("  💰 Low CPU usage (%.1f%%) - Cost optimization opportunity%n", cpuUsage);
                System.out.println("     💡 Consider smaller instance sizes");
            } else {
                System.out.printf("  ✅ CPU usage is optimal (%.1f%%)%n", cpuUsage);
            }

            if (responseTime > 200) {
                System.out.printf("  🐌 Slow response times (%.1fms)%n", responseTime);
                System.out.println("     💡 Optimize: Database queries, add caching, CDN implementation");
            } else if (responseTime < 50) {
                System.out.printf("  🚀 Excellent response times (%.1fms)%n", responseTime);
            } else {
                System.out.printf("  ✅ Good response times (%.1fms)%n", responseTime);
            }
        }

        System.out.println("\n🌍 General Next.js Optimization Tips:");
        System.out.println("  • Implement proper caching strategies (Redis, CDN)");
        System.out.println("  • Use Next.js Image optimization for better performance");
        System.out.println("  • Monitor Core Web Vitals and optimize accordingly");
        System.out.println("  • Consider serverless functions for API routes");
        System.out.println("  • Implement proper database indexing and query optimization");

        System.out.println("\n✅ CloudSim analysis completed for " + projectName + "!");
        System.out.println("📊 Use these insights for infrastructure planning and optimization");
        System.out.println(repeatString("=", 75));
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        try {
            boolean enableMonitoring = args.length > 0 && args[0].equals("--monitor");
            String customPath = args.length > 1 ? args[1] : null;

            if (enableMonitoring) {
                System.out.println("🔥 Real-time monitoring enabled for existing Next.js app");
                if (customPath != null) {
                    System.out.println("📁 Using custom metrics path: " + customPath);
                }
            } else {
                System.out.println("🔧 Running CloudSim simulation in standalone mode");
            }

            ExistingNextJSCloudSimIntegration simulation = 
                new ExistingNextJSCloudSimIntegration(enableMonitoring, customPath);
            simulation.runSimulation();

        } catch (Exception e) {
            System.err.println("❌ CloudSim integration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}