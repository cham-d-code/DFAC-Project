# DFAC-Project (Distributed File Access Control)

A distributed system project designed to manage file access control across multiple replicas with fault tolerance and visualization.

## 📂 Project Structure Explained

Here is what every file and folder is for:

- **`dfac-replica-manager/`**: The core backend server.
  - Acts as a "Replica Manager" (RM) that handles file locking requests.
  - Implements the logic for distributed coordination using ZooKeeper.
  - Exposes a gRPC interface for clients and the visualizer.

- **`dfac-visualizer/`**: The web-based frontend application.
  - Provides a User Interface (UI) to view the status of all Replica Managers.
  - Allows users to request/release locks manually for testing.
  - Communicates with RMs via gRPC.

- **`dfac-worker/`**: A command-line client for stress testing.
  - Simulates multiple workers requesting access to files to test the system's robustness.

- **`dfac-common/`**: Shared code.
  - Contains Protocol Buffer (`.proto`) definitions shared between RM, Visualizer, and Worker.
  - Common utility classes.

- **`k8s/`**: Kubernetes Manifests.
  - Contains `.yaml` files to deploy the system to a Kubernetes cluster (like Azure AKS).
  - Defines Deployments, Services, and Ingress rules.

- **`docker-compose.yml`**: Local orchestration file.
  - Defines how to run the entire system (Zookeeper + 3 RMs + Visualizer) locally on your machine using Docker.
  - `docker-compose up` spins up the whole environment in seconds.

- **`AZURE_DEPLOYMENT_GUIDE.md`**: Cloud documentation.
  - A step-by-step guide to deploying this system to Microsoft Azure.

- **`deploy-azure.ps1`**: Automation script.
  - A helper PowerShell script to automate some Azure setup steps.

## ⚙️ How It Works

1. **Coordination (ZooKeeper)**:
   - ZooKeeper acts as the "source of truth". It keeps track of which Replica Manager is the "Leader" and handles distributed locks.

2. **Replica Managers (RM)**:
   - You run multiple RMs (e.g., RM-1, RM-2, RM-3).
   - They coordinate with each other via ZooKeeper to decide who can access a file.
   - If one RM crashes, others continue to work (Fault Tolerance).

3. **Visualizer**:
   - Polling: The Visualizer asks RMs for their status every few seconds.
   - User Actions: When you click "Lock" in the UI, the Visualizer sends a gRPC command to an RM.

## ☁️ Cloud Architecture (Azure)

The system is designed to run on **Azure Kubernetes Service (AKS)**.

- **Container Registry (ACR)**: Stores your Docker images (like a private Docker Hub).
- **Kubernetes Cluster (AKS)**:
  - **Pods**: Runs instances of your apps (e.g., 3 pods for RMs, 1 pod for Visualizer).
  - **Services**: Internal load balancers that let pods talk to each other.
  - **Ingress**: The entry point that lets you access the Visualizer from the internet.

### Cloud Workflow
1. **Build**: Docker images are built locally.
2. **Push**: Images are pushed to Azure Container Registry (ACR).
3. **Deploy**: Kubernetes pulls images from ACR and starts them on the cluster.
4. **Access**: You access the app via a public IP provided by the Azure Load Balancer.

## 🚀 Quick Start (Local)

1. **Build the project**:
   ```powershell
   gradle build
   ```

2. **Run with Docker Compose**:
   ```powershell
   docker-compose up
   ```

3. **Access UI**: [http://localhost:8080](http://localhost:8080)

For full cloud deployment, see [AZURE_DEPLOYMENT_GUIDE.md](AZURE_DEPLOYMENT_GUIDE.md).
