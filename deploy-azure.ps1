# DFAC Azure Deployment Script
# Run this script step by step in PowerShell

# ============================================
# STEP 1: Configuration (EDIT THESE VALUES)
# ============================================
$RESOURCE_GROUP = "dfac-rg"
$LOCATION = "eastus"
$ACR_NAME = "dfacregistry"  # Must be globally unique, use lowercase
$AKS_CLUSTER = "dfac-aks"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DFAC Azure Deployment Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# ============================================
# STEP 2: Login to Azure
# ============================================
Write-Host "`n[1/8] Logging into Azure..." -ForegroundColor Yellow
az login

# ============================================
# STEP 3: Create Resource Group
# ============================================
Write-Host "`n[2/8] Creating Resource Group..." -ForegroundColor Yellow
az group create --name $RESOURCE_GROUP --location $LOCATION

# ============================================
# STEP 4: Create Azure Container Registry
# ============================================
Write-Host "`n[3/8] Creating Azure Container Registry..." -ForegroundColor Yellow
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic
az acr update -n $ACR_NAME --admin-enabled true

# ============================================
# STEP 5: Create AKS Cluster
# ============================================
Write-Host "`n[4/8] Creating AKS Cluster (this takes 5-10 minutes)..." -ForegroundColor Yellow
az aks create `
  --resource-group $RESOURCE_GROUP `
  --name $AKS_CLUSTER `
  --node-count 2 `
  --node-vm-size Standard_B2s `
  --enable-addons monitoring `
  --generate-ssh-keys `
  --attach-acr $ACR_NAME

# Get AKS credentials
Write-Host "`n[5/8] Getting AKS credentials..." -ForegroundColor Yellow
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER

# ============================================
# STEP 6: Build and Push Docker Images
# ============================================
Write-Host "`n[6/8] Building and pushing Docker images..." -ForegroundColor Yellow

# Login to ACR
az acr login --name $ACR_NAME

# Build images
$ACR_LOGIN_SERVER = "$ACR_NAME.azurecr.io"

Write-Host "Building Replica Manager image..." -ForegroundColor Gray
docker build -t "$ACR_LOGIN_SERVER/dfac-replica-manager:v1" -f dfac-replica-manager/Dockerfile .

Write-Host "Building Visualizer image..." -ForegroundColor Gray
docker build -t "$ACR_LOGIN_SERVER/dfac-visualizer:v1" -f dfac-visualizer/Dockerfile .

# Push images
Write-Host "Pushing images to ACR..." -ForegroundColor Gray
docker push "$ACR_LOGIN_SERVER/dfac-replica-manager:v1"
docker push "$ACR_LOGIN_SERVER/dfac-visualizer:v1"

# ============================================
# STEP 7: Deploy to Kubernetes
# ============================================
Write-Host "`n[7/8] Deploying to Kubernetes..." -ForegroundColor Yellow

# Install NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml

# Wait for ingress controller
Write-Host "Waiting for ingress controller to be ready..." -ForegroundColor Gray
Start-Sleep -Seconds 30

# Deploy applications
kubectl apply -f k8s/zookeeper.yaml
Write-Host "Waiting for ZooKeeper to be ready..." -ForegroundColor Gray
kubectl wait --for=condition=ready pod -l app=zookeeper --timeout=180s

kubectl apply -f k8s/replica-manager.yaml
kubectl apply -f k8s/visualizer.yaml
kubectl apply -f k8s/ingress.yaml

# ============================================
# STEP 8: Verify Deployment
# ============================================
Write-Host "`n[8/8] Verifying deployment..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

kubectl get pods
kubectl get svc
kubectl get ingress

# Get external IP
Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`nTo get the external IP address, run:" -ForegroundColor Cyan
Write-Host "  kubectl get ingress dfac-ingress" -ForegroundColor White
Write-Host "`nIt may take a few minutes for the IP to be assigned." -ForegroundColor Gray
