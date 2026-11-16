// API Base URL (relative path when served by Spring Boot, or absolute localhost if opened standalone)
const API_BASE = window.location.origin.includes(":8080") ? "/api" : "http://localhost:8080/api";

let isBackendLive = false;
let users = [];
let departments = [];
let categories = [];
let complaints = [];
let staffList = [];
let staffPerformance = [];

// Current authenticated user session (defaults to Student Aarav Sharma)
let currentUser = {
  userId: 1,
  name: "Aarav Sharma",
  email: "aarav.sharma@example.com",
  roomNumber: "A-101",
  hostelBlock: "Block A",
  role: "STUDENT"
};

// Fallback seed data in case backend is offline
const fallbackUsers = [
  { userId: 1, name: "Aarav Sharma", email: "aarav.sharma@example.com", roomNumber: "A-101", hostelBlock: "Block A", role: "STUDENT" },
  { userId: 2, name: "Ananya Iyer", email: "ananya.iyer@example.com", roomNumber: "A-102", hostelBlock: "Block A", role: "STUDENT" },
  { userId: 3, name: "Rohan Verma", email: "rohan.verma@example.com", roomNumber: "A-201", hostelBlock: "Block A", role: "STUDENT" },
  { userId: 4, name: "Priya Nair", email: "priya.nair@example.com", roomNumber: "B-105", hostelBlock: "Block B", role: "STUDENT" },
  { userId: 5, name: "Karan Mehta", email: "karan.mehta@example.com", roomNumber: "B-210", hostelBlock: "Block B", role: "STUDENT" },
  { userId: 16, name: "Hostel Warden (Admin)", email: "warden@hostel.edu", roomNumber: "Admin-01", hostelBlock: "Admin Block", role: "WARDEN" }
];

const fallbackDepartments = [
  { departmentId: 1, departmentName: "Maintenance" },
  { departmentId: 2, departmentName: "IT & Wi-Fi" },
  { departmentId: 3, departmentName: "Housekeeping" },
  { departmentId: 4, departmentName: "Security" },
  { departmentId: 5, departmentName: "Mess & Cafeteria" },
  { departmentId: 6, departmentName: "Hostel Administration" },
  { departmentId: 7, departmentName: "Electrical" },
  { departmentId: 8, departmentName: "Plumbing" }
];

const fallbackCategories = [
  { categoryId: 1, categoryName: "Noise & Disturbance" },
  { categoryId: 2, categoryName: "Water Leakage & Supply" },
  { categoryId: 3, categoryName: "Power Outage & Geyser" },
  { categoryId: 4, categoryName: "Wi-Fi & LAN Network" },
  { categoryId: 5, categoryName: "Cleanliness & Garbage" },
  { categoryId: 6, categoryName: "Pest Control & Insects" },
  { categoryId: 7, categoryName: "Furniture Damage & Carpentry" },
  { categoryId: 8, categoryName: "Mess Food Quality" }
];

const views = {
  dashboard: document.getElementById("dashboard-view"),
  complaints: document.getElementById("complaints-view"),
  departments: document.getElementById("departments-view"),
  staff: document.getElementById("staff-view"),
  new: document.getElementById("new-view")
};

const pageTitle = document.getElementById("page-title");
const searchInput = document.getElementById("search-input");
const statusFilter = document.getElementById("status-filter");
const priorityFilter = document.getElementById("priority-filter");
const departmentFilter = document.getElementById("department-filter");
const userField = document.getElementById("user-field");
const categoryField = document.getElementById("category-field");
const departmentField = document.getElementById("department-field");
const complaintForm = document.getElementById("complaint-form");
const detailDialog = document.getElementById("detail-dialog");
const closeDialog = document.getElementById("close-dialog");
const detailBody = document.getElementById("detail-body");
const backendStatusText = document.getElementById("backend-status-text");
const backendStatusBadge = document.getElementById("backend-status");

// Login Elements
const loginDialog = document.getElementById("login-dialog");
const closeLoginDialog = document.getElementById("close-login-dialog");
const switchUserBtn = document.getElementById("switch-user-btn");
const userAvatar = document.getElementById("user-avatar");
const currentUserName = document.getElementById("current-user-name");
const currentUserRole = document.getElementById("current-user-role");
const currentUserExtra = document.getElementById("current-user-extra");
const staffNavTab = document.getElementById("staff-nav-tab");

// Feedback Elements
const feedbackDialog = document.getElementById("feedback-dialog");
const closeFeedbackDialog = document.getElementById("close-feedback-dialog");
const feedbackForm = document.getElementById("feedback-form");
const feedbackComplaintId = document.getElementById("feedback-complaint-id");
const feedbackRatingSelect = document.getElementById("feedback-rating-select");
const feedbackCommentsText = document.getElementById("feedback-comments-text");

// Staff Management Elements
const openAddStaffBtn = document.getElementById("open-add-staff-btn");
const addStaffDialog = document.getElementById("add-staff-dialog");
const closeAddStaffDialog = document.getElementById("close-add-staff-dialog");
const addStaffForm = document.getElementById("add-staff-form");
const newStaffName = document.getElementById("new-staff-name");
const newStaffDepartment = document.getElementById("new-staff-department");
const newStaffPhone = document.getElementById("new-staff-phone");
const staffRows = document.getElementById("staff-rows");

// Update status badge UI
function updateBackendStatus(live) {
  isBackendLive = live;
  if (!backendStatusBadge || !backendStatusText) return;
  if (live) {
    backendStatusBadge.style.background = "#dcfce7";
    backendStatusBadge.style.color = "#15803d";
    backendStatusBadge.style.borderColor = "#86efac";
    backendStatusBadge.firstElementChild.style.background = "#22c55e";
    backendStatusText.textContent = "Spring Boot + DB Connected";
  } else {
    backendStatusBadge.style.background = "#fef3c7";
    backendStatusBadge.style.color = "#b45309";
    backendStatusBadge.style.borderColor = "#fde68a";
    backendStatusBadge.firstElementChild.style.background = "#f59e0b";
    backendStatusText.textContent = "Offline Preview Mode";
  }
}

// Initial Data Fetch
async function loadInitialData() {
  try {
    const statsRes = await fetch(`${API_BASE}/dashboard/stats`, { signal: AbortSignal.timeout(3000) });
    if (statsRes.ok) {
      updateBackendStatus(true);
      const [uRes, dRes, cRes, compRes, stRes] = await Promise.all([
        fetch(`${API_BASE}/users`),
        fetch(`${API_BASE}/departments`),
        fetch(`${API_BASE}/categories`),
        fetch(`${API_BASE}/complaints`),
        fetch(`${API_BASE}/staff`)
      ]);
      users = await uRes.json();
      departments = await dRes.json();
      categories = await cRes.json();
      complaints = await compRes.json();
      staffList = await stRes.json();
      await loadStaffPerformance();
    } else {
      throw new Error("Backend not OK");
    }
  } catch (err) {
    console.warn("Spring Boot backend not reachable, using embedded data:", err);
    updateBackendStatus(false);
    users = fallbackUsers;
    departments = fallbackDepartments;
    categories = fallbackCategories;
    complaints = [];
  }

  // Restore session from localStorage if saved
  const savedUser = localStorage.getItem("hostel_cms_user");
  if (savedUser) {
    try { currentUser = JSON.parse(savedUser); } catch(e){}
  }

  updateUserUI();
  populateDropdowns();
  renderAll();
}

async function loadStaffPerformance() {
  if (!isBackendLive) return;
  try {
    const res = await fetch(`${API_BASE}/staff/performance`);
    if (res.ok) {
      staffPerformance = await res.json();
    }
  } catch (e) {
    console.warn("Could not load staff performance", e);
  }
}

function updateUserUI() {
  if (!currentUserName || !currentUserRole) return;
  currentUserName.textContent = currentUser.name;
  userAvatar.textContent = (currentUser.name || "U").charAt(0).toUpperCase();
  currentUserRole.textContent = currentUser.role;

  if (currentUser.role === "STUDENT") {
    currentUserRole.className = "pill status-resolved";
    currentUserExtra.textContent = `(${currentUser.roomNumber || 'Hostel'})`;
    if (staffNavTab) staffNavTab.style.display = "none";
  } else if (currentUser.role === "WARDEN") {
    currentUserRole.className = "pill priority-high";
    currentUserExtra.textContent = "(Warden Admin)";
    if (staffNavTab) staffNavTab.style.display = "block";
  } else {
    currentUserRole.className = "pill status-in-progress";
    currentUserExtra.textContent = `(${currentUser.departmentName || 'Staff'})`;
    if (staffNavTab) staffNavTab.style.display = "none";
  }

  // Pre-select current user in complaint form if student
  if (userField && currentUser.role === "STUDENT") {
    userField.value = currentUser.userId;
  }
}

function populateDropdowns() {
  departmentFilter.innerHTML = '<option value="All">All</option>' +
    departments.map(d => `<option value="${d.departmentId}">${d.departmentName}</option>`).join("");

  userField.innerHTML = users.map(u => 
    `<option value="${u.userId}" ${u.userId === currentUser.userId ? 'selected' : ''}>${u.name} (${u.roomNumber ? u.roomNumber : 'Hostel'})</option>`
  ).join("");

  categoryField.innerHTML = categories.map(c => 
    `<option value="${c.categoryId}">${c.categoryName}</option>`
  ).join("");

  departmentField.innerHTML = departments.map(d => 
    `<option value="${d.departmentId}">${d.departmentName}</option>`
  ).join("");

  if (newStaffDepartment) {
    newStaffDepartment.innerHTML = departments.map(d =>
      `<option value="${d.departmentId}">${d.departmentName}</option>`
    ).join("");
  }
}

function renderAll() {
  renderDashboard();
  renderComplaintsTable();
  renderDepartmentsView();
  renderStaffView();
}

// Dashboard rendering
function renderDashboard() {
  const total = complaints.length;
  const pending = complaints.filter(c => c.status === "Pending").length;
  const inProgress = complaints.filter(c => c.status === "In Progress").length;
  const resolved = complaints.filter(c => c.status === "Resolved").length;
  const highPriority = complaints.filter(c => c.priority === "High").length;
  const rate = total > 0 ? Math.round((resolved / total) * 100) : 0;

  document.getElementById("metric-grid").innerHTML = `
    <article class="metric">
      <span>Total Complaints</span>
      <strong>${total}</strong>
      <span>Logged across wings</span>
    </article>
    <article class="metric">
      <span>Pending Review</span>
      <strong>${pending}</strong>
      <span>Awaiting assignment</span>
    </article>
    <article class="metric">
      <span>In Progress</span>
      <strong>${inProgress}</strong>
      <span>Assigned to staff</span>
    </article>
    <article class="metric">
      <span>Resolved (Rate)</span>
      <strong>${rate}%</strong>
      <span>${resolved} of ${total} closed</span>
    </article>
  `;

  document.getElementById("status-total").textContent = `${total} complaints recorded`;

  document.getElementById("status-bars").innerHTML = `
    <div class="bar-row">
      <strong>Pending</strong>
      <div class="bar-track"><div class="bar-fill pending" style="width: ${total ? (pending/total)*100 : 0}%"></div></div>
      <span>${pending}</span>
    </div>
    <div class="bar-row">
      <strong>In Progress</strong>
      <div class="bar-track"><div class="bar-fill in-progress" style="width: ${total ? (inProgress/total)*100 : 0}%"></div></div>
      <span>${inProgress}</span>
    </div>
    <div class="bar-row">
      <strong>Resolved</strong>
      <div class="bar-track"><div class="bar-fill resolved" style="width: ${total ? (resolved/total)*100 : 0}%"></div></div>
      <span>${resolved}</span>
    </div>
  `;

  const medPriority = complaints.filter(c => c.priority === "Medium").length;
  const lowPriority = complaints.filter(c => c.priority === "Low").length;
  document.getElementById("priority-stack").innerHTML = `
    <div class="priority-segment priority-high" style="flex: ${highPriority || 1}">High (${highPriority})</div>
    <div class="priority-segment priority-medium" style="flex: ${medPriority || 1}">Med (${medPriority})</div>
    <div class="priority-segment priority-low" style="flex: ${lowPriority || 1}">Low (${lowPriority})</div>
  `;

  const recent = complaints.slice(0, 6);
  document.getElementById("recent-rows").innerHTML = recent.map(c => `
    <tr>
      <td>#${c.complaintId}</td>
      <td><strong>${escapeHtml(c.userName || 'Resident')}</strong><br><small style="color:var(--muted)">${escapeHtml(c.roomNumber || 'Room')} (${escapeHtml(c.hostelBlock || 'Hostel')})</small></td>
      <td>${escapeHtml(c.departmentName || 'Dept')}</td>
      <td><span class="pill priority-${c.priority.toLowerCase()}">${c.priority}</span></td>
      <td><span class="pill status-${c.status.toLowerCase().replace(' ', '-')}">${c.status}</span></td>
      <td>${formatDate(c.dateRegistered)}</td>
    </tr>
  `).join("");
}

// Complaints Table
function renderComplaintsTable() {
  const query = searchInput.value.trim().toLowerCase();
  const statusVal = statusFilter.value;
  const priorityVal = priorityFilter.value;
  const deptVal = departmentFilter.value;

  const filtered = complaints.filter(c => {
    const matchesSearch = !query ||
      String(c.complaintId).includes(query) ||
      (c.userName && c.userName.toLowerCase().includes(query)) ||
      (c.roomNumber && c.roomNumber.toLowerCase().includes(query)) ||
      (c.description && c.description.toLowerCase().includes(query)) ||
      (c.departmentName && c.departmentName.toLowerCase().includes(query));

    const matchesStatus = statusVal === "All" || c.status.toLowerCase() === statusVal.toLowerCase();
    const matchesPriority = priorityVal === "All" || c.priority.toLowerCase() === priorityVal.toLowerCase();
    const matchesDept = deptVal === "All" || String(c.departmentId) === String(deptVal);

    return matchesSearch && matchesStatus && matchesPriority && matchesDept;
  });

  document.getElementById("complaint-count").textContent = `${filtered.length} found`;

  const tbody = document.getElementById("complaint-rows");
  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:30px; color:var(--muted);">No matching complaints found.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(c => {
    const isOwner = currentUser.role === "STUDENT" && c.userId === currentUser.userId;
    const canRate = isOwner && c.status === "Resolved" && !c.feedbackRating;

    return `
      <tr>
        <td><strong>#${c.complaintId}</strong></td>
        <td>
          <strong>${escapeHtml(c.userName || 'Student')}</strong>
          ${isOwner ? '<span class="pill status-resolved" style="font-size:9px; padding:0 4px; margin-left:4px;">Mine</span>' : ''}
          <br><small style="color:var(--muted)">${escapeHtml(c.roomNumber || '')} ${escapeHtml(c.hostelBlock || '')}</small>
        </td>
        <td>${escapeHtml(c.categoryName || 'General')}</td>
        <td>${escapeHtml(c.departmentName || 'Dept')}</td>
        <td><span class="pill priority-${c.priority.toLowerCase()}">${c.priority}</span></td>
        <td><span class="pill status-${c.status.toLowerCase().replace(' ', '-')}">${c.status}</span></td>
        <td>${formatDate(c.dateRegistered)}</td>
        <td>
          <div style="display:flex; gap:6px; align-items:center;">
            <button class="ghost-action" style="padding:4px 8px; font-size:12px; min-height:28px;" onclick="viewDetails(${c.complaintId})">Inspect</button>
            ${canRate ? `<button class="primary-action" style="padding:4px 8px; font-size:12px; min-height:28px; background:var(--warn);" onclick="openFeedbackModal(${c.complaintId})">Rate</button>` : ''}
          </div>
        </td>
      </tr>
    `;
  }).join("");
}

// Department Performance View
function renderDepartmentsView() {
  const list = document.getElementById("department-list");
  list.innerHTML = departments.map(d => {
    const deptComplaints = complaints.filter(c => c.departmentId === d.departmentId);
    const total = deptComplaints.length;
    const resolved = deptComplaints.filter(c => c.status === "Resolved").length;
    const pending = deptComplaints.filter(c => c.status === "Pending").length;
    const rate = total > 0 ? Math.round((resolved / total) * 100) : 100;

    return `
      <div class="department-row">
        <div>
          <strong>${escapeHtml(d.departmentName)}</strong>
          <small>${total} complaints assigned</small>
        </div>
        <div class="bar-track">
          <div class="bar-fill resolved" style="width:${rate}%"></div>
        </div>
        <div>
          <strong>${rate}%</strong>
          <small>Resolved</small>
        </div>
        <div>
          <span class="pill status-${pending > 0 ? 'pending' : 'resolved'}">${pending} open</span>
        </div>
      </div>
    `;
  }).join("");
}

// Staff & Technician Roster View (Warden Only)
function renderStaffView() {
  if (!staffRows) return;

  const data = staffPerformance.length > 0 ? staffPerformance : staffList.map(s => {
    const assigned = complaints.filter(c => c.assignedStaffId === s.staffId);
    const resolved = assigned.filter(c => c.status === "Resolved");
    const pending = assigned.filter(c => c.status !== "Resolved");
    const rated = assigned.filter(c => c.feedbackRating);
    const avg = rated.length > 0 ? rated.reduce((acc, x) => acc + x.feedbackRating, 0) / rated.length : 0;
    return {
      staffId: s.staffId,
      name: s.name,
      departmentName: s.departmentName,
      phone: s.phone,
      totalAssigned: assigned.length,
      resolvedCount: resolved.length,
      pendingCount: pending.length,
      avgRating: Math.round(avg * 10) / 10
    };
  });

  if (data.length === 0) {
    staffRows.innerHTML = `<tr><td colspan="8" style="text-align:center; padding:30px; color:var(--muted);">No staff members found.</td></tr>`;
    return;
  }

  staffRows.innerHTML = data.map(s => `
    <tr>
      <td>#${s.staffId}</td>
      <td><strong>${escapeHtml(s.name)}</strong></td>
      <td>${escapeHtml(s.departmentName || 'Maintenance')}</td>
      <td><code>${escapeHtml(s.phone || '9876543210')}</code></td>
      <td><strong>${s.totalAssigned}</strong> tasks</td>
      <td><span class="pill status-resolved" style="min-height:22px; font-size:11px;">${s.resolvedCount} closed</span></td>
      <td><span class="pill ${s.pendingCount > 0 ? 'status-pending' : 'status-resolved'}" style="min-height:22px; font-size:11px;">${s.pendingCount} active</span></td>
      <td>
        ${s.avgRating > 0 ? `<strong style="color:#b45309;">⭐ ${s.avgRating}</strong> / 5.0` : '<span style="color:var(--muted);">No ratings</span>'}
      </td>
    </tr>
  `).join("");
}

// View Details Modal with Role-Specific Controls
window.viewDetails = async function(id) {
  const complaint = complaints.find(c => c.complaintId === id);
  if (!complaint) return;

  let historyHtml = '<p style="color:var(--muted)">Loading history...</p>';
  detailDialog.showModal();

  renderModalContent(complaint, historyHtml);

  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/complaints/${id}/history`);
      if (res.ok) {
        const history = await res.json();
        historyHtml = history.map(h => `
          <div style="display:flex; justify-content:space-between; padding:6px 0; border-bottom:1px solid var(--line); font-size:13px;">
            <span><strong class="pill status-${h.status.toLowerCase().replace(' ', '-')}">${h.status}</strong></span>
            <span style="color:var(--muted)">${formatDate(h.updatedOn)}</span>
          </div>
        `).join("");
        renderModalContent(complaint, historyHtml);
      }
    } catch (e) {
      console.warn("Could not load history", e);
    }
  } else {
    historyHtml = `
      <div style="font-size:13px; color:var(--muted)">
        <div>Status: <strong>${complaint.status}</strong></div>
        <div>Logged: ${formatDate(complaint.dateRegistered)}</div>
      </div>
    `;
    renderModalContent(complaint, historyHtml);
  }
};

function renderModalContent(complaint, historyHtml) {
  document.getElementById("detail-title").textContent = `Complaint #${complaint.complaintId} — Details`;

  // Filter staff by department
  const eligibleStaff = staffList.filter(s => s.departmentId === complaint.departmentId);
  const staffOptions = eligibleStaff.map(s => 
    `<option value="${s.staffId}" ${complaint.assignedStaffId === s.staffId ? 'selected' : ''}>${s.name} (${s.phone})</option>`
  ).join("");

  const isOwner = currentUser.role === "STUDENT" && complaint.userId === currentUser.userId;
  const isWarden = currentUser.role === "WARDEN";
  const isStaff = currentUser.role === "STAFF";
  const canAssign = isWarden || isStaff;

  detailBody.innerHTML = `
    <div class="detail-grid">
      <div class="detail-item">
        <span>Resident Details</span>
        <strong>${escapeHtml(complaint.userName || 'Student')}</strong>
        <div>${escapeHtml(complaint.userEmail || '')}</div>
        <small style="color:var(--muted)">Room: ${escapeHtml(complaint.roomNumber || '')} | ${escapeHtml(complaint.hostelBlock || '')}</small>
      </div>
      <div class="detail-item">
        <span>Department & Category</span>
        <strong>${escapeHtml(complaint.departmentName || '')}</strong>
        <div>${escapeHtml(complaint.categoryName || '')}</div>
        <small style="color:var(--muted)">Priority: <span class="pill priority-${complaint.priority.toLowerCase()}">${complaint.priority}</span></small>
      </div>
      <div class="detail-item">
        <span>Current Status</span>
        <span class="pill status-${complaint.status.toLowerCase().replace(' ', '-')}">${complaint.status}</span>
        <div style="margin-top:6px; font-size:12px; color:var(--muted)">Registered: ${formatDate(complaint.dateRegistered)}</div>
        ${complaint.dateResolved ? `<div style="font-size:12px; color:var(--ok)">Resolved: ${formatDate(complaint.dateResolved)}</div>` : ''}
      </div>
      <div class="detail-item">
        <span>Assigned Technician</span>
        <strong>${complaint.assignedStaffName ? escapeHtml(complaint.assignedStaffName) : '<em style="color:var(--muted)">Unassigned</em>'}</strong>
        ${complaint.feedbackRating ? `<div style="margin-top:4px;">Rating: <strong>${'⭐'.repeat(complaint.feedbackRating)}</strong> (${complaint.feedbackRating}/5)<br><small style="color:var(--muted)">"${escapeHtml(complaint.feedbackComments || '')}"</small></div>` : ''}
      </div>
    </div>

    <div class="panel" style="background:#f8fafc; border:1px solid var(--line); padding:12px; border-radius:8px;">
      <span style="font-size:12px; text-transform:uppercase; color:var(--muted); font-weight:800; display:block; margin-bottom:6px;">Issue Description</span>
      <p style="margin:0; font-size:14px; line-height:1.5;">${escapeHtml(complaint.description)}</p>
    </div>

    <div class="panel" style="padding:12px; border:1px solid var(--line); border-radius:8px;">
      <span style="font-size:12px; text-transform:uppercase; color:var(--muted); font-weight:800; display:block; margin-bottom:8px;">Lifecycle & Audit Trail</span>
      ${historyHtml}
    </div>

    <div class="action-bar">
      <strong>Actions (${currentUser.role}):</strong>

      ${canAssign ? `
        <select id="assign-staff-select" style="min-height:34px; padding:4px 8px; font-size:13px; width:auto;">
          <option value="">-- Assign Technician --</option>
          ${staffOptions}
        </select>
        <button class="primary-action" style="min-height:34px; padding:0 12px; font-size:13px;" onclick="doAssignStaff(${complaint.complaintId})">Assign</button>
        <button class="ghost-action" style="min-height:34px; padding:0 12px; font-size:13px;" onclick="doUpdateStatus(${complaint.complaintId}, 'In Progress')">Mark In Progress</button>
        <button class="primary-action" style="min-height:34px; padding:0 12px; font-size:13px; background:var(--ok);" onclick="doUpdateStatus(${complaint.complaintId}, 'Resolved')">Resolve</button>
      ` : ''}

      ${isOwner && complaint.status === 'Resolved' && !complaint.feedbackRating ? `
        <button class="primary-action" style="min-height:34px; padding:0 14px; font-size:13px; background:var(--warn);" onclick="openFeedbackModal(${complaint.complaintId})">⭐ Rate Resolution</button>
      ` : ''}

      ${isOwner && complaint.status !== 'Resolved' ? `
        <span style="font-size:12px; color:var(--muted);">Your ticket is being handled by hostel maintenance.</span>
      ` : ''}
    </div>
  `;
}

// Live Assign Action (Both Warden and Staff can assign)
window.doAssignStaff = async function(complaintId) {
  const select = document.getElementById("assign-staff-select");
  const staffId = select ? select.value : null;
  if (!staffId) {
    alert("Please select a technician to assign.");
    return;
  }

  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/complaints/${complaintId}/assign`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ staffId: parseInt(staffId) })
      });
      if (res.ok) {
        await refreshData();
        await loadStaffPerformance();
        renderStaffView();
        viewDetails(complaintId);
        alert("Staff successfully assigned via JDBC transaction!");
      }
    } catch (e) {
      alert("Error assigning staff: " + e.message);
    }
  } else {
    const c = complaints.find(x => x.complaintId === complaintId);
    if (c) {
      c.assignedStaffId = parseInt(staffId);
      const s = staffList.find(x => x.staffId === parseInt(staffId));
      if (s) c.assignedStaffName = s.name;
      c.status = "In Progress";
      renderAll();
      viewDetails(complaintId);
    }
  }
};

// Live Status Update Action
window.doUpdateStatus = async function(complaintId, newStatus) {
  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/complaints/${complaintId}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newStatus })
      });
      if (res.ok) {
        await refreshData();
        await loadStaffPerformance();
        renderStaffView();
        viewDetails(complaintId);
      }
    } catch (e) {
      alert("Error updating status: " + e.message);
    }
  } else {
    const c = complaints.find(x => x.complaintId === complaintId);
    if (c) {
      c.status = newStatus;
      renderAll();
      viewDetails(complaintId);
    }
  }
};

// Open Feedback Dialog
window.openFeedbackModal = function(complaintId) {
  feedbackComplaintId.value = complaintId;
  feedbackForm.reset();
  feedbackDialog.showModal();
};

feedbackForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const complaintId = parseInt(feedbackComplaintId.value);
  const rating = parseInt(feedbackRatingSelect.value);
  const comments = feedbackCommentsText.value.trim();

  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/complaints/${complaintId}/feedback`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ rating, comments })
      });
      if (res.ok) {
        feedbackDialog.close();
        alert("Thank you! Resident feedback recorded in database.");
        await refreshData();
        await loadStaffPerformance();
        renderStaffView();
        viewDetails(complaintId);
      } else {
        const err = await res.json();
        alert("Error: " + (err.error || "Failed to submit feedback."));
      }
    } catch (err) {
      alert("Submission error: " + err.message);
    }
  } else {
    const c = complaints.find(x => x.complaintId === complaintId);
    if (c) {
      c.feedbackRating = rating;
      c.feedbackComments = comments;
      feedbackDialog.close();
      renderAll();
      viewDetails(complaintId);
    }
  }
});

// Add Staff Form Submission (Warden Only)
if (openAddStaffBtn) {
  openAddStaffBtn.addEventListener("click", () => {
    if (addStaffDialog) {
      addStaffForm.reset();
      addStaffDialog.showModal();
    }
  });
}

if (closeAddStaffDialog) {
  closeAddStaffDialog.addEventListener("click", () => addStaffDialog.close());
}

if (addStaffForm) {
  addStaffForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = newStaffName.value.trim();
    const departmentId = parseInt(newStaffDepartment.value);
    const phone = newStaffPhone.value.trim();

    if (!name || !departmentId) return;

    if (isBackendLive) {
      try {
        const res = await fetch(`${API_BASE}/staff`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ name, departmentId, phone })
        });
        if (res.ok) {
          const created = await res.json();
          addStaffDialog.close();
          alert(`Staff member ${created.name} successfully added to database!`);
          const stRes = await fetch(`${API_BASE}/staff`);
          staffList = await stRes.json();
          await loadStaffPerformance();
          renderStaffView();
          populateDropdowns();
        } else {
          const err = await res.json();
          alert("Failed to add staff: " + (err.error || "Error"));
        }
      } catch (err) {
        alert("Error: " + err.message);
      }
    } else {
      const newStaff = {
        staffId: (staffList[staffList.length - 1]?.staffId || 30) + 1,
        name,
        departmentId,
        departmentName: departments.find(d => d.departmentId === departmentId)?.departmentName || "Maintenance",
        phone
      };
      staffList.push(newStaff);
      addStaffDialog.close();
      renderStaffView();
      populateDropdowns();
      alert(`Staff member ${name} added locally.`);
    }
  });
}

// Role Switcher / Login Logic
window.loginAsRole = function(roleType) {
  if (roleType === "student") {
    currentUser = {
      userId: 1,
      name: "Aarav Sharma",
      email: "aarav.sharma@example.com",
      roomNumber: "A-101",
      hostelBlock: "Block A",
      role: "STUDENT"
    };
  } else if (roleType === "warden") {
    currentUser = {
      userId: 16,
      name: "Hostel Warden (Admin)",
      email: "warden@hostel.edu",
      roomNumber: "Admin-01",
      hostelBlock: "Admin Block",
      role: "WARDEN"
    };
  } else if (roleType === "staff") {
    currentUser = {
      userId: 1003,
      staffId: 3,
      name: "Kunal Bedi",
      email: "kunal.bedi@staff.hostel.edu",
      departmentName: "IT & Wi-Fi",
      role: "STAFF"
    };
  }
  localStorage.setItem("hostel_cms_user", JSON.stringify(currentUser));
  updateUserUI();
  populateDropdowns();
  renderAll();
  loginDialog.close();
  alert(`Switched active actor to: ${currentUser.name} (${currentUser.role})`);
};

window.submitCustomLogin = async function() {
  const input = document.getElementById("custom-email-input").value.trim();
  if (!input) return;

  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: input })
      });
      if (res.ok) {
        currentUser = await res.json();
        localStorage.setItem("hostel_cms_user", JSON.stringify(currentUser));
        updateUserUI();
        populateDropdowns();
        renderAll();
        loginDialog.close();
        alert(`Welcome, ${currentUser.name} (${currentUser.role})!`);
        return;
      }
    } catch (e) {
      console.warn("Auth endpoint fallback", e);
    }
  }

  const foundUser = users.find(u => u.email.toLowerCase() === input.toLowerCase() || String(u.userId) === input);
  if (foundUser) {
    currentUser = { ...foundUser, role: foundUser.role || "STUDENT" };
    localStorage.setItem("hostel_cms_user", JSON.stringify(currentUser));
    updateUserUI();
    populateDropdowns();
    renderAll();
    loginDialog.close();
    alert(`Logged in as ${currentUser.name}!`);
  } else {
    alert("User email not found. Please try 'aarav.sharma@example.com' or 'warden@hostel.edu'.");
  }
};

switchUserBtn.addEventListener("click", () => loginDialog.showModal());
closeLoginDialog.addEventListener("click", () => loginDialog.close());
closeFeedbackDialog.addEventListener("click", () => feedbackDialog.close());

// Refresh data from API
async function refreshData() {
  if (!isBackendLive) return;
  const [compRes, statsRes] = await Promise.all([
    fetch(`${API_BASE}/complaints`),
    fetch(`${API_BASE}/dashboard/stats`)
  ]);
  complaints = await compRes.json();
  renderAll();
}

// Form Submission (Create Complaint)
complaintForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const userId = parseInt(userField.value);
  const categoryId = parseInt(categoryField.value);
  const departmentId = parseInt(departmentField.value);
  const priority = document.getElementById("priority-field").value;
  const description = document.getElementById("description-field").value.trim();

  if (!description) return;

  const payload = { userId, categoryId, departmentId, priority, description };

  if (isBackendLive) {
    try {
      const res = await fetch(`${API_BASE}/complaints`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        const created = await res.json();
        alert(`Complaint #${created.complaintId} successfully registered in database!`);
        complaintForm.reset();
        await refreshData();
        await loadStaffPerformance();
        renderStaffView();
        switchView("complaints");
      } else {
        const err = await res.json();
        alert("Submission failed: " + (err.error || "Unknown error"));
      }
    } catch (err) {
      alert("Connection error: " + err.message);
    }
  } else {
    const newId = (complaints[0]?.complaintId || 100) + 1;
    const u = users.find(x => x.userId === userId) || {};
    const d = departments.find(x => x.departmentId === departmentId) || {};
    const cat = categories.find(x => x.categoryId === categoryId) || {};

    const newComp = {
      complaintId: newId,
      userId,
      userName: u.name,
      roomNumber: u.roomNumber,
      hostelBlock: u.hostelBlock,
      categoryId,
      categoryName: cat.categoryName,
      departmentId,
      departmentName: d.departmentName,
      priority,
      description,
      status: "Pending",
      dateRegistered: new Date().toISOString()
    };
    complaints.unshift(newComp);
    complaintForm.reset();
    renderAll();
    switchView("complaints");
    alert(`Complaint #${newId} recorded locally (offline mode).`);
  }
});

// Navigation tab switching
document.querySelectorAll(".nav-tab").forEach(tab => {
  tab.addEventListener("click", () => switchView(tab.dataset.view));
});

document.querySelectorAll("[data-view-target]").forEach(btn => {
  btn.addEventListener("click", () => switchView(btn.dataset.viewTarget));
});

function switchView(viewName) {
  Object.keys(views).forEach(key => {
    if (views[key]) views[key].classList.remove("active");
  });
  document.querySelectorAll(".nav-tab").forEach(tab => {
    tab.classList.toggle("active", tab.dataset.view === viewName);
  });
  if (views[viewName]) {
    views[viewName].classList.add("active");
  }
  const titles = {
    dashboard: "Hostel Maintenance Dashboard",
    complaints: "Hostel Complaints Directory",
    departments: "Maintenance Wings & Performance",
    staff: "Staff & Technician Management (Warden)",
    new: "Lodge New Complaint"
  };
  pageTitle.textContent = titles[viewName] || "Dashboard";
}

// Search and Filter Listeners
searchInput.addEventListener("input", renderComplaintsTable);
statusFilter.addEventListener("change", renderComplaintsTable);
priorityFilter.addEventListener("change", renderComplaintsTable);
departmentFilter.addEventListener("change", renderComplaintsTable);

closeDialog.addEventListener("click", () => detailDialog.close());
detailDialog.addEventListener("click", (e) => {
  if (e.target === detailDialog) detailDialog.close();
});

// Helpers
function formatDate(dateStr) {
  if (!dateStr) return "-";
  try {
    const d = new Date(dateStr);
    return isNaN(d) ? String(dateStr) : d.toLocaleDateString("en-IN", { month: "short", day: "numeric", year: "numeric" });
  } catch (e) {
    return String(dateStr);
  }
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

// Start
loadInitialData();
