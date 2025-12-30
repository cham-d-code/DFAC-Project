# 🔒 How to Get an HTTPS Address for Your Application

To get a secure `https://` address, you cannot just use a raw IP address (like `20.12.34.56`). You need a **Domain Name** and a **TLS Certificate**.

We will use a free trick called **sslip.io** (which turns your IP into a domain) and **cert-manager** (which automatically gets a free certificate from Let's Encrypt).

---

## Step 1: Install cert-manager
`cert-manager` is a tool for Kubernetes that talks to Let's Encrypt to get certificates for you.

Run these commands in your PowerShell:

```powershell
# Add the Jetstack Helm repository
helm repo add jetstack https://charts.jetstack.io
helm repo update

# Install cert-manager
helm install cert-manager jetstack/cert-manager `
  --namespace cert-manager `
  --create-namespace `
  --version v1.13.3 `
  --set installCRDs=true
```

> **Note:** If you don't have `helm` installed, [install it here](https://helm.sh/docs/intro/install/) or use `winget install Helm.Helm`.

---

## Step 2: Create a "ClusterIssuer"
This tells `cert-manager` how to contact Let's Encrypt.

1. Create a file named `k8s/cluster-issuer.yaml`:

```yaml
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: YOUR_EMAIL@example.com  # <--- REPLACE THIS WITH YOUR EMAIL
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
      - http01:
          ingress:
            class: nginx
```

2. Apply it to your cluster:
```powershell
kubectl apply -f k8s/cluster-issuer.yaml
```

---

## Step 3: Get Your External IP
Find the public IP address your Ingress is using:

```powershell
kubectl get ingress
```

Copy the **ADDRESS** (e.g., `20.40.10.5`).

---

## Step 4: Update Your Ingress for HTTPS
Now we configure the Ingress to use your IP as a domain (using `sslip.io`) and request a certificate.

Edit `k8s/ingress.yaml`:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: dfac-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/proxy-body-size: "50m"
    # Enable cert-manager
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
    - hosts:
        # REPLACE THE IP BELOW with your actual External IP from Step 3
        - dfac.20.40.10.5.sslip.io
      secretName: dfac-tls-secret
  rules:
    - host: dfac.20.40.10.5.sslip.io  # <--- UPDATE THIS TOO
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: dfac-visualizer-service
                port:
                  number: 80
```

---

## Step 5: Apply Changes
```powershell
kubectl apply -f k8s/ingress.yaml
```

Wait about 1-2 minutes. Then open your browser to:
**https://dfac.20.40.10.5.sslip.io** (Replace with your IP)

You should see the secure lock icon! 🔒
