const router = require("express").Router();
const Job = require("../models/Job");

/* ================= CREATE JOB ================= */
router.post("/", async (req, res) => {
  try {
    const { title, city } = req.body;

    // ✅ Validation
    if (!title || !city) {
      return res.status(400).json({ message: "Title and city are required" });
    }

    const newJob = new Job({
      title,
      city,
      createdBy: "employer" // you can later replace with user ID
    });

    await newJob.save();

    res.status(201).json({
      message: "Job posted successfully",
      job: newJob
    });

  } catch (error) {
    res.status(500).json({
      message: "Error posting job",
      error: error.message
    });
  }
});


/* ================= GET ALL JOBS ================= */
router.get("/", async (req, res) => {
  try {
    const jobs = await Job.find().sort({ _id: -1 });

    res.status(200).json(jobs);

  } catch (error) {
    res.status(500).json({
      message: "Error fetching jobs",
      error: error.message
    });
  }
});


/* ================= GET SINGLE JOB ================= */
router.get("/:id", async (req, res) => {
  try {
    const job = await Job.findById(req.params.id);

    if (!job) {
      return res.status(404).json({ message: "Job not found" });
    }

    res.status(200).json(job);

  } catch (error) {
    res.status(500).json({
      message: "Error fetching job",
      error: error.message
    });
  }
});


/* ================= DELETE JOB ================= */
router.delete("/:id", async (req, res) => {
  try {
    const job = await Job.findByIdAndDelete(req.params.id);

    if (!job) {
      return res.status(404).json({ message: "Job not found" });
    }

    res.status(200).json({ message: "Job deleted successfully" });

  } catch (error) {
    res.status(500).json({
      message: "Error deleting job",
      error: error.message
    });
  }
});

module.exports = router;