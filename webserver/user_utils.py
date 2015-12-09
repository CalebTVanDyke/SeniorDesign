from db_conn import DbRequest
import hashlib
import random
import string
from flask import session
from carrier_map import CarrierMap

cmap = CarrierMap()

class User:
	"""All of the info for a user.  receivers is in a coma seperated string"""
	def __init__(self, username, primary_phone, primary_email, receivers, sensitivity):
		self.username = username
		self.sensitivity = sensitivity
		self.primary_phone = primary_phone
		self.primary_email = primary_email
		splitRec = receivers.split(",")
		self.receivers = []
		for rec in splitRec:
			split = rec.split("@")
			self.receivers.append(Receiver(split[0], split[1]))

	def receiver_string(self):
		combined = ""
		for i in range(len(self.receivers)):
			if i == len(self.receivers) - 1:
				combined += self.receivers[i].email
			else:
				combined += self.receivers[i].email + ","
		return combined

	def add_receiver(self, receiverStr):
		split = receiverStr.split("@")
		self.receivers.append(Receiver(split[0], split[1]))

	def delete_receiver(self, receiverStr):
		split = receiverStr.split("@")
		rec = Receiver(split[0], split[1])
		self.receivers.remove(rec)


class Receiver:
	def __init__(self, data, email):
		self.data = data
		self.isPhone = False
		self.email = data + "@" + email
		if cmap.getCarrierFromEmail(email) != None:
			self.isPhone = True
			self.carrier = cmap.getCarrierFromEmail(email)
	def __eq__(self, other):
		return self.email == other.email
		

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
	def register(db, username, password, email, phone, carrierEmail):
		cmd = "SELECT username FROM `users` WHERE username=\'" + username + "\';"
		cur = db.query(cmd)
		if cur == None:
			receivers = email + "," + phone + "@" + carrierEmail
			passHash = make_pw_hash(username, password)
			cmd = """INSERT INTO `users` (`username`, `password_hash`, `email`, `phone`, `receivers`) VALUES ('{0}','{1}','{2}','{3}', '{4}')""" \
				.format(username, passHash, email, phone, receivers)
			result = db.query(cmd)
			return True
		else:
			return False

	@staticmethod
	def change_password(db, user_id, old_password, new_password):
		cmd = "SELECT password_hash, username FROM `users` WHERE id=\'{0}\';".format(user_id)
		result = db.query(cmd)
		if result == None:
			return False
		username = result[0]['username']
		if valid_pw(username, old_password, result[0]['password_hash']):
			pass_hash = make_pw_hash(username, new_password)
			cmd = "UPDATE `users` SET password_hash='{0}'".format(pass_hash)
			result = db.query(cmd)
			return True
		return False

	@staticmethod
	def get_user_info(db, user_id):
		cmd = "SELECT username, phone, email, receivers, sensitivity FROM `users` WHERE id=\'{0}\';".format(user_id)
		result = db.query(cmd)
		if result == None:
			return None
		username = result[0]['username']
		primary_email = result[0]['email']
		primary_phone = result[0]['phone']
		receivers = result[0]['receivers']
		sensitivity = result[0]['sensitivity']
		return User(username, primary_phone, primary_email, receivers, sensitivity)

	@staticmethod
	def save_user_info(db, user_id, primary_phone, primary_email, sensitivity):
		cmd = "UPDATE `users` SET `phone`='{0}', email='{1}', sensitivity='{2}' WHERE id={3};".format(primary_phone, primary_email, sensitivity, user_id)
		print cmd
		db.query(cmd)
		return True

	@staticmethod
	def add_receiver(db, data, user_id):
		user = UserUtils.get_user_info(db, user_id)
		user.add_receiver(data)
		cmd = "UPDATE `users` SET `receivers`='{0}' WHERE id={1};".format(user.receiver_string(), user_id)
		db.query(cmd)
		return True

	@staticmethod
	def delete_receiver(db, data, user_id):
		user = UserUtils.get_user_info(db, user_id)
		user.delete_receiver(data)
		cmd = "UPDATE `users` SET `receivers`='{0}' WHERE id={1};".format(user.receiver_string(), user_id)
		db.query(cmd)
		return True


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

