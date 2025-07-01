<div align="center">

  <img src="https://github.com/intrain-ai/.github/blob/main/assets/Banner%20-%20intrain.ai.png" alt="inTrain.ai Banner" width="100%"/>

  <h1>📱 inTrain.ai – Android Mobile App</h1>

  <p>
    <img src="https://img.shields.io/badge/inTraini.ai-6471e5?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
    <img src="https://img.shields.io/badge/Jetpack_Compose-3DDC84?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
    <img src="https://img.shields.io/badge/Android_Studio-4285F4?style=for-the-badge&logo=android-studio&logoColor=white"/>
  </p>

  <h4>Modern Android App for the inTrain.ai Learning Ecosystem</h4>

</div>

---

## 📲 Overview

`mobile-intrain` adalah aplikasi Android official dari **inTrain.ai**, platform pembelajaran dan mentoring berbasis AI.  
Dibangun dengan arsitektur **MVVM**, **Jetpack Compose**, dan konsumsi API berbasis **Retrofit**, aplikasi ini menawarkan:

- Mentor Booking System
- CV Review via AI
- Riwayat Chat & Feedback
- Roadmap Progress Learning
- Admin & User POV dalam satu app

---

## 🚀 Features

- 🔐 **Authentication System**
- 👨‍🏫 **Mentorship Registration & Booking**
- 📄 **CV Review by AI (ATS)**
- 📚 **Learning Roadmap + Step Tracking**
- 🧠 **AI Chat History (with feedback)**
- 🧭 **Dynamic UI Based on User Roles**
- 💬 **Realtime Dialog (Coming Soon)**

---

## ⚙️ Tech Stack

| Layer         | Tech Stack                          |
|--------------|--------------------------------------|
| Language      | Kotlin                              |
| UI Toolkit    | Jetpack Compose                     |
| Architecture  | MVVM + ViewModel + LiveData         |
| Network       | Retrofit2 + Gson                    |
| State Holder  | StateFlow / LiveData                |
| Local Storage | SharedPreferences                   |
| Dependency    | Manual DI / (Hilt-ready)            |

---

## 📁 Project Structure

```plaintext
📦 mobile-intrain/
├── ui/                 # Screens (Compose UI)
│   ├── auth/           # Login/Register
│   ├── home/           # Dashboard, BottomNav
│   ├── mentor/         # Mentor list, detail, create schedule
│   ├── review/         # CV review screens
│   ├── history/        # History chat & CV review
├── viewmodel/          # ViewModel per screen
├── model/              # Data models (DTO, API response)
├── API/                # Retrofit API config
├── helper/             # SharedPrefHelper, constants
├── MainActivity.kt     # Launcher activity
└── navigation/         # NavGraph config
```
## 🧪 How to Run
1. Clone project:
```bash
git clone https://github.com/intrain-ai/mobile-intrain.git
```
2. Buka di Android Studio (Giraffe / Hedgehog+)
3. Sync Gradle dan Run via Emulator / Device

## 🌐 API Integration

Aplikasi ini terhubung ke backend **inTrain.ai** melalui REST API.  
Base URL dapat ditemukan dan dikonfigurasi di file `ApiConfig.kt`.

🔗 Untuk dokumentasi lebih lanjut, cek [Backend Repository](https://github.com/intrain-ai/backend-intrain)

### Contoh Endpoint yang Digunakan

- `GET /mentors` — Menampilkan daftar mentor  
- `POST /cv/review` — Mengirim CV untuk direview oleh AI  
- `GET /history` — Menampilkan riwayat sesi (chat & review)  
- `POST /availability` — Menambahkan jadwal ketersediaan mentor  
- `... dan endpoint lainnya`


## 🧠 Available User Roles
- User (Jobseeker): bisa daftar, review CV, lihat roadmap, book mentor
- Mentor: bisa register jadi mentor, atur ketersediaan (availability)

## 🤝 Contributing
Pengen bantuin? Boleh banget!
1. Fork repo
2. Create new branch
3. Commit your changes
4. Open Pull Request

## 👨‍💻 Built With 💙 by

<div align="center">

| Name                              | Student ID     |
|-----------------------------------|----------------|
| **Aldi Rahman Hermawan**          | 41822010004    |
| **Ari Satrio Murdoko Andjalmo**   | 41822010025    |

</div>

---

<div align="center">
  Powered by <strong>Jetpack Compose</strong>, <strong>Kotlin</strong>, and <em>semangat belajar 😎</em>
</div>

<div align="center"> <sub>© 2025 inTrain.ai — All rights reserved.</sub> </div>
