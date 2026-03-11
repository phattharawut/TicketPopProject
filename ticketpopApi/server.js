const express = require("express");
const mysql = require("mysql2/promise");
require("dotenv").config();
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const multer = require("multer");
const path = require("path");
const fs = require("fs");
const saltRounds = 10;

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// ==========================================
// --- IMAGE UPLOAD CONFIG ---
// ==========================================
const uploadsDir = path.join(__dirname, "uploads");
if (!fs.existsSync(uploadsDir)) fs.mkdirSync(uploadsDir);

app.use("/uploads", express.static(uploadsDir));

const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, uploadsDir),
    filename: (req, file, cb) => {
        const ext = path.extname(file.originalname).toLowerCase() || ".jpg";
        cb(null, `poster_${Date.now()}${ext}`);
    }
});

const upload = multer({
    storage,
    limits: { fileSize: 5 * 1024 * 1024 }, // 5MB
    fileFilter: (req, file, cb) => {
        const allowed = /jpeg|jpg|png|webp/;
        const ok = allowed.test(path.extname(file.originalname).toLowerCase())
            && allowed.test(file.mimetype);
        ok ? cb(null, true) : cb(new Error("เฉพาะรูป JPG/PNG/WEBP เท่านั้น"));
    }
});

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

app.get("/", (req, res) => {
    res.send("<h1>TICKETPOP API is running!</h1>");
});

// ==========================================
// --- AUTHENTICATION ---
// ==========================================

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

        const newUserId = result.insertId;
        const token = jwt.sign({ userId: newUserId, role: 'Customer' }, JWT_SECRET, { expiresIn: '1d' });

        res.status(201).json(createResponse(true, "สมัครสมาชิกสำเร็จ", {
            token: token,
            user: { id: newUserId.toString(), fullName, email, phone, role: 'Customer', level: "Bronze" }
        }));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, "เกิดข้อผิดพลาดในการลงทะเบียน"));
    }
});

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

// ==========================================
// --- CONCERTS ---
// ==========================================

app.get("/api/concerts", async (req, res) => {
    try {
        const [results] = await pool.execute(`
            SELECT c.concert_id AS concertId, c.title, c.description, 
                   c.venue_name AS venueName, 
                   DATE_FORMAT(c.show_date, '%Y-%m-%d') AS showDate, 
                   c.show_time AS showTime, 
                   c.poster_image_url AS posterImageUrl, c.status,
                   MIN(z.price) AS minPrice
            FROM concerts c
            LEFT JOIN zones z ON c.concert_id = z.concert_id
            GROUP BY c.concert_id
        `);
        res.json(createResponse(true, "Concerts fetched", results));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

app.get("/api/concerts/:concertId", async (req, res) => {
    try {
        const [rows] = await pool.execute(`
            SELECT c.concert_id AS concertId, c.title, c.description,
                   c.venue_name AS venueName,
                   DATE_FORMAT(c.show_date, '%Y-%m-%d') AS showDate,
                   c.show_time AS showTime,
                   c.poster_image_url AS posterImageUrl, c.status,
                   MIN(z.price) AS minPrice
            FROM concerts c
            LEFT JOIN zones z ON c.concert_id = z.concert_id
            WHERE c.concert_id = ?
            GROUP BY c.concert_id
        `, [req.params.concertId]);
        if (rows.length === 0) return res.status(404).json(createResponse(false, "ไม่พบคอนเสิร์ตนี้"));
        res.json(createResponse(true, "Concert detail fetched", rows[0]));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

// [POST] /api/concerts — Admin สร้างคอนเสิร์ตใหม่พร้อมโซน
app.post("/api/concerts", async (req, res) => {
    const connection = await pool.getConnection();
    try {
        await connection.beginTransaction();

        const { title, description, venueName, showDate, showTime, posterImageUrl, zones } = req.body;

        if (!title || !venueName || !showDate || !zones || zones.length === 0) {
            return res.status(400).json(createResponse(false, "กรุณากรอกข้อมูลให้ครบ"));
        }

        const [concertResult] = await connection.execute(
            "INSERT INTO concerts (title, description, venue_name, show_date, show_time, poster_image_url, status) VALUES (?, ?, ?, ?, ?, ?, 'Active')",
            [title, description || "", venueName, showDate, showTime || "00:00", posterImageUrl || null]
        );
        const concertId = concertResult.insertId;

        for (const zone of zones) {
            const [zoneResult] = await connection.execute(
                "INSERT INTO zones (concert_id, zone_name, type, price, capacity, color_code) VALUES (?, ?, ?, ?, ?, ?)",
                [concertId, zone.zoneName, zone.type, zone.price, zone.capacity, zone.colorCode || "#7B2FBE"]
            );
            const zoneId = zoneResult.insertId;

            // Auto-generate seats สำหรับโซนนั่ง
            if (zone.type === "Seated" && zone.capacity > 0) {
                const letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                const seatsPerRow = zone.seatsPerRow || 5;
                for (let i = 0; i < zone.capacity; i++) {
                    const rowIdx = Math.floor(i / seatsPerRow);
                    const colIdx = (i % seatsPerRow) + 1;
                    const rowLabel = letters[rowIdx] || "Z";
                    await connection.execute(
                        "INSERT INTO seats (concert_id, zone_id, row_label, number_label, is_active, is_reserved) VALUES (?, ?, ?, ?, 1, 0)",
                        [concertId, zoneId, rowLabel, colIdx.toString()]
                    );
                }
            }
        }

        await connection.commit();
        res.json(createResponse(true, "สร้างคอนเสิร์ตสำเร็จ!", { concertId }));
    } catch (err) {
        await connection.rollback();
        console.error("Create Concert Error:", err);
        res.status(500).json(createResponse(false, err.message));
    } finally {
        connection.release();
    }
});

// [GET] /api/concerts/:concertId/zones-seats — ดึงทุกโซน + seats ของ concert นั้น
app.get("/api/concerts/:concertId/zones-seats", async (req, res) => {
    try {
        const concertId = req.params.concertId;

        // ดึง zones
        const [zones] = await pool.execute(
            "SELECT zone_id AS zoneId, zone_name AS zoneName, type, price, capacity, color_code AS colorCode FROM zones WHERE concert_id = ?",
            [concertId]
        );

        // ดึง seats ทุกตัวของ concert นี้
        const [seats] = await pool.execute(`
            SELECT s.seat_id AS seatId, s.zone_id AS zoneId,
                   s.row_label AS rowLabel, s.number_label AS numberLabel,
                   s.is_active AS isActive, s.is_reserved AS isReserved
            FROM seats s
            WHERE s.concert_id = ?
            ORDER BY s.zone_id, s.row_label, CAST(s.number_label AS UNSIGNED)
        `, [concertId]);

        // group seats ตาม zoneId
        const seatsByZone = {};
        for (const seat of seats) {
            if (!seatsByZone[seat.zoneId]) seatsByZone[seat.zoneId] = [];
            seatsByZone[seat.zoneId].push({
                seatId: seat.seatId,
                zoneId: seat.zoneId,
                rowLabel: seat.rowLabel,
                numberLabel: seat.numberLabel,
                isActive: seat.isActive ? 1 : 0,
                isReserved: seat.isReserved ? 1 : 0
            });
        }

        const result = zones.map(z => ({
            ...z,
            seats: seatsByZone[z.zoneId] || []
        }));

        res.json(createResponse(true, "Zones and seats fetched", result));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, err.message));
    }
});

app.put("/api/concerts/reorder", async (req, res) => {
    try {
        const { orders } = req.body;
        // orders = [{ concertId: 1, sortOrder: 0 }, ...]
        for (const item of orders) {
            await pool.execute(
                "UPDATE concerts SET sort_order = ? WHERE concert_id = ?",
                [item.sortOrder, item.concertId]
            );
        }
        res.json(createResponse(true, "เรียงลำดับสำเร็จ"));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- ZONES & SEATS ---
// ==========================================

app.get("/api/zones/:concertId", async (req, res) => {
    try {
        const concertId = req.params.concertId;
        const [rows] = await pool.execute(`
            SELECT z.zone_id AS zoneId, z.concert_id AS concertId, z.zone_name AS zoneName, 
                   z.type, z.price, z.capacity, z.color_code AS colorCode,
                   (SELECT COUNT(*) FROM seats s WHERE s.zone_id = z.zone_id AND s.is_reserved = 0) AS remainingSeats
            FROM zones z
            WHERE z.concert_id = ?
        `, [concertId]);

        const mappedRows = rows.map(r => ({
            ...r,
            capacity: r.type === 'Seated' ? r.remainingSeats : r.capacity
        }));

        res.json(createResponse(true, "Zones fetched", mappedRows));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

app.get("/api/seats/:zoneId", async (req, res) => {
    try {
        const zoneId = req.params.zoneId;
        const [rows] = await pool.execute("SELECT * FROM seats WHERE zone_id = ?", [zoneId]);

        const mapSeat = (r) => {
            let active = 1;
            if (r.is_active !== undefined && r.is_active !== null) {
                active = (r.is_active === true || r.is_active == 1) ? 1 : 0;
            }
            let reserved = 0;
            if (r.is_reserved !== undefined && r.is_reserved !== null) {
                reserved = (r.is_reserved === true || r.is_reserved == 1) ? 1 : 0;
            }
            return {
                seatId: r.seat_id,
                zoneId: r.zone_id,
                rowLabel: r.row_label,
                numberLabel: r.number_label,
                isAvailable: (r.is_available === true || r.is_available == 1) ? 1 : 0,
                isActive: active,
                isReserved: reserved
            };
        };

        if (rows.length === 0) {
            const [zones] = await pool.execute("SELECT * FROM zones WHERE zone_id = ?", [zoneId]);
            if (zones.length > 0 && zones[0].type === 'Seated') {
                const zone = zones[0];
                const capacity = zone.capacity || 25;
                const letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                const seatsPerRow = 5;
                for (let i = 0; i < capacity; i++) {
                    const rowIdx = Math.floor(i / seatsPerRow);
                    const colIdx = (i % seatsPerRow) + 1;
                    const rowLabel = letters[rowIdx] || 'Z';
                    await pool.execute(
                        "INSERT INTO seats (concert_id, zone_id, row_label, number_label, is_active, is_reserved) VALUES (?, ?, ?, ?, 1, 0)",
                        [zone.concert_id, zoneId, rowLabel, colIdx.toString()]
                    );
                }
                const [newRows] = await pool.execute("SELECT * FROM seats WHERE zone_id = ?", [zoneId]);
                return res.json(createResponse(true, "Seats generated and fetched", newRows.map(mapSeat)));
            }
        }

        res.json(createResponse(true, "Seats fetched", rows.map(mapSeat)));
    } catch (err) { res.status(500).json(createResponse(false, err.message)); }
});

// ==========================================
// --- BOOKINGS ---
// ==========================================

app.post("/api/bookings", async (req, res) => {
    const connection = await pool.getConnection();
    try {
        await connection.beginTransaction();

        const { userId, zoneId, seatIds, standingCount, totalAmount, paymentMethod } = req.body;

        const [bookingResult] = await connection.execute(
            "INSERT INTO bookings (user_id, total_amount, status, payment_method) VALUES (?, ?, 'Paid', ?)",
            [userId, totalAmount, paymentMethod]
        );
        const bookingId = bookingResult.insertId;

        if (seatIds && seatIds.length > 0) {
            for (const seatId of seatIds) {
                const [seatRows] = await connection.execute("SELECT is_reserved FROM seats WHERE seat_id = ? FOR UPDATE", [seatId]);
                if (seatRows[0].is_reserved === 1) {
                    throw new Error(`Seat ${seatId} is already reserved.`);
                }
                await connection.execute(
                    "INSERT INTO tickets (booking_id, zone_id, seat_id) VALUES (?, ?, ?)",
                    [bookingId, zoneId, seatId]
                );
                await connection.execute(
                    "UPDATE seats SET is_reserved = 1 WHERE seat_id = ?",
                    [seatId]
                );
            }
        } else if (standingCount && standingCount > 0) {
            for (let i = 0; i < standingCount; i++) {
                await connection.execute(
                    "INSERT INTO tickets (booking_id, zone_id, seat_id) VALUES (?, ?, NULL)",
                    [bookingId, zoneId]
                );
            }
        } else {
            throw new Error("No seats or standing count provided.");
        }

        await connection.commit();
        res.json(createResponse(true, "จองสำเร็จ", { bookingId, status: "Paid" }));
    } catch (err) {
        await connection.rollback();
        console.error("Booking Error:", err);
        res.status(500).json(createResponse(false, err.message));
    } finally {
        connection.release();
    }
});

// ==========================================
// --- TICKETS ---
// ==========================================

// [GET] /api/users/:userId/tickets — คนที่ 7
app.get("/api/users/:userId/tickets", async (req, res) => {
    try {
        const sql = `
            SELECT t.ticket_id AS ticketId, b.booking_id AS bookingId, z.zone_id AS zoneId,
                   z.zone_name AS zoneName, t.seat_id AS seatId, s.row_label AS rowLabel,
                   s.number_label AS numberLabel, c.title AS concertTitle,
                   c.poster_image_url AS posterUrl,
                   DATE_FORMAT(c.show_date, '%Y-%m-%d') AS showDate, c.show_time AS showTime,
                   c.venue_name AS venueName
            FROM tickets t
            JOIN bookings b ON t.booking_id = b.booking_id
            JOIN zones z ON t.zone_id = z.zone_id
            JOIN concerts c ON z.concert_id = c.concert_id
            LEFT JOIN seats s ON t.seat_id = s.seat_id
            WHERE b.user_id = ?
        `;
        const [results] = await pool.execute(sql, [req.params.userId]);
        res.json(createResponse(true, "Tickets fetched", results));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [GET] /api/tickets/user/:userId — Pooh-admin
app.get("/api/tickets/user/:userId", async (req, res) => {
    try {
        const userId = req.params.userId;
        const [rows] = await pool.execute(`
            SELECT t.ticket_id AS ticketId,
                   c.title AS concertTitle, c.poster_image_url AS posterUrl,
                   z.zone_name AS zoneName,
                   s.row_label AS rowLabel, s.number_label AS numberLabel,
                   DATE_FORMAT(c.show_date, '%Y-%m-%d') AS showDate, c.show_time AS showTime,
                   c.venue_name AS venueName
            FROM tickets t
            JOIN bookings b ON t.booking_id = b.booking_id
            JOIN zones z ON t.zone_id = z.zone_id
            JOIN concerts c ON z.concert_id = c.concert_id
            LEFT JOIN seats s ON t.seat_id = s.seat_id
            WHERE b.user_id = ?
            ORDER BY b.booking_date DESC
        `, [userId]);
        res.json(createResponse(true, "Tickets fetched", rows));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, err.message));
    }
});

// [GET] /api/tickets/:ticketId — ใช้ร่วมกัน (คนที่ 1 สแกน + คนที่ 7 QR)
app.get("/api/tickets/:ticketId", async (req, res) => {
    try {
        const ticketId = req.params.ticketId;
        const [rows] = await pool.execute(`
            SELECT t.ticket_id AS ticketId, b.booking_id AS bookingId,
                   u.full_name AS holderName,
                   c.title AS concertTitle, c.poster_image_url AS posterUrl,
                   z.zone_id AS zoneId, z.zone_name AS zoneName,
                   t.seat_id AS seatId,
                   s.row_label AS rowLabel, s.number_label AS numberLabel,
                   DATE_FORMAT(c.show_date, '%Y-%m-%d') AS showDate, c.show_time AS showTime,
                   c.venue_name AS venueName, t.is_used AS isUsed
            FROM tickets t
            JOIN bookings b ON t.booking_id = b.booking_id
            JOIN users u ON b.user_id = u.user_id
            JOIN zones z ON t.zone_id = z.zone_id
            JOIN concerts c ON z.concert_id = c.concert_id
            LEFT JOIN seats s ON t.seat_id = s.seat_id
            WHERE t.ticket_id = ?
        `, [ticketId]);

        if (rows.length === 0) {
            return res.status(404).json(createResponse(false, "ไม่พบตั๋วนี้ในระบบ"));
        }

        const t = rows[0];
        const seatLabel = (t.rowLabel && t.numberLabel) ? `${t.rowLabel}${t.numberLabel}` : null;
        res.json(createResponse(true, "Ticket fetched", {
            ticketId: t.ticketId,
            bookingId: t.bookingId,
            zoneId: t.zoneId,
            zoneName: t.zoneName,
            seatId: t.seatId,
            rowLabel: t.rowLabel,
            numberLabel: t.numberLabel,
            seatLabel: seatLabel,
            concertTitle: t.concertTitle,
            showDate: t.showDate,
            showTime: t.showTime,
            venueName: t.venueName,
            posterUrl: t.posterUrl,
            holderName: t.holderName,
            isUsed: t.isUsed === 1
        }));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// [PUT] /api/tickets/:ticketId/use — Mark as used
app.put("/api/tickets/:ticketId/use", async (req, res) => {
    try {
        const ticketId = req.params.ticketId;
        const [rows] = await pool.execute("SELECT is_used FROM tickets WHERE ticket_id = ?", [ticketId]);

        if (rows.length === 0) return res.status(404).json(createResponse(false, "ไม่พบตั๋วนี้"));
        if (rows[0].is_used === 1) return res.status(400).json(createResponse(false, "ตั๋วนี้ถูกใช้งานไปแล้ว"));

        await pool.execute("UPDATE tickets SET is_used = 1 WHERE ticket_id = ?", [ticketId]);
        res.json(createResponse(true, "เช็คอินสำเร็จ!"));
    } catch (err) {
        console.error(err);
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- ADMIN STATS ---
// ==========================================

app.get("/api/admin/stats", async (req, res) => {
    try {
        const [[ticketRow]] = await pool.execute(
            "SELECT COUNT(*) AS totalTickets FROM tickets"
        );
        const [[revenueRow]] = await pool.execute(
            "SELECT COALESCE(SUM(total_amount), 0) AS totalRevenue FROM bookings WHERE status = 'Paid'"
        );
        const [[concertRow]] = await pool.execute(
            "SELECT COUNT(*) AS totalConcerts FROM concerts WHERE status = 'Active'"
        );
        const [[usedRow]] = await pool.execute(
            "SELECT COUNT(*) AS usedTickets FROM tickets WHERE is_used = 1"
        );

        const totalTickets = ticketRow.totalTickets;
        const usedTickets = usedRow.usedTickets;
        const attendanceRate = totalTickets > 0
            ? Math.round((usedTickets / totalTickets) * 100)
            : 0;

        res.json(createResponse(true, "Stats fetched", {
            totalTickets: totalTickets.toString(),
            totalRevenue: revenueRow.totalRevenue.toLocaleString("th-TH"),
            totalConcerts: concertRow.totalConcerts.toString(),
            attendanceRate: `${attendanceRate}%`
        }));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

app.get("/api/admin/recent-concerts", async (req, res) => {
    try {
        const [rows] = await pool.execute(`
            SELECT c.concert_id AS concertId, c.title,
                   DATE_FORMAT(c.show_date, '%d %b %Y') AS showDateFormatted,
                   c.status
            FROM concerts c
            ORDER BY c.concert_id DESC
            LIMIT 5
        `);
        res.json(createResponse(true, "Recent concerts fetched", rows));
    } catch (err) {
        res.status(500).json(createResponse(false, err.message));
    }
});

// ==========================================
// --- IMAGE UPLOAD ---
// ==========================================

app.post("/api/upload", (req, res) => {
    upload.single("image")(req, res, (err) => {
        if (err) {
            // multer error (file too large, wrong type, etc.)
            console.error("Upload error:", err.message);
            return res.status(400).json(createResponse(false, err.message || "อัพโหลดไม่สำเร็จ"));
        }
        if (!req.file) {
            return res.status(400).json(createResponse(false, "ไม่พบไฟล์รูปภาพ"));
        }
        // ใช้ IP จริงของเครื่องแทน host header เพื่อให้ Android emulator เปิดได้
        const host = req.get("host") || "localhost:8080";
        const fileUrl = `http://${host}/uploads/${req.file.filename}`;
        console.log("Uploaded:", fileUrl);
        res.json(createResponse(true, "อัพโหลดรูปสำเร็จ", { url: fileUrl }));
    });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, "0.0.0.0", () => console.log(`TICKETPOP API running on http://localhost:${PORT}`));
