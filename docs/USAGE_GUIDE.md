# Usage Guide - CloudSim Integration for Existing Next.js App

## Step-by-Step Integration Process

### Phase 1: Setup and Installation

1. **Download Integration Package**
   ```bash
   # Extract the integration package
   unzip cloudsim-integration-for-existing-nextjs.zip
   cd cloudsim-integration-for-existing-nextjs
   ```

2. **Run Integration Script**
   ```bash
   # Linux/macOS
   chmod +x integration-scripts/integrate-with-github-repo.sh
   ./integration-scripts/integrate-with-github-repo.sh

   # Windows
   integration-scripts\integrate-with-github-repo.bat
   ```

### Phase 2: Automatic Process (Handled by Script)

The integration script automatically:

1. **Clones Your Repository**
   - Downloads your existing Next.js app from GitHub
   - Installs all existing dependencies
   - Preserves your existing code completely

2. **Adds Monitoring Integration**
   - Copies monitoring files to your project
   - Creates optional API endpoints
   - Sets up performance tracking

3. **Builds CloudSim Simulation**
   - Compiles the Java-based simulation
   - Configures integration with your app
   - Prepares real-time analysis system

4. **Starts Integrated System**
   - Launches your existing Next.js app
   - Begins performance monitoring
   - Starts CloudSim simulation
   - Connects real data to simulation

### Phase 3: Using the Integrated System

#### Your Next.js App (Unchanged)
- **URL**: `http://localhost:3000`
- **Functionality**: Exactly the same as before
- **Performance**: No impact on existing features
- **Monitoring**: Happens transparently in background

#### Real-Time Metrics Collection
The system automatically tracks:

```javascript
// Performance Metrics (Updated every 5 seconds)
{
  "projectName": "Cloud_project",
  "responseTime": 125.50,        // Average API response time
  "cpuUsage": 45.2,             // Node.js process CPU usage
  "memoryUsage": 67.8,          // Memory consumption in MB
  "requestCount": 1547,         // Total requests processed
  "errorCount": 12,             // Failed requests
  "successCount": 1535,         // Successful requests
  "activeConnections": 8,       // Current active connections
  "pages": {
    "home": 245,                // Home page visits
    "api": 892,                 // API route calls  
    "other": 387                // Other page visits
  },
  "routes": {
    "/": 245,                   // Specific route metrics
    "/api/users": 156,
    "/api/posts": 234
    // ... more routes
  }
}
```

#### CloudSim Plus Simulation
The simulation creates:

1. **5 Virtual Machines** representing different components:
   - **VM0**: Next.js Application Server (4 cores, 4GB RAM)
   - **VM1**: API/Backend Server (2 cores, 2GB RAM)
   - **VM2**: Static Content Server (2 cores, 2GB RAM)
   - **VM3**: Database Server (4 cores, 4GB RAM)
   - **VM4**: Build/CI Server (6 cores, 8GB RAM)

2. **Dynamic Cloudlets** based on real usage:
   - Page rendering tasks (scaled by real page views)
   - API processing tasks (scaled by real API calls)
   - Static file serving (based on asset requests)
   - Image optimization (Next.js image processing)
   - Build/deployment processes

### Phase 4: Analysis and Recommendations

#### Real-Time Performance Analysis
```
📈 Performance Analysis for Cloud_project
==========================================
✅ Successful simulations: 26/26 (100.0%)
⏱️  Total simulation time: 185.42 seconds
💰 Estimated hourly cost: $0.4720

🔄 Average execution times by component:
  Cloud_project Page Rendering    : 6.24 seconds
  Cloud_project API Processing    : 3.12 seconds
  Static Assets                   : 1.05 seconds
  Image Processing               : 12.88 seconds
  Build/Deploy                   : 28.15 seconds
```

#### Infrastructure Cost Analysis
```
💰 Infrastructure Cost Analysis
===============================
💵 Estimated hourly cost: $0.4720
📅 Estimated monthly cost: $339.84
📊 Cost per request: $0.000305

💸 Cost breakdown by component:
  Cloud_project Page Rendering    : $0.1890 (40.0%)
  Cloud_project API Processing    : $0.1416 (30.0%)
  Static Assets                   : $0.0472 (10.0%)
  Image Processing               : $0.0708 (15.0%)
  Build/Deploy                   : $0.0236 (5.0%)
```

#### Real vs. Simulated Comparison
```
🔄 Real App vs CloudSim Comparison
==================================
🌐 Real Response Time: 125.50 ms
🖥️  Simulated Avg Time: 132.40 ms
🎯 Simulation accuracy: 94.8%
📊 Real CPU Usage: 45.2% (influenced infrastructure scaling)
💻 Real Request Count: 1547 (scaled workload generation)
```

#### Optimization Recommendations
```
💡 Optimization Recommendations for Cloud_project
=================================================
🎯 Performance Recommendations:
✅ Page rendering is optimal (6.2s avg)
⚠️ API processing is slow (3.1s avg)
   💡 Consider: Database optimization, Redis cache, API caching

🔧 Infrastructure Recommendations:
✅ CPU usage is optimal (45.2%)
🚀 Excellent response times (125.5ms)

💰 Cost optimization opportunities:
  • Consider reserved instances for predictable workloads
  • Use auto-scaling for API servers during traffic spikes
  • Implement CDN for static assets to reduce server load
```

## Advanced Usage Scenarios

### Scenario 1: Load Testing Integration
Generate load on your existing app while monitoring:

```bash
# In one terminal: Start the integrated system
./integration-scripts/integrate-with-github-repo.sh

# In another terminal: Generate load
curl -X GET http://localhost:3000/ &
curl -X GET http://localhost:3000/api/some-endpoint &
# Repeat to simulate load

# Watch CloudSim adjust resource recommendations in real-time
```

### Scenario 2: Development Workflow Integration
Use during development to understand performance impact:

```bash
# 1. Start integration with your existing app
./integration-scripts/integrate-with-github-repo.sh

# 2. Make changes to your existing code
# 3. Test features in your app at localhost:3000
# 4. CloudSim provides real-time infrastructure impact analysis
# 5. Make informed decisions about cloud resource needs
```

### Scenario 3: Production Planning
Simulate production load patterns:

```javascript
// Add to your existing app for production-like testing
const monitor = require('./integrate-with-existing-nextjs');

// Simulate different usage patterns
for (let i = 0; i < 100; i++) {
  monitor.trackCustomEvent('simulated-user-action', Math.random() * 200);
}
```

## Understanding the Output

### Console Output Interpretation

1. **Startup Messages**
   ```
   🔗 CloudSim Integration for Existing Next.js Repository
   ✅ All prerequisites found
   ✅ Repository ready: Cloud_project
   ✅ Next.js dependencies installed
   ✅ Monitoring integration added
   ```

2. **Real-Time Metrics**
   ```
   📊 [15:30:32] CloudSim Metrics (Cloud_project):
      🌐 Requests: 1547 | Active: 8
      ⚡ Avg Response: 125.50ms
      💻 CPU: 45.2% | Memory: 67.8MB
      📈 Success: 99.2% | Uptime: 45m
   ```

3. **CloudSim Analysis**
   ```
   🎯 CloudSim Analysis Results for Cloud_project
   ===============================================
   [Detailed CloudSim table with execution times]
   [Performance analysis by workload type]
   [Cost breakdown and recommendations]
   ```

### Files Generated

During integration, these files are created:

```
Cloud_project/                           # Your existing repo
├── integrate-with-existing-nextjs.js    # Monitoring system
├── cloudsim-metrics.json               # Real-time metrics (auto-updated)
├── pages/api/cloudsim-metrics.js        # Metrics API endpoint
├── start-with-cloudsim.js              # Integrated launcher
└── [all your existing files unchanged]

cloudsim-integration-for-existing-nextjs/
├── cloudsim-simulation/                 # CloudSim Plus code
├── performance-monitoring/              # Monitoring utilities
├── integration-scripts/                 # Setup scripts
└── docs/                               # Documentation
```

## Customization Options

### Monitoring Customization
Edit `Cloud_project/integrate-with-existing-nextjs.js`:

```javascript
// Change update frequency
updateInterval: 3000,  // Update every 3 seconds instead of 5

// Change metrics file location
metricsFile: 'my-custom-metrics.json',

// Enable/disable console output
enableConsoleOutput: false,

// Customize project name
projectName: 'My-Awesome-Next-App'
```

### CloudSim Customization
Edit `cloudsim-simulation/resources/simulation.properties`:

```properties
# Adjust VM specifications
vm.template.nextjs.cores=6
vm.template.nextjs.ram=8192

# Change cost parameters
datacenter.cost.processing=3.50
datacenter.cost.memory=0.060

# Modify performance thresholds
threshold.response.time.warning=200
threshold.cpu.usage.warning=80
```

## Troubleshooting Common Issues

### Issue 1: Integration Script Fails
**Symptoms**: Script exits with error during setup
**Solutions**:
```bash
# Check prerequisites
node --version  # Should be 16+
java -version   # Should be 8+
mvn --version   # Should be 3.6+

# Run with debug mode
DEBUG=true ./integration-scripts/integrate-with-github-repo.sh
```

### Issue 2: Metrics Not Generated
**Symptoms**: `cloudsim-metrics.json` file not created
**Solutions**:
```bash
# Check if monitoring is loaded
cd Cloud_project
node -e "console.log(require('./integrate-with-existing-nextjs').getMetrics())"

# Verify file permissions
ls -la cloudsim-metrics.json

# Check app startup logs
npm run dev  # Look for monitoring initialization messages
```

### Issue 3: CloudSim Simulation Fails
**Symptoms**: CloudSim compilation or execution errors
**Solutions**:
```bash
# Clean rebuild CloudSim
cd cloudsim-simulation
mvn clean compile -X  # Verbose output

# Check Java version compatibility
java -version  # CloudSim requires Java 8+

# Verify metrics file exists
ls -la ../Cloud_project/cloudsim-metrics.json
```

### Issue 4: Inaccurate Simulation Results
**Symptoms**: CloudSim results don't match real app performance
**Solutions**:
```bash
# Ensure app is generating sufficient load
curl http://localhost:3000/  # Generate some requests

# Check metrics file content
cat Cloud_project/cloudsim-metrics.json

# Verify monitoring is tracking requests
# Visit localhost:3000/api/cloudsim-metrics
```

## Best Practices

### 1. Generate Realistic Load
- Use your app normally for several minutes before checking results
- Navigate through different pages
- Make API calls
- Perform typical user actions

### 2. Monitor Over Time
- Let the system run for at least 10-15 minutes
- CloudSim accuracy improves with more data
- Watch for trends in performance metrics

### 3. Compare Different Scenarios
- Test with different usage patterns
- Compare light vs. heavy load scenarios
- Analyze performance during different operations

### 4. Use Results for Planning
- Use cost estimates for budget planning
- Use performance recommendations for optimization
- Use scaling suggestions for production deployment

This integration provides a unique window into how your specific Next.js application performs in cloud environments, using real data instead of theoretical models.