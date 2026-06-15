#!/usr/bin/env bash
#   TG_TOKEN, TG_CHAT            — bot token and chat id (from GitHub secrets)
#   REPO, REF, EVENT, RUN_URL    — GitHub context
#   BT BB FT FB DP               — job results (success|failure|cancelled|skipped)
set -euo pipefail


msg=$(printf '*CI/CD* — %s\nВетка: %s • событие: %s\n\n%s Backend - Test\n%s Backend - Build\n%s Frontend - Test\n%s Frontend - Build\n%s Docker - Publish to YCR\n\n🔗 %s' \
  "$REPO" "$REF" "$EVENT" \
  "$($BT)" "$($BB)" "$($FT)" "$($FB)" "$($DP)" \
  "$RUN_URL")

curl -sS -X POST "https://api.telegram.org/bot${TG_TOKEN}/sendMessage" \
  -d chat_id="${TG_CHAT}" \
  -d parse_mode=Markdown \
  --data-urlencode "text=${msg}"
