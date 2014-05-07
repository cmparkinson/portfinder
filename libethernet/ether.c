/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

#include "ether.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <sys/uio.h>

#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/bpf.h>
#include <net/if.h>

#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <ctype.h>

#include <errno.h>
#include <unistd.h>

#include <stdlib.h>

int global_bpf = -1;

int openInterface(const char* iface)
{
	int bpf = bpf_open(iface);
	if (bpf == -1)
		return -1;
	
	global_bpf = bpf;
	return 0;
}

int generateFrame(char unsigned *src_addr, char unsigned *dest_addr, unsigned short int type)
{
	if (global_bpf == -1)
		return -1;
	
	unsigned char payload[0];
	
	int bytes = send_frame(global_bpf, src_addr, dest_addr, type, payload, sizeof(payload));
	if (bytes > 0)
		return 0;
	else
		return 1;
}

int closeInterface()
{
	if (global_bpf == -1)
		return 0;
	int ret = close(global_bpf);
	global_bpf = -1;
	return ret;
}

int testInterface(const char* iface)
{
	int bpf = bpf_open(iface);
	if (bpf != -1)
	{
		close(bpf);
		return 0;
	}
	
	return 1;
}

int bpf_open(const char* iface)
{
	int bpf = -1;
	char buf[11] = { 0 };
	
	for (int i = 0; i < 99; i++)
	{
		sprintf(buf, "/dev/bpf%i", i);
		bpf = open(buf, O_RDWR);
		
		if (bpf != -1)
			break;
	}
	
	if (bpf == -1)
	{
		// Could not open BPF device
		return -1;
	}
	
	struct ifreq bound_if;
	strcpy(bound_if.ifr_name, iface);
	if (ioctl(bpf, BIOCSETIF, &bound_if) < 0)
	{
		// BIOCSETIF failed
		return -2;
	}

	int buf_len = 1;
	
	if (ioctl(bpf, BIOCSHDRCMPLT, &buf_len) == -1)
	{
		// Could not change header completion value
		return -3;
	}	

	return bpf;
}

int bpf_close(int bpf)
{
	return close(bpf);
}

int send_frame(int bpf,
			   const unsigned char* src_addr,
			   const unsigned char* dest_addr,
			   unsigned short int type,
			   const unsigned char* payload,
			   unsigned int payload_size)
{
	if (bpf == -1)
	{
		fprintf(stderr, "ERROR: Invalid BPF device: %i\n", bpf);
		return -1;
	}
	
	struct ethernet_frame frame;
	
	memcpy(frame.dest_addr, dest_addr, sizeof(frame.dest_addr));
	memcpy(frame.src_addr, src_addr, sizeof(frame.src_addr));
	frame.type = type;

	unsigned char* buf = malloc(sizeof(frame) + payload_size);
	memcpy(buf, &frame, sizeof(frame));
	memcpy(buf + sizeof(frame), payload, payload_size);
	int sent = write(bpf, buf, sizeof(frame) + payload_size);
	free(buf);
	return sent;
}

void print_header(struct ethernet_frame frame)
{
	printf("Src: %x:%x:%x:%x:%x:%x Dst: %x:%x:%x:%x:%x:%x Type: %x\n",
			frame.src_addr[0],
			frame.src_addr[1],
			frame.src_addr[2],
			frame.src_addr[3],
			frame.src_addr[4],
			frame.src_addr[5],
			frame.dest_addr[0],
			frame.dest_addr[1],
			frame.dest_addr[2],
			frame.dest_addr[3],
			frame.dest_addr[4],
			frame.dest_addr[5],
			frame.type);
}
