SELECT *
FROM refresh_tokens
WHERE user_id = ?
  AND code = ?
LIMIT 1;