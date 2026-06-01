#!/bin/bash
minikube kubectl -- get secret monitoring-grafana -n monitoring -o jsonpath="{.data.admin-password}" | base64 -d