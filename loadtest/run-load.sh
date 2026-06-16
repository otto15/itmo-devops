#!/bin/bash
# open  http://localhost:8089  and press Start (fields prefilled).
# Ctrl-C stops the generator.
set -e

TARGET="${1:-http://192.168.122.17}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "============================================================"
echo " Locust UI : http://localhost:8089"
echo "============================================================"

exec docker run --rm \
  -p 8089:8089 \
  -v "$SCRIPT_DIR/locustfile.py:/mnt/locust/locustfile.py:ro" \
  locustio/locust:2.31.8 \
  -f /mnt/locust/locustfile.py --host "$TARGET" -u 200 -r 20
