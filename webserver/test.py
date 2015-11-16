from db_conn import DbRequest
from user_utils import UserUtils

db = DbRequest()
print UserUtils.get_user_info(db, 3).receivers[0].carrier
