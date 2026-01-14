-- =================================================
-- INIT ROLES
-- =================================================
INSERT INTO roles (name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');



-- =================================================
-- INIT HOROSCOPE MESSAGES
-- =================================================
INSERT INTO horoscope_messages (category, message)
SELECT 'LOVE', 'Năm mới mang đến nhiều cảm xúc tích cực trong chuyện tình cảm.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages WHERE category = 'LOVE'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'CAREER', 'Công việc năm nay có nhiều cơ hội phát triển, hãy mạnh dạn nắm bắt.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages WHERE category = 'CAREER'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'FINANCE', 'Tài chính ổn định, tránh chi tiêu bốc đồng.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages WHERE category = 'FINANCE'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'HEALTH', 'Sức khỏe tốt, chú ý nghỉ ngơi điều độ.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages WHERE category = 'HEALTH'
);



-- =================================================
-- INIT LUCKY REWARDS
-- reward_type: message | points | sticker | avatar
-- =================================================
INSERT INTO lucky_rewards (name, reward_type, value, message, active)
SELECT 'Lời chúc may mắn', 'message', 0, 'Chúc bạn một năm mới an khang thịnh vượng!', true
    WHERE NOT EXISTS (
    SELECT 1 FROM lucky_rewards WHERE name = 'Lời chúc may mắn'
);

INSERT INTO lucky_rewards (name, reward_type, value, message, active)
SELECT '10 Điểm may mắn', 'points', 10, NULL, true
    WHERE NOT EXISTS (
    SELECT 1 FROM lucky_rewards WHERE name = '10 Điểm may mắn'
);

INSERT INTO lucky_rewards (name, reward_type, value, message, active)
SELECT 'Sticker Mèo Thần Tài', 'sticker', 1, NULL, true
    WHERE NOT EXISTS (
    SELECT 1 FROM lucky_rewards WHERE name = 'Sticker Mèo Thần Tài'
);

INSERT INTO lucky_rewards (name, reward_type, value, message, active)
SELECT 'Avatar Tết 2026', 'avatar', 2, NULL, true
    WHERE NOT EXISTS (
    SELECT 1 FROM lucky_rewards WHERE name = 'Avatar Tết 2026'
);



-- =================================================
-- INIT SHOP ITEMS
-- category: AVATAR, STICKER, FRAME, FLOWER, LANTERN
-- =================================================
INSERT INTO shop_items (name, price, category, image_url, active)
SELECT 'Avatar Hoa Mai', 50, 'AVATAR', '/images/avatar/hoa-mai.png', true
    WHERE NOT EXISTS (
    SELECT 1 FROM shop_items WHERE name = 'Avatar Hoa Mai'
);

INSERT INTO shop_items (name, price, category, image_url, active)
SELECT 'Sticker Lân Sư Rồng', 30, 'STICKER', '/images/sticker/lan-su-rong.png', true
    WHERE NOT EXISTS (
    SELECT 1 FROM shop_items WHERE name = 'Sticker Lân Sư Rồng'
);

INSERT INTO shop_items (name, price, category, image_url, active)
SELECT 'Khung Tết Truyền Thống', 40, 'FRAME', '/images/frame/tet-truyen-thong.png', true
    WHERE NOT EXISTS (
    SELECT 1 FROM shop_items WHERE name = 'Khung Tết Truyền Thống'
);

INSERT INTO shop_items (name, price, category, image_url, active)
SELECT 'Hoa Đào May Mắn', 20, 'FLOWER', '/images/flower/hoa-dao.png', true
    WHERE NOT EXISTS (
    SELECT 1 FROM shop_items WHERE name = 'Hoa Đào May Mắn'
);

INSERT INTO shop_items (name, price, category, image_url, active)
SELECT 'Đèn Lồng Đỏ', 25, 'LANTERN', '/images/lantern/den-long-do.png', true
    WHERE NOT EXISTS (
    SELECT 1 FROM shop_items WHERE name = 'Đèn Lồng Đỏ'
);
