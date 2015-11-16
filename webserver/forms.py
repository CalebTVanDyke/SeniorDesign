from flask.ext.wtf import Form
from wtforms import TextField, PasswordField, validators, SelectField
from user_utils import UserUtils
from flask import session
from carrier_map import CarrierMap


cmap = CarrierMap()

class LoginForm(Form):
    username = TextField('Username', [validators.Required()])
    password = PasswordField('Password', [validators.Required()])

    def validate(self, db):
        rv = Form.validate(self)
        if not rv:
            return False
        return UserUtils.login(db, self.username.data, self.password.data)

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
            if UserUtils.register(db, self.username.data, self.password.data, self.email.data, self.phone.data):
                return True
            else:
                self.username.errors.append('User name is already taken. Please select another.')
                return False
        return True;

class ChangePasswordForm(Form):
    old_password = PasswordField('Current Password', [validators.Required()])
    new_password = PasswordField('New Password', [
        validators.Required(),
        validators.EqualTo('confirm', message='Passwords must match')
    ])
    confirm = PasswordField('Repeat New Password')

    def validate(self, db):
        rv = Form.validate(self)
        if not rv:
            return False
        else:
            if UserUtils.change_password(db, session["user_id"], self.old_password.data, self.new_password.data):
                return True
            else:
                self.old_password.errors.append('Current password was not correct.')
                return False

class GeneralSettingForm(Form):
    primary_email = TextField('Primary E-mail', [
        validators.Required(),
        validators.Email(message="Invalid Email")])
    primary_phone = TextField('Primary Phone', [validators.Required()])

    def validate(self, db):
        rv = Form.validate(self)
        if not rv:
            return False
        else:
            return True

class AlertSettingsForm(Form):
    email = TextField('E-mail', [validators.optional(), validators.Email(message="Invalid Email")])
    phone = TextField('Phone', [validators.optional()])
    carrier = SelectField('Carrier', choices=cmap.getInputList())

    def validate(self):
        rv = Form.validate(self)
        if not rv:
            return False
        if self.email.data == "" and self.phone.data == "":
            self.email.errors.append('Please enter either an email or a phone number and carrier')
            return False
        return True
        