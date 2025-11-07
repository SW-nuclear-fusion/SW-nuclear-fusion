-- 1. 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    phone VARCHAR(255) UNIQUE,
    role VARCHAR(255),
    font_size VARCHAR(255),
    birthdate DATE,
    gender VARCHAR(255),
    provider VARCHAR(255),
    provider_id VARCHAR(255),
    plant_color VARCHAR(255),
    plant_name VARCHAR(255),
    plant_exp INT NOT NULL DEFAULT 0,
    user_water INT NOT NULL DEFAULT 10,
    user_affection INT NOT NULL DEFAULT 10
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 알람 테이블
CREATE TABLE IF NOT EXISTS alarms (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      medication_name VARCHAR(255) NOT NULL,
    notification_time TIME NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 알람 요일 테이블
CREATE TABLE IF NOT EXISTS alarm_days (
                                          alarm_id BIGINT NOT NULL,
                                          alarm_day VARCHAR(10) NOT NULL,
    FOREIGN KEY (alarm_id) REFERENCES alarms(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 알람 체크 기록 테이블
CREATE TABLE IF NOT EXISTS alarm_check_logs (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                user_id BIGINT NOT NULL,
                                                alarm_id BIGINT NOT NULL,
                                                check_date DATE NOT NULL,
                                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (alarm_id) REFERENCES alarms(id) ON DELETE CASCADE,
    CONSTRAINT UQ_USER_ALARM_DATE UNIQUE (user_id, alarm_id, check_date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 퀴즈 질문 테이블
CREATE TABLE IF NOT EXISTS quiz_questions (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              category VARCHAR(255) NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    correct_answer VARCHAR(255) NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 퀴즈 보기 테이블
CREATE TABLE IF NOT EXISTS quiz_options (
                                            question_id BIGINT NOT NULL,
                                            option_text VARCHAR(255),
    FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 퀴즈 시도 기록 테이블
CREATE TABLE IF NOT EXISTS quiz_attempts (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             attempt_date DATE NOT NULL,
                                             correct_count INT NOT NULL DEFAULT 0,
                                             total_count INT NOT NULL DEFAULT 0,
                                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT UQ_USER_ATTEMPT_DATE UNIQUE (user_id, attempt_date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 방문 미션 테이블
CREATE TABLE IF NOT EXISTS location_missions (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 name VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    radius_meters DOUBLE NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 방문 미션 완료 기록 테이블
CREATE TABLE IF NOT EXISTS mission_logs (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            user_id BIGINT NOT NULL,
                                            mission_id BIGINT NOT NULL,
                                            completion_date DATE NOT NULL,
                                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (mission_id) REFERENCES location_missions(id) ON DELETE CASCADE,
    CONSTRAINT UQ_USER_MISSION_DATE UNIQUE (user_id, mission_id, completion_date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 일일 감정 기록 테이블
CREATE TABLE IF NOT EXISTS daily_moods (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           user_id BIGINT NOT NULL,
                                           mood_date DATE NOT NULL,
                                           mood_icon VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT UQ_USER_MOOD_DATE UNIQUE (user_id, mood_date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;