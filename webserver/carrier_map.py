

class CarrierMap():
	"""Maps email address to carrier name and vice versa"""
	def __init__(self):
		self.carriers = ["Verizon", "AT&T", "Sprint", "T-Mobile", "U.S. Cellular"]
		self.emails = ["vtext.com", "txt.att.net", "messaging.sprintpcs.com", "tmomail.net", "email.uscc.net"]
		self.carrierToEmail = {self.carriers[0] : self.emails[0],
									self.carriers[1] : self.emails[1],
									self.carriers[2] : self.emails[2],
									self.carriers[3] : self.emails[3],
									self.carriers[4] : self.emails[4]}
		self.emailToCarrier = {self.emails[0] : self.carriers[0],
									self.emails[1] : self.carriers[1],
									self.emails[2] : self.carriers[2],
									self.emails[3] : self.carriers[3],
									self.emails[4] : self.carriers[4]}

	def getEmailFromCarrier(self, carrier):
		if carrier in self.carrierToEmail.keys():
			return self.carrierToEmail[carrier]
		return None

	def getCarrierFromEmail(self, email):
		if email in self.emailToCarrier.keys():
			return self.emailToCarrier[email]
		return None

	def getEmails(self):
		return self.emails

	def getCarriers(self):
		return self.carriers

	def getInputList(self):
		choices = []
		for i in range(len(self.emails)):
			choices.append((self.emails[i], self.carriers[i]))
		return choices