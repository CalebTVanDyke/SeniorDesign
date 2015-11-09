import httplib
import json
import urllib

class DbRequest:

	def query(self, query):
		conn = httplib.HTTPConnection("databasesupport.arlenburroughs.com")
		query = urllib.quote(query)
		conn.request("GET", "/492_db_query.php?query=" + query)
		response = conn.getresponse()
		jsonStr = response.read()
		conn.close()
		print jsonStr
		if (jsonStr != None and jsonStr != ""):
			return json.loads(jsonStr)
		return None
