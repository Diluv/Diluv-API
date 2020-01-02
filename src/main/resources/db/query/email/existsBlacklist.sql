SELECT (eb.ebC + edb.edbC)
FROM (SELECT COUNT(*) as ebC FROM email_blacklist WHERE email = ?) as eb,
     (SELECT COUNT(*) as edbC FROM email_domain_blacklist WHERE domain = ?) as edb