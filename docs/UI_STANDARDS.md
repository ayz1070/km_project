# UI/UX 개발 표준 가이드

## 🎨 디자인 시스템

### 기술 스택
- **Bootstrap 5.3+** + **Thymeleaf**
- **Bootstrap Icons** + **시스템 폰트**
- **반응형 디자인** (모바일 우선)

## 🌈 색상 시스템

### 핵심 색상
```css
:root {
  --bs-primary: #0d6efd; --bs-success: #198754; --bs-danger: #dc3545;
  --brand-primary: #2c3e50; --gray-700: #343a40;
}
```

## 📝 타이포그래피

```css
body {
  font-family: -apple-system, "Apple SD Gothic Neo", "맑은 고딕", sans-serif;
  font-size: 16px; line-height: 1.6; color: var(--gray-700);
}
```

- **제목**: h1~h6, display-1~display-6 클래스
- **본문**: lead, 기본 p, small.text-muted

## 🧩 컴포넌트 라이브러리

### 1. 로그인 폼
```html
<div class="row justify-content-center">
  <div class="col-md-6 col-lg-4">
    <div class="card shadow">
      <div class="card-body">
        <h3 class="text-center mb-4">로그인</h3>
        <form th:action="@{/auth/login}" th:object="${loginForm}" method="post">
          <!-- 사용자명 입력 -->
          <div class="mb-3">
            <input type="text" class="form-control" th:field="*{username}" placeholder="사용자명">
          </div>
          <!-- 패스워드 입력 -->
          <div class="mb-3">
            <input type="password" class="form-control" th:field="*{password}" placeholder="패스워드">
          </div>
          <!-- 로그인 버튼 -->
          <button type="submit" class="btn btn-primary w-100">로그인</button>
          <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
        </form>
      </div>
    </div>
  </div>
</div>
```

### 2. 네비게이션
```html
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="/">KM Spring Boot</a>
    <div class="navbar-nav ms-auto">
      <div class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
          <span th:text="${#authentication.name}">사용자</span>
        </a>
        <ul class="dropdown-menu">
          <li><a class="dropdown-item" href="/auth/logout">로그아웃</a></li>
        </ul>
      </div>
    </div>
  </div>
</nav>
```

### 3. 알림 메시지
```html
<div th:if="${message}" class="alert alert-success alert-dismissible" th:text="${message}">
  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<div th:if="${error}" class="alert alert-danger alert-dismissible" th:text="${error}">
  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

### 4. 데이터 테이블
```html
<div class="card">
  <div class="card-header d-flex justify-content-between">
    <h5>사용자 목록</h5>
    <button class="btn btn-primary btn-sm">추가</button>
  </div>
  <div class="card-body">
    <table class="table table-striped">
      <thead>
        <tr><th>ID</th><th>사용자명</th><th>이메일</th><th>작업</th></tr>
      </thead>
      <tbody>
        <tr th:each="user : ${users}">
          <td th:text="${user.id}"></td>
          <td th:text="${user.username}"></td>
          <td th:text="${user.email}"></td>
          <td>
            <button class="btn btn-sm btn-outline-primary">편집</button>
            <button class="btn btn-sm btn-outline-danger">삭제</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
```

### 5. 모달
```html
<div class="modal fade" id="confirmModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">확인</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">정말 실행하시겠습니까?</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
        <button type="button" class="btn btn-primary">확인</button>
      </div>
    </div>
  </div>
</div>
```

## 📱 반응형 디자인

### Bootstrap 그리드 시스템
- **xs**: <576px, **sm**: ≥576px, **md**: ≥768px, **lg**: ≥992px
- **사용법**: `col-12 col-md-6 col-lg-4`

## 🎭 Thymeleaf 패턴

### 기본 레이아웃
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title th:text="${title}">KM Spring Boot</title>
  <link href="/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
  <div th:replace="fragments/header"></div>
  <main class="container mt-4" th:fragment="content"></main>
  <script src="/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

### 핵심 문법
- `th:text="${변수}"`: 텍스트 출력
- `th:if="${조건}"`: 조건부 렌더링
- `th:each="item : ${list}"`: 반복
- `th:field="*{필드}"`: 폼 바인딩

## 🚀 성능 최적화

- **CSS**: Bootstrap CDN 사용, 불필요한 컴포넌트 제거
- **JS**: 필요한 Bootstrap 컴포넌트만 초기화
- **이미지**: `loading="lazy"`, Bootstrap Icons 사용

## ✅ 접근성

- **ARIA**: `aria-label`, `aria-describedby` 속성 사용
- **폼**: `<label>` 연결, 필수 필드 표시
- **색상**: WCAG AA 기준 4.5:1 대비율 준수
- **키보드**: 모든 인터액션 키보드 접근 가능

간결하고 실용적인 UI 표준으로 빠른 개발이 가능합니다.