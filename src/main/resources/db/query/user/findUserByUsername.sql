SELECT id,
       username,
       email,
       password,
       password_type,
       mfa,
       mfa_secret,
       created_at
FROM users
WHERE username = ?
LIMIT 1;