from db_conn import DbRequest
from user_utils import UserUtils

db = DbRequest()
print UserUtils.save_user_info(db, 3, "5153217935", "ctvandyke24@gmail.com")