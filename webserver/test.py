from db_conn import DbRequest
from user_utils import UserUtils

db = DbRequest()
UserUtils.change_password(db, 3, "root", "root")