#!/bin/bash
# =============================================================================
# Music Application — Semi-Automated System Integration Test
# Tests all microservices + API Gateway end-to-end with real HTTP calls.
# Usage: chmod +x system_integration_test.sh && ./system_integration_test.sh
# Assumes all services are running locally (docker-compose or manual).
# =============================================================================

# ─── Colours ─────────────────────────────────────────────────────────────────
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# ─── Configuration ────────────────────────────────────────────────────────────
GATEWAY="http://localhost:8080"
ARTIST_SVC="http://localhost:7001"
USER_SVC="http://localhost:7002"
PODCAST_SVC="http://localhost:7003"
CATALOG_SVC="http://localhost:7004"
ORDER_SVC="http://localhost:7005"
AD_SVC="http://localhost:7006"

LOG_FILE="system_integration_results_$(date +%Y%m%d_%H%M%S).log"
PASS=0
FAIL=0
SKIP=0

# IDs captured during tests — shared across sections
ARTIST_ID=""
ALBUM_ID=""
USER_ID=""
USERNAME=""
PODCAST_ID=""
ORDER_ID=""
AD_ID=""

# ─── Helpers ─────────────────────────────────────────────────────────────────

log() { echo -e "$1" | tee -a "$LOG_FILE"; }

header() {
    log ""
    log "${CYAN}${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    log "  $1"
    log "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
}

# assert_status <test_name> <expected_status> <actual_status> [response_body]
assert_status() {
    local name="$1"
    local expected="$2"
    local actual="$3"
    local body="$4"

    if [ "$actual" -eq "$expected" ]; then
        log "${GREEN}  ✅ PASS${RESET} — $name (HTTP $actual)"
        ((PASS++))
    else
        log "${RED}  ❌ FAIL${RESET} — $name | expected HTTP $expected, got $actual"
        [ -n "$body" ] && log "     Body: $body"
        ((FAIL++))
    fi
}

# http_get <url> → prints "STATUS BODY"
http_get()    { curl -s -o /tmp/sit_body -w "%{http_code}" "$1"; cat /tmp/sit_body; }
http_post()   { curl -s -o /tmp/sit_body -w "%{http_code}" -X POST   -H "Content-Type: application/json" -d "$2" "$1"; cat /tmp/sit_body; }
http_put()    { curl -s -o /tmp/sit_body -w "%{http_code}" -X PUT    -H "Content-Type: application/json" -d "$2" "$1"; cat /tmp/sit_body; }
http_patch()  { curl -s -o /tmp/sit_body -w "%{http_code}" -X PATCH  -H "Content-Type: application/json" "$1"; cat /tmp/sit_body; }
http_delete() { curl -s -o /tmp/sit_body -w "%{http_code}" -X DELETE "$1"; cat /tmp/sit_body; }

do_request() {
    local method="$1" url="$2" body="$3"
    case "$method" in
        GET)    STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" "$url") ;;
        POST)   STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" -X POST   -H "Content-Type: application/json" -d "$body" "$url") ;;
        PUT)    STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" -X PUT    -H "Content-Type: application/json" -d "$body" "$url") ;;
        PATCH)  STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" -X PATCH  -H "Content-Type: application/json" "$url") ;;
        DELETE) STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" -X DELETE "$url") ;;
    esac
    BODY=$(cat /tmp/sit_body)
    echo "$STATUS"
}

extract_field() { echo "$1" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('$2',''))" 2>/dev/null; }

# ─── Health Checks ────────────────────────────────────────────────────────────

check_service() {
    local name="$1" url="$2"
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 3 "$url/actuator/health" 2>/dev/null)
    if [ "$STATUS" = "200" ]; then
        log "${GREEN}  ✅ $name is UP${RESET} ($url)"
        return 0
    else
        log "${YELLOW}  ⚠️  $name is DOWN${RESET} ($url) — skipping its tests"
        return 1
    fi
}

# ═════════════════════════════════════════════════════════════════════════════
# MAIN
# ═════════════════════════════════════════════════════════════════════════════

log "${BOLD}Music Application — System Integration Test${RESET}"
log "Started: $(date)"
log "Log file: $LOG_FILE"

# ─── 1. Health Check All Services ────────────────────────────────────────────

header "1. SERVICE HEALTH CHECKS"

GATEWAY_UP=false; ARTIST_UP=false; USER_UP=false
CATALOG_UP=false; PODCAST_UP=false; ORDER_UP=false; AD_UP=false

check_service "API Gateway"     "$GATEWAY"    && GATEWAY_UP=true
check_service "Artist Service"  "$ARTIST_SVC" && ARTIST_UP=true
check_service "User Service"    "$USER_SVC"   && USER_UP=true
check_service "Catalog Service" "$CATALOG_SVC"&& CATALOG_UP=true
check_service "Podcast Service" "$PODCAST_SVC"&& PODCAST_UP=true
check_service "Order Service"   "$ORDER_SVC"  && ORDER_UP=true
check_service "Ad Service"      "$AD_SVC"     && AD_UP=true

# ─── 2. Artist Service ───────────────────────────────────────────────────────

header "2. ARTIST SERVICE (direct: $ARTIST_SVC)"

if $ARTIST_UP; then
    # GET all artists
    STATUS=$(do_request GET "$ARTIST_SVC/api/v1/artists")
    assert_status "GET all artists" 200 "$STATUS" "$BODY"

    # POST create artist
    ARTIST_BODY='{
      "firstName": "SIT-Test",
      "lastName": "Artist",
      "biography": "Created by system integration test",
      "genres": [{"genre": "Rock"}],
      "socialMediaLinks": []
    }'
    STATUS=$(do_request POST "$ARTIST_SVC/api/v1/artists" "$ARTIST_BODY")
    assert_status "POST create artist" 201 "$STATUS" "$BODY"
    ARTIST_ID=$(extract_field "$BODY" "artistIdentifier")
    log "     → Captured artistId: $ARTIST_ID"

    if [ -n "$ARTIST_ID" ]; then
        # GET artist by ID
        STATUS=$(do_request GET "$ARTIST_SVC/api/v1/artists/$ARTIST_ID")
        assert_status "GET artist by ID" 200 "$STATUS" "$BODY"

        # PUT update artist
        UPDATE_BODY="{\"firstName\": \"SIT-Updated\", \"lastName\": \"Artist\", \"biography\": \"Updated\"}"
        STATUS=$(do_request PUT "$ARTIST_SVC/api/v1/artists/$ARTIST_ID" "$UPDATE_BODY")
        assert_status "PUT update artist" 200 "$STATUS" "$BODY"

        # GET non-existent artist
        STATUS=$(do_request GET "$ARTIST_SVC/api/v1/artists/NON-EXISTENT-ID")
        assert_status "GET non-existent artist → 404" 404 "$STATUS"

        # DELETE artist
        STATUS=$(do_request DELETE "$ARTIST_SVC/api/v1/artists/$ARTIST_ID")
        assert_status "DELETE artist" 204 "$STATUS" "$BODY"

        # Verify deleted
        STATUS=$(do_request GET "$ARTIST_SVC/api/v1/artists/$ARTIST_ID")
        assert_status "GET deleted artist → 404" 404 "$STATUS"
    else
        log "${YELLOW}  ⚠️  Could not capture artistId — skipping artist sub-tests${RESET}"
        ((SKIP+=4))
    fi
else
    log "${YELLOW}  ⏭  Skipped — service is DOWN${RESET}"; ((SKIP+=6))
fi

# ─── 3. User Service ─────────────────────────────────────────────────────────

header "3. USER SERVICE (direct: $USER_SVC)"

if $USER_UP; then
    STATUS=$(do_request GET "$USER_SVC/api/v1/users")
    assert_status "GET all users" 200 "$STATUS" "$BODY"

    SIT_USERNAME="sit_user_$(date +%s)"
    USER_BODY="{
      \"username\": \"$SIT_USERNAME\",
      \"email\": \"${SIT_USERNAME}@test.com\",
      \"password\": \"password123\",
      \"fullname\": \"SIT Test User\",
      \"age\": 25,
      \"country\": \"Canada\"
    }"
    STATUS=$(do_request POST "$USER_SVC/api/v1/users" "$USER_BODY")
    assert_status "POST create user" 201 "$STATUS" "$BODY"
    USER_ID=$(extract_field "$BODY" "userId")
    USERNAME=$SIT_USERNAME
    log "     → Captured userId: $USER_ID, username: $USERNAME"

    if [ -n "$USER_ID" ]; then
        STATUS=$(do_request GET "$USER_SVC/api/v1/users/id/$USER_ID")
        assert_status "GET user by ID" 200 "$STATUS" "$BODY"

        STATUS=$(do_request GET "$USER_SVC/api/v1/users/$USERNAME")
        assert_status "GET user by username" 200 "$STATUS" "$BODY"

        STATUS=$(do_request GET "$USER_SVC/api/v1/users/id/NON-EXISTENT")
        assert_status "GET non-existent user → 404" 404 "$STATUS"

        UPDATE_USER="{\"username\": \"$USERNAME\", \"email\": \"updated@test.com\", \"password\": \"new\", \"fullname\": \"Updated\", \"age\": 26, \"country\": \"Canada\"}"
        STATUS=$(do_request PUT "$USER_SVC/api/v1/users/$USERNAME" "$UPDATE_USER")
        assert_status "PUT update user" 200 "$STATUS" "$BODY"

        STATUS=$(do_request DELETE "$USER_SVC/api/v1/users/$USERNAME")
        assert_status "DELETE user" 204 "$STATUS" "$BODY"
    else
        log "${YELLOW}  ⚠️  Could not capture userId — skipping user sub-tests${RESET}"
        ((SKIP+=5))
    fi
else
    log "${YELLOW}  ⏭  Skipped — service is DOWN${RESET}"; ((SKIP+=6))
fi

# ─── 4. Catalog Service ──────────────────────────────────────────────────────

header "4. CATALOG SERVICE (direct: $CATALOG_SVC)"

if $CATALOG_UP; then
    STATUS=$(do_request GET "$CATALOG_SVC/api/v1/album")
    assert_status "GET all albums" 200 "$STATUS" "$BODY"

    # Need a real artist ID — reuse one from the first album if exists
    FIRST_ARTIST_ID=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d[0].get('artistIdentifier','ART-001') if d else 'ART-001')" 2>/dev/null || echo "ART-001")

    ALBUM_BODY="{
      \"title\": \"SIT Test Album\",
      \"artistId\": \"$FIRST_ARTIST_ID\",
      \"releaseDate\": \"2024-01-01\",
      \"albumType\": \"LP\",
      \"recordLabel\": \"SIT Records\",
      \"song\": [{\"title\": \"Track 1\", \"lyrics\": \"test lyrics\"}]
    }"
    STATUS=$(do_request POST "$CATALOG_SVC/api/v1/album/create" "$ALBUM_BODY")
    assert_status "POST create album" 201 "$STATUS" "$BODY"
    ALBUM_ID=$(extract_field "$BODY" "albumId")
    log "     → Captured albumId: $ALBUM_ID"

    if [ -n "$ALBUM_ID" ]; then
        STATUS=$(do_request GET "$CATALOG_SVC/api/v1/album/$ALBUM_ID")
        assert_status "GET album by ID" 200 "$STATUS" "$BODY"

        STATUS=$(do_request GET "$CATALOG_SVC/api/v1/album/NON-EXISTENT")
        assert_status "GET non-existent album → 404" 404 "$STATUS"

        UPDATE_ALBUM="{\"title\": \"SIT Updated Album\", \"artistId\": \"$FIRST_ARTIST_ID\", \"albumType\": \"EP\", \"recordLabel\": \"New Records\", \"song\": [{\"title\": \"Track 1\", \"lyrics\": \"lyrics\"}]}"
        STATUS=$(do_request PUT "$CATALOG_SVC/api/v1/album/update/$ALBUM_ID" "$UPDATE_ALBUM")
        assert_status "PUT update album" 200 "$STATUS" "$BODY"

        STATUS=$(do_request DELETE "$CATALOG_SVC/api/v1/album/delete/$ALBUM_ID")
        assert_status "DELETE album" 204 "$STATUS" "$BODY"
    else
        log "${YELLOW}  ⚠️  Could not capture albumId — skipping album sub-tests${RESET}"
        ((SKIP+=4))
    fi
else
    log "${YELLOW}  ⏭  Skipped — service is DOWN${RESET}"; ((SKIP+=5))
fi

# ─── 5. Podcast Service ──────────────────────────────────────────────────────

header "5. PODCAST SERVICE (direct: $PODCAST_SVC)"

if $PODCAST_UP; then
    STATUS=$(do_request GET "$PODCAST_SVC/api/v1/podcasts")
    assert_status "GET all podcasts" 200 "$STATUS" "$BODY"

    PODCAST_BODY='{
      "title": "SIT Test Podcast",
      "hostname": "SIT Host",
      "description": "Integration test podcast",
      "pricingModel": "FREE"
    }'
    STATUS=$(do_request POST "$PODCAST_SVC/api/v1/podcasts" "$PODCAST_BODY")
    assert_status "POST create podcast" 201 "$STATUS" "$BODY"
    PODCAST_ID=$(extract_field "$BODY" "podcastId")
    log "     → Captured podcastId: $PODCAST_ID"

    if [ -n "$PODCAST_ID" ]; then
        STATUS=$(do_request GET "$PODCAST_SVC/api/v1/podcasts/$PODCAST_ID")
        assert_status "GET podcast by ID" 200 "$STATUS" "$BODY"

        STATUS=$(do_request GET "$PODCAST_SVC/api/v1/podcasts/NON-EXISTENT")
        assert_status "GET non-existent podcast → 404" 404 "$STATUS"

        # Episode CRUD
        EPISODE_BODY='{"episodeTitle": "SIT Episode 1", "status": "DRAFT"}'
        STATUS=$(do_request POST "$PODCAST_SVC/api/v1/podcasts/$PODCAST_ID/episodes" "$EPISODE_BODY")
        assert_status "POST create episode" 201 "$STATUS" "$BODY"
        EPISODE_ID=$(extract_field "$BODY" "episodeId")

        if [ -n "$EPISODE_ID" ]; then
            STATUS=$(do_request GET "$PODCAST_SVC/api/v1/podcasts/$PODCAST_ID/episodes/$EPISODE_ID")
            assert_status "GET episode by ID" 200 "$STATUS" "$BODY"

            STATUS=$(do_request DELETE "$PODCAST_SVC/api/v1/podcasts/$PODCAST_ID/episodes/$EPISODE_ID")
            assert_status "DELETE episode" 204 "$STATUS" "$BODY"
        fi

        STATUS=$(do_request DELETE "$PODCAST_SVC/api/v1/podcasts/$PODCAST_ID")
        assert_status "DELETE podcast" 204 "$STATUS" "$BODY"
    else
        log "${YELLOW}  ⚠️  Could not capture podcastId — skipping sub-tests${RESET}"
        ((SKIP+=5))
    fi
else
    log "${YELLOW}  ⏭  Skipped — service is DOWN${RESET}"; ((SKIP+=6))
fi

# ─── 6. Order Service ────────────────────────────────────────────────────────

header "6. ORDER SERVICE (direct: $ORDER_SVC)"

if $ORDER_UP; then
    STATUS=$(do_request GET "$ORDER_SVC/api/v1/orders")
    assert_status "GET all orders" 200 "$STATUS" "$BODY"

    # Requires a user email that exists in the user service
    # Adjust this email to match seeded data in your data.sql
    ORDER_USER_EMAIL="malick@email.com"

    ORDER_BODY="{
      \"userEmail\": \"$ORDER_USER_EMAIL\",
      \"orderStatus\": \"PENDING\",
      \"orderItems\": [
        {
          \"productType\": \"ALBUM_PURCHASE\",
          \"displayName\": \"Cowboy Sunset\",
          \"artistName\": \"Reba McEntire\",
          \"price\": 14.99,
          \"quantity\": 1
        }
      ],
      \"payments\": [
        {
          \"amount\": 14.99,
          \"method\": \"CREDIT_CARD\",
          \"status\": \"PENDING\",
          \"currency\": \"USD\"
        }
      ]
    }"

    STATUS=$(do_request POST "$ORDER_SVC/api/v1/orders" "$ORDER_BODY")
    assert_status "POST create order" 201 "$STATUS" "$BODY"
    ORDER_ID=$(extract_field "$BODY" "orderId")
    log "     → Captured orderId: $ORDER_ID"

    if [ -n "$ORDER_ID" ]; then
        STATUS=$(do_request GET "$ORDER_SVC/api/v1/orders/$ORDER_ID")
        assert_status "GET order by ID" 200 "$STATUS" "$BODY"

        STATUS=$(do_request GET "$ORDER_SVC/api/v1/orders/NON-EXISTENT")
        assert_status "GET non-existent order → 404" 404 "$STATUS"

        UPDATE_ORDER="{\"userEmail\": \"$ORDER_USER_EMAIL\", \"orderStatus\": \"COMPLETED\", \"orderItems\": [], \"payments\": []}"
        STATUS=$(do_request PUT "$ORDER_SVC/api/v1/orders/$ORDER_ID" "$UPDATE_ORDER")
        assert_status "PUT update order status to COMPLETED" 200 "$STATUS" "$BODY"

        STATUS=$(do_request DELETE "$ORDER_SVC/api/v1/orders/$ORDER_ID/cancel")
        assert_status "DELETE (cancel) order" 204 "$STATUS" "$BODY"
    else
        log "${YELLOW}  ⚠️  Could not capture orderId — skipping sub-tests${RESET}"
        ((SKIP+=4))
    fi
else
    log "${YELLOW}  ⏭  Skipped — service is DOWN${RESET}"; ((SKIP+=5))
fi

# ─── 7. API Gateway (End-to-End) ─────────────────────────────────────────────

header "7. API GATEWAY END-TO-END ($GATEWAY)"

if $GATEWAY_UP; then

    # Artists via gateway
    log "\n${BOLD}  → Artists via Gateway${RESET}"
    STATUS=$(do_request GET "$GATEWAY/api/v1/artists")
    assert_status "GW: GET all artists" 200 "$STATUS" "$BODY"

    GW_ARTIST_BODY='{"firstName":"GW-Artist","lastName":"Test","biography":"via gateway","genres":[],"socialMediaLinks":[]}'
    STATUS=$(do_request POST "$GATEWAY/api/v1/artists" "$GW_ARTIST_BODY")
    assert_status "GW: POST create artist" 201 "$STATUS" "$BODY"
    GW_ARTIST_ID=$(extract_field "$BODY" "artistIdentifier")

    if [ -n "$GW_ARTIST_ID" ]; then
        STATUS=$(do_request GET "$GATEWAY/api/v1/artists/$GW_ARTIST_ID")
        assert_status "GW: GET artist by ID" 200 "$STATUS"
        STATUS=$(do_request DELETE "$GATEWAY/api/v1/artists/$GW_ARTIST_ID")
        assert_status "GW: DELETE artist" 204 "$STATUS"
    fi

    # Users via gateway
    log "\n${BOLD}  → Users via Gateway${RESET}"
    STATUS=$(do_request GET "$GATEWAY/api/v1/users")
    assert_status "GW: GET all users" 200 "$STATUS"

    GW_USERNAME="gw_user_$(date +%s)"
    GW_USER_BODY="{\"username\":\"$GW_USERNAME\",\"email\":\"${GW_USERNAME}@test.com\",\"password\":\"pw\",\"fullname\":\"GW User\",\"age\":22,\"country\":\"Canada\"}"
    STATUS=$(do_request POST "$GATEWAY/api/v1/users" "$GW_USER_BODY")
    assert_status "GW: POST create user" 201 "$STATUS" "$BODY"
    GW_USER_ID=$(extract_field "$BODY" "userId")

    if [ -n "$GW_USER_ID" ]; then
        STATUS=$(do_request GET "$GATEWAY/api/v1/users/id/$GW_USER_ID")
        assert_status "GW: GET user by ID" 200 "$STATUS"
        STATUS=$(do_request DELETE "$GATEWAY/api/v1/users/$GW_USERNAME")
        assert_status "GW: DELETE user" 204 "$STATUS"
    fi

    # Albums via gateway
    log "\n${BOLD}  → Albums via Gateway${RESET}"
    STATUS=$(do_request GET "$GATEWAY/api/v1/album")
    assert_status "GW: GET all albums" 200 "$STATUS"

    # Orders via gateway
    log "\n${BOLD}  → Orders via Gateway${RESET}"
    STATUS=$(do_request GET "$GATEWAY/api/v1/orders")
    assert_status "GW: GET all orders" 200 "$STATUS"

    # 404 error propagation via gateway
    log "\n${BOLD}  → Error propagation via Gateway${RESET}"
    STATUS=$(do_request GET "$GATEWAY/api/v1/artists/NON-EXISTENT")
    assert_status "GW: 404 propagation — artist not found" 404 "$STATUS"

    STATUS=$(do_request GET "$GATEWAY/api/v1/orders/NON-EXISTENT")
    assert_status "GW: 404 propagation — order not found" 404 "$STATUS"

    # Malformed JSON via gateway
    STATUS=$(curl -s -o /tmp/sit_body -w "%{http_code}" -X POST \
        -H "Content-Type: application/json" -d "bad-json" \
        "$GATEWAY/api/v1/artists")
    assert_status "GW: malformed JSON → 400" 400 "$STATUS"

else
    log "${YELLOW}  ⏭  Skipped — Gateway is DOWN${RESET}"; ((SKIP+=12))
fi

# ─── Summary ─────────────────────────────────────────────────────────────────

TOTAL=$((PASS + FAIL + SKIP))
log ""
log "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
log "${BOLD}  RESULTS — $(date)${RESET}"
log "  Total : $TOTAL"
log "  ${GREEN}Pass  : $PASS${RESET}"
log "  ${RED}Fail  : $FAIL${RESET}"
log "  ${YELLOW}Skip  : $SKIP${RESET}"
log "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${RESET}"
log "Full log saved to: $LOG_FILE"

if [ $FAIL -gt 0 ]; then
    exit 1
else
    exit 0
fi
