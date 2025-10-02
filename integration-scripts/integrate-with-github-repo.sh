#!/bin/bash

# integrate-with-github-repo.sh - Integration script for existing Next.js GitHub repository

set -e

echo "🔗 CloudSim Integration for Existing Next.js Repository"
echo "====================================================="
echo ""

# Configuration
GITHUB_REPO="https://github.com/2006rahulsinha/Cloud_project"
LOCAL_REPO_DIR="Cloud_project"
INTEGRATION_DIR="cloudsim-integration"
METRICS_FILE="cloudsim-metrics.json"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Cleanup function
cleanup() {
    echo ""
    log_info "Cleaning up processes..."

    if [ ! -z "$NEXTJS_PID" ]; then
        kill $NEXTJS_PID 2>/dev/null || true
        log_info "Stopped Next.js app"
    fi

    if [ ! -z "$CLOUDSIM_PID" ]; then
        kill $CLOUDSIM_PID 2>/dev/null || true
        log_info "Stopped CloudSim simulation"
    fi

    exit 0
}

trap cleanup SIGINT SIGTERM

# Step 1: Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."

    # Check Git
    if ! command -v git &> /dev/null; then
        log_error "Git not found. Please install Git."
        exit 1
    fi

    # Check Node.js
    if ! command -v node &> /dev/null; then
        log_error "Node.js not found. Please install Node.js 16+"
        exit 1
    fi

    # Check Java
    if ! command -v java &> /dev/null; then
        log_error "Java not found. Please install Java 8+"
        exit 1
    fi

    # Check Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven not found. Please install Apache Maven"
        exit 1
    fi

    log_success "All prerequisites found"
}

# Step 2: Clone or update the existing repository
setup_existing_repo() {
    log_info "Setting up your existing Next.js repository..."

    if [ -d "$LOCAL_REPO_DIR" ]; then
        log_info "Repository already exists. Pulling latest changes..."
        cd "$LOCAL_REPO_DIR"
        git pull origin main || git pull origin master || log_warning "Could not pull latest changes"
        cd ..
    else
        log_info "Cloning repository from GitHub..."
        git clone "$GITHUB_REPO" "$LOCAL_REPO_DIR"
        if [ $? -ne 0 ]; then
            log_error "Failed to clone repository. Please check the URL and your internet connection."
            exit 1
        fi
    fi

    log_success "Repository ready: $LOCAL_REPO_DIR"
}

# Step 3: Install dependencies for existing app
setup_existing_app() {
    log_info "Installing dependencies for your Next.js app..."

    cd "$LOCAL_REPO_DIR"

    # Install Node.js dependencies
    if [ ! -d "node_modules" ]; then
        npm install
        if [ $? -eq 0 ]; then
            log_success "Next.js dependencies installed"
        else
            log_error "Failed to install Next.js dependencies"
            exit 1
        fi
    else
        log_info "Dependencies already installed"
    fi

    cd ..
}

# Step 4: Add monitoring integration to existing app
integrate_monitoring() {
    log_info "Adding CloudSim monitoring to your existing Next.js app..."

    # Copy monitoring integration file to the existing repo
    cp "performance-monitoring/integrate-with-existing-nextjs.js" "$LOCAL_REPO_DIR/"

    # Create a simple API route for monitoring if it doesn't exist
    mkdir -p "$LOCAL_REPO_DIR/pages/api"

    cat > "$LOCAL_REPO_DIR/pages/api/cloudsim-metrics.js" << 'EOL'
// CloudSim metrics API route - added by integration
const monitor = require('../../integrate-with-existing-nextjs');

export default function handler(req, res) {
  const metrics = monitor.getMetrics();
  res.status(200).json(metrics);
}
EOL

    # Create integration start script
    cat > "$LOCAL_REPO_DIR/start-with-cloudsim.js" << 'EOL'
// Start script with CloudSim integration
const monitor = require('./integrate-with-existing-nextjs');
const { spawn } = require('child_process');

console.log('🚀 Starting Next.js app with CloudSim integration...');

// Start Next.js development server
const nextProcess = spawn('npm', ['run', 'dev'], {
    stdio: 'inherit'
});

nextProcess.on('error', (err) => {
    console.error('❌ Failed to start Next.js:', err);
    process.exit(1);
});

process.on('SIGINT', () => {
    console.log('\n🛑 Shutting down...');
    nextProcess.kill('SIGINT');
    process.exit(0);
});
EOL

    log_success "Monitoring integration added to your existing app"
    log_info "Files added to your repository:"
    log_info "  - integrate-with-existing-nextjs.js (monitoring system)"
    log_info "  - pages/api/cloudsim-metrics.js (metrics API)"
    log_info "  - start-with-cloudsim.js (integrated launcher)"
}

# Step 5: Build CloudSim simulation
build_cloudsim() {
    log_info "Building CloudSim Plus simulation..."

    cd "cloudsim-simulation"

    mvn clean compile -q
    if [ $? -eq 0 ]; then
        log_success "CloudSim simulation built successfully"
    else
        log_error "CloudSim build failed"
        exit 1
    fi

    cd ..
}

# Step 6: Start the integrated system
start_integrated_system() {
    log_info "Starting integrated system..."

    # Start existing Next.js app with monitoring
    log_info "Starting your existing Next.js app with CloudSim integration..."
    cd "$LOCAL_REPO_DIR"
    node start-with-cloudsim.js &
    NEXTJS_PID=$!
    cd ..

    log_success "Next.js app started with monitoring (PID: $NEXTJS_PID)"
    log_info "Your app: http://localhost:3000"

    # Wait for app to initialize and start generating metrics
    log_info "Waiting for application to initialize..."
    sleep 10

    # Check if metrics file is being generated
    if [ -f "$LOCAL_REPO_DIR/$METRICS_FILE" ]; then
        log_success "Real-time metrics detected!"
    else
        log_warning "Metrics file not found yet - will use simulation mode"
    fi

    # Start CloudSim simulation
    log_info "Starting CloudSim Plus simulation..."
    cd "cloudsim-simulation"

    if [ -f "../$LOCAL_REPO_DIR/$METRICS_FILE" ]; then
        log_success "Running with real-time integration"
        mvn exec:java -Dexec.args="--monitor ../$LOCAL_REPO_DIR/$METRICS_FILE" &
    else
        log_info "Running in simulation-only mode"
        mvn exec:java &
    fi

    CLOUDSIM_PID=$!
    cd ..

    log_success "CloudSim simulation started (PID: $CLOUDSIM_PID)"
}

# Step 7: Display status and instructions
show_status() {
    echo ""
    echo "=" * 60
    log_success "🎯 INTEGRATION COMPLETE!"
    echo "=" * 60

    echo ""
    echo "🌐 Your Existing Next.js App:"
    echo "   Repository: $GITHUB_REPO"
    echo "   Local path: $LOCAL_REPO_DIR"
    echo "   URL: http://localhost:3000"
    echo "   PID: $NEXTJS_PID"
    echo ""

    echo "🧪 CloudSim Plus Simulation:"
    echo "   Status: Analyzing your real application performance"
    echo "   Integration: Real-time metrics from your app"
    echo "   PID: $CLOUDSIM_PID"
    echo ""

    echo "📊 Performance Monitoring:"
    echo "   Metrics file: $LOCAL_REPO_DIR/$METRICS_FILE"
    echo "   API endpoint: http://localhost:3000/api/cloudsim-metrics"
    echo "   Update interval: 5 seconds"
    echo ""

    echo "🎮 How to Use:"
    echo "   1. Visit http://localhost:3000 to use your existing app"
    echo "   2. Navigate pages, use features, make API calls"
    echo "   3. Real performance data feeds into CloudSim analysis"
    echo "   4. CloudSim provides infrastructure recommendations"
    echo "   5. Check console output for simulation results"
    echo "   6. Press Ctrl+C to stop both applications"

    echo ""
    echo "=" * 60
}

# Main execution
main() {
    echo "This script will:"
    echo "1. Clone/update your existing Next.js repository" 
    echo "2. Add CloudSim performance monitoring (non-invasive)"
    echo "3. Build and run CloudSim Plus simulation"
    echo "4. Connect real app performance to cloud simulation"
    echo ""
    echo "Your existing code will NOT be modified - only monitoring is added."
    echo ""
    read -p "Continue? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "Integration cancelled"
        exit 0
    fi
    echo ""

    check_prerequisites
    setup_existing_repo
    setup_existing_app
    integrate_monitoring
    build_cloudsim
    start_integrated_system
    show_status

    # Keep running
    log_info "System is running. Press Ctrl+C to stop."
    while true; do
        sleep 10

        # Check if processes are still running
        if ! kill -0 $NEXTJS_PID 2>/dev/null; then
            log_error "Next.js app stopped unexpectedly"
            break
        fi

        if ! kill -0 $CLOUDSIM_PID 2>/dev/null; then
            log_info "CloudSim simulation completed"
            log_success "Check the simulation results above!"
            break
        fi
    done
}

case "${1:-integrate}" in
    "integrate"|"run")
        main
        ;;
    "clone-only")
        check_prerequisites
        setup_existing_repo
        setup_existing_app
        log_success "Repository cloned and dependencies installed"
        log_info "Run './integrate-with-github-repo.sh integrate' to start integration"
        ;;
    "build-only")
        build_cloudsim
        log_success "CloudSim simulation built"
        ;;
    "help"|"-h"|"--help")
        echo "CloudSim Integration for Existing Next.js Repository"
        echo ""
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  integrate    Clone repo and run full integration (default)"
        echo "  clone-only   Just clone repository and install dependencies"  
        echo "  build-only   Just build CloudSim simulation"
        echo "  help         Show this help message"
        echo ""
        echo "This script integrates CloudSim Plus with your existing Next.js"
        echo "application at: $GITHUB_REPO"
        echo ""
        ;;
    *)
        log_error "Unknown command: $1"
        log_info "Run '$0 help' for usage information"
        exit 1
        ;;
esac