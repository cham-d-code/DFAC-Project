# 🛠️ Applying the "Access Denied" Fix to Azure

This guide explains how to update your hosted application on Azure after applying the code fixes.

## 1. Prerequisites
Open **PowerShell** in your project directory:
```powershell
cd d:\DISTRIBUTED\DFAC-Project
```

Login to Azure if you haven't already:
```powershell
az login
```

### ⚠️ IMPORTANT: Ensure Docker is Running
The error `DOCKER_COMMAND_ERROR` means Docker Desktop is not running.
1. Start **Docker Desktop** from your Start Menu.
2. Wait for the engine to start (whale icon stops animating).
3. Verify it works:
   ```powershell
   docker version
   ```
   If this command fails, the subsequent steps **will not work**.

### Set Your Variables
**Important:** You must use the same registry name you created earlier.
```powershell
# CHECK: Replace 'dfacregistry1234' with YOUR actual registry name
$ACR_NAME = "dfacregistry1234" 
$REGISTRY_URL = "$ACR_NAME.azurecr.io"
```

Login to your registry:
```powershell
az acr login --name $ACR_NAME
```

## 2. Rebuild & Push Images
We need to compile the new Java code into Docker images and send them to the cloud.

### Build Replica Manager
```powershell
docker build -t dfac-replica-manager:v1 -f dfac-replica-manager/Dockerfile .
docker tag dfac-replica-manager:v1 $REGISTRY_URL/dfac-replica-manager:v1
docker push $REGISTRY_URL/dfac-replica-manager:v1
```

### Build Visualizer
```powershell
docker build -t dfac-visualizer:v1 -f dfac-visualizer/Dockerfile .
docker tag dfac-visualizer:v1 $REGISTRY_URL/dfac-visualizer:v1
docker push $REGISTRY_URL/dfac-visualizer:v1
```

## 3. Restart Kubernetes Pods
This forces Kubernetes to pull the new images and restart the application.

```powershell
kubectl rollout restart deployment dfac-rm
kubectl rollout restart deployment dfac-visualizer
```

## 4. Verification
Wait about 1-2 minutes for the pods to restart. You can check the status:
```powershell
kubectl get pods
```
(Wait until all show "Running" and "Age" is recent)

Once verified, access your website again. The "Access Denied" error should be resolved!
