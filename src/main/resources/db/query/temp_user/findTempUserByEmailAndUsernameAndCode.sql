SELECT id,
       username,
       email,
       password,
       password_type,
       created_at,
       verificationCode
FROM temp_users
WHERE email=?
  AND username=?
  AND verificationCode=?
LIMIT 1;