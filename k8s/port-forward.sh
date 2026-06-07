#!/bin/bash

export KUBECONFIG=${KUBECONFIG:-$HOME/.kube/config}

kubectl port-forward --address 0.0.0.0 -n monitoring svc/monitoring-grafana 3000:80 &
kubectl port-forward --address 0.0.0.0 -n monitoring svc/monitoring-kube-prometheus-prometheus 9090:9090 &
kubectl port-forward --address 0.0.0.0 svc/frontend 8080:80 &
kubectl port-forward --address 0.0.0.0 svc/backend 8081:8080 &

wait
