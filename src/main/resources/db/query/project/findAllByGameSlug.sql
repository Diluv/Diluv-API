SELECT p.name,
       p.slug,
       p.summary,
       p.description,
       p.logo_url,
       p.cache_downloads,
       p.created_at,
       p.updated_at,
       g.name     AS game_name,
       u.username as owner_username
FROM project p,
     game g,
     users u
WHERE (g.slug = ?)
  AND p.owner_id = u.id
LIMIT 20;
