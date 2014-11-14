#!/usr/bin/python
import sys,os,json,urllib,urllib2

username = os.environ.get("username")
password = os.environ.get("password")

params = {'fqdn':username,'password':password}

rst = json.load(urllib2.urlopen("http://localhost:8080/api/vpn-auth?%s" % urllib.urlencode(params)))

sys.exit(0 if rst[0] else 1)
