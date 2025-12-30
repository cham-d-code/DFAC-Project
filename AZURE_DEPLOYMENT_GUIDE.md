# 🚀 Complete Azure Deployment Guide for DFAC Project

**A Beginner-Friendly, Step-by-Step Guide**

This guide will walk you through deploying your DFAC (Distributed File Access Control) system to Microsoft Azure using Docker containers and Kubernetes. No prior cloud experience required!

---

## 📚 Table of Contents

1. [Understanding the Concepts](#1-understanding-the-concepts)
2. [Prerequisites - What You Need to Install](#2-prerequisites---what-you-need-to-install)
3. [Step 1: Verify Docker is Working](#step-1-verify-docker-is-working)
4. [Step 2: Build Docker Images Locally](#step-2-build-docker-images-locally)
5. [Step 3: Test Locally with Docker Compose](#step-3-test-locally-with-docker-compose)
6. [Step 4: Create Azure Account](#step-4-create-azure-account)
7. [Step 5: Install Azure CLI](#step-5-install-azure-cli)
8. [Step 6: Create Azure Resources](#step-6-create-azure-resources)
9. [Step 7: Push Images to Azure](#step-7-push-images-to-azure)
10. [Step 8: Deploy to Kubernetes](#step-8-deploy-to-kubernetes)
11. [Step 9: Access Your Application](#step-9-access-your-application)
12. [Troubleshooting](#troubleshooting)

---

## 1. Understanding the Concepts

### What is Docker? 🐳
Docker is like a shipping container for software. It packages your application with everything it needs (code, libraries, settings) so it runs the same way everywhere - on your laptop, on a server, or in the cloud.

**Think of it like this:** Instead of saying "it works on my machine," Docker ensures your app works on ANY machine.

### What is Kubernetes (K8s)? ☸️
Kubernetes is like a smart manager for your Docker containers. It:
- **Runs multiple copies** of your app (for handling more users)
- **Restarts crashed containers** automatically (fault tolerance)
- **Distributes traffic** across containers (load balancing)
- **Scales up/down** based on demand

### What is Azure? ☁️
Microsoft Azure is a cloud platform where you can rent computers (virtual machines) and services. Instead of buying servers, you pay monthly for what you use.

### Our Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                         AZURE CLOUD                              │
│                                                                  │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │            AZURE KUBERNETES SERVICE (AKS)                 │  │
│   │                                                           │  │
│   │   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐    │  │
│   │   │ ZooKeeper   │   │ Replica     │   │ Visualizer  │    │  │
│   │   │ (1 pod)     │   │ Manager     │   │ (1 pod)     │    │  │
│   │   │             │   │ (2 pods)    │   │             │    │  │
│   │   │ Coordinates │   │ Manages     │   │ Web UI      │    │  │
│   │   │ locks       │   │ file locks  │   │ for users   │    │  │
│   │   └─────────────┘   └─────────────┘   └─────────────┘    │  │
│   │                                                           │  │
│   └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│   Cost: ~$45/month                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Prerequisites - What You Need to Install

### ✅ Checklist
Before starting, make sure you have:

| Tool | Purpose | Download Link |
|------|---------|---------------|
| **Docker Desktop** | Runs containers locally | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop) |
| **Azure CLI** | Manages Azure from command line | [Install guide below](#step-5-install-azure-cli) |
| **kubectl** | Manages Kubernetes | Installed automatically with Azure CLI |
| **Git** | Version control (already installed) | ✅ You have this |

---

## Step 1: Verify Docker is Working

### 1.1 Start Docker Desktop
1. Open **Docker Desktop** from Start Menu
2. Wait until the whale icon 🐳 in system tray stops animating
3. It should say "Docker Desktop is running"

### 1.2 Verify in Terminal
Open **PowerShell** and run:

```powershell
docker version
```

**Expected output:** You should see Client and Server versions (not an error)

```powershell
docker run hello-world
```

**Expected output:** "Hello from Docker!" message

> ⚠️ **If you see errors:** Make sure Docker Desktop is fully started (whale icon not animating)

---

## Step 2: Build Docker Images Locally

### 2.1 Open PowerShell in Project Directory

```powershell
cd d:\DISTRIBUTED\DFAC-Project
```

### 2.2 Build the Replica Manager Image

```powershell
docker build -t dfac-replica-manager:v1 -f dfac-replica-manager/Dockerfile .
```

**What this does:**
- `-t dfac-replica-manager:v1` = Names the image "dfac-replica-manager" with tag "v1"
- `-f dfac-replica-manager/Dockerfile` = Uses the Dockerfile in that folder
- `.` = Uses current directory as build context

⏱️ **This takes 5-15 minutes** the first time (downloads dependencies)

### 2.3 Build the Visualizer Image

```powershell
docker build -t dfac-visualizer:v1 -f dfac-visualizer/Dockerfile .
```

### 2.4 Verify Images Were Created

```powershell
docker images
```

You should see:
```
REPOSITORY             TAG       SIZE
dfac-replica-manager   v1        ~300MB
dfac-visualizer        v1        ~300MB
```

---

## Step 3: Test Locally with Docker Compose

### 3.1 Start All Services

```powershell
docker-compose up
```

**What this does:**
- Starts ZooKeeper (coordination service)
- Starts 3 Replica Managers
- Starts Visualizer web app
- Creates network between them

### 3.2 Access the Application
Open browser: **http://localhost:8080**

### 3.3 Stop Services
Press `Ctrl+C` in the terminal, then:

```powershell
docker-compose down
```

---

## Step 4: Create Azure Account

### 4.1 Sign Up for Free Account
1. Go to: **https://azure.microsoft.com/free/**
2. Click "Start free"
3. Sign in with Microsoft account (or create one)
4. Complete verification (phone number, credit card for verification only)

### 4.2 What You Get Free
- **$200 credit** for 30 days
- **12 months free** of popular services
- **Always free** services (limited)

> 💡 **Tip:** The $200 credit is enough to run this project for several months!

---

## Step 5: Install Azure CLI

### 5.1 Install on Windows

Open **PowerShell as Administrator** and run:

```powershell
winget install -e --id Microsoft.AzureCLI
```

**Or download installer:** https://aka.ms/installazurecliwindows

### 5.2 Restart PowerShell
Close and reopen PowerShell for changes to take effect.

### 5.3 Verify Installation

```powershell
az --version
```

### 5.4 Login to Azure

```powershell
az login
```

This opens a browser window. Sign in with your Azure account.

---

## Step 6: Create Azure Resources

### 6.1 Set Variables
Run these commands to set up variables (you can change the names):

```powershell
$RESOURCE_GROUP = "dfac-rg"
$LOCATION = "eastus"
$ACR_NAME = "dfacregistry$(Get-Random -Maximum 9999)"  # Must be unique
$AKS_CLUSTER = "dfac-aks"
```

### 6.2 Create Resource Group
A resource group is like a folder for your Azure resources.

```powershell
az group create --name $RESOURCE_GROUP --location $LOCATION
```

### 6.3 Create Container Registry (ACR)
This is where your Docker images will be stored in Azure.

```powershell
# Create the registry
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic

# Enable admin access
az acr update -n $ACR_NAME --admin-enabled true

# Get the registry URL (save this!)
az acr show --name $ACR_NAME --query loginServer --output tsv
```

**Save the output!** It looks like: `dfacregistry1234.azurecr.io`

### 6.4 Create Kubernetes Cluster (AKS)
This creates the Kubernetes cluster where your app will run.

```powershell
az aks create `
  --resource-group $RESOURCE_GROUP `
  --name $AKS_CLUSTER `
  --node-count 2 `
  --node-vm-size Standard_B2s `
  --generate-ssh-keys `
  --attach-acr $ACR_NAME
```

⏱️ **This takes 5-10 minutes**

### 6.5 Get Kubernetes Credentials
This configures kubectl to connect to your cluster.

```powershell
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER
```

### 6.6 Verify Connection

```powershell
kubectl get nodes
```

You should see 2 nodes in "Ready" status.

---

## Step 7: Push Images to Azure

### 7.1 Login to Container Registry

```powershell
az acr login --name $ACR_NAME
```

### 7.2 Tag Images for Azure
Replace `dfacregistry1234` with YOUR registry name from Step 6.3:

```powershell
$REGISTRY = "dfacregistry1234.azurecr.io"  # Your registry URL

docker tag dfac-replica-manager:v1 $REGISTRY/dfac-replica-manager:v1
docker tag dfac-visualizer:v1 $REGISTRY/dfac-visualizer:v1
```

### 7.3 Push Images

```powershell
docker push $REGISTRY/dfac-replica-manager:v1
docker push $REGISTRY/dfac-visualizer:v1
```

⏱️ **This takes 5-10 minutes** (uploading images)

### 7.4 Verify Images in Azure

```powershell
az acr repository list --name $ACR_NAME --output table
```

---

## Step 8: Deploy to Kubernetes

### 8.1 Update Kubernetes Files
Before deploying, update the image names in the YAML files:

**Edit `k8s/replica-manager.yaml` line 51:**
```yaml
image: YOUR_REGISTRY.azurecr.io/dfac-replica-manager:v1
```

**Edit `k8s/visualizer.yaml` line 46:**
```yaml
image: YOUR_REGISTRY.azurecr.io/dfac-visualizer:v1
```

### 8.2 Install NGINX Ingress Controller

```powershell
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
```

Wait 2 minutes for it to start.

### 8.3 Deploy ZooKeeper

```powershell
kubectl apply -f k8s/zookeeper.yaml
```

Wait for it to be ready:
```powershell
kubectl get pods -w
```

Press `Ctrl+C` when you see "Running" status.

### 8.4 Deploy Replica Manager

```powershell
kubectl apply -f k8s/replica-manager.yaml
```

### 8.5 Deploy Visualizer

```powershell
kubectl apply -f k8s/visualizer.yaml
```

### 8.6 Deploy Ingress (External Access)

```powershell
kubectl apply -f k8s/ingress.yaml
```

### 8.7 Check All Pods

```powershell
kubectl get pods
```

All pods should show "Running" status.

---

## Step 9: Access Your Application

### 9.1 Get External IP Address

```powershell
kubectl get ingress
```

**Note:** It may take 2-5 minutes for the IP to be assigned.

Look for the "ADDRESS" column - this is your public IP!

### 9.2 Access the Application
Open browser and go to: `http://YOUR_EXTERNAL_IP`

🎉 **Congratulations!** Your DFAC system is now running in the cloud!

---

## Step 10: Manage Costs

### View Current Spending

```powershell
az consumption usage list --output table
```

Or visit: https://portal.azure.com → Cost Management

### Stop Everything (Saves Money!)
When not using the cluster, you can stop it:

```powershell
az aks stop --name $AKS_CLUSTER --resource-group $RESOURCE_GROUP
```

### Start Again

```powershell
az aks start --name $AKS_CLUSTER --resource-group $RESOURCE_GROUP
```

### Delete Everything (Stops All Charges)

```powershell
az group delete --name $RESOURCE_GROUP --yes
```

> ⚠️ **Warning:** This deletes EVERYTHING permanently!

---

## Troubleshooting

### Docker Build Fails
```powershell
# Clean up and try again
docker system prune -a
docker build --no-cache -t dfac-replica-manager:v1 -f dfac-replica-manager/Dockerfile .
```

### Pods Not Starting
```powershell
# Check pod status
kubectl describe pod POD_NAME

# Check logs
kubectl logs POD_NAME
```

### Can't Access Application
```powershell
# Check ingress
kubectl get ingress

# Check services
kubectl get svc
```

### Azure Login Issues
```powershell
# Clear credentials and login again
az logout
az login
```

---

## Quick Reference Commands

| Task | Command |
|------|---------|
| Check Docker | `docker version` |
| Build image | `docker build -t NAME:TAG -f Dockerfile .` |
| List images | `docker images` |
| Azure login | `az login` |
| Get AKS credentials | `az aks get-credentials --resource-group RG --name CLUSTER` |
| List pods | `kubectl get pods` |
| View pod logs | `kubectl logs POD_NAME` |
| Get external IP | `kubectl get ingress` |
| Stop AKS | `az aks stop --name CLUSTER --resource-group RG` |

---

## Estimated Monthly Cost: ~$45

| Resource | Cost |
|----------|------|
| 2x Standard_B2s VMs | ~$30 |
| Container Registry (Basic) | ~$5 |
| Load Balancer | ~$10 |
| **Total** | **~$45/month** |

> 💡 Use the $200 free credit to test for ~4 months at no cost!
