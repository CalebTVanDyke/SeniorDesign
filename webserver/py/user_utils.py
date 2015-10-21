from db_conn import DbRequest
import hashlib
import random
import string
from flask import session

class UserUtils:
	
	@staticmethod
	def login(db, username, password):
		cmd = "SELECT password_hash, id FROM `users` WHERE username=\'{0}\';".format(username)
		result = db.query(cmd)
		if result == None:
			return False
		if valid_pw(username, password, result[0]['password_hash']):
			session["user_id"] = result[0]['id']
			session["username"] = username
			return True
		return False

	@staticmethod
	def register(db, username, password, email, phone):
		cmd = "SELECT username FROM `users` WHERE username=\'" + username + "\';"
		cur = db.query(cmd)
		if cur == None:
			passHash = make_pw_hash(username, password)
			cmd = """INSERT INTO `users` (`username`, `password_hash`, `email`, `phone`) VALUES ('{0}','{1}','{2}','{3}')""" \
				.format(username, passHash, email, phone)
			result = db.query(cmd)
			return True
		else:
			return False

def hashUser(user_id):
    return user_id + '|' + hmac.new(SECRET, user_id).hexdigest()

def make_pw_hash(name, pw, salt=None):
    if not salt:
        salt = make_salt()
    h = hashlib.sha256(name + pw + salt).hexdigest()
    return '%s,%s' % (h, salt)

def valid_pw(name, pw, h):
    salt = h.split(',')[1]
    hashc = make_pw_hash(name, pw, salt)
    return hashc == h

def make_salt():
    return ''.join(random.choice(string.letters) for x in xrange(5))

