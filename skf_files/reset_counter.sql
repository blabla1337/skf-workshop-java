use auditlogs;
UPDATE counter SET count=0,blocker=0,access=0 WHERE userID=1;
UPDATE counter SET count=0,blocker=0,access=0 WHERE userID=2;
exit;