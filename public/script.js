async function loadJobs() {
  const res = await fetch("/jobs");
  const jobs = await res.json();

  const container = document.getElementById("jobs");
  container.innerHTML = "";

  jobs.forEach(job => {
    container.innerHTML += `
      <div class="job">
        <h3>${job.title}</h3>
        <p>${job.company}</p>
        <p>${job.location}</p>
        <p>${job.salary}</p>
        <button>View & Apply</button>
      </div>
    `;
  });
}

// Load on start
loadJobs();