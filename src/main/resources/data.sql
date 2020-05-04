INSERT INTO role (role_id, role)
VALUES (101, "ADMIN"), (102, "USER")
ON DUPLICATE KEY UPDATE role_id = role_id;

INSERT INTO user_account (user_id, username, password)
VALUES  (201, "admin", "$2y$12$YlXEdLOhX35AYO60kcC3Su8zJI0zKO/7vmS9QXrBPBhJRcx1uJZTi"),
(202, "user", "$2y$12$YiMOV0G16lJGl4y.TPYi9.kQK9Pop1q4dnEppxW0Sg9iAk.mPcN46")
ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO user_role (user_id, role_id)
VALUES (201, 101), (202, 102)
ON DUPLICATE KEY UPDATE user_id = user_id;
