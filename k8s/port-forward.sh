#!/bin/bash
kubectl port-forward --address 0.0.0.0 -n monitoring svc/monitoring-grafana 30565:80 &
kubectl port-forward --address 0.0.0.0 -n monitoring svc/monitoring-kube-prometheus-prometheus 30090:9090 &
kubectl port-forward --address 0.0.0.0 svc/backend 8081:8080 &
kubectl port-forward --address 0.0.0.0 svc/frontend 8080:80 &

wait
