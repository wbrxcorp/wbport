all: libnss_openvpn.so

libnss_openvpn.so: libnss_openvpn.cpp
	g++ -lboost_system -lboost_iostreams -lboost_regex -lpthread -fPIC -g -shared -Wl,-soname,libnss_openvpn.so.2 -o $@ $<

install: all
	cp -av libnss_openvpn.so /lib/libnss_openvpn.so.2

clean:
	rm libnss_openvpn.so

