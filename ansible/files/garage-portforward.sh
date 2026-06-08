#!/bin/bash
# Exposes cluster services on the VM's LAN interface (0.0.0.0) so they are
# reachable from other hosts on the local network as http://<VM_IP>:<port>.
# Managed by the garage-portforward.service systemd unit.
#
# Each service is forwarded in its own retry loop, so one missing/restarting
# service (e.g. the on-demand Locust UI) never breaks the others.
set -u

export KUBECONFIG=/home/devops/.kube/config
KUBECTL=/usr/local/bin/kubectl

# Wait until the API server is reachable (cluster may still be coming up).
until $KUBECTL get nodes >/dev/null 2>&1; do
  echo "waiting for kube-apiserver..."
  sleep 5
done

# forward <namespace|-> <svc> <local:remote>
forward() {
  local ns="$1" svc="$2" map="$3" nsarg=""
  [ "$ns" != "-" ] && nsarg="-n $ns"
  while true; do
    $KUBECTL port-forward --address 0.0.0.0 $nsarg "svc/$svc" "$map" 2>/dev/null
    sleep 3   # service/pod gone or restarted — retry
  done
}

forward monitoring grafana                                     3000:3000 &   # Grafana
forward monitoring prometheus                                  9090:9090 &   # Prometheus
forward garage-app frontend                                    8080:80   &   # Frontend web UI
forward garage-app backend                                     8081:8080 &   # Backend API

wait
