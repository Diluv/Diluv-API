DELETE
FROM refresh_tokens
WHERE user_id = ?
  AND code = ?;