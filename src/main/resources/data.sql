-- =================================================
-- INIT ROLES
-- =================================================
INSERT INTO roles (name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');


-- ===============================
-- INIT ADMIN USERS
-- ===============================
INSERT INTO users (
    email,
    password_hash,
    full_name,
    is_active,
    created_at
)
SELECT
    'tuan.admin@tetapp.com',
    '$2a$10$ybAaRhYgKgrTYADj6hcs1OidebnjBne0PtktjV1kX4Wl5mO6sctrC',
    'System Admin 1',
    true,
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'tuan.admin@tetapp.com'
);

INSERT INTO users (
    email,
    password_hash,
    full_name,
    is_active,
    created_at
)
SELECT
    'huy.admin@tetapp.com',
    '$2a$10$y/BwjG.Wvn6f4KuOCntgyuJ5Fcxgff37fDAXk7nSQD7TvJgB.7bSS',
    'System Admin 2',
    true,
    NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'huy.admin@tetapp.com'
);
-- ===============================
-- ASSIGN ROLE_ADMIN
-- ===============================
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email IN ('tuan.admin@tetapp.com', 'huy.admin@tetapp.com')
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
);

-- =================================================
-- INIT HOROSCOPE MESSAGES
-- =================================================
INSERT INTO horoscope_messages (category, message)
SELECT 'overview', 'Một thay đổi nhỏ trong suy nghĩ có thể mang lại kết quả bất ngờ.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'overview'
      AND message = 'Một thay đổi nhỏ trong suy nghĩ có thể mang lại kết quả bất ngờ.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'overview', 'Hôm nay là thời điểm thích hợp để bắt đầu điều bạn đã ấp ủ.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'overview'
      AND message = 'Hôm nay là thời điểm thích hợp để bắt đầu điều bạn đã ấp ủ.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'overview', 'Giữ tinh thần tích cực sẽ giúp mọi việc diễn ra suôn sẻ hơn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'overview'
      AND message = 'Giữ tinh thần tích cực sẽ giúp mọi việc diễn ra suôn sẻ hơn.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'overview', 'Một cơ hội bất ngờ có thể đến từ những cuộc trò chuyện quen thuộc.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'overview'
      AND message = 'Một cơ hội bất ngờ có thể đến từ những cuộc trò chuyện quen thuộc.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'love', 'Cảm xúc của bạn hôm nay khá ổn định và chân thành.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'love'
      AND message = 'Cảm xúc của bạn hôm nay khá ổn định và chân thành.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'love', 'Nếu còn độc thân, bạn có thể nhận được sự chú ý bất ngờ.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'love'
      AND message = 'Nếu còn độc thân, bạn có thể nhận được sự chú ý bất ngờ.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'love', 'Hãy chia sẻ nhiều hơn để mối quan hệ thêm gắn kết.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'love'
      AND message = 'Hãy chia sẻ nhiều hơn để mối quan hệ thêm gắn kết.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'love', 'Sự quan tâm nhỏ cũng đủ tạo ra niềm vui lớn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'love'
      AND message = 'Sự quan tâm nhỏ cũng đủ tạo ra niềm vui lớn.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'career', 'Công việc hôm nay diễn ra đúng theo kế hoạch đã đề ra.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'career'
      AND message = 'Công việc hôm nay diễn ra đúng theo kế hoạch đã đề ra.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'career', 'Một ý tưởng mới có thể giúp bạn ghi điểm với cấp trên.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'career'
      AND message = 'Một ý tưởng mới có thể giúp bạn ghi điểm với cấp trên.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'career', 'Hãy cẩn thận trong giao tiếp để tránh hiểu lầm không đáng có.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'career'
      AND message = 'Hãy cẩn thận trong giao tiếp để tránh hiểu lầm không đáng có.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'career', 'Sự kiên trì sẽ mang lại kết quả tích cực trong thời gian tới.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'career'
      AND message = 'Sự kiên trì sẽ mang lại kết quả tích cực trong thời gian tới.'
);
INSERT INTO horoscope_messages (category, message)
SELECT 'finance', 'Tài chính ở mức ổn định, chưa nên mạo hiểm đầu tư lớn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'finance'
      AND message = 'Tài chính ở mức ổn định, chưa nên mạo hiểm đầu tư lớn.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'finance', 'Một khoản chi nhỏ nhưng cần thiết có thể xuất hiện.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'finance'
      AND message = 'Một khoản chi nhỏ nhưng cần thiết có thể xuất hiện.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'finance', 'Biết cân đối chi tiêu sẽ giúp bạn yên tâm hơn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'finance'
      AND message = 'Biết cân đối chi tiêu sẽ giúp bạn yên tâm hơn.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'finance', 'Hôm nay thích hợp để lên kế hoạch tài chính dài hạn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'finance'
      AND message = 'Hôm nay thích hợp để lên kế hoạch tài chính dài hạn.'
);
INSERT INTO horoscope_messages (category, message)
SELECT 'health', 'Ngủ đủ giấc sẽ giúp cơ thể hồi phục năng lượng.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'health'
      AND message = 'Ngủ đủ giấc sẽ giúp cơ thể hồi phục năng lượng.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'health', 'Đừng quên uống đủ nước trong ngày hôm nay.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'health'
      AND message = 'Đừng quên uống đủ nước trong ngày hôm nay.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'health', 'Một buổi vận động nhẹ sẽ giúp tinh thần thoải mái hơn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'health'
      AND message = 'Một buổi vận động nhẹ sẽ giúp tinh thần thoải mái hơn.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'health', 'Giữ tinh thần tích cực sẽ cải thiện sức khỏe tổng thể.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'health'
      AND message = 'Giữ tinh thần tích cực sẽ cải thiện sức khỏe tổng thể.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'relationship', 'Các mối quan hệ xung quanh bạn khá hài hòa.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'relationship'
      AND message = 'Các mối quan hệ xung quanh bạn khá hài hòa.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'relationship', 'Sự chân thành giúp bạn duy trì mối quan hệ bền vững.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'relationship'
      AND message = 'Sự chân thành giúp bạn duy trì mối quan hệ bền vững.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'relationship', 'Một cuộc trò chuyện thẳng thắn sẽ giúp giải quyết hiểu lầm.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'relationship'
      AND message = 'Một cuộc trò chuyện thẳng thắn sẽ giúp giải quyết hiểu lầm.'
);

INSERT INTO horoscope_messages (category, message)
SELECT 'relationship', 'Hãy lắng nghe nhiều hơn để kết nối sâu sắc hơn.'
    WHERE NOT EXISTS (
    SELECT 1 FROM horoscope_messages
    WHERE category = 'relationship'
      AND message = 'Hãy lắng nghe nhiều hơn để kết nối sâu sắc hơn.'
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
