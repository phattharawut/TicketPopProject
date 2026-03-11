# 🎫 TICKETPOP — ระบบจองตั๋วคอนเสิร์ตออนไลน์

แอปพลิเคชัน Android สำหรับจองตั๋วคอนเสิร์ต รองรับผู้ใช้ทั่วไปและ Admin

---

## 📋 สารบัญ
- [ภาพรวม](#ภาพรวม)
- [เทคโนโลยี](#เทคโนโลยี)
- [โครงสร้างโปรเจกต์](#โครงสร้างโปรเจกต์)
- [การติดตั้งและรันระบบ](#การติดตั้งและรันระบบ)
- [API Endpoints](#api-endpoints)
- [ฟีเจอร์หลัก](#ฟีเจอร์หลัก)

---

## ภาพรวม

TICKETPOP แบ่งออกเป็น 2 ส่วนหลัก:

| ส่วน | รายละเอียด |
|------|-----------|
| **Android App** | Kotlin + Jetpack Compose |
| **Backend API** | Node.js + Express (port 8080) |
| **Database** | MySQL / MariaDB — `ticketpop_db` |

ผู้ใช้มี 2 ประเภท: **Customer** (จองตั๋ว) และ **Admin** (จัดการระบบ)

---

## เทคโนโลยี

### Frontend (Android)
- **Kotlin** + **Jetpack Compose**
- **Retrofit2** + Gson — HTTP Client
- **Navigation Compose** — Navigation Graph
- **ZXing Core 3.5.2** — QR Code Generator
- **Coil** — Image Loading
- **ViewModel** + **StateFlow** / **MutableState**

### Backend
- **Node.js** + **Express.js**
- **mysql2** — MySQL Connection Pool
- **JWT** (`jsonwebtoken`) — Authentication
- **bcryptjs** — Password Hashing
- **multer** — File Upload (max 5MB)

---

## โครงสร้างโปรเจกต์

```
Project TICKETPOP/
├── app/                            # Android App
│   └── src/main/java/com/example/ticketpop/
│       ├── MainActivity.kt         # NavGraph + BottomNav
│       ├── data/
│       │   ├── model/              # Kotlin data classes
│       │   ├── remote/             # Retrofit API interfaces
│       │   └── repository/         # Repository layer
│       ├── ui/
│       │   ├── auth/               # Login, Register, Profile
│       │   ├── home/               # Splash, Home, BottomNav
│       │   ├── concert/            # Detail, Artist, Venue
│       │   ├── seat/               # ZoneSelect, SeatMap, Standing
│       │   ├── payment/            # OrderSummary, Payment, Success
│       │   ├── ticket/             # MyTickets, TicketQR, History
│       │   └── admin/              # Dashboard, Create, Edit, Scan, SeatLayout
│       ├── utils/
│       │   ├── Constants.kt        # Routes + Config
│       │   └── SessionManager.kt   # JWT + User session
│       └── ui/theme/               # Colors, Typography
│
├── ticketpopApi/                   # Node.js Backend
│   ├── server.js                   # Express API server
│   ├── package.json
│   └── uploads/                    # Poster images
│
├── ticketpop_db (3).sql            # MySQL dump (import ตอนตั้งระบบ)
├── TICKETPOP_Report.html           # รายงานระบบฉบับสมบูรณ์
└── README.md
```

---

## การติดตั้งและรันระบบ

### 1. ตั้งค่าฐานข้อมูล MySQL

```sql
-- สร้าง database
CREATE DATABASE ticketpop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Import ข้อมูล
mysql -u root ticketpop_db < "ticketpop_db (3).sql"
```

> หรือใช้ phpMyAdmin import ไฟล์ `ticketpop_db (3).sql` เข้า database `ticketpop_db`

### 2. รัน Backend API

```bash
cd ticketpopApi
npm install
node server.js
# API จะรันที่ http://localhost:8080
```

**Environment Variables** (ไม่บังคับ — มี default แล้ว):
```
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=ticketpop_db
JWT_SECRET=ticketpop_super_secret_key
PORT=8080
```

### 3. ตั้งค่า Android App

แก้ไข `Constants.kt`:

```kotlin
// สำหรับ Android Emulator
const val BASE_URL = "http://10.0.2.2:8080/"

// สำหรับ อุปกรณ์จริง (ใส่ IP เครื่องที่รัน server)
// const val BASE_URL = "http://192.168.x.x:8080/"
```

### 4. Build & Run

เปิดโปรเจกต์ด้วย **Android Studio** แล้ว Run บน Emulator หรืออุปกรณ์จริง

---

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | สมัครสมาชิก |
| POST | `/api/auth/login` | เข้าสู่ระบบ |
| POST | `/api/auth/update-profile` | แก้ไขโปรไฟล์ |
| POST | `/api/auth/change-password` | เปลี่ยนรหัสผ่าน |
| GET | `/api/auth/user-stats/:userId` | ดึงสถิติผู้ใช้ |

### Concerts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/concerts` | รายการคอนเสิร์ต (ซ่อน Ended/Cancelled) |
| GET | `/api/concerts/:id` | รายละเอียดคอนเสิร์ต |
| POST | `/api/concerts` | Admin สร้างคอนเสิร์ต |
| PUT | `/api/concerts/:id` | Admin แก้ไขคอนเสิร์ต |
| DELETE | `/api/concerts/:id` | Admin ลบ (Soft Delete → Cancelled) |

### Zones & Seats
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/zones/:concertId` | ดึงโซน + ที่นั่งคงเหลือ |
| GET | `/api/seats/:zoneId` | ดึงที่นั่งในโซน |
| PUT | `/api/seats/:seatId/toggle` | Admin เปิด/ปิดที่นั่ง |

### Bookings & Tickets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings` | จองตั๋ว |
| GET | `/api/users/:userId/tickets` | ตั๋วทั้งหมดของ user |
| GET | `/api/tickets/:ticketId` | ดึงตั๋วตาม ID |
| PUT | `/api/tickets/:ticketId/use` | Admin Check-in ตั๋ว |

### Admin & Upload
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/stats` | สถิติ Dashboard |
| GET | `/api/admin/recent-concerts` | Concert ล่าสุด 5 รายการ |
| POST | `/api/upload` | อัปโหลดรูป poster |

---

## ฟีเจอร์หลัก

### Customer
- ✅ สมัครสมาชิก / เข้าสู่ระบบ
- ✅ ดูรายการและรายละเอียดคอนเสิร์ต
- ✅ เลือกโซน (Seated / Standing)
- ✅ เลือกที่นั่งจากแผนผัง (max 4 ที่)
- ✅ ชำระเงิน (PromptPay / Credit Card)
- ✅ ดู QR Code ตั๋ว (ZXing จริง)
- ✅ ดูประวัติการซื้อตั๋ว
- ✅ แก้ไขโปรไฟล์ / เปลี่ยนรหัสผ่าน

### Admin
- ✅ Dashboard สรุปสถิติ (รายได้/ตั๋ว/คอนเสิร์ต/อัตราเข้าชม)
- ✅ สร้าง / แก้ไข / ลบคอนเสิร์ต
- ✅ จัดการ Seat Layout (เปิด/ปิดที่นั่ง)
- ✅ สแกน QR / กรอก Ticket ID ตรวจสอบตั๋ว
- ✅ Check-in (Mark ตั๋วว่าใช้แล้ว)

### ระบบ Soft Delete
คอนเสิร์ตที่ถูกลบจะเปลี่ยน `status = 'Cancelled'` ไม่ลบข้อมูลจริง
ตั๋วของ user จะแสดงสีแดงพร้อมข้อความ **"คอนเสิร์ตถูกยกเลิก"**

---

## Account ทดสอบ

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@ad.com` | `1234` |
| Customer | `test@test.com` | `1234` |

---

> 📄 ดูรายงานระบบฉบับสมบูรณ์ได้ที่ `TICKETPOP_Report.html`
