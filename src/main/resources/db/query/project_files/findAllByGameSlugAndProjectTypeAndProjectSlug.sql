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
WHERE pf.id = p.id
  AND p.game_slug=?
  AND p.project_type_slug=?
  AND p.slug=?
LIMIT 20;
