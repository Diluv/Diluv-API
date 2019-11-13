SELECT id,
       username,
       email,
       password,
       password_type,
       mfa,
       mfa_secret,
       avatar_url
FROM users
WHERE username=?
LIMIT 1