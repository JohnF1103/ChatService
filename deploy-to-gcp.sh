#!/bin/bash
# Deploy ChatService to Google Cloud Run
PROJECT_ID=peppy-ward-447115-i5
SERVICE_NAME=chatservice
REGION=us-central1

# Build and push Docker image
gcloud builds submit --tag gcr.io/$PROJECT_ID/$SERVICE_NAME

# Deploy to Cloud Run
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/$SERVICE_NAME \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated
