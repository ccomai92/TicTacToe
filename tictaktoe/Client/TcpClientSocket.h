#ifndef TCP_CLIENT_SOCKET_H
#define TCP_CLIENT_SOCKET_H

#include <iostream>
#include <string>
#include <vector>

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

class TcpClientSocket {
public:
    TcpClientSocket(int port);
    ~TcpClientSocket();

    int connectTo(char *ipName);            // connect to the server
    int sendTo(std::string message);             // send a messsage
    std::vector<std::string> recvFrom();  // receive a message of size

private:
    // int srcPort;                         // this TCP port
    int destPort;
    int sd;                         // this TCP socket descriptor
    struct sockaddr_in myAddr;      // my socket address for internet
    struct sockaddr_in destAddr;    // a destination socket address for internet

    void parseMessage(std::vector<std::string> &result, std::string input);
};


#endif