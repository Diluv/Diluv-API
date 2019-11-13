SELECT pf.id,
       pf.sha512,
       pf.crc32,
       pf.size,
       pf.changelog,
       pf.created_at,
       pf.updated_at,
       pf.reviewed,
       pf.released,
       pf.project_id,
       pf.user_id
FROM project_files pf,
     projects p
WHERE (p.game_slug=? AND p.project_type_slug=? AND p.slug=?)
  AND pf.id = p.id
LIMIT 20;
