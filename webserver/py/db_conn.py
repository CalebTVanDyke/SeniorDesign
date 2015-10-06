import httplib
import json
import urllib

class DbRequest:
	def __init__(self):
		self.conn = httplib.HTTPConnection("databasesupport.arlenburroughs.com")

	def query(self, query):
		print query
		query = urllib.quote(query)
		self.conn.request("GET", "/492_db_query.php?query=" + query)
		response = self.conn.getresponse()
		jsonStr = response.read()
		if (jsonStr != None and jsonStr != ""):
			return json.loads(jsonStr)
		return None
