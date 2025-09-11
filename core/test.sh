#!/bin/bash

# API Testing Script
# This script performs a complete workflow: user signup, login, organization registration, admin approval, and event creation

set -e  # Exit on any error

BASE_URL="http://localhost:8082"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# Function to extract JSON value using grep and sed
extract_json_value() {
    local json="$1"
    local key="$2"
    echo "$json" | grep -o "\"$key\":[^,}]*" | sed "s/\"$key\":\"*//g" | sed 's/"*$//g'
}

echo "Starting API workflow test..."
echo "================================="

# Step 1: Sign up a new user
print_info "Step 1: Signing up new user..."
signup_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/auth/signup" \
  --header 'content-type: application/json' \
  --data '{
  "username": "ayush",
  "password": "123",
  "first_name": "ayush",
  "last_name": "sri",
  "email": "test@gmail.com"
}')

# Extract HTTP status code (last line)
signup_status=$(echo "$signup_response" | tail -n1)
signup_body=$(echo "$signup_response" | head -n -1)

if [ "$signup_status" -ne 200 ] && [ "$signup_status" -ne 201 ]; then
    print_error "User signup failed with status: $signup_status"
    print_error "Response: $signup_body"
    exit 1
fi

print_success "User signup completed successfully"

# Step 2: Login the new user
print_info "Step 2: Logging in new user..."
login_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/auth/login" \
  --header 'content-type: application/json' \
  --data '{
  "username": "ayush",
  "password": "123"
}')

login_status=$(echo "$login_response" | tail -n1)
login_body=$(echo "$login_response" | head -n -1)

if [ "$login_status" -ne 200 ]; then
    print_error "User login failed with status: $login_status"
    print_error "Response: $login_body"
    exit 1
fi

# Extract JWT token
user_jwt=$(extract_json_value "$login_body" "jwt")

if [ -z "$user_jwt" ]; then
    print_error "Failed to extract JWT token from login response"
    print_error "Response: $login_body"
    exit 1
fi

print_success "User login completed successfully"
echo "User JWT Token: $user_jwt"

# Step 3: Register a new organization
print_info "Step 3: Registering new organization..."
org_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/api/v1/organizations/apply" \
  --header "authorization: Bearer $user_jwt" \
  --header 'content-type: application/json' \
  --data '{
  "name": "xHacks",
  "description": "We organize hakathons",
  "email": "info@xhack.com",
  "tags": [
    "hackathons",
    "coding",
    "inovation"
  ],
  "websiteUrl": "https://www.mhacks.com",
  "locations": "New York, USA"
}')

org_status=$(echo "$org_response" | tail -n1)
org_body=$(echo "$org_response" | head -n -1)

if [ "$org_status" -ne 200 ] && [ "$org_status" -ne 201 ]; then
    print_error "Organization registration failed with status: $org_status"
    print_error "Response: $org_body"
    exit 1
fi

# Extract registration ID
registration_id=$(extract_json_value "$org_body" "registration_id")

if [ -z "$registration_id" ]; then
    print_error "Failed to extract registration_id from organization response"
    print_error "Response: $org_body"
    exit 1
fi

print_success "Organization registration completed successfully"
echo "Registration ID: $registration_id"

# Step 4: Admin login
print_info "Step 4: Admin login..."
admin_login_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/auth/login" \
  --header 'content-type: application/json' \
  --data '{
  "username": "admin",
  "password": "adminadmin"
}')

admin_login_status=$(echo "$admin_login_response" | tail -n1)
admin_login_body=$(echo "$admin_login_response" | head -n -1)

if [ "$admin_login_status" -ne 200 ]; then
    print_error "Admin login failed with status: $admin_login_status"
    print_error "Response: $admin_login_body"
    exit 1
fi

# Extract admin JWT token
admin_jwt=$(extract_json_value "$admin_login_body" "jwt")

if [ -z "$admin_jwt" ]; then
    print_error "Failed to extract admin JWT token from login response"
    print_error "Response: $admin_login_body"
    exit 1
fi

print_success "Admin login completed successfully"
echo "Admin JWT Token: $admin_jwt"

# Step 5: Admin approves the organization
print_info "Step 5: Admin approving organization..."
approve_response=$(curl -s -w "\n%{http_code}" --request PUT \
  --url "$BASE_URL/api/v1/organizations/applications/$registration_id/approve" \
  --header "authorization: Bearer $admin_jwt" \
  --header 'content-type: application/json')

approve_status=$(echo "$approve_response" | tail -n1)
approve_body=$(echo "$approve_response" | head -n -1)

if [ "$approve_status" -ne 200 ]; then
    print_error "Organization approval failed with status: $approve_status"
    print_error "Response: $approve_body"
    exit 1
fi

print_success "Organization approval completed successfully"

# Step 6: Login with organization account
print_info "Step 6: Logging in with organization account..."
org_login_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/auth/login" \
  --header 'content-type: application/json' \
  --data '{
  "username": "xHacks",
  "password": "xHacks00xx"
}')

org_login_status=$(echo "$org_login_response" | tail -n1)
org_login_body=$(echo "$org_login_response" | head -n -1)

if [ "$org_login_status" -ne 200 ]; then
    print_error "Organization login failed with status: $org_login_status"
    print_error "Response: $org_login_body"
    exit 1
fi

# Extract organization JWT token
org_jwt=$(extract_json_value "$org_login_body" "jwt")

if [ -z "$org_jwt" ]; then
    print_error "Failed to extract organization JWT token from login response"
    print_error "Response: $org_login_body"
    exit 1
fi

print_success "Organization login completed successfully"
echo "Organization JWT Token: $org_jwt"

# Step 7: Create a new event
print_info "Step 7: Creating new event..."
event_response=$(curl -s -w "\n%{http_code}" --request POST \
  --url "$BASE_URL/api/events/register" \
  --header "authorization: Bearer $org_jwt" \
  --header 'content-type: application/json' \
  --data '{
  "name": "Tech  23 2025",
  "organization_id": "f3bd4cb7-68a2-4189-bffa-5d24eff4951c",
  "location": "Montreal, Canada",
  "begin_time": "2026-09-01T09:00:00Z",
  "end_time": "2026-09-01T17:00:00Z",
  "access_strategy": "once",
  "format": "online",
  "is_free": true,
  "options": [
    "requires_payment",
    "requires_approval"
  ],
  "accessible_from": "2025-08-01T00:00:00",
  "accessible_to": "2025-08-25T23:59:59",
  "total_tickets_to_be_sold": 100,
  "description": "tech event"
}')

event_status=$(echo "$event_response" | tail -n1)
event_body=$(echo "$event_response" | head -n -1)

if [ "$event_status" -ne 200 ] && [ "$event_status" -ne 201 ]; then
    print_error "Event creation failed with status: $event_status"
    print_error "Response: $event_body"
    exit 1
fi

print_success "Event creation completed successfully"
echo "Event Response: $event_body"

echo ""
echo "================================="
print_success "All operations completed successfully!"
echo "================================="

# Summary
echo ""
echo "SUMMARY:"
echo "--------"
echo "User JWT Token: $user_jwt"
echo "Admin JWT Token: $admin_jwt"
echo "Organization JWT Token: $org_jwt"
echo "Registration ID: $registration_id"