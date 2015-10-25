from db_conn import DbRequest
from random import randint
from random import uniform
import time
import datetime


db = DbRequest()

START_TIME = 1438473600
END_TIME = 1438560000
DATA_INTERVAL = 10

time = datetime.datetime.utcfromtimestamp(START_TIME)
time.strftime('%Y-%m-%d %H:%M:%S')
print(time)

curTime = START_TIME

count = 0;

while curTime < END_TIME:
	time = datetime.datetime.utcfromtimestamp(curTime)
	time.strftime('%Y-%m-%d %H:%M:%S')
	oxygen = randint(95, 100)
	heart = randint(80, 110)
	temp = uniform(97.0, 100)
	# query = "DELETE FROM `readings` WHERE time = '{0}';".format(time);
	query = "INSERT INTO `readings` (`user_id`, `time`, `blood_oxygen`, `heart_rate`, `temp`) VALUES ('3', '{0}', '{1}', '{2}', '{3}');" \
		.format(time, oxygen, heart, temp)
	print query
	db.query(query)
	curTime += DATA_INTERVAL