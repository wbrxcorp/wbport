#!/usr/bin/python2.7
import sys,os,hashlib
import MySQLdb

DB_HOST="localhost"
DB_NAME="wbport"
DB_USER="wbport"
DB_PASSWORD=""

def mkhash(password, salt):
    return hashlib.sha256("%s%s" % (salt, password) ).hexdigest()

def check_password(cur, username, password):
    cur.execute("select password from users,servers where users.id=servers.user_id and fqdn=%s", (username,))
    row = cur.fetchone()
    if row is None: return False
    #else
    (salt,hash) = row[0].split('$')
    return hash == mkhash(password, salt)

conn = MySQLdb.connect(host=DB_HOST,db=DB_NAME,user=DB_USER,passwd=DB_PASSWORD)
cur = conn.cursor()
success = check_password(cur, os.environ.get("username"), os.environ.get("password"))
cur.close()
conn.close()

sys.exit(0 if success else 1)
