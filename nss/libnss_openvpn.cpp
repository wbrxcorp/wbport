/*
 * g++ -lboost_system -lboost_iostreams -lboost_regex -lpthread -fPIC -g -shared -Wl,-soname,libnss_openvpn.so.2 -o /lib/libnss_openvpn.so.2 libnss_openvpn.cpp
 * debian run deps: apt-get install libboost-system1.49.0 libboost-regex1.49.0
 * build deps: apt-get install apt-get install libboost-all-dev
*/

#include <string.h>
#include <nss.h>
#include <iostream>
#include <vector>
#include <map>
#include <boost/asio.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/regex.hpp>

static uint32_t lookup(const std::string& hostname)
{
	if (!boost::regex_match(hostname, boost::regex(".+\\.wbport$"))) {
		throw NSS_STATUS_NOTFOUND;
	}

	boost::asio::ip::tcp::iostream s("localhost", "openvpn");
	if (!s) {
		throw NSS_STATUS_TRYAGAIN;
	}
	s << "status 3" << std::endl;
	s.flush();
	std::string line;
	std::map<std::string,std::string> clients;
	std::map<std::string,std::string> rev;
	while (std::getline(s, line)) {
		if (line[0] == '>') continue;
		if (line == "END\r") break;
		//std::cout << '"' << line << '"' << std::endl;
		std::vector<std::string> splitted;
		boost::algorithm::split(splitted, line, boost::is_any_of("\t"));
		if (splitted[0] == "CLIENT_LIST" && splitted[1] != "UNDEF") {
			clients[splitted[1]] = splitted[3];
			rev[splitted[3]] = splitted[1];
		}
	}
	s << "quit" << std::endl;
	s.flush();

	std::string hn = boost::regex_replace(std::string(hostname), boost::regex("\\.wbport$"), std::string(""));
	std::map<std::string,std::string>::iterator  it = clients.find(hn);
	if (it == clients.end()) {
		throw NSS_STATUS_NOTFOUND;
	}

	return inet_addr((*it).second.c_str());
}

#define ALIGN(a) (((a+sizeof(void*)-1)/sizeof(void*))*sizeof(void*))

static enum nss_status fill_in_hostent(
				const char *hn,
                struct hostent *result,
                char *buffer, size_t buflen,
                int *errnop, int *h_errnop,
				int32_t *ttlp,
                char **canonp,
				uint32_t addr) {

	size_t alen = 4;

	size_t l = strlen(hn);
	size_t ms = ALIGN(l+1)+sizeof(char*)+ALIGN(alen)+sizeof(char*)*2;
	if (buflen < ms) {
		*errnop = ENOMEM;
		*h_errnop = NO_RECOVERY;
		return NSS_STATUS_TRYAGAIN;
	}

	/* First, fill in hostname */
	char* r_name = buffer;
	memcpy(r_name, hn, l+1);
	size_t idx = ALIGN(l+1);

	/* Second, create aliases array */
	char* r_aliases = buffer + idx;
	*(char**) r_aliases = NULL;
	idx += sizeof(char*);

	/* Third, add address */
	char* r_addr = buffer + idx;
	*(uint32_t*) r_addr = addr;
	idx += ALIGN(alen);

	/* Fourth, add address pointer array */
	char* r_addr_list = buffer + idx;
	((char**) r_addr_list)[0] = r_addr;
	((char**) r_addr_list)[1] = NULL;
	idx += sizeof(char*)*2;

	/* Verify the size matches */
	assert(idx == ms);

	result->h_name = r_name;
	result->h_aliases = (char**) r_aliases;
	result->h_addrtype = AF_INET;
	result->h_length = alen;
	result->h_addr_list = (char**) r_addr_list;

	if (ttlp) *ttlp = 0;
	if (canonp) *canonp = r_name;

	return NSS_STATUS_SUCCESS;
}

extern "C" {
enum nss_status _nss_openvpn_gethostbyname3_r(
                const char *name,
                int af,
                struct hostent *host,
                char *buffer, size_t buflen,
                int *errnop, int *h_errnop,
                int32_t *ttlp,
                char **canonp) {

	//std::cout << "_nss_openvpn_gethostbyname3_r" << std::endl;

	if (af == AF_UNSPEC) af = AF_INET;

	if (af != AF_INET) {
		*errnop = EAFNOSUPPORT;
		*h_errnop = NO_DATA;
		return NSS_STATUS_UNAVAIL;
	}

	try {
		uint32_t addr = lookup(name);
		return fill_in_hostent(name, host, buffer, buflen, errnop, h_errnop, ttlp, canonp, addr);
	}
	catch (enum nss_status& st) {
		if (st == NSS_STATUS_NOTFOUND) {
			*errnop = ENOENT;
			*h_errnop = HOST_NOT_FOUND;
		} else if (st == NSS_STATUS_TRYAGAIN) {
			*errnop = EINVAL;
			*h_errnop = NO_RECOVERY;
		} else {
			*errnop = EINVAL;
			*h_errnop = NO_RECOVERY;
		}
		return st;
	}
}

enum nss_status _nss_openvpn_gethostbyname2_r(
                const char *name,
                 int af,
                 struct hostent *host,
                char *buffer, size_t buflen,
                 int *errnop, int *h_errnop) {
 
         return _nss_openvpn_gethostbyname3_r(
                         name,
                         af,
                         host,
                         buffer, buflen,
                         errnop, h_errnop,
                         NULL,
                         NULL);
}

enum nss_status _nss_openvpn_gethostbyname_r(
	const char *name,
	struct hostent* host,
	char *buffer, size_t buflen,
	int *errnop, int *h_errnop
	) {

	std::cout << "_nss_openvpn_gethostbyname_r" << std::endl;

	try {
		uint32_t addr = lookup(name);
		return fill_in_hostent(name, host, buffer, buflen, errnop, h_errnop, NULL, NULL, addr);
	}
	catch (enum nss_status& st) {
		if (st == NSS_STATUS_NOTFOUND) {
			*errnop = ENOENT;
			*h_errnop = HOST_NOT_FOUND;
		} else if (st == NSS_STATUS_TRYAGAIN) {
			*errnop = EINVAL;
			*h_errnop = NO_RECOVERY;
		} else {
			*errnop = EINVAL;
			*h_errnop = NO_RECOVERY;
		}
		return st;
	}

}
} // extern "C"

