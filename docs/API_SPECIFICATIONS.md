# API 설계 명세서

## 🌐 API 개요

### 기본 정보
- **Base URL**: `/api/v1`
- **인증**: Session 기반 + CSRF 보호
- **권한**: ADMIN, USER 역할 기반
- **응답 형식**: JSON

## 📋 공통 응답 형식

### 성공 응답
```json
{ "success": true, "data": {...}, "message": "성공" }
```

### 오류 응답  
```json
{ "success": false, "error": {"code": "ERROR_CODE", "message": "에러 메시지"} }
```

### 페이지네이션
```json
{ "success": true, "data": {"content": [...], "page": {...}} }
```

## 🔐 인증 API

### 로그인
```http
POST /api/v1/auth/login
{ "username": "admin", "password": "password123" }
```

### 로그아웃
```http
POST /api/v1/auth/logout
X-CSRF-TOKEN: {csrf_token}
```

### 현재 사용자 정보
```http
GET /api/v1/auth/me
Authorization: Session
```

### 패스워드 변경
```http
PUT /api/v1/auth/password
{ "currentPassword": "old", "newPassword": "new", "confirmPassword": "new" }
```

## 👤 사용자 관리 API

### 사용자 목록 (ADMIN)
```http
GET /api/v1/users?page=0&size=20&status=ACTIVE
```

### 사용자 상세 (ADMIN/SELF)
```http
GET /api/v1/users/{id}
```

### 사용자 생성 (ADMIN)
```http
POST /api/v1/users
{ "username": "newuser", "password": "pass123", "email": "new@example.com", "fullName": "사용자", "role": "USER" }
```

### 사용자 수정 (ADMIN/SELF)
```http
PUT /api/v1/users/{id}
{ "email": "updated@example.com", "fullName": "수정된 이름" }
```

### 사용자 삭제 (ADMIN)
```http
DELETE /api/v1/users/{id}
```

## 📊 기타 API

### 역할 목록 (ADMIN)
```http
GET /api/v1/roles
```

### 대시보드 통계 (ADMIN)
```http
GET /api/v1/dashboard/stats
```

## 🚨 주요 에러 코드

| 코드 | Status | 설명 |
|------|--------|------|
| `AUTHENTICATION_FAILED` | 401 | 로그인 실패 |
| `ACCESS_DENIED` | 403 | 권한 없음 |
| `VALIDATION_ERROR` | 400 | 입력 오류 |
| `USER_NOT_FOUND` | 404 | 사용자 없음 |
| `CSRF_TOKEN_MISSING` | 403 | CSRF 토큰 누락 |

## 🔧 사용 예시

### JavaScript
```javascript
// 로그인
fetch('/api/v1/auth/login', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({username: 'admin', password: 'pass123'}),
  credentials: 'include'
});

// CSRF 토큰 포함 요청
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
fetch('/api/v1/users', {
  method: 'POST',
  headers: {'Content-Type': 'application/json', 'X-CSRF-TOKEN': csrfToken},
  body: JSON.stringify(userData),
  credentials: 'include'
});
```

간결하고 실용적인 REST API 명세로 빠른 구현이 가능합니다.