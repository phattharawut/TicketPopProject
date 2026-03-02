const express = require("express");
const mysql = require("mysql2/promise");
require("dotenv").config();
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken"); // เพิ่ม jwt สำหรับทำ Authentication
const saltRounds = 10;

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// กำหนด Secret Key สำหรับ JWT (ควรใส่ใน .env ในการทำงานจริง)
const JWT_SECRET = process.env.JWT_SECRET || "ticketpop_super_secret_key";

const pool = mysql.createPool({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "ticketpop_db",
    waitForConnections: true,
    connectionLimit: 10,
});

// Helper function สำหรับห่อหุ้ม Response ให้ตรงกับ ApiResponse<T>
const createResponse = (success, message, data = null) => {
    return { success, message, data };
};

// ==========================================
// --- AUTHENTICATION ---
// ==========================================

// [POST] /api/auth/register
app.post("/api/auth/register", async (req, res) => {
    try {
        const { username, password, fullName, email, phoneNumber } = req.body;
        const hashedPassword = await bcrypt.hash(password, saltRounds);
        
        const [result] = await pool.execute(
            "INSERT INTO USERS (username, password_hash, full_name, email, phone_number, role) VALUES (?, ?, ?, ?, ?, 'Customer')",
            [username, hashedPassword, fullName, email, phoneNumber]
        );
        
        // ดึงข้อมูล User กลับไปให้
        const [users] = await pool.execute("SELECT user_id AS userId, username, full_name AS fullName, email, phone_number AS phoneNumber, role, created_at AS createdAt FROM USERS WHERE user_id = ?", [result.insertId]);
        
        res.status(201).json(createResponse(true, "Register successful", users[0]));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [POST] /api/auth/login
app.post("/api/auth/login", async (req, res) => {
    try {
        const { username, password } = req.body;
        const [users] = await pool.execute("SELECT * FROM USERS WHERE username = ?", [username]);

        if (users.length === 0) return res.status(401).json(createResponse(false, "User not found"));

        const user = users[0];
        const isMatch = await bcrypt.compare(password, user.password_hash);
        if (!isMatch) return res.status(401).json(createResponse(false, "Invalid password"));

        // สร้าง JWT Token
        const token = jwt.sign({ userId: user.user_id, role: user.role }, JWT_SECRET, { expiresIn: '1d' });

        const userData = {
            userId: user.user_id,
            username: user.username,
            fullName: user.full_name,
            email: user.email,
            phoneNumber: user.phone_number,
            role: user.role,
            createdAt: user.created_at,
            token: token // ส่ง Token พ่วงไปกับ Object หรือแยกต่างหากตามตกลงกันในทีม
        };

        res.json(createResponse(true, "Login successful", userData));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- CONCERTS ---
// ==========================================

// [GET] /api/concerts
app.get("/api/concerts", async (req, res) => {
    try {
        const [results] = await pool.execute(
            "SELECT concert_id AS concertId, title, description, venue_name AS venueName, show_date AS showDate, show_time AS showTime, poster_image_url AS posterImageUrl, status FROM CONCERTS"
        );
        res.json(createResponse(true, "Concerts fetched", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [GET] /api/concerts/{concertId}
app.get("/api/concerts/:concertId", async (req, res) => {
    try {
        const [results] = await pool.execute(
            "SELECT concert_id AS concertId, title, description, venue_name AS venueName, show_date AS showDate, show_time AS showTime, poster_image_url AS posterImageUrl, status FROM CONCERTS WHERE concert_id = ?", 
            [req.params.concertId]
        );
        if (results.length === 0) return res.status(404).json(createResponse(false, "Concert not found"));
        res.json(createResponse(true, "Concert detail fetched", results[0]));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [POST] /api/concerts
app.post("/api/concerts", async (req, res) => {
    try {
        const { title, description, venueName, showDate, showTime, posterImageUrl } = req.body;
        const status = 'Upcoming'; // ค่าเริ่มต้น
        
        const [result] = await pool.execute(
            "INSERT INTO CONCERTS (title, description, venue_name, show_date, show_time, poster_image_url, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
            [title, description, venueName, showDate, showTime, posterImageUrl, status]
        );
        
        const [newConcert] = await pool.execute("SELECT concert_id AS concertId, title, description, venue_name AS venueName, show_date AS showDate, show_time AS showTime, poster_image_url AS posterImageUrl, status FROM CONCERTS WHERE concert_id = ?", [result.insertId]);
        res.status(201).json(createResponse(true, "Concert created", newConcert[0]));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- ZONES & SEATS ---
// ==========================================

// [GET] /api/concerts/{concertId}/zones
app.get("/api/concerts/:concertId/zones", async (req, res) => {
    try {
        const [results] = await pool.execute(
            "SELECT zone_id AS zoneId, concert_id AS concertId, zone_name AS zoneName, price, type, color_code AS colorCode, capacity FROM ZONES WHERE concert_id = ?", 
            [req.params.concertId]
        );
        res.json(createResponse(true, "Zones fetched", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [GET] /api/zones/{zoneId}/seats
app.get("/api/zones/:zoneId/seats", async (req, res) => {
    try {
        const [results] = await pool.execute(
            "SELECT seat_id AS seatId, zone_id AS zoneId, row_label AS rowLabel, number_label AS numberLabel, is_active AS isActive, is_reserved AS isReserved FROM SEATS WHERE zone_id = ?", 
            [req.params.zoneId]
        );
        res.json(createResponse(true, "Seats fetched", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- BOOKINGS & TICKETS ---
// ==========================================

// [POST] /api/bookings
app.post("/api/bookings", async (req, res) => {
    const connection = await pool.getConnection();
    try {
        await connection.beginTransaction();

        const { userId, zoneId, seatIds, paymentMethod } = req.body; // เปลี่ยน key ให้ตรงกับ Client

        // ดึงราคาโซนเพื่อคำนวณ totalAmount แบบปลอดภัย (ไม่เชื่อถือค่าจาก Client 100%)
        const [zones] = await connection.query("SELECT price FROM ZONES WHERE zone_id = ?", [zoneId]);
        if (zones.length === 0) throw new Error("Zone not found");
        const totalAmount = zones[0].price * seatIds.length;

        // 1. ตรวจสอบที่นั่ง
        const [seats] = await connection.query(
            "SELECT seat_id FROM SEATS WHERE seat_id IN (?) AND is_reserved = FALSE FOR UPDATE",
            [seatIds]
        );

        if (seats.length !== seatIds.length) {
            throw new Error("Some seats are already taken!");
        }

        // 2. สร้างรายการจอง
        const [bookingResult] = await connection.execute(
            "INSERT INTO BOOKINGS (user_id, total_amount, status, payment_method) VALUES (?, ?, 'Paid', ?)",
            [userId, totalAmount, paymentMethod]
        );
        const bookingId = bookingResult.insertId;

        // 3. สร้างตั๋วและอัปเดตสถานะที่นั่ง (เปลี่ยนฟิลด์ตาม DB ล่าสุด)
        for (let seatId of seatIds) {
            await connection.execute(
                "INSERT INTO TICKETS (booking_id, zone_id, seat_id) VALUES (?, ?, ?)",
                [bookingId, zoneId, seatId]
            );
            await connection.execute("UPDATE SEATS SET is_reserved = TRUE WHERE seat_id = ?", [seatId]);
        }

        await connection.commit();
        
        // ส่งคืน Booking data
        const [newBooking] = await connection.execute("SELECT booking_id AS bookingId, user_id AS userId, total_amount AS totalAmount, booking_date AS bookingDate, status, payment_method AS paymentMethod FROM BOOKINGS WHERE booking_id = ?", [bookingId]);
        res.status(201).json(createResponse(true, "Booking successful", newBooking[0]));

    } catch (err) {
        await connection.rollback();
        res.status(400).json(createResponse(false, err.message));
    } finally {
        connection.release();
    }
});

// [GET] /api/users/{userId}/tickets
app.get("/api/users/:userId/tickets", async (req, res) => {
    try {
        const sql = `
            SELECT t.ticket_id AS ticketId, b.booking_id AS bookingId, z.zone_id AS zoneId, 
                   z.zone_name AS zoneName, t.seat_id AS seatId, s.row_label AS rowLabel, 
                   s.number_label AS numberLabel, c.title AS concertTitle, 
                   c.show_date AS showDate, c.show_time AS showTime, c.venue_name AS venueName
            FROM TICKETS t
            JOIN BOOKINGS b ON t.booking_id = b.booking_id
            JOIN ZONES z ON t.zone_id = z.zone_id
            JOIN CONCERTS c ON z.concert_id = c.concert_id
            LEFT JOIN SEATS s ON t.seat_id = s.seat_id
            WHERE b.user_id = ?
        `;
        const [results] = await pool.execute(sql, [req.params.userId]);
        res.json(createResponse(true, "Tickets fetched", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [GET] /api/tickets/{ticketId}
app.get("/api/tickets/:ticketId", async (req, res) => {
    try {
        const sql = `
            SELECT t.ticket_id AS ticketId, b.booking_id AS bookingId, z.zone_id AS zoneId, 
                   z.zone_name AS zoneName, t.seat_id AS seatId, s.row_label AS rowLabel, 
                   s.number_label AS numberLabel, c.title AS concertTitle, 
                   c.show_date AS showDate, c.show_time AS showTime, c.venue_name AS venueName
            FROM TICKETS t
            JOIN BOOKINGS b ON t.booking_id = b.booking_id
            JOIN ZONES z ON t.zone_id = z.zone_id
            JOIN CONCERTS c ON z.concert_id = c.concert_id
            LEFT JOIN SEATS s ON t.seat_id = s.seat_id
            WHERE t.ticket_id = ?
        `;
        const [results] = await pool.execute(sql, [req.params.ticketId]);
        if (results.length === 0) return res.status(404).json(createResponse(false, "Ticket not found"));
        res.json(createResponse(true, "Ticket fetched", results[0]));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

const PORT = process.env.PORT || 8080; // เปลี่ยนเป็น 8080 ตาม Constants.kt ใน Android
app.listen(PORT, () => console.log(`TICKETPOP API running on port ${PORT}`));