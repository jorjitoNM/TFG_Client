# 🌟 NOMADA - Android Client ✨

<div align="center">

<img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/app_logo_v1.png" alt="NOMADA Logo" width="200"/>

### 🗺️ *Discover. Create. Share. Explore.* 🌍

*A location-based social note-taking experience that brings your memories to life!*

</div>

---

## 🚀 What is NOMADA?

#### Nomada is a **revolutionary** 📱 location-based note-taking Android application that transforms how you capture and share experiences!
#### Create notes tied to specific places, discover hidden gems from other users, and build a social network around real-world locations! 🌟
---

## ✨ Amazing Features

### 🔐 **Secure Authentication System**
- 🆔 **Smart Registration & Login** with real-time validation
- 👆 **Biometric Authentication** - Login with your fingerprint or face!
- 🔒 **Military-grade Security** with encrypted credential storage
- 🎨 **Beautiful UI** with custom themes and animations

### 📝 **Rich Note Management**

Create different types of notes with unique icons for each category:

<div align="center">

| Note Type | Icon | Description |
|-----------|------|-------------|
| **Classic** 📖 | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/classic.svg" width="32"/> | Everyday thoughts |
| **Event** 🎉 | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/event.svg" width="32"/> | Special occasions and events |
| **Historical** 🏛️ | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/historical.svg" width="32"/> | Cultural heritage sites |
| **Food** 🍕 | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/food.svg" width="32"/> | Culinary adventures and reviews |
| **Landscape** 🏔️ | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/landscape.svg" width="32"/> | Breathtaking natural landscapes |
| **Cultural** 🎭 | <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/cultural.svg" width="32"/> | Art, museums, and cultural experiences |

</div>

- 🔒 **Privacy Controls** - Public, Private, or Friends-only
- ⭐ **Rating System** - Rate your experiences from 1–10
- 📍 **GPS Integration** - Automatic location tagging

### 🗺️ **Interactive Map Experience**
- 🌍 **Google Maps Integration** with custom markers
- 🔍 **Smart Location Search** - Find any place in the world
- 📍 **Real-time GPS** - Always know where you are
- 🎨 **Dark/Light Mode** support for comfortable viewing
- 🏷️ **Filter by Note Type** - Find exactly what you're looking for

<div align="center">
<img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/multinote.svg" alt="Multiple Notes Marker" width="48"/>
<br/>
<em>Special marker for locations with multiple notes</em>
</div>

### 👥 **Social Features**
- 👤 **User Profiles** - Showcase your adventures
- 🔍 **User Discovery** - Find and connect with fellow explorers
- 👍 **Like & Favorite System** - Show appreciation for great content
- 🌐 **Social Interactions** - Engage with the community

---

## 🛠️ **Cutting-Edge Technology Stack**

### 🏗️ **Architecture**
- 🎯 **MVVM Pattern** - Clean, maintainable code structure
- 🧩 **Jetpack Compose** - Modern, declarative UI framework
- 🏛️ **Clean Architecture** - Separation of concerns done right

### 📦 **Dependencies & Libraries**
- 💉 **Hilt** - Dependency injection made simple
- 🧭 **Compose Navigation** - Smooth screen transitions
- 🌐 **Retrofit + OkHttp** - Robust API communication
- 💾 **Room + DataStore** - Efficient local storage
- 🔥 **Firebase** - Crashlytics and Firestore integration
- 🖼️ **Coil** - Lightning-fast image loading

---

## 🚀 **Quick Start Guide**

### 📋 **Prerequisites**
- 🤖 **Android Studio** Arctic Fox or later
- 📱 **Android SDK 35**
- 🗺️ **Google Maps API Key** (we'll help you set it up!)

### ⚙️ **Build Configuration**

🌍 **Two Environments Available:**
- 🔧 **Development**: `http://192.168.0.63:8080/`
- 🌐 **Production**: `https://informatica.iesquevedo.es/nomada/`

📱 **Device Requirements:**
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 35
- **Compile SDK**: 35

### 🎯 **Installation Steps**

1. 📥 **Clone the repository**
   ```bash
   git clone https://github.com/jorjitoNM/TFG_Client.git
   ```

2. 🔧 **Open in Android Studio**

3. 🗺️ **Configure Google Maps API Key**
    - Get your API key from Google Cloud Console
    - Add it to your `AndroidManifest.xml`

4. 🎛️ **Select Build Variant**
    - Choose `development` for local testing
    - Choose `production` for release builds

5. ▶️ **Build and Run!**

---

## 📱 **App Structure & Flow**

### 🎬 **User Journey**
```
🌟 Splash Screen → 🚪 Start Screen → 🔐 Authentication → 🏠 Main App
                                                        ↓
                    🗺️ Map View ← → 📝 Notes List ← → 👤 Profile ← → 🔍 Search
```

### 🏗️ **Main Components**
- 🎬 **MainActivity** - App entry point with splash integration
- 🧭 **Navigation System** - Seamless screen transitions
- 💉 **Hilt Application** - Dependency injection setup

---

## 🔒 **Security Features**

- 👆 **Biometric Authentication** - Your device, your security
- 🔐 **Encrypted Storage** - Your data stays safe
- 🌐 **HTTPS Communication** - Secure API connections
- 🛡️ **Android Keystore** - Hardware-backed security

---

## 🎨 **Additional UI Elements**

<div align="center">

<h3>🎨 <b>App Icons & Assets</b></h3>

<span>
  <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/google_logo.png" width="16"/>
  <span>Google Authentication</span>
</span>

<br>

<span>
  <img src="https://github.com/jorjitoNM/TFG_Client/blob/main/app/src/main/res/drawable/google_marker.svg" width="16"/>
  <span>Google Marker</span>
</span>

</div>


---

## 🤝 **Contributing**

We welcome contributions! This project showcases:
- 🏗️ **Modern Android Development** practices
- 🧩 **Clean Architecture** principles
- 🎨 **Jetpack Compose** mastery
- 💉 **Dependency Injection** with Hilt
- 🔄 **Reactive Programming** patterns
