from flask.ext.wtf import Form
from wtforms import TextField, PasswordField, validators
from SqlConnect import MySqlConnection
import hashlib
import random
import string

SECRET = 'MyLittleSecret'

class LoginForm(Form):
    username = TextField('Username', [validators.Required()])
    password = PasswordField('Password', [validators.Required()])

    def validate(self, db):
        rv = Form.validate(self)
        if not rv:
            return False
        cmd = "SELECT passwordhash FROM user WHERE username=\'{0}\';".format(self.username.data)
        result = db.execute(cmd)
        if result == None:
            return False
        info = result.fetchone()
        if info == None:
            return False
        return valid_pw(self.username.data, self.password.data, info[0])

class RegisterForm(Form):
    username = TextField('Username', [validators.Required()])
    password = PasswordField('Password', [
        validators.Required(),
        validators.EqualTo('confirm', message='Passwords must match')
    ])
    confirm = PasswordField('Repeat Password')
    email = TextField('E-mail', [
        validators.Required(),
        validators.Email(message="Invalid Email")])
    phone = TextField('Phone', [validators.Required()])

    def validate(self, db):
        rv = Form.validate(self)
        if not rv:
            return False
        else:
            cmd = "SELECT username FROM user WHERE username=\'" + self.username.data + "\';"
            cur = db.execute(cmd)
            if cur == None:
                passHash = make_pw_hash(self.username.data, self.password.data)
                cmd = """INSERT INTO user (username, passwordhash, email, phone) VALUES ('{0}','{1}','{2}','{3}')""" \
                    .format(self.username.data, passHash, self.email.data, self.phone.data)
                result = db.execute(cmd)
                db.commit()
            else:
                self.username.errors.append('User name is already taken. Please select another.')
                return False
        return True;

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