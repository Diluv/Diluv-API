SELECT name,
       slug,
       game_slug
FROM project_types
WHERE game_slug = ?
LIMIT 20;
