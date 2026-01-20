#!/bin/bash
# HytaleAE2 Deployment Script
# Builds and deploys the plugin to Hytale

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="HytaleAE2"
JAR_NAME="HytaleAE2-0.2.0.jar"
BUILD_OUTPUT="build/libs/$JAR_NAME"
HYTALE_MODS_PATH="$APPDATA/Hytale/UserData/Mods"

# Functions
print_banner() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║  $1${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}⚡ $1${NC}"
}

# Main deployment flow
main() {
    print_banner "HytaleAE2 Deployment"
    
    # Step 1: Build
    print_info "Building project..."
    if ./gradlew clean build --info; then
        print_success "Build completed"
    else
        print_error "Build failed"
        exit 1
    fi
    
    # Step 2: Verify JAR
    if [ ! -f "$BUILD_OUTPUT" ]; then
        print_error "JAR not found at $BUILD_OUTPUT"
        exit 1
    fi
    print_success "JAR created: $BUILD_OUTPUT"
    
    # Step 3: Check Mods directory
    if [ ! -d "$HYTALE_MODS_PATH" ]; then
        print_error "Hytale mods directory not found: $HYTALE_MODS_PATH"
        print_info "Creating directory..."
        mkdir -p "$HYTALE_MODS_PATH"
    fi
    
    # Step 4: Deploy
    print_info "Deploying to Hytale..."
    cp "$BUILD_OUTPUT" "$HYTALE_MODS_PATH/$JAR_NAME"
    
    if [ $? -eq 0 ]; then
        print_success "Deployed to: $HYTALE_MODS_PATH/$JAR_NAME"
    else
        print_error "Deployment failed"
        exit 1
    fi
    
    # Step 5: Show file info
    JAR_SIZE=$(du -h "$HYTALE_MODS_PATH/$JAR_NAME" | cut -f1)
    print_info "File size: $JAR_SIZE"
    print_info "Last modified: $(stat -c %y "$HYTALE_MODS_PATH/$JAR_NAME")"
    
    print_banner "Deployment Complete!"
    echo -e "${GREEN}Plugin is ready to use in Hytale${NC}"
    echo -e "Start Hytale and check the logs for: ${YELLOW}HytaleAE2${NC}"
}

# Error handling
trap 'print_error "Deployment failed"; exit 1' ERR

# Run main
main "$@"
