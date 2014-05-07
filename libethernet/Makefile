CC=gcc
CFLAGS=-c -Wall

all: library

library: ether.o ifacestatuspoll.o ifacelist.o
	$(CC) -dynamiclib -flat_namespace ether.o ifacestatuspoll.o ifacelist.o -o libethernet.dylib

ether.o : ether.c ether.h
	$(CC) $(CFLAGS) ether.c

ifacestatuspoll.o : ifacestatuspoll.c ifacestatuspoll.h
	$(CC) $(CFLAGS) ifacestatuspoll.c

ifacelist.o : ifacelist.c ifacelist.h
	$(CC) $(CFLAGS) ifacelist.c

clean:
	rm -rf *.o libethernet.dylib
