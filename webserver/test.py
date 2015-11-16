from db_conn import DbRequest
from user_utils import UserUtils

db = DbRequest()
user = UserUtils.get_user_info(db, 3)
print user.receiver_string()
user.delete_receiver("cvandyke@iastate.edu")
print user.receiver_string()
