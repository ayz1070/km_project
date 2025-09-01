-- 기본 역할(Role) 추가 (중복 방지)
INSERT INTO roles (id, name, description, created_at, updated_at) 
SELECT 1, 'ADMIN', '시스템 관리자', NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (id, name, description, created_at, updated_at) 
SELECT 2, 'USER', '일반 사용자', NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

-- 기본 사용자 데이터 추가 (중복 방지)
INSERT INTO users (username, password, email, full_name, status, role_id, created_at, updated_at) 
SELECT 'admin', 'admin123', 'admin@example.com', '관리자', 'ACTIVE', 1, NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, email, full_name, status, role_id, created_at, updated_at) 
SELECT 'test', 'test123', 'test@example.com', '테스트 사용자', 'ACTIVE', 2, NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'test');

INSERT INTO users (username, password, email, full_name, status, role_id, created_at, updated_at) 
SELECT 'km', 'km123', 'km@koreamarkers.com', 'KM 사용자', 'ACTIVE', 2, NOW(), NOW() 
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'km');