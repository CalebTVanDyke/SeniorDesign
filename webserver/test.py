from db_conn import DbRequest
from user_utils import UserUtils

db = DbRequest()
print db.query("SELECT sensitivity, avg_heart_rate, avg_blood_ox, avg_temp FROM `users` where id=3")

# [{u'Extra': u'auto_increment', u'Default': None, u'Field': u'id', u'Key': u'PRI', u'Null': u'NO', u'Type': u'int(11)'}, {u'Extra': u'', u'Default': None, u'Field': u'user_id', u'Key': u'', u'Null': u'NO', u'Type': u'int(11)'}, {u'Extra': u'', u'Default': None, u'Field': u'time', u'Key': u'', u'Null': u'NO', u'Type': u'datetime'}, {u'Extra': u'', u'Default': None, u'Field': u'blood_oxygen', u'Key': u'', u'Null': u'NO', u'Type': u'text'}, {u'Extra': u'', u'Default': None, u'Field': u'heart_rate', u'Key': u'', u'Null': u'NO', u'Type': u'text'}, {u'Extra': u'', u'Default': None, u'Field': u'temp', u'Key': u'', u'Null': u'NO', u'Type': u'text'}]
# [{u'Extra': u'auto_increment', u'Default': None, u'Field': u'id', u'Key': u'PRI', u'Null': u'NO', u'Type': u'int(11)'}, {u'Extra': u'', u'Default': None, u'Field': u'username', u'Key': u'', u'Null': u'NO', u'Type': u'text'}, {u'Extra': u'', u'Default': None, u'Field': u'password_hash', u'Key': u'', u'Null': u'NO', u'Type': u'varchar(256)'}, {u'Extra': u'', u'Default': None, u'Field': u'email', u'Key': u'', u'Null': u'NO', u'Type': u'text'}, {u'Extra': u'', u'Default': None, u'Field': u'phone', u'Key': u'', u'Null': u'NO', u'Type': u'text'}, {u'Extra': u'', u'Default': None, u'Field': u'receivers', u'Key': u'', u'Null': u'YES', u'Type': u'varchar(1000)'}, {u'Extra': u'', u'Default': None, u'Field': u'avg_blood_ox', u'Key': u'', u'Null': u'NO', u'Type': u'double'}, {u'Extra': u'', u'Default': None, u'Field': u'avg_heart_rate', u'Key': u'', u'Null': u'NO', u'Type': u'double'}, {u'Extra': u'', u'Default': None, u'Field': u'avg_temp', u'Key': u'', u'Null': u'NO', u'Type': u'double'}, {u'Extra': u'', u'Default': u'm', u'Field': u'sensitivity', u'Key': u'', u'Null': u'NO', u'Type': u'varchar(1)'}]
