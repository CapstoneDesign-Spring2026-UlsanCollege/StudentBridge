// ================= BASE URL (IMPORTANT FIX) =================
const BASE_URL = window.location.origin;

// ================= MODAL CONTROL =================
function openLogin() {
  document.getElementById("loginModal").style.display = "block";
}

function openSignup() {
  document.getElementById("signupModal").style.display = "block";
}

function openPostJob() {
  document.getElementById("jobModal").style.display = "block";
}

function closeModal(id) {
  document.getElementById(id).style.display = "none";
}

// ================= SIGNUP =================
async function signup() {
  try {
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const role = document.getElementById("role").value;

    const res = await fetch(`${BASE_URL}/api/auth/signup`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ email, password, role })
    });

    const data = await res.json();
    alert(data.message);

    closeModal("signupModal");
  } catch (error) {
    console.error(error);
    alert("Signup failed");
  }
}

// ================= LOGIN =================
async function login() {
  try {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();
    console.log("Login response:", data); // DEBUG

    if (data.success) {
      localStorage.setItem("user", JSON.stringify(data.user));
      window.location.href = "dashboard.html";
    } else {
      alert("Invalid login");
    }
  } catch (error) {
    console.error(error);
    alert("Login error");
  }
}

// ================= POST JOB =================
async function postJob() {
  try {
    const title = document.getElementById("jobTitle").value;
    const city = document.getElementById("jobCity").value;
    const description = document.getElementById("jobDesc").value;

    const res = await fetch(`${BASE_URL}/api/jobs/add`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ title, city, description })
    });

    const data = await res.json();
    alert(data.message);

    closeModal("jobModal");
    loadJobs();
  } catch (error) {
    console.error(error);
    alert("Failed to post job");
  }
}

// ================= LOAD JOBS =================
async function loadJobs() {
  try {
    const res = await fetch(`${BASE_URL}/api/jobs`);
    const jobs = await res.json();

    displayJobs(jobs);
  } catch (error) {
    console.error(error);
  }
}

// ================= DISPLAY JOBS =================
function displayJobs(jobs) {
  const container = document.getElementById("jobs");

  if (!container) return; // IMPORTANT (dashboard fix)

  container.innerHTML = "";

  if (jobs.length === 0) {
    container.innerHTML = "<p>No jobs found</p>";
    return;
  }

  jobs.forEach(job => {
    container.innerHTML += `
      <div class="job-card">
        <h3>${job.title}</h3>
        <p><strong>City:</strong> ${job.city}</p>
        <p>${job.description}</p>
        <button onclick="applyJob(${job.id})">Apply</button>
      </div>
    `;
  });
}

// ================= SEARCH =================
async function searchJobs() {
  try {
    const title = document.getElementById("searchTitle").value.toLowerCase();
    const city = document.getElementById("searchCity").value.toLowerCase();

    const res = await fetch(`${BASE_URL}/api/jobs`);
    const jobs = await res.json();

    const filtered = jobs.filter(job =>
      job.title.toLowerCase().includes(title) &&
      job.city.toLowerCase().includes(city)
    );

    displayJobs(filtered);
  } catch (error) {
    console.error(error);
  }
}

// ================= QUICK SEARCH =================
function quickSearch(keyword) {
  document.getElementById("searchTitle").value = keyword;
  searchJobs();
}

// ================= APPLY JOB =================
function applyJob(id) {
  const user = JSON.parse(localStorage.getItem("user"));

  if (!user) {
    alert("Please login first");
    return;
  }

  alert("Applied successfully! (Next step: backend API)");
}

// ================= AUTO LOAD =================
window.onload = () => {
  loadJobs();
};