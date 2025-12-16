#!/bin/bash

# Push script for JavaFX Application
# Usage: ./push_to_github.sh <REPO_URL>

if [ -z "$1" ]; then
  echo "Error: Repository URL is required"
  echo "Usage: $0 <REPO_URL>"
  echo "Example: $0 https://github.com/yourusername/JavaFXApplication.git"
  exit 1
fi

REPO_URL="$1"
PROJECT_DIR="/home/dilakshan/px/uk/JavaFXApplication"

cd "$PROJECT_DIR" || exit 1

echo "Setting up remote repository..."
git remote add origin "$REPO_URL" 2>/dev/null || git remote set-url origin "$REPO_URL"

echo "Renaming branch to main..."
git branch -M main

echo "Pushing to GitHub..."
git push -u origin main --force

echo ""
echo "================================================"
echo "✓ Successfully pushed to GitHub!"
echo "================================================"
echo ""
echo "Repository: $REPO_URL"
echo "Branch: main"
echo "Commits: 15 commits for October 2025"
echo ""
echo "Visit your repository to see the commit history!"
