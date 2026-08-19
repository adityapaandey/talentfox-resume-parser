// Configuration
const API_BASE_URL = 'http://localhost:8080/api/resume-parser';

// State
let selectedFiles = [];
let parsedResumes = [];
let allSkills = new Set();

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initializeEventListeners();
    checkBackendConnection();
});

function initializeEventListeners() {
    // Upload area
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('fileInput');
    
    uploadArea.addEventListener('click', () => fileInput.click());
    uploadArea.addEventListener('dragover', handleDragOver);
    uploadArea.addEventListener('dragleave', handleDragLeave);
    uploadArea.addEventListener('drop', handleDrop);
    fileInput.addEventListener('change', handleFileSelect);
    
    // Parse button
    document.getElementById('parseBtn').addEventListener('click', handleUpload);
    
    // Navigation
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const section = item.dataset.section;
            switchSection(section);
        });
    });
    
    // Search
    document.getElementById('searchInput').addEventListener('input', handleSearch);
}

async function checkBackendConnection() {
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        if (response.ok) {
            showToast('Connected to Resume Parser API', 'success');
        }
    } catch (error) {
        showToast('Cannot connect to backend. Please start the server.', 'error');
    }
}

// File Handling
function handleDragOver(e) {
    e.preventDefault();
    e.currentTarget.classList.add('dragover');
}

function handleDragLeave(e) {
    e.preventDefault();
    e.currentTarget.classList.remove('dragover');
}

function handleDrop(e) {
    e.preventDefault();
    e.currentTarget.classList.remove('dragover');
    
    const files = Array.from(e.dataTransfer.files).filter(file => file.type === 'application/pdf');
    
    if (files.length === 0) {
        showToast('Please drop only PDF files', 'error');
        return;
    }
    
    selectedFiles = files;
    updateFileList();
}

function handleFileSelect(e) {
    selectedFiles = Array.from(e.target.files);
    updateFileList();
}

function updateFileList() {
    const fileList = document.getElementById('fileList');
    const filesContainer = document.getElementById('files');
    const fileCount = document.getElementById('fileCount');
    const actionButtons = document.getElementById('actionButtons');
    
    if (selectedFiles.length === 0) {
        fileList.style.display = 'none';
        actionButtons.style.display = 'none';
        return;
    }
    
    fileList.style.display = 'block';
    actionButtons.style.display = 'flex';
    fileCount.textContent = selectedFiles.length;
    
    filesContainer.innerHTML = selectedFiles.map((file, index) => `
        <div class="file-item">
            <div class="file-info">
                <i class="fas fa-file-pdf"></i>
                <span>${file.name}</span>
            </div>
            <button class="file-remove" onclick="removeFile(${index})">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `).join('');
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
}

function clearFiles() {
    selectedFiles = [];
    document.getElementById('fileInput').value = '';
    updateFileList();
}

// Upload and Parse
async function handleUpload() {
    if (selectedFiles.length === 0) return;
    
    const formData = new FormData();
    selectedFiles.forEach(file => formData.append('files', file));
    
    const progressContainer = document.getElementById('progressContainer');
    const progressBar = document.getElementById('progressBar');
    const progressText = document.getElementById('progressText');
    
    progressContainer.style.display = 'block';
    progressBar.style.width = '30%';
    progressText.textContent = `Processing ${selectedFiles.length} file(s)...`;
    
    try {
        const response = await fetch(`${API_BASE_URL}/parse-multiple`, {
            method: 'POST',
            body: formData
        });
        
        progressBar.style.width = '70%';
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        progressBar.style.width = '100%';
        
        if (data.results) {
            data.results.forEach(result => {
                if (result.success) {
                    result.data.parsedDate = new Date().toISOString();
                    parsedResumes.push(result.data);
                    if (result.data.skills) {
                        result.data.skills.forEach(skill => allSkills.add(skill));
                    }
                }
            });
        }
        
        updateStats();
        updateCandidatesView();
        updateAnalytics();
        updateSkillsMatrix();
        updateSkillFilter();
        
        showToast(`Successfully parsed ${data.parsed_count} resume(s)`, 'success');
        
        if (data.error_count > 0) {
            showToast(`Failed to parse ${data.error_count} file(s)`, 'error');
        }
        
        clearFiles();
        setTimeout(() => {
            progressContainer.style.display = 'none';
            progressBar.style.width = '0%';
        }, 1000);
        
    } catch (error) {
        console.error('Error:', error);
        showToast('Error uploading files: ' + error.message, 'error');
        progressContainer.style.display = 'none';
    }
}

// Update Stats
function updateStats() {
    document.getElementById('totalUploaded').textContent = selectedFiles.length + parsedResumes.length;
    document.getElementById('totalParsed').textContent = parsedResumes.length;
    document.getElementById('uniqueSkills').textContent = allSkills.size;
    document.getElementById('candidateCount').textContent = parsedResumes.length;
    
    // Calculate average experience
    const expValues = parsedResumes
        .map(r => {
            if (!r.experienceYears) return null;
            const match = r.experienceYears.match(/\d+/);
            return match ? parseInt(match[0]) : null;
        })
        .filter(v => v !== null);
    
    if (expValues.length > 0) {
        const avg = expValues.reduce((a, b) => a + b, 0) / expValues.length;
        document.getElementById('avgTime').textContent = avg.toFixed(1) + ' yrs';
    }
}

// Update Candidates View
function updateCandidatesView(resumes = null) {
    const grid = document.getElementById('candidatesGrid');
    const displayResumes = resumes || parsedResumes;
    
    if (displayResumes.length === 0) {
        grid.innerHTML = `
            <div class="no-data">
                <i class="fas fa-user-friends"></i>
                <h3>No Candidates Yet</h3>
                <p>Upload and parse resumes to see candidates here</p>
            </div>
        `;
        return;
    }
    
    grid.innerHTML = displayResumes.map((resume, index) => {
        const initials = (resume.name || 'Unknown').split(' ').map(n => n[0]).join('').substring(0, 2);
        const skills = (resume.skills || []).slice(0, 5);
        
        return `
            <div class="candidate-card" onclick="viewCandidate(${index})">
                <div class="candidate-header">
                    <div class="candidate-avatar">${initials}</div>
                    <div class="candidate-name">
                        <h3>${resume.name || 'Unknown'}</h3>
                        <p>${resume.experienceYears || 'Experience N/A'}</p>
                    </div>
                </div>
                <div class="candidate-info">
                    ${resume.email ? `
                        <div class="info-row">
                            <i class="fas fa-envelope"></i>
                            <span>${resume.email}</span>
                        </div>
                    ` : ''}
                    ${resume.phone ? `
                        <div class="info-row">
                            <i class="fas fa-phone"></i>
                            <span>${resume.phone}</span>
                        </div>
                    ` : ''}
                    ${resume.education ? `
                        <div class="info-row">
                            <i class="fas fa-graduation-cap"></i>
                            <span>${resume.education.substring(0, 50)}...</span>
                        </div>
                    ` : ''}
                </div>
                <div class="candidate-skills">
                    ${skills.map(skill => `<span class="skill-badge">${skill}</span>`).join('')}
                    ${resume.skills && resume.skills.length > 5 ? `<span class="skill-badge">+${resume.skills.length - 5} more</span>` : ''}
                </div>
            </div>
        `;
    }).join('');
}

// View Candidate Detail
function viewCandidate(index) {
    const resume = parsedResumes[index];
    const modal = document.getElementById('candidateModal');
    const modalName = document.getElementById('modalName');
    const modalBody = document.getElementById('modalBody');
    
    modalName.textContent = resume.name || 'Unknown Candidate';
    modalBody.innerHTML = `
        <div class="candidate-details">
            ${resume.email ? `<p><strong><i class="fas fa-envelope"></i> Email:</strong> ${resume.email}</p>` : ''}
            ${resume.phone ? `<p><strong><i class="fas fa-phone"></i> Phone:</strong> ${resume.phone}</p>` : ''}
            ${resume.linkedin ? `<p><strong><i class="fab fa-linkedin"></i> LinkedIn:</strong> <a href="${resume.linkedin}" target="_blank">${resume.linkedin}</a></p>` : ''}
            ${resume.github ? `<p><strong><i class="fab fa-github"></i> GitHub:</strong> <a href="${resume.github}" target="_blank">${resume.github}</a></p>` : ''}
            ${resume.experienceYears ? `<p><strong><i class="fas fa-briefcase"></i> Experience:</strong> ${resume.experienceYears}</p>` : ''}
            ${resume.education ? `<p><strong><i class="fas fa-graduation-cap"></i> Education:</strong> ${resume.education}</p>` : ''}
            ${resume.skills && resume.skills.length > 0 ? `
                <div style="margin-top: 20px;">
                    <strong><i class="fas fa-cogs"></i> Skills:</strong>
                    <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px;">
                        ${resume.skills.map(skill => `<span class="skill-badge">${skill}</span>`).join('')}
                    </div>
                </div>
            ` : ''}
            ${resume.summary ? `
                <div style="margin-top: 20px;">
                    <strong><i class="fas fa-file-alt"></i> Summary:</strong>
                    <p style="margin-top: 10px; line-height: 1.6;">${resume.summary}</p>
                </div>
            ` : ''}
        </div>
    `;
    
    modal.classList.add('active');
}

function closeModal() {
    document.getElementById('candidateModal').classList.remove('active');
}

// Analytics
function updateAnalytics() {
    updateActivityList();
}

function updateActivityList() {
    const list = document.getElementById('activityList');
    const recentResumes = parsedResumes.slice(-5).reverse();
    
    if (recentResumes.length === 0) {
        list.innerHTML = '<div class="no-data" style="padding: 20px;"><p>No activity yet</p></div>';
        return;
    }
    
    list.innerHTML = recentResumes.map(resume => `
        <div class="activity-item">
            <div class="activity-icon">
                <i class="fas fa-user-plus"></i>
            </div>
            <div class="activity-info">
                <h4>${resume.name || 'Unknown'} added</h4>
                <p>${new Date(resume.parsedDate).toLocaleString()}</p>
            </div>
        </div>
    `).join('');
}

// Skills Matrix
function updateSkillsMatrix() {
    const matrix = document.getElementById('skillsMatrix');
    
    if (allSkills.size === 0) {
        matrix.innerHTML = `
            <div class="no-data">
                <i class="fas fa-cogs"></i>
                <h3>No Skills Data</h3>
                <p>Parse resumes to see skills matrix</p>
            </div>
        `;
        return;
    }
    
    const skillCounts = {};
    parsedResumes.forEach(resume => {
        if (resume.skills) {
            resume.skills.forEach(skill => {
                skillCounts[skill] = (skillCounts[skill] || 0) + 1;
            });
        }
    });
    
    const sortedSkills = Object.entries(skillCounts)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 20);
    
    const maxCount = sortedSkills[0][1];
    
    matrix.innerHTML = sortedSkills.map(([skill, count]) => `
        <div class="skill-row">
            <div class="skill-name">${skill}</div>
            <div class="skill-bar">
                <div class="skill-bar-fill" style="width: ${(count / maxCount) * 100}%"></div>
            </div>
            <div class="skill-count">${count} candidate${count > 1 ? 's' : ''}</div>
        </div>
    `).join('');
}

// Filters
function updateSkillFilter() {
    const select = document.getElementById('skillFilter');
    select.innerHTML = '<option value="">All Skills</option>' +
        Array.from(allSkills).sort().map(skill => 
            `<option value="${skill}">${skill}</option>`
        ).join('');
}

function applyFilters() {
    const expFilter = document.getElementById('expFilter').value;
    const skillFilter = document.getElementById('skillFilter').value;
    const sortBy = document.getElementById('sortBy').value;
    
    let filtered = [...parsedResumes];
    
    // Filter by experience
    if (expFilter) {
        filtered = filtered.filter(resume => {
            if (!resume.experienceYears) return false;
            const exp = parseInt(resume.experienceYears.match(/\d+/)?.[0] || 0);
            
            if (expFilter === '0-2') return exp >= 0 && exp <= 2;
            if (expFilter === '3-5') return exp >= 3 && exp <= 5;
            if (expFilter === '6-10') return exp >= 6 && exp <= 10;
            if (expFilter === '10+') return exp > 10;
            return true;
        });
    }
    
    // Filter by skill
    if (skillFilter) {
        filtered = filtered.filter(resume => 
            resume.skills && resume.skills.includes(skillFilter)
        );
    }
    
    // Sort
    if (sortBy === 'name') {
        filtered.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    } else if (sortBy === 'experience') {
        filtered.sort((a, b) => {
            const expA = parseInt(a.experienceYears?.match(/\d+/)?.[0] || 0);
            const expB = parseInt(b.experienceYears?.match(/\d+/)?.[0] || 0);
            return expB - expA;
        });
    } else {
        filtered.sort((a, b) => new Date(b.parsedDate || 0) - new Date(a.parsedDate || 0));
    }
    
    updateCandidatesView(filtered);
}

function handleSearch(e) {
    const query = e.target.value.toLowerCase();
    
    if (!query) {
        applyFilters();
        return;
    }
    
    const filtered = parsedResumes.filter(resume => 
        (resume.name || '').toLowerCase().includes(query) ||
        (resume.email || '').toLowerCase().includes(query) ||
        (resume.phone || '').toLowerCase().includes(query) ||
        (resume.skills || []).some(skill => skill.toLowerCase().includes(query))
    );
    
    updateCandidatesView(filtered);
}

// Export
async function exportToExcel() {
    if (parsedResumes.length === 0) {
        showToast('No data to export', 'error');
        return;
    }
    
    try {
        showToast('Preparing Excel file...', 'info');
        
        const response = await fetch(`${API_BASE_URL}/export-excel`);
        
        if (!response.ok) {
            throw new Error('Failed to export to Excel');
        }
        
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = 'parsed_resumes.xlsx';
        
        if (contentDisposition) {
            const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
            if (filenameMatch) {
                filename = filenameMatch[1];
            }
        }
        
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        showToast('Excel file downloaded successfully!', 'success');
        
    } catch (error) {
        console.error('Error:', error);
        showToast('Error exporting to Excel: ' + error.message, 'error');
    }
}

async function clearAllCandidates() {
    if (!confirm('Are you sure you want to clear all candidates?')) return;
    
    try {
        await fetch(`${API_BASE_URL}/clear`, { method: 'DELETE' });
        parsedResumes = [];
        allSkills.clear();
        updateStats();
        updateCandidatesView();
        updateAnalytics();
        updateSkillsMatrix();
        showToast('All candidates cleared', 'info');
    } catch (error) {
        showToast('Error clearing candidates: ' + error.message, 'error');
    }
}

// Navigation
function switchSection(section) {
    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
    document.querySelector(`[data-section="${section}"]`).classList.add('active');
    
    document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
    document.getElementById(`${section}-section`).classList.add('active');
    
    const titles = {
        'upload': 'Upload Resumes',
        'candidates': 'Candidate Database',
        'analytics': 'Analytics Dashboard',
        'skills': 'Skills Matrix'
    };
    
    document.getElementById('pageTitle').textContent = titles[section];
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('active');
}

function toggleTheme() {
    // Theme toggle implementation
    showToast('Theme toggle coming soon!', 'info');
}

// Toast
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}
