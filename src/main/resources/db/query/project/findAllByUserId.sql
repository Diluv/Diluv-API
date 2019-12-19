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
FROM projects p,
     games g,
     users u
WHERE (user_id=? OR EXISTS(
        SELECT *
        FROM project_authors
        WHERE author_id=?
    ))
  AND p.slug = g.slug
  AND p.user_id = u.id
LIMIT 20;
