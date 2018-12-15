#include "TcpClientSocket.h"
using namespace std;

TcpClientSocket::TcpClientSocket(int port): destPort(port), sd(NULL_SD) {
    this->sd = socket(AF_INET, SOCK_STREAM, 0);
}

TcpClientSocket::~TcpClientSocket() {
    // close the socket being used
    if (this->sd != NULL_SD) {
        close(sd);
    }
}

vector<string> TcpClientSocket::recvFrom() {
    char message[50];
    int size = 50;
    int nRead = read(this->sd, message, size);
    string input(message);
    vector<string> messageBuffer;
    this->parseMessage(messageBuffer, input);
    if (messageBuffer.size() == 0) {
        cerr << "Empty message" << endl;
    }
    return messageBuffer;
}


// for client socket
int TcpClientSocket::connectTo(char *ipName) {
    struct hostent *host = gethostbyname(ipName);
    if (host == nullptr) {
        cerr << "Cannot find host name" << endl;
        return false;
    }

    bzero((char *)& this->destAddr, sizeof(this->destAddr));
    this->destAddr.sin_family = AF_INET;
    this->destAddr.sin_addr.s_addr =
        inet_addr(inet_ntoa(*(struct in_addr*)*host->h_addr_list));
    this->destAddr.sin_port = htons(this->destPort);
    return connect(this->sd, (sockaddr *) &this->destAddr, sizeof(this->destAddr));
}

int TcpClientSocket::sendTo(string message) {
    // convert input string into char[]
    int size = message.size();
    char sendingMessage[size + 1];
    strcpy(sendingMessage, message.c_str());

    return write(this->sd, sendingMessage, size + 1);
}

void TcpClientSocket::parseMessage(vector<string> &result, string input) {
    string temp = "";
    int size = input.size();
    for (int i = 0; i < size; i++) {
        char current = input[i];
        if (current != ' ') {
            temp += current;
        } else {
            result.push_back(temp);
            temp = "";
        }
    }
    result.push_back(temp);
}
