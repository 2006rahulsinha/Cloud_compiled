# CloudSim Plus Integration for Your Existing Next.js App

🔗 **Seamlessly integrate CloudSim Plus simulation with your existing Next.js application from GitHub**

This integration package adds real-time performance monitoring and cloud simulation capabilities to your existing Next.js application at `https://github.com/2006rahulsinha/Cloud_project` without modifying your existing code.

## 🌟 What This Does

- **📊 Monitors Your Existing App**: Adds non-invasive performance monitoring to your current Next.js application
- **🧪 Real-Time CloudSim Analysis**: Uses actual performance data from your app to drive cloud simulations
- **💡 Infrastructure Recommendations**: Provides optimization suggestions based on real usage patterns
- **🔄 Zero Code Changes**: Your existing codebase remains untouched - monitoring is added externally

## 🚀 Quick Start

### Prerequisites
- **Node.js 16+**: [Download](https://nodejs.org/)
- **Java 8+**: [Download](https://adoptium.net/)
- **Maven 3.6+**: [Download](https://maven.apache.org/)
- **Git**: [Download](https://git-scm.com/)

### Option 1: Automatic Integration (Recommended)

```bash
# Download and extract this integration package
unzip cloudsim-integration-for-existing-nextjs.zip
cd cloudsim-integration-for-existing-nextjs

# Run the integration script (Linux/macOS)
chmod +x integration-scripts/integrate-with-github-repo.sh
./integration-scripts/integrate-with-github-repo.sh

# Windows users
integration-scripts\integrate-with-github-repo.bat
```

### Option 2: Manual Integration

```bash
# 1. Clone your existing repository
git clone https://github.com/2006rahulsinha/Cloud_project
cd Cloud_project
npm install

# 2. Copy the monitoring integration
cp ../performance-monitoring/integrate-with-existing-nextjs.js ./

# 3. Add monitoring to your app (add this to any API route or page)
const monitor = require('./integrate-with-existing-nextjs');
// Your existing code remains the same

# 4. Start your app with monitoring
node -e "require('./integrate-with-existing-nextjs'); require('child_process').spawn('npm', ['run', 'dev'], {stdio: 'inherit'})"

# 5. In another terminal, run CloudSim
cd ../cloudsim-simulation
mvn clean compile
mvn exec:java -Dexec.args="--monitor ../Cloud_project/cloudsim-metrics.json"
```

## 📊 How It Works

1. **Your Existing App Runs**: Your Next.js application starts normally at `localhost:3000`
2. **Performance Monitoring**: The integration monitors:
   - Response times for pages and API routes
   - CPU and memory usage
   - Request patterns and error rates
   - Page view statistics
3. **Real-Time Data Export**: Metrics are written to `cloudsim-metrics.json` every 5 seconds
4. **CloudSim Analysis**: The simulation reads real data and:
   - Adjusts VM specifications based on actual CPU usage
   - Scales workloads based on real response times
   - Models infrastructure costs based on actual usage
   - Provides optimization recommendations

## 🎯 Integration Features

### Non-Invasive Monitoring
- **Zero Code Changes**: Your existing application code is not modified
- **External Integration**: Monitoring is added as a separate module
- **Optional API Routes**: Optionally adds `/api/cloudsim-metrics` endpoint
- **Graceful Degradation**: If monitoring fails, your app continues normally

### Real-Time Performance Analysis
```javascript
// The monitoring automatically tracks:
{
  "projectName": "Cloud_project",
  "responseTime": 125.50,      // Average response time
  "cpuUsage": 45.2,           // CPU utilization %
  "memoryUsage": 67.8,        // Memory usage in MB
  "requestCount": 1547,       // Total requests processed
  "activeConnections": 8,     // Current active connections
  "pages": {
    "home": 245,              // Home page visits
    "api": 892,               // API route calls
    "other": 387              // Other pages
  }
}
```

### CloudSim Plus Analysis
- **Dynamic VM Scaling**: VM specs adjust based on real CPU usage
- **Workload Modeling**: Cloudlets scale with actual response times
- **Cost Analysis**: Real infrastructure cost projections
- **Performance Recommendations**: Specific optimization suggestions

## 📈 Expected Results

### Real vs. Simulated Comparison
```
🔄 Real App vs CloudSim Comparison
==================================
🌐 Real Response Time: 125.50 ms
🖥️  Simulated Avg Time: 132.40 ms
🎯 Simulation accuracy: 94.8%
📊 Real CPU Usage: 45.2% (influenced infrastructure scaling)
```

### Infrastructure Recommendations
```
💡 Optimization Recommendations for Cloud_project
================================================
✅ Page rendering is optimal (6.2s avg)
⚠️ API processing is slow (3.1s avg)
   💡 Consider: Database optimization, Redis cache
✅ Build process is efficient (28.2s avg)

🔧 Infrastructure Recommendations:
✅ CPU usage is optimal (45.2%)
🚀 Excellent response times (125.5ms)
```

### Cost Analysis
```
💰 Infrastructure Cost Analysis
==============================
💵 Estimated hourly cost: $0.4720
📅 Estimated monthly cost: $339.84
📊 Cost per request: $0.000305
```

## 🛠️ Files Added to Your Repository

When you run the integration, these files are added to your existing Next.js project:

```
Cloud_project/                           # Your existing repository
├── integrate-with-existing-nextjs.js    # Performance monitoring system
├── cloudsim-metrics.json               # Real-time metrics (auto-generated)
├── pages/api/cloudsim-metrics.js        # Optional metrics API endpoint
└── start-with-cloudsim.js              # Integrated launcher script
```

**Your existing files are NOT modified.**

## 🎮 Usage Instructions

### 1. Start the Integrated System
```bash
# The integration script automatically:
# - Clones your repository
# - Installs dependencies  
# - Adds monitoring
# - Starts your app with CloudSim
```

### 2. Use Your Existing App
- Visit `http://localhost:3000` (your normal app URL)
- Navigate through your existing pages
- Use your existing API routes
- Perform normal user interactions

### 3. View Real-Time Analysis
- Monitor console output for CloudSim analysis
- Check `cloudsim-metrics.json` for real-time data
- Visit `http://localhost:3000/api/cloudsim-metrics` for metrics API

### 4. Get Infrastructure Recommendations
- CloudSim provides specific recommendations based on your app's performance
- Cost analysis helps with cloud infrastructure planning
- Performance insights identify optimization opportunities

## 🔧 Configuration Options

### Monitoring Configuration
Edit `integrate-with-existing-nextjs.js` to customize:

```javascript
const monitor = new ExistingNextJSMonitor({
    metricsFile: 'cloudsim-metrics.json',
    updateInterval: 5000,           // Update every 5 seconds
    enableConsoleOutput: true,      // Show metrics in console
    projectName: 'Your-App-Name',   // Custom project name
    maxRequestHistory: 1000         // Keep last 1000 requests
});
```

### CloudSim Configuration
Edit `cloudsim-simulation/resources/simulation.properties`:

```properties
# VM Configuration
vm.template.nextjs.cores=4
vm.template.nextjs.ram=4096

# Performance Thresholds
threshold.response.time.warning=300
threshold.cpu.usage.warning=75

# Cost Configuration
datacenter.cost.processing=2.80
```

## 📊 Advanced Features

### Custom Event Tracking
Add custom tracking to your existing code:

```javascript
const monitor = require('./integrate-with-existing-nextjs');

// Track custom events
monitor.trackCustomEvent('user-login', 150, { userId: 123 });
monitor.trackCustomEvent('database-query', 45, { query: 'SELECT * FROM users' });
```

### API Route Integration
Wrap existing API routes for automatic tracking:

```javascript
// In your existing API routes
const monitor = require('../../integrate-with-existing-nextjs');

export default monitor.trackApiRoute(async (req, res) => {
    // Your existing API code here - no changes needed
    const result = await yourExistingFunction();
    res.json(result);
});
```

### Page View Tracking
Add to existing pages for detailed analytics:

```javascript
// In your existing pages
import { useEffect } from 'react';
const monitor = require('../integrate-with-existing-nextjs');

export default function YourExistingPage() {
    useEffect(() => {
        monitor.trackPageView('/your-page');
    }, []);

    // Your existing component code - no changes needed
    return <YourExistingComponent />;
}
```

## 🚨 Troubleshooting

### Common Issues

1. **Port 3000 already in use**
   ```bash
   # Find and stop conflicting process
   lsof -ti:3000 | xargs kill -9
   ```

2. **Repository clone fails**
   ```bash
   # Check internet connection and repository URL
   git clone https://github.com/2006rahulsinha/Cloud_project
   ```

3. **CloudSim build fails**
   ```bash
   cd cloudsim-simulation
   mvn clean compile -X  # Detailed error output
   ```

4. **Metrics file not generated**
   - Check if Next.js app is running
   - Verify monitoring integration is loaded
   - Check file permissions

### Debug Mode
Enable detailed logging:

```bash
export DEBUG=true
./integration-scripts/integrate-with-github-repo.sh
```

## 🎯 Research Applications

Perfect for:

- **Performance Analysis**: Understanding your app's real-world performance characteristics
- **Infrastructure Planning**: Determining optimal cloud configurations for your specific app
- **Cost Optimization**: Analyzing cloud costs based on actual usage patterns
- **Academic Research**: Studying real application performance vs. simulated environments
- **DevOps Optimization**: Making data-driven infrastructure decisions

## 🔮 What Makes This Special

Unlike generic cloud simulations, this integration:

✅ **Uses YOUR actual application performance data**  
✅ **Provides recommendations specific to YOUR codebase**  
✅ **Analyzes YOUR real user interaction patterns**  
✅ **Estimates costs based on YOUR actual usage**  
✅ **Requires ZERO changes to your existing code**  

## 📞 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Verify all prerequisites are installed correctly
3. Ensure your GitHub repository is accessible
4. Check that port 3000 is available

## 🎊 Ready to Analyze Your Real App!

This integration provides unique insights into how your existing Next.js application would perform in various cloud configurations, using actual performance data rather than theoretical models.

**Download the integration package and discover the optimal cloud infrastructure for your specific application!**

---

🚀 **Your existing Next.js app + CloudSim Plus = Data-driven cloud optimization!**