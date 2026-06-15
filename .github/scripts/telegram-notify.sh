#!/usr/bin/env bash
#   TG_TOKEN, TG_CHAT            - bot token and chat id (from GitHub secrets)
#   REPO, REF, EVENT, RUN_URL    - GitHub context
#   BT BB FT FB DP               - job results (success|failure|cancelled|skipped)
set -euo pipefail


msg=$(printf '<b>CI/CD</b> — %s\nBranch: %s -- event: %s\n\n%s Backend - Test\n%s Backend - Build\n%s Frontend - Test\n%s Frontend - Build\n%s Docker - Publish to YCR\n\n<a href="%s">Open CI</a>' \
  "$REPO" "$REF" "$EVENT" \
  "$BT" "$BB" "$FT" "$FB" "$DP" \
  "$RUN_URL")

curl -sS -X POST "https://api.telegram.org/bot${TG_TOKEN}/sendMessage" \
  -d chat_id="${TG_CHAT}" \
  -d parse_mode=HTML \
  --data-urlencode "text=${msg}"
