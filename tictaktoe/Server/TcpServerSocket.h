#ifndef TCP_SERVER_SOCKET_H
#define TCP_SERVER_SOCKET_H

#include <iostream>
#include <string>

extern "C" {
    #include <sys/types.h>  // for sockets
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <arpa/inet.h>

    #include <netdb.h>      // for gethostbyname()
    #include <unistd.h>     // for close()
    #include <string.h>     // for bzero()

    #include <sys/poll.h>   // for poll()
}

const int NULL_SD = -1;

class TcpServerSocket {
public:
    TcpServerSocket(int port);
    ~TcpServerSocket();

    bool setDestAddress(char *ipName);      // set the IP addr given an IP name
    int acceptFrom();                           // accept and return socket number
    int sendTo(int sd, std::string message);    // send a message of size
    int recvFrom(int sd, char *message, int size);  // receive a message of size

private:
    int port;                       // this TCP port
    int sd;                         // this TCP socket descriptor
    struct sockaddr_in myAddr;      // my socket address for internet
    struct sockaddr srcAddr;        // source socket address for internet
};


#endif