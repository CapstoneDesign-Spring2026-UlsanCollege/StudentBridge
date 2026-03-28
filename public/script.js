// Open login modal
function openModal() {
  document.getElementById("loginModal").style.display = "block";
}

// Close modal
function closeModal() {
  document.getElementById("loginModal").style.display = "none";
}

// Signup
async function signup() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const res = await fetch("/signup", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({ email, password })
  });

  alert(await res.text());
}

// Login
async function login() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const res = await fetch("/login", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({ email, password })
  });

  if (res.ok) {
    window.location.href = "dashboard.html";
  } else {
    alert("Login failed");
  }
}

// Load & filter jobs
async function loadJobs() {
  const keyword = document.getElementById("searchTitle").value.toLowerCase();

  const res = await fetch("/jobs");
  const jobs = await res.json();

  const container = document.getElementById("jobs");
  container.innerHTML = "";

  jobs
    .filter(job => job.title.toLowerCase().includes(keyword))
    .forEach(job => {
      container.innerHTML += `
        <div class="job">
          <h3>${job.title}</h3>
          <p>${job.company}</p>
          <p>${job.location}</p>
          <p>${job.salary}</p>
          <button>Apply</button>
        </div>
      `;
    });
}

// Load jobs automatically
loadJobs();