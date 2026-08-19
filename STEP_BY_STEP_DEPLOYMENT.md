# 🚀 TalentFox Resume Parser - Step-by-Step Deployment Guide

## ✅ Prerequisites Check

You have:
- ✅ Node.js v24.15.0 installed
- ✅ Java 21 installed
- ✅ Application working locally

---

# 🌐 Deployment Using Web UI (EASIEST - NO CLI NEEDED)

## Part 1: Deploy Backend on Railway (5 minutes)

### Step 1: Prepare Your Code
```powershell
cd c:\Users\adipande1\Downloads\talentfor-hr

# Initialize Git repository (if not already done)
git init
git add .
git commit -m "Initial commit - TalentFox Resume Parser"
```

### Step 2: Push to GitHub
1. Go to [github.com](https://github.com)
2. Click "New Repository"
3. Name: `talentfox-resume-parser`
4. Click "Create repository"
5. Copy the commands shown and run:

```powershell
git remote add origin https://github.com/YOUR-USERNAME/talentfox-resume-parser.git
git branch -M main
git push -u origin main
```

### Step 3: Deploy on Railway

1. **Go to Railway**
   - Visit: https://railway.app
   - Click "Start a New Project"
   - Choose "Deploy from GitHub repo"

2. **Connect GitHub**
   - Click "Login with GitHub"
   - Authorize Railway
   - Select your repository: `talentfox-resume-parser`

3. **Configure Deployment**
   - Railway will auto-detect the Java project
   - Root Directory: Click "Change" → Select `backend-java` folder
   - Click "Deploy"

4. **Wait for Build** (2-3 minutes)
   - Watch the build logs
   - Wait for "Success" message

5. **Get Your Backend URL**
   - Click "Settings" tab
   - Scroll to "Domains"
   - Click "Generate Domain"
   - Copy the URL (e.g., `https://talentfox-production.up.railway.app`)
   - **SAVE THIS URL** - you'll need it for frontend!

6. **Test Backend**
   - Open: `https://YOUR-URL.railway.app/api/resume-parser/health`
   - Should see: `{"status":"healthy",...}`

✅ **Backend deployed successfully!**

---

## Part 2: Deploy Frontend on Vercel (3 minutes)

### Step 1: Update Frontend Config

1. Open `frontend/app.js` in your editor
2. Find line 2:
   ```javascript
   const API_BASE_URL = 'http://localhost:8080/api/resume-parser';
   ```
3. Replace with your Railway URL:
   ```javascript
   const API_BASE_URL = 'https://YOUR-RAILWAY-URL.railway.app/api/resume-parser';
   ```
4. Save the file

### Step 2: Commit Changes
```powershell
cd c:\Users\adipande1\Downloads\talentfor-hr
git add frontend/app.js
git commit -m "Update API URL for production"
git push
```

### Step 3: Deploy on Vercel

1. **Go to Vercel**
   - Visit: https://vercel.com
   - Click "Sign Up" → Choose "Continue with GitHub"

2. **Import Project**
   - Click "Add New..." → "Project"
   - Find your repository: `talentfox-resume-parser`
   - Click "Import"

3. **Configure Project**
   - Framework Preset: `Other`
   - Root Directory: Click "Edit" → Select `frontend` folder
   - Build Command: Leave empty
   - Output Directory: Leave empty
   - Click "Deploy"

4. **Wait for Deployment** (1-2 minutes)
   - Watch the progress
   - Wait for "Congratulations" message

5. **Get Your Frontend URL**
   - Copy the URL shown (e.g., `https://talentfox-resume-parser.vercel.app`)

6. **Test Your App**
   - Click "Visit" or open the URL
   - You should see the TalentFox logo and interface

✅ **Frontend deployed successfully!**

---

# 🎯 Your Live App URLs

After deployment, you'll have:

- **Frontend**: `https://talentfox-resume-parser.vercel.app`
- **Backend API**: `https://talentfox-production.railway.app`

---

# 📋 Alternative: Using CLI (Optional)

If you prefer command line:

## Install CLIs

```powershell
# Install Railway CLI
npm install -g @railway/cli

# Install Vercel CLI
npm install -g vercel
```

## Deploy Backend with Railway CLI

```powershell
cd c:\Users\adipande1\Downloads\talentfor-hr\backend-java

# Login to Railway
railway login

# Initialize project
railway init

# Deploy
railway up

# Get URL
railway domain
```

## Deploy Frontend with Vercel CLI

```powershell
cd c:\Users\adipande1\Downloads\talentfor-hr\frontend

# Login to Vercel
vercel login

# Deploy
vercel

# Follow prompts:
# - Set up and deploy? Yes
# - Which scope? Your account
# - Link to existing project? No
# - Project name? talentfox-frontend
# - Directory? ./
# - Override settings? No

# Deploy to production
vercel --prod
```

---

# 🔧 Troubleshooting

## Backend Issues

### Build Fails on Railway

**Solution 1: Check Java Version**
- Go to Railway project settings
- Variables → Add:
  - `JAVA_VERSION` = `21`

**Solution 2: Build Command**
- Settings → Build Command:
  ```
  ./mvnw clean package -DskipTests
  ```

**Solution 3: Start Command**
- Settings → Start Command:
  ```
  java -jar target/resume-parser-1.0.0.jar
  ```

### CORS Errors

Backend already allows all origins. If still having issues:
- Check backend URL is correct in frontend
- Ensure using HTTPS (not HTTP)

## Frontend Issues

### Can't Connect to Backend

1. Verify backend URL in `app.js`
2. Test backend health endpoint directly
3. Check browser console for errors

### Upload Not Working

Backend needs to be running on Railway. Check:
- Railway deployment status
- Backend health endpoint responds

---

# 💰 Cost Breakdown

## FREE Forever Plan

**Railway:**
- ✅ 500 hours/month FREE
- ✅ $5 credit on signup
- ✅ Automatic sleep after 15 min inactivity

**Vercel:**
- ✅ Unlimited deployments FREE
- ✅ Automatic HTTPS
- ✅ Global CDN

**Total: $0/month** 🎉

---

# 📱 Using Your Deployed App

1. Share the Vercel URL with your team
2. Upload PDF resumes
3. Parse and download Excel
4. All data processes on Railway backend

---

# 🔄 Updating Your App

When you make changes:

```powershell
# Make your code changes

cd c:\Users\adipande1\Downloads\talentfor-hr
git add .
git commit -m "Your update message"
git push

# Railway and Vercel auto-deploy on push!
```

Both platforms auto-deploy when you push to GitHub!

---

# ✅ Deployment Checklist

- [ ] Git repository created
- [ ] Code pushed to GitHub
- [ ] Railway project created
- [ ] Backend deployed on Railway
- [ ] Backend URL copied
- [ ] Frontend config updated with backend URL
- [ ] Frontend deployed on Vercel
- [ ] Both URLs tested and working
- [ ] Upload and parse features tested
- [ ] Excel export tested

---

# 🎉 Success!

Your TalentFox Resume Parser is now live and accessible worldwide!

**Share your app:**
- Frontend: `https://your-app.vercel.app`
- Anyone can access it
- Free hosting
- Auto-scales
- HTTPS enabled

Done! 🚀
