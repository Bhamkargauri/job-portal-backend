## 📂 Franchisor APIs – Candidate Management (Job Portal)

This module enables Franchisors to manage candidate records efficiently in a job posting portal.

### ✨ Features:
- ✅ Create candidate (with resume upload)
- 📥 Bulk upload candidates via Excel (Apache POI)
- ✏️ Update candidate info + resume
- 📄 Download resume in original format
- 🔎 Search/filter candidates by job post or placement status
- 🚫 Soft delete candidate records
- 👥 Secured with Spring Security (FRANCHISOR role)

### 🛠 Tech Stack:
- Spring Boot, Spring Security, Java
- MySQL
- RESTful API
- Apache POI (for Excel support)
- JWT Authentication

### 🔐 Authorization:
All endpoints are restricted to users with the `FRANCHISOR` role via `@PreAuthorize`.
