-- H2 데이터베이스 문법 기준

-- 1. 사용자 데이터 (테스트용)
INSERT INTO users (user_id, password, name, phone, role, font_size, birthdate, gender, plant_color, plant_name, plant_exp, user_water, user_affection)
VALUES (
           'test',
           '$2a$10$IEChcIqOTxkRsaO47XEMJ.LMQkyNR2PZfGxk6ddiYNJp7l2wlG/fC', -- '1234'의 해시
           'test',
           '01012341234',
           'SENIOR',
           '기본',
           '2001-01-01',
           '남성',
           'blue',
           '퍼렁',
           15,
           10,
           10
       );

-- [!!!] (추가) 알람 데이터 (alarm_check_logs의 외래 키 충돌 방지)
INSERT INTO alarms (user_id, medication_name, notification_time, enabled)
VALUES (1, '비타민 D', '09:00:00', true); -- alarm_id = 1
INSERT INTO alarms (user_id, medication_name, notification_time, enabled)
VALUES (1, '마그네슘', '21:00:00', true); -- alarm_id = 2


-- 2. 퀴즈 데이터
-- 퀴즈 1 (계산)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (1, '계산', '100 빼기 7은 얼마입니까?', '93');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (1, '91'), (1, '92'), (1, '93'), (1, '94');

-- 퀴즈 2 (날짜)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (2, '날짜', '1년은 총 몇 월까지 있습니까?', '12월');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (2, '10월'), (2, '11월'), (2, '12월'), (2, '13월');

-- 퀴즈 3 (장소)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (3, '장소', '우리나라의 수도는 어디입니까?', '서울');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (3, '서울'), (3, '부산'), (3, '대구'), (3, '인천');

-- 퀴즈 4 (계절)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (4, '계절', '다음 중 봄에 피는 꽃은 무엇일까요?', '벚꽃');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (4, '벚꽃'), (4, '동백꽃'), (4, '코스모스'), (4, '해바라기');

-- 퀴즈 5 (상식)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (5, '상식', '다음 중 바다에 살지 않는 동물은?', '호랑이');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (5, '고래'), (5, '상어'), (5, '호랑이'), (5, '돌고래');

-- 퀴즈 6 (날짜)
INSERT INTO quiz_questions (id, category, question_text, correct_answer) VALUES
    (6, '날짜', '일주일은 며칠입니까?', '7일');
INSERT INTO quiz_options (question_id, option_text) VALUES
                                                        (6, '5일'), (6, '6일'), (6, '7일'), (6, '8일');

-- 3. (선택) 방문 미션 데이터 예시
INSERT INTO location_missions (id, name, latitude, longitude, radius_meters) VALUES
    (1, '영남대', 35.8350, 128.7454, 100); -- 위도, 경도 예시 (영남대)
INSERT INTO location_missions (id, name, latitude, longitude, radius_meters) VALUES
    (2, '경산시청', 35.8220, 128.7434, 100);

-- 4. 10월 30일 테스트 데이터
INSERT INTO alarm_check_logs (user_id, alarm_id, check_date) VALUES (1, 1, '2025-10-30');
INSERT INTO alarm_check_logs (user_id, alarm_id, check_date) VALUES (1, 2, '2025-10-30');
INSERT INTO quiz_attempts (user_id, attempt_date, correct_count, total_count) VALUES (1, '2025-10-30', 3, 3);
INSERT INTO daily_moods (user_id, mood_date, mood_icon) VALUES (1, '2025-10-30', 'happy'); -- [!] 'happy' 상태 저장

-- 5. (ID 시퀀스 값 재설정 - PK 충돌 방지)
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) + 1 FROM USERS);
ALTER TABLE alarms ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM alarms);
ALTER TABLE quiz_questions ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM quiz_questions);
ALTER TABLE location_missions ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM location_missions);
ALTER TABLE alarm_check_logs ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM alarm_check_logs);
ALTER TABLE quiz_attempts ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM quiz_attempts);
ALTER TABLE daily_moods ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM daily_moods);

