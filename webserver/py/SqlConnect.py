import mysql.connector

config = {
  'user': 'root',
  'password': '',
  'host': '127.0.0.1',
  'database': 'senior_design',
  'raise_on_warnings': True,
  'port': 3037,
  'unix_socket': '/tmp/mysql.sock'
}

class MySqlConnection:

	_connection = None
	_cursor = None

	def __init__(self):
		self._connection = mysql.connector.connect(**config)
		self._cursor = self._connection.cursor(buffered=True)

	def execute(self, cmd):
		self._cursor.execute(cmd)
		return self._cursor

	def commit(self):
		self._connection.commit()

	def __del__(self):
		self._connection.close()

		



