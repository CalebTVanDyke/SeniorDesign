from db_conn import DbRequest

db = DbRequest()
cmd = "SELECT * FROM `readings` WHERE DATE(time) >= '2015-08-02' AND DATE(time) <= '2015-08-03' AND user_id=3 ORDER BY time asc"
print cmd
print(db.query(cmd))