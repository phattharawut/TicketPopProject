const express = require("express");
const mysql = require("mysql2/promise");
require("dotenv").config();
const bcrypt = require("bcrypt");
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

// Helper function
const createResponse = (success, message, data = null) => {
    return { success, message, data };
};

// ==========================================
// --- AUTHENTICATION ---
// ==========================================

// [POST] /api/auth/register
// Android sends: { fullName, email, phone, password }
app.post("/api/auth/register", async (req, res) => {
    try {
        const { fullName, email, phone, password } = req.body;
        
        // Check if user already exists
        const [existing] = await pool.execute("SELECT * FROM users WHERE email = ?", [email]);
        if (existing.length > 0) {
            return res.status(400).json(createResponse(false, "อีเมลนี้ถูกใช้งานแล้ว"));
        }

        const hashedPassword = await bcrypt.hash(password, saltRounds);
        const username = email.split('@')[0]; // Simple username from email

        const [result] = await pool.execute(
            "INSERT INTO users (username, password_hash, full_name, email, phone_number, role) VALUES (?, ?, ?, ?, ?, 'Customer')",
            [username, hashedPassword, fullName, email, phone]
        );
        
        const user = {
            id: result.insertId.toString(),
            fullName: fullName,
            email: email,
            phone: phone,
            level: "Bronze"
        };
        
        res.status(201).json({ user: user }); // Match Android AuthResponse
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดในการลงทะเบียน: " + err.message));
    }
});

// [POST] /api/auth/login
// Android sends: { email, password }
app.post("/api/auth/login", async (req, res) => {
    try {
        const { email, password } = req.body;

        // Search by email or username
        const [users] = await pool.execute("SELECT * FROM users WHERE email = ? OR username = ?", [email, email]);

        if (users.length === 0) {
            return res.status(401).json(createResponse(false, "ไม่พบผู้ใช้งานนี้"));
        }

        const user = users[0];
        const isMatch = await bcrypt.compare(password, user.password_hash);
        if (!isMatch) {
            return res.status(401).json(createResponse(false, "รหัสผ่านไม่ถูกต้อง"));
        }

        const token = jwt.sign({ userId: user.user_id, role: user.role }, JWT_SECRET, { expiresIn: '1d' });

        const userData = {
            id: user.user_id.toString(),
            fullName: user.full_name,
            email: user.email,
            phone: user.phone_number,
            level: "Bronze", // Default level
            profileImageUrl: null
        };

        res.json({
            token: token,
            user: userData
        });
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดในการเข้าสู่ระบบ"));
    }
});

// ==========================================
// --- CONCERTS & OTHER APIS ---
// ==========================================

app.get("/api/concerts", async (req, res) => {
    try {
        const [results] = await pool.execute("SELECT * FROM concerts");
        res.json(createResponse(true, "Fetched concerts", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`TICKETPOP API running on http://localhost:${PORT}`);
});