from db_conn import DbRequest
import datetime
import time

class DateUtils:

	@staticmethod
	def GetAvailableDates(db, user_id):
		cmd = 'SELECT time FROM `readings` WHERE user_id={0} GROUP BY DATE(time) ORDER BY time asc;'.format(user_id)
		result = db.query(cmd)
		dates = '['
		for i in range(len(result) - 1):
			dates += '"' + result[i]['time'] + '",'
		dates += '"' + result[len(result) - 1]['time'] + '"]'
		return dates

	@staticmethod
	def GetDataForDay(db, date, user_id, dataGap):
		singleDayNoDate = 'No data was found for date: {0}.'
		cmd = "SELECT * FROM `readings` WHERE DATE(time)='{0}' AND user_id={1}".format(date, user_id)
		return applyDataFrequency(result, dataGap)

	@staticmethod
	def GetDataDateRange(db, startDate, endDate, user_id, dataGap):
		dateRangeNoData = 'role="alert">No data was found for date range: {0} to {1}.'
		cmd = "SELECT * FROM `readings` WHERE DATE(time) >= '{0}' AND DATE(time) <= '{1}' " \
					"AND user_id={2} ORDER BY time asc".format(startDate, endDate, user_id)
		result = db.query(cmd)
		return applyDataFrequency(result, dataGap)

	@staticmethod
	def GetDataDateTimeRange(db, startDateTime, endDateTime, user_id, dataGap):
		dateRangeNoData = 'No data was found for date range: {0} to {1}.'
		cmd = "SELECT * FROM `readings` WHERE time >= '{0}' AND time <= '{1}' " \
					"AND user_id={2} ORDER BY time asc".format(startDateTime, endDateTime, user_id)
		print cmd
		result = db.query(cmd)
		return applyDataFrequency(result, dataGap)
		
def applyDataFrequency(result, dataGap):
	lastTime = 0
	heartRate = []
	temp = []
	bloodOxygen = []
	if result == None:
		return {'heart_rate' : heartRate, 'temp' : temp, 'blood_oxygen' : bloodOxygen}
	for item in result:
		dt = time.mktime(datetime.datetime.strptime(item['time'], "%Y-%m-%d %H:%M:%S").timetuple())
		if lastTime == 0 or dt - lastTime >= dataGap:
			heartRate.append({'time': item['time'], 'heart_rate': item['heart_rate']})
			temp.append({'time': item['time'], 'temp': item['temp']})
			bloodOxygen.append({'time': item['time'], 'blood_oxygen': item['blood_oxygen']})
			lastTime = dt
	return {'heart_rate' : heartRate, 'temp' : temp, 'blood_oxygen' : bloodOxygen}

