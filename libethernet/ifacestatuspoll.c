/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

#include "ifacestatuspoll.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <ifaddrs.h>
#include <net/if.h>
#include <string.h>

int poll(const char* name)
{
	struct ifaddrs *initial, *current, *found = NULL;
	
	if (getifaddrs(&initial) == -1)
		return -1;
	
	for (current = initial; current != NULL; current = current->ifa_next)
	{
		if (strcmp(name, current->ifa_name) != 0)
			continue;
		
		if (!(current->ifa_flags & IFF_UP))
			continue;
		
		if (current->ifa_addr->sa_family == AF_LINK && found == NULL)
		{
			found = current;
			continue;
		}
		
		if (current->ifa_addr->sa_family == AF_INET)
		{
			found = current;
			break;
		}
	}
	
	if (found == NULL)
		return -2;
	
	int ret = found->ifa_addr->sa_family == AF_INET ? 0 : 1;
	freeifaddrs(initial);
	return ret;
}

