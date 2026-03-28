const express = require("express");
const sqlite3 = require("sqlite3").verbose();
const bodyParser = require("body-parser");
const cors = require("cors");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static("public"));

// Database
const db = new sqlite3.Database("./database.db");

// Create table
db.run(`
CREATE TABLE IF NOT EXISTS jobs (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT,
  company TEXT,
  location TEXT,
  salary TEXT,
  type TEXT
)
`);

// Insert sample data
db.run(`INSERT INTO jobs (title, company, location, salary, type)
SELECT 'Part-Time Barista','Java Corner Cafe','Downtown','$15/hour','Part Time'
WHERE NOT EXISTS (SELECT 1 FROM jobs)`);

// API: Get jobs
app.get("/jobs", (req, res) => {
  db.all("SELECT * FROM jobs", [], (err, rows) => {
    res.json(rows);
  });
});

// API: Add job
app.post("/jobs", (req, res) => {
  const { title, company, location, salary, type } = req.body;

  db.run(
    "INSERT INTO jobs (title, company, location, salary, type) VALUES (?,?,?,?,?)",
    [title, company, location, salary, type],
    () => res.send("Job added")
  );
});

app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));