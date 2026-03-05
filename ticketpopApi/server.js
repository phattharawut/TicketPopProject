const express = require("express");
const mysql = require("mysql2/promise");
require("dotenv").config();
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const saltRounds = 10;

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const JWT_SECRET = process.env.JWT_SECRET || "ticketpop_super_secret_key";

const pool = mysql.createPool({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "ticketpop_db",
    waitForConnections: true,
    connectionLimit: 10,
});

const createResponse = (success, message, data = null) => {
    return { success, message, data };
};

// --- หน้าแรก (Root Path) ---
app.get("/", (req, res) => {
    res.send("<h1>TICKETPOP API is running!</h1><p>The server is connected and ready to serve requests.</p>");
});

// ==========================================
// --- AUTHENTICATION ---
// ==========================================

// [POST] /api/auth/register
app.post("/api/auth/register", async (req, res) => {
    try {
        let { fullName, email, phone, password } = req.body;
        fullName = fullName?.trim();
        email = email?.trim();
        phone = phone?.trim();

        if (!phone || phone.length !== 10) {
            return res.status(400).json(createResponse(false, "เบอร์โทรศัพท์ต้องมี 10 หลัก"));
        }

        const username = email.split('@')[0];

        const [existing] = await pool.execute(
            "SELECT * FROM users WHERE email = ? OR phone_number = ? OR username = ?",
            [email, phone, username]
        );
        if (existing.length > 0) {
            return res.status(400).json(createResponse(false, "อีเมล, เบอร์โทร หรือชื่อผู้ใช้นี้ถูกใช้งานแล้ว"));
        }

        const hashedPassword = await bcrypt.hash(password, saltRounds);

        const [result] = await pool.execute(
            "INSERT INTO users (username, password_hash, full_name, email, phone_number, role) VALUES (?, ?, ?, ?, ?, 'Customer')",
            [username, hashedPassword, fullName, email, phone]
        );

        res.status(201).json(createResponse(true, "สมัครสมาชิกสำเร็จ", {
            user: { id: result.insertId.toString(), fullName, email, phone, role: 'Customer', level: "Bronze" }
        }));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดในการลงทะเบียน"));
    }
});

// [POST] /api/auth/login
app.post("/api/auth/login", async (req, res) => {
    try {
        let { email, username, password } = req.body;
        let identifier = (email || username)?.trim();

        if (!identifier) {
            return res.status(400).json(createResponse(false, "กรุณากรอกอีเมล, เบอร์โทรศัพท์ หรือชื่อผู้ใช้งาน"));
        }

        const [users] = await pool.execute(
            "SELECT * FROM users WHERE email = ? OR phone_number = ? OR username = ?",
            [identifier, identifier, identifier]
        );

        if (users.length === 0) return res.status(401).json(createResponse(false, "ไม่พบผู้ใช้งานนี้"));

        const user = users[0];
        let isMatch = false;
        try { isMatch = await bcrypt.compare(password, user.password_hash); }
        catch (e) { isMatch = (password === user.password_hash); }

        if (!isMatch && password === user.password_hash) {
            isMatch = true;
        }

        if (!isMatch) return res.status(401).json(createResponse(false, "รหัสผ่านไม่ถูกต้อง"));

        const token = jwt.sign({ userId: user.user_id, role: user.role }, JWT_SECRET, { expiresIn: '1d' });

        res.json(createResponse(true, "เข้าสู่ระบบสำเร็จ", {
            token: token,
            user: {
                id: user.user_id.toString(),
                username: user.username,
                fullName: user.full_name,
                email: user.email,
                phone: user.phone_number,
                role: user.role,
                level: "Bronze"
            }
        }));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดในการเข้าสู่ระบบ"));
    }
});

// [POST] /api/auth/update-profile
app.post("/api/auth/update-profile", async (req, res) => {
    try {
        let { userId, fullName, phone } = req.body;
        phone = phone?.trim();

        if (!phone || phone.length !== 10) {
            return res.status(400).json(createResponse(false, "เบอร์โทรศัพท์ต้องมี 10 หลัก"));
        }

        await pool.execute("UPDATE users SET full_name = ?, phone_number = ? WHERE user_id = ?", [fullName, phone, userId]);
        const [updated] = await pool.execute("SELECT * FROM users WHERE user_id = ?", [userId]);
        const user = updated[0];

        res.json(createResponse(true, "อัปเดตโปรไฟล์สำเร็จ", {
            id: user.user_id.toString(),
            fullName: user.full_name,
            email: user.email,
            phone: user.phone_number,
            role: user.role,
            level: "Bronze"
        }));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

// [POST] /api/auth/change-password
app.post("/api/auth/change-password", async (req, res) => {
    try {
        const { userId, oldPassword, newPassword } = req.body;
        const [users] = await pool.execute("SELECT * FROM users WHERE user_id = ?", [userId]);
        if (users.length === 0) return res.status(404).json(createResponse(false, "ไม่พบผู้ใช้"));
        const user = users[0];

        let isMatch = false;
        try { isMatch = await bcrypt.compare(oldPassword, user.password_hash); }
        catch (e) { isMatch = (oldPassword === user.password_hash); }

        if (!isMatch) return res.status(400).json(createResponse(false, "รหัสผ่านเดิมไม่ถูกต้อง"));

        const hashedNewPassword = await bcrypt.hash(newPassword, saltRounds);
        await pool.execute("UPDATE users SET password_hash = ? WHERE user_id = ?", [hashedNewPassword, userId]);
        res.json(createResponse(true, "เปลี่ยนรหัสผ่านสำเร็จแล้ว"));
    } catch (err) { res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดที่เซิร์ฟเวอร์")); }
});

// [GET] /api/auth/user-stats/:userId
app.get("/api/auth/user-stats/:userId", async (req, res) => {
    try {
        const userId = req.params.userId;
        const [bookings] = await pool.execute("SELECT COUNT(*) as ticketCount FROM bookings WHERE user_id = ? AND status = 'Paid'", [userId]);
        const [spending] = await pool.execute("SELECT SUM(total_amount) as totalSpending FROM bookings WHERE user_id = ? AND status = 'Paid'", [userId]);
        const [history] = await pool.execute("SELECT COUNT(*) as historyCount FROM bookings WHERE user_id = ? AND status != 'Cancelled'", [userId]);

        res.json({
            ticketCount: bookings[0].ticketCount.toString(),
            points: (spending[0].totalSpending || 0).toLocaleString(),
            historyCount: history[0].historyCount.toString()
        });
    } catch (err) {
        console.error("Stats Error:", err);
        res.status(500).json(createResponse(false, "Error fetching stats"));
    }
});

// --- API อื่นๆ (Concerts, Bookings) ---
app.get("/api/concerts", async (req, res) => {
    try {
        const [results] = await pool.execute("SELECT concert_id AS concertId, title, description, venue_name AS venueName, show_date AS showDate, show_time AS showTime, poster_image_url AS posterImageUrl, status FROM concerts");
        res.json(createResponse(true, "Concerts fetched", results));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, "0.0.0.0", () => console.log(`TICKETPOP API running on http://localhost:${PORT}`));