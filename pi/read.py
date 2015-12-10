import serial
import httplib
import json
import urllib
import urllib2
import json
import random
import threading
import sys
import time

safeData = True
lastHeart = 90
lastAlert = 0;
lock = threading.Lock()

def getUserInput():
	global safeData
	while True:
		ch = raw_input()
		if ch == 'x':
			sys.exit(0)
		elif ch == 'e':
			with lock:
				safeData = False
				print safeData

def loadData():
	threading.Timer(5.0, loadData).start()
	global safeData
	global lastHeart
	global lastAlert
	if safeData:
		oxygen = random.randint(95, 100)
		if lastHeart <= 80:
			heart = random.randint(80, 82)
		elif lastHeart >= 100:
			heart = random.randint(98, 100)
		else:
			heart = random.randint(lastHeart-2, lastHeart+2)
		temp = random.uniform(97.0, 99.0)
		lastHeart = heart
	else:
		print 'using unsafe data'
		oxygen = random.randint(50, 60)
		heart = random.randint(40, 50)
		temp = random.uniform(110, 115)
		with lock:
			safeData = True
	conn = httplib.HTTPConnection("cvandyke.ddns.net")
	# query = urllib.quote(query)
	conn.request("GET", "loadData?user_id={0}&heartRate={1}&bloodOxygen={2}&temp={3}".format(3, heart, oxygen, temp))
	response = conn.getresponse()
	jsonStr = response.read()
	conn.close()
	alert = json.loads(jsonStr)
	if alert['alert']:
		curTime = time.time()
		if curTime - lastAlert > 600:
			urllib2.urlopen("http://databasesupport.arlenburroughs.com/dec1505/alert.php?user_id='3'")
			lastAlert = curTime
			print "alert sent"

loadData()
t1 = threading.Thread(target=getUserInput)
t1.start()
