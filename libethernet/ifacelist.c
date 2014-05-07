/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

#include "ifacelist.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <ifaddrs.h>
#include <net/if.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

struct ifaddrs *iface_data;
char **iface_list = NULL;
int iface_count = 0;

int build_iface_list()
{
	int i, count = 0;
	struct ifaddrs *initial, *start_copied = NULL, *current, *alloc, *previous = NULL;
	char **list = NULL;
	
	if (getifaddrs(&initial) == -1)
		return -1;
	
	for (current = initial; current != NULL; current = current->ifa_next)
	{
		if (!(current->ifa_flags & IFF_UP) || (current->ifa_addr->sa_family != AF_LINK))
			continue;
		
		if ((alloc = malloc(sizeof(struct ifaddrs))) == NULL)
		{
			iface_count = count;
			iface_list = list;
			iface_data = start_copied;
			return -2;
		}
		
		memcpy(alloc, current, sizeof(struct ifaddrs));
		
		if (start_copied == NULL)
			start_copied = alloc;
		
		if (previous != NULL)
			previous->ifa_next = alloc;
		
		previous = alloc;
		count++;
	}
	
	// The last interface in the linked list can't have an ifa_next pointer
	alloc->ifa_next = NULL;
	
	if (count == 0)
		return 1;
	
	int mem = (count + 1) * sizeof(*list);
	list = malloc(mem);
	
	for (i = 0, current = start_copied; i < count; i++, current = current->ifa_next)
	{
		int len = strlen(current->ifa_name);
		list[i] = malloc(len + 1);
		strcpy(list[i], current->ifa_name);
	}
	
	// NULL terminate the array of arrays
	list[count] = NULL;
	
	iface_count = count;
	iface_list = list;
	iface_data = start_copied;
	freeifaddrs(initial);
	return 0;
}

void free_iface_data(struct ifaddrs *list)
{
	struct ifaddrs *ptr;
	struct ifaddrs *start;
	
	if (list == NULL)
		return;
	
	ptr = list;
	
	while (ptr != NULL)
	{
		start = ptr;
		ptr = ptr->ifa_next;
		free(start);
	}
	
	list = NULL;
}

void free_iface_list(char **list, int count)
{
	if (list == NULL || count == 0)
		return;
	
	for (int i = 0; i < count; i++)
		free(list[i]);
	
	free(list);
}

void release()
{
	free_iface_data(iface_data);
	free_iface_list(iface_list, iface_count);
}

char **getInterfaceList()
{
	int ret = build_iface_list();
	if (ret == 0)
		return iface_list;
	
#ifdef DEBUG
	printf("build_iface_list() returned %d\n", ret);
#endif
	
	return NULL;
}
