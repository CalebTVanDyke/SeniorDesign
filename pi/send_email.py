import smtplib

fromaddr = 'dec1505@arlenburroughs.com'
toaddrs  = 'ctvandyke24@gmail.com'
msg = 'There was a terrible error that occured and I wanted you to know!'


# Credentials (if needed)
username = 'dec1505@arlenburroughs.com'
password = 'verynice!'

# The actual mail send
print "here"
server = smtplib.SMTP('gator3110.hostgator.com:993')
print "here2"
server.starttls()
print "here3"
server.login(username,password)
print "here4"
server.sendmail(fromaddr, toaddrs, msg)
print "here5"
server.quit()