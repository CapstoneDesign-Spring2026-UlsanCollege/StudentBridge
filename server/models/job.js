const mongoose = require("mongoose");

const JobSchema = new mongoose.Schema({
  title: String,
  city: String,
  createdBy: String
});

module.exports = mongoose.model("Job", JobSchema);