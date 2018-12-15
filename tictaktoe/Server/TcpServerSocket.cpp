#include "TcpServerSocket.h"
using namespace std;

TcpServerSocket::TcpServerSocket(int port): port(port), sd(NULL_SD) {
    this->sd = socket(AF_INET, SOCK_STREAM, 0);
    if (this->sd < 0) {
        cerr << "Cannot open a TCP socket" << endl;
    }

    // Bind local address
    bzero ((char*) &this->myAddr, sizeof(this->myAddr));        // zero-initilaize myAddr
    this->myAddr.sin_family = AF_INET;                    // use address family internet
    this->myAddr.sin_addr.s_addr = htonl(INADDR_ANY);     // receive from any address
    this->myAddr.sin_port = htons(this->port);                  // Set my Socket port
    if (bind(this->sd, (sockaddr*) &this->myAddr, sizeof(this->myAddr)) < 0) {
        cerr << "Cannot bind the local address to the TCP socket" << endl;
    }
    listen(this->sd, 5);
}

TcpServerSocket::~TcpServerSocket() {
    // close the socket being used
    if (this->sd != NULL_SD) {
        close(this->sd);
    }
}

int TcpServerSocket::acceptFrom() {
    socklen_t srcAddrSize = sizeof(this->srcAddr);

    bzero((char *) &this->srcAddr, srcAddrSize);

    int newSd = accept(this->sd, (sockaddr *) &this->srcAddr, &srcAddrSize);
    return newSd;
}

int TcpServerSocket::sendTo(int sd, string message) {
    // convert input string into char[]
    int size = message.size();
    char sendingMessage[size + 1];
    strcpy(sendingMessage, message.c_str());

    return write(sd, sendingMessage, size + 1);
}

int TcpServerSocket::recvFrom(int sd, char *message, int size) {
    return read(sd, message, size);
}


/* for client socket
bool TcpServerSocket::setDestAddress(char *ipName) {
    struct hostnet *host = gethostbyname(ipName);
    if (host == nullptr) {
        cerr << "Cannot find host name" << endl;
        return false;
    }

    bzero((char *)& this->destAddr, sizeof(this->destAddr));
    this->destAddr.sin_family = AF_INET;
    this->destAddr.sin_addr.s_addr =
        inet_addr(inet_ntoa(*(struct in_addr*)*host->h_addr_list));
    this->destAddr.sin_port = htons(port);
    return true;
}

int TcpServerSocket::sendTo(char *message, int size) {
    return sendto(this->sd, message, size, 0, (sockaddr *) &this->destAddr, sizeof(this->destAddr));
}
*/