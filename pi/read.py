import serial
import httplib
import json
import urllib
import random

# ser = serial.Serial('COM4', 9600, timeout=1)
# x = ser.read(11).decode("utf-8");
oxygen = random.randint(95, 100)
heart = random.randint(80, 110)
temp = random.uniform(97.0, 100)
conn = httplib.HTTPConnection("cvandyke.ddns.net")
# query = urllib.quote(query)
conn.request("GET", "loadData?user_id={0}&heartRate={1}&bloodOxygen={2}&temp={3}".format(3, heart, oxygen, temp))
response = conn.getresponse()
jsonStr = response.read()
conn.close()
print jsonStr
# ser.close()
