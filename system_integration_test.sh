#!/usr/bin/env bash
# =============================================================================
# Music Application — Semi-Automated System Integration Test
# Covers: User, Catalog, Podcast, Order — all via API Gateway on port 8080
#
# Usage:
#   HOST=localhost PORT=8080 ./test_all.bash
#   or just: ./test_all.bash
# =============================================================================

: ${HOST=localhost}
: ${PORT=8080}

BASE_URL="http://$HOST:$PORT"

# Colours
GREEN='\033[0;32m'
RED='\033[0;31m'
RESET='\033[0m'

echo "Starting tests against ${BASE_URL}"
echo ""

# =============================================================================
# assertCurl — checks HTTP status code, stores body in $RESPONSE
# =============================================================================
function assertCurl() {
    local expectedHttpCode=$1
    local curlCmd="$2 -w \"%{http_code}\""
    local result=$(eval $curlCmd)
    local httpCode="${result:(-3)}"
    RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

    if [ "$httpCode" = "$expectedHttpCode" ]; then
        if [ "$httpCode" = "200" ]; then
            echo -e "${GREEN}Test OK${RESET} (HTTP Code: $httpCode)"
        else
            echo -e "${GREEN}Test OK${RESET} (HTTP Code: $httpCode, $RESPONSE)"
        fi
    else
        echo -e "${RED}Test FAILED${RESET}, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
        echo "  - Failing command : $curlCmd"
        echo "  - Response Body   : $RESPONSE"
        exit 1
    fi
}

# =============================================================================
# assertEqual — compares expected vs actual value from $RESPONSE
# =============================================================================
function assertEqual() {
    local expected=$1
    local actual=$2

    if [ "$actual" = "$expected" ]; then
        echo -e "${GREEN}Test OK${RESET} (actual value: $actual)"
    else
        echo -e "${RED}Test FAILED${RESET}, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
        exit 1
    fi
}

# =============================================================================
# Wait loop — checks if the gateway responds (any HTTP code means it is up).
# 000 means no connection at all.
# =============================================================================
echo "Waiting for services to start..."
while [ "$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/api/v1/users)" = "000" ]; do
    sleep 5
    echo "Still waiting..."
done
echo -e "${GREEN}Services are up!${RESET}"
echo ""

# =============================================================================
# setupTestdata — creates one entity per service using your exact Swagger bodies.
# Captured IDs are reused in the create-then-verify tests below.
# All seeded-data tests use IDs from your data.sql directly.
# =============================================================================

NEW_USER_ID=""
NEW_USERNAME="sit_user_$(date +%s)"
NEW_PODCAST_ID=""
NEW_ALBUM_ID=""
NEW_ORDER_ID=""

function setupTestdata() {
    echo "--- Setting up test data ---"

    # ── User ──────────────────────────────────────────────────────────────────
    local userBody="{
  \"username\": \"${NEW_USERNAME}\",
  \"email\": \"${NEW_USERNAME}@sit.com\",
  \"password\": \"password123\",
  \"fullname\": \"SIT Test User\",
  \"age\": 25,
  \"country\": \"Canada\"
}"
    NEW_USER_ID=$(curl -s -X POST "$BASE_URL/api/v1/users" \
        -H "Content-Type: application/json" \
        -d "$userBody" | jq -r '.userId')
    echo "Created User    → userId: $NEW_USER_ID  username: $NEW_USERNAME"

    # ── Podcast ───────────────────────────────────────────────────────────────
    local podcastBody='{
  "title": "SIT Test Podcast",
  "hostname": "SIT Host",
  "description": "Created by integration test",
  "pricingModel": "FREE"
}'
    NEW_PODCAST_ID=$(curl -s -X POST "$BASE_URL/api/v1/podcasts" \
        -H "Content-Type: application/json" \
        -d "$podcastBody" | jq -r '.podcastId')
    echo "Created Podcast → podcastId: $NEW_PODCAST_ID"

    # ── Album ─────────────────────────────────────────────────────────────────
    # artistId must exist in your data.sql — grab the first one available
    local firstArtistId=$(curl -s "$BASE_URL/api/v1/artists" | jq -r '.[0].artistIdentifier // empty')
    [ -z "$firstArtistId" ] && firstArtistId="ART-001"

    local albumBody="{
  \"title\": \"SIT Test Album\",
  \"artistId\": \"${firstArtistId}\",
  \"albumType\": \"LP\",
  \"recordLabel\": \"SIT Records\",
  \"song\": [{\"title\": \"SIT Track 1\", \"lyrics\": \"Test lyrics\"}]
}"
    NEW_ALBUM_ID=$(curl -s -X POST "$BASE_URL/api/v1/album/create" \
        -H "Content-Type: application/json" \
        -d "$albumBody" | jq -r '.albumId')
    echo "Created Album   → albumId: $NEW_ALBUM_ID"

    # ── Order ─────────────────────────────────────────────────────────────────
    local orderBody='{
  "userEmail": "malick@email.com",
  "orderStatus": "PENDING",
  "orderItems": [
    {
      "productType": "ALBUM_PURCHASE",
      "displayName": "Southern Skies",
      "artistName": "Reba McEntire",
      "price": 14.99,
      "quantity": 1
    }
  ],
  "payments": [
    {
      "method": "CREDIT_CARD",
      "amount": 14.99,
      "status": "PENDING",
      "currency": "USD"
    }
  ]
}'
    NEW_ORDER_ID=$(curl -s -X POST "$BASE_URL/api/v1/orders" \
        -H "Content-Type: application/json" \
        -d "$orderBody" | jq -r '.orderId')
    echo "Created Order   → orderId: $NEW_ORDER_ID"

    echo "--- Test data ready ---"
    echo ""
}

setupTestdata

# =============================================================================
# USER TESTS
# Seeded IDs used: username=malick, userId=user-001
# =============================================================================

echo "═══════════════════════════════════════════════"
echo "  USER"
echo "═══════════════════════════════════════════════"

echo -e "\nTest 1: GET all users returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/users \
    -H 'accept: application/json'"

echo -e "\nTest 2: GET user by seeded username (malick) returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/users/malick \
    -H 'accept: application/json'"
assertEqual '"malick"' $(echo $RESPONSE | jq '.username')

echo -e "\nTest 3: GET user by seeded ID (user-001) returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/users/id/user-001 \
    -H 'accept: application/json'"
assertEqual '"user-001"' $(echo $RESPONSE | jq '.userId')

echo -e "\nTest 4: GET newly created user by ID returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/users/id/$NEW_USER_ID \
    -H 'accept: application/json'"
assertEqual "\"$NEW_USER_ID\"" $(echo $RESPONSE | jq '.userId')

echo -e "\nTest 5: GET user by newly created username returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/users/$NEW_USERNAME \
    -H 'accept: application/json'"
assertEqual '"SIT Test User"' $(echo $RESPONSE | jq '.fullname')

echo -e "\nTest 6: GET non-existent user by ID returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/users/id/NON-EXISTENT-ID \
    -H 'accept: application/json'"

echo -e "\nTest 7: GET non-existent user by username returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/users/ghost-user \
    -H 'accept: application/json'"

echo -e "\nTest 8: POST user with malformed JSON returns 400"
assertCurl 400 "curl -s -X POST $BASE_URL/api/v1/users \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d 'not-json'"

echo -e "\nTest 9: PUT update seeded user (nroos) returns 200 and updated fullname"
assertCurl 200 "curl -s -X PUT $BASE_URL/api/v1/users/nroos \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"username\":\"nroos\",\"email\":\"Malick.konate@email.com\",\"password\":\"pwd\",\"fullname\":\"Zie Abdoul Malick Konate\",\"age\":22,\"country\":\"Canada\"}'"
assertEqual '"Zie Abdoul Malick Konate"' $(echo $RESPONSE | jq '.fullname')

echo -e "\nTest 10: DELETE newly created user returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/users/$NEW_USERNAME \
    -H 'accept: */*'"

echo -e "\nTest 11: GET deleted user returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/users/$NEW_USERNAME \
    -H 'accept: application/json'"

# =============================================================================
# CATALOG TESTS
# Seeded IDs used: ALB-015 (get), ALB-007 (update), ALB-016 (delete)
# =============================================================================

echo ""
echo "═══════════════════════════════════════════════"
echo "  CATALOG"
echo "═══════════════════════════════════════════════"

echo -e "\nTest 1: GET all albums returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/album \
    -H 'accept: application/json'"

echo -e "\nTest 2: GET seeded album (ALB-015) returns 200 and correct albumId"
assertCurl 200 "curl -s $BASE_URL/api/v1/album/ALB-015 \
    -H 'accept: application/json'"
assertEqual '"ALB-015"' $(echo $RESPONSE | jq '.albumId')

echo -e "\nTest 3: GET non-existent album returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/album/NON-EXISTENT-ID \
    -H 'accept: application/json'"

echo -e "\nTest 4: GET newly created album returns 200 and correct albumId"
assertCurl 200 "curl -s $BASE_URL/api/v1/album/$NEW_ALBUM_ID \
    -H 'accept: application/json'"
assertEqual "\"$NEW_ALBUM_ID\"" $(echo $RESPONSE | jq '.albumId')
assertEqual '"SIT Test Album"' $(echo $RESPONSE | jq '.title')

echo -e "\nTest 5: POST album with no songs returns 422"
assertCurl 422 "curl -s -X POST $BASE_URL/api/v1/album/create \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"title\":\"Bad Album\",\"artistId\":\"ART-001\",\"albumType\":\"LP\",\"recordLabel\":\"X\",\"song\":[]}'"

echo -e "\nTest 6: POST album with malformed JSON returns 400"
assertCurl 400 "curl -s -X POST $BASE_URL/api/v1/album/create \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d 'not-json'"

echo -e "\nTest 7: PUT update seeded album (ALB-007) returns 200 and updated title"
assertCurl 200 "curl -s -X PUT $BASE_URL/api/v1/album/update/ALB-007 \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"title\":\"SIT Updated Title\",\"artistId\":\"ART-001\",\"albumType\":\"LP\",\"recordLabel\":\"SIT Records\",\"song\":[{\"title\":\"Track 1\",\"lyrics\":\"lyrics\"}]}'"
assertEqual '"SIT Updated Title"' $(echo $RESPONSE | jq '.title')

echo -e "\nTest 8: PUT non-existent album returns 404"
assertCurl 404 "curl -s -X PUT $BASE_URL/api/v1/album/update/NON-EXISTENT \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"title\":\"X\",\"artistId\":\"ART-001\",\"albumType\":\"LP\",\"recordLabel\":\"X\",\"song\":[{\"title\":\"T\",\"lyrics\":\"L\"}]}'"

echo -e "\nTest 9: DELETE seeded album (ALB-016) returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/album/delete/ALB-016 \
    -H 'accept: */*'"

echo -e "\nTest 10: GET deleted album (ALB-016) returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/album/ALB-016 \
    -H 'accept: application/json'"

echo -e "\nTest 11: DELETE newly created album returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/album/delete/$NEW_ALBUM_ID \
    -H 'accept: */*'"

# =============================================================================
# PODCAST TESTS
# Seeded IDs used: pod_country_007 (get), pod_country_002 (update),
#                  pod_country_004 (delete)
# =============================================================================

echo ""
echo "═══════════════════════════════════════════════"
echo "  PODCAST"
echo "═══════════════════════════════════════════════"

echo -e "\nTest 1: GET all podcasts returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/podcasts \
    -H 'accept: application/json'"

echo -e "\nTest 2: GET seeded podcast (pod_country_007) returns 200 and correct podcastId"
assertCurl 200 "curl -s $BASE_URL/api/v1/podcasts/pod_country_007 \
    -H 'accept: application/json'"
assertEqual '"pod_country_007"' $(echo $RESPONSE | jq '.podcastId')

echo -e "\nTest 3: GET non-existent podcast returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/podcasts/NON-EXISTENT-ID \
    -H 'accept: application/json'"

echo -e "\nTest 4: GET newly created podcast returns 200 and correct podcastId"
assertCurl 200 "curl -s $BASE_URL/api/v1/podcasts/$NEW_PODCAST_ID \
    -H 'accept: application/json'"
assertEqual "\"$NEW_PODCAST_ID\"" $(echo $RESPONSE | jq '.podcastId')
assertEqual '"SIT Test Podcast"' $(echo $RESPONSE | jq '.title')

echo -e "\nTest 5: POST podcast with malformed JSON returns 400"
assertCurl 400 "curl -s -X POST $BASE_URL/api/v1/podcasts \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d 'not-json'"

echo -e "\nTest 6: PUT update seeded podcast (pod_country_002) returns 200"
assertCurl 200 "curl -s -X PUT $BASE_URL/api/v1/podcasts/pod_country_002 \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"title\":\"SIT Updated Podcast\",\"hostname\":\"Updated Host\",\"description\":\"Updated desc\",\"pricingModel\":\"FREE\"}'"
assertEqual '"SIT Updated Podcast"' $(echo $RESPONSE | jq '.title')

echo -e "\nTest 7: PUT non-existent podcast returns 404"
assertCurl 404 "curl -s -X PUT $BASE_URL/api/v1/podcasts/NON-EXISTENT \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"title\":\"X\",\"hostname\":\"X\",\"description\":\"X\",\"pricingModel\":\"FREE\"}'"

echo -e "\nTest 8: DELETE seeded podcast (pod_country_004) returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/podcasts/pod_country_004 \
    -H 'accept: application/json'"

echo -e "\nTest 9: GET deleted podcast (pod_country_004) returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/podcasts/pod_country_004 \
    -H 'accept: application/json'"

echo -e "\nTest 10: DELETE newly created podcast returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/podcasts/$NEW_PODCAST_ID \
    -H 'accept: application/json'"

# =============================================================================
# ORDER TESTS
# Seeded IDs used: ord_103 (get/update), ord_101 (delete)
# =============================================================================

echo ""
echo "═══════════════════════════════════════════════"
echo "  ORDER"
echo "═══════════════════════════════════════════════"

echo -e "\nTest 1: GET all orders returns 200"
assertCurl 200 "curl -s $BASE_URL/api/v1/orders \
    -H 'accept: application/json'"

echo -e "\nTest 2: GET seeded order (ord_103) returns 200 and correct orderId"
assertCurl 200 "curl -s $BASE_URL/api/v1/orders/ord_103 \
    -H 'accept: application/json'"
assertEqual '"ord_103"' $(echo $RESPONSE | jq '.orderId')

echo -e "\nTest 3: GET non-existent order returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/orders/NON-EXISTENT-ID \
    -H 'accept: application/json'"

echo -e "\nTest 4: GET newly created order returns 200 and status PENDING"
assertCurl 200 "curl -s $BASE_URL/api/v1/orders/$NEW_ORDER_ID \
    -H 'accept: application/json'"
assertEqual "\"$NEW_ORDER_ID\"" $(echo $RESPONSE | jq '.orderId')
assertEqual '"PENDING"' $(echo $RESPONSE | jq '.orderStatus')

echo -e "\nTest 5: POST order with empty JSON body returns 400"
assertCurl 400 "curl -s -X POST $BASE_URL/api/v1/orders \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{}'"

echo -e "\nTest 6: POST order with malformed JSON returns 400"
assertCurl 400 "curl -s -X POST $BASE_URL/api/v1/orders \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d 'not-json'"

echo -e "\nTest 7: POST order with digital product quantity > 1 returns 409"
assertCurl 409 "curl -s -X POST $BASE_URL/api/v1/orders \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"userEmail\":\"malick@email.com\",\"orderStatus\":\"PENDING\",\"orderItems\":[{\"productType\":\"ALBUM_PURCHASE\",\"displayName\":\"Southern Skies\",\"artistName\":\"Reba McEntire\",\"price\":14.99,\"quantity\":2}],\"payments\":[{\"amount\":29.98,\"method\":\"CREDIT_CARD\",\"status\":\"PENDING\",\"currency\":\"USD\"}]}'"

echo -e "\nTest 8: POST order with payment mismatch returns 409"
assertCurl 409 "curl -s -X POST $BASE_URL/api/v1/orders \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"userEmail\":\"malick@email.com\",\"orderStatus\":\"PENDING\",\"orderItems\":[{\"productType\":\"ALBUM_PURCHASE\",\"displayName\":\"Southern Skies\",\"artistName\":\"Reba McEntire\",\"price\":14.99,\"quantity\":1}],\"payments\":[{\"amount\":5.00,\"method\":\"CREDIT_CARD\",\"status\":\"PENDING\",\"currency\":\"USD\"}]}'"

echo -e "\nTest 9: PUT update seeded order (ord_103) to COMPLETED returns 200"
assertCurl 200 "curl -s -X PUT $BASE_URL/api/v1/orders/ord_103 \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d '{\"userEmail\":\"malick@email.com\",\"orderStatus\":\"COMPLETED\",\"orderItems\":[{\"productType\":\"ALBUM_PURCHASE\",\"displayName\":\"Southern Skies\",\"artistName\":\"Reba McEntire\",\"price\":14.99,\"quantity\":1},{\"productType\":\"PODCAST_SUBSCRIPTION\",\"displayName\":\"Honky Tonk History\",\"artistName\":\"Host\",\"price\":4.99,\"quantity\":1}],\"payments\":[{\"method\":\"CREDIT_CARD\",\"amount\":19.98,\"paymentStatus\":\"COMPLETED\",\"currency\":\"USD\"}]}'"
assertEqual '"COMPLETED"' $(echo $RESPONSE | jq '.orderStatus')

echo -e "\nTest 10: DELETE seeded order (ord_101) returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/orders/ord_101 \
    -H 'accept: */*'"

echo -e "\nTest 11: GET deleted order (ord_101) returns 404"
assertCurl 404 "curl -s $BASE_URL/api/v1/orders/ord_101 \
    -H 'accept: application/json'"

echo -e "\nTest 12: DELETE newly created order returns 204"
assertCurl 204 "curl -s -X DELETE $BASE_URL/api/v1/orders/$NEW_ORDER_ID/cancel \
    -H 'accept: */*'"

# =============================================================================
# DONE
# =============================================================================

echo ""
echo "═══════════════════════════════════════════════"
echo -e "  ${GREEN}All tests passed!${RESET}"
echo "═══════════════════════════════════════════════"
echo ""