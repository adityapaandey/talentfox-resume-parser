# TalentFox Resume Parser - Free Deployment Guide

## 🚀 Free Hosting Options

### Option 1: Railway (Recommended) ⭐
**Backend: FREE with 500 hours/month**

1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your `backend-java` folder
5. Railway will auto-detect Java and deploy
6. Copy the generated URL (e.g., `https://your-app.railway.app`)

**Frontend: Deploy on Vercel**
1. Go to [vercel.com](https://vercel.com)
2. Sign up with GitHub
3. Click "New Project" → Select `frontend` folder
4. Update `vercel.json` with your Railway backend URL
5. Deploy!

---

### Option 2: Render
**Backend: FREE tier available**

1. Go to [render.com](https://render.com)
2. Sign up with GitHub
3. Click "New" → "Web Service"
4. Connect your GitHub repo (backend-java folder)
5. Render will use `render.yaml` config
6. Deploy and get your URL

**Frontend: Same as above (Vercel)**

---

### Option 3: Google Cloud Run (FREE tier)
**Backend deployment:**

```bash
cd backend-java

# Build Docker image
docker build -t talentfox-backend .

# Tag for GCR
docker tag talentfox-backend gcr.io/YOUR-PROJECT-ID/talentfox-backend

# Push to GCR
docker push gcr.io/YOUR-PROJECT-ID/talentfox-backend

# Deploy to Cloud Run
gcloud run deploy talentfox-backend \
  --image gcr.io/YOUR-PROJECT-ID/talentfox-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

---

### Option 4: Heroku (FREE alternative)
**Note: Heroku ended free tier, use alternatives above**

---

## 📝 Configuration Steps

### After Backend Deployment:
1. Copy your backend URL (e.g., `https://your-app.railway.app`)
2. Update `frontend/app.js`:
   ```javascript
   const API_BASE_URL = 'https://your-app.railway.app/api/resume-parser';
   ```
3. Update `frontend/vercel.json` with backend URL
4. Deploy frontend to Vercel

### CORS Configuration:
Backend already has CORS enabled in Spring Boot.

---

## 🎯 Recommended Setup

**Best FREE Combination:**
- ✅ **Backend**: Railway (500 hrs/month FREE)
- ✅ **Frontend**: Vercel (Unlimited FREE)

**Total Cost: $0/month**

---

## 📦 Files Created for Deployment

- ✅ `backend-java/Dockerfile` - Docker containerization
- ✅ `backend-java/railway.json` - Railway config
- ✅ `backend-java/render.yaml` - Render config
- ✅ `frontend/vercel.json` - Vercel config

---

## 🔗 Quick Links

- [Railway](https://railway.app) - Backend hosting
- [Vercel](https://vercel.com) - Frontend hosting
- [Render](https://render.com) - Alternative backend
- [Google Cloud Run](https://cloud.google.com/run) - Cloud option

---

## ⚡ Fast Deploy (Railway + Vercel)

**Step 1: Deploy Backend on Railway**
```bash
# Install Railway CLI (optional)
npm i -g @railway/cli

# Login
railway login

# Deploy
cd backend-java
railway up
```

**Step 2: Deploy Frontend on Vercel**
```bash
# Install Vercel CLI (optional)
npm i -g vercel

# Deploy
cd frontend
vercel
```

**Or use web UI - No CLI needed!**

---

## 📱 Access Your App

After deployment:
- **Frontend**: `https://your-app.vercel.app`
- **Backend**: `https://your-app.railway.app`

Done! 🎉
