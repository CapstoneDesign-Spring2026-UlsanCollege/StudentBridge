const express = require("express");
const sqlite3 = require("sqlite3").verbose();
const bodyParser = require("body-parser");
const cors = require("cors");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static("public"));

// ================= DATABASE =================
const db = new sqlite3.Database("./database.db");

// Create tables
db.serialize(() => {
  // Users table
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      email TEXT UNIQUE,
      password TEXT,
      role TEXT
    )
  `);

  // Jobs table (UPDATED)
  db.run(`
    CREATE TABLE IF NOT EXISTS jobs (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT,
      city TEXT,
      description TEXT
    )
  `);
});

// ================= AUTH APIs =================

// Signup
app.post("/api/auth/signup", (req, res) => {
  const { email, password, role } = req.body;

  db.run(
    "INSERT INTO users (email, password, role) VALUES (?, ?, ?)",
    [email, password, role],
    function (err) {
      if (err) {
        return res.json({ message: "User already exists" });
      }
      res.json({ message: "Signup successful" });
    }
  );
});

// Login
app.post("/api/auth/login", (req, res) => {
  const { email, password } = req.body;

  db.get(
    "SELECT * FROM users WHERE email=? AND password=?",
    [email, password],
    (err, user) => {
      if (user) {
        res.json({ success: true, user });
      } else {
        res.json({ success: false });
      }
    }
  );
});

// ================= JOB APIs =================

// Get all jobs
app.get("/api/jobs", (req, res) => {
  db.all("SELECT * FROM jobs", [], (err, rows) => {
    if (err) return res.json([]);
    res.json(rows);
  });
});

// Add job
app.post("/api/jobs/add", (req, res) => {
  const { title, city, description } = req.body;

  db.run(
    "INSERT INTO jobs (title, city, description) VALUES (?, ?, ?)",
    [title, city, description],
    function (err) {
      if (err) {
        return res.json({ message: "Error adding job" });
      }
      res.json({ message: "Job posted successfully" });
    }
  );
});

// ================= DEFAULT =================
app.get("/", (req, res) => {
  res.sendFile(__dirname + "/public/index.html");
});

// ================= START SERVER =================
app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`);
});