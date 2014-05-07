/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

struct ethernet_frame
{
	unsigned char dest_addr[6];
	unsigned char src_addr[6];
	unsigned short int type;
};

int bpf_open(const char* iface);
int bpf_close(int bpf);
int send_frame(int bpf,
			   const unsigned char* src_addr,
			   const unsigned char* dest_addr,
			   unsigned short int type,
			   const unsigned char* payload,
			   unsigned int payload_size);
void print_header(struct ethernet_frame frame);
int openInterface(const char* iface);
int closeInterface();
int generateFrame(char unsigned *src_addr,
				  char unsigned *dest_addr,
				  unsigned short int type);


