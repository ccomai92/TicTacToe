#include "TcpServerSocket.h"
#include "AdminManager.h"
#include "GameManager.h"
#include <iostream>
#include <string>
#include <vector>
using namespace std;

const int SERVER_PORT = 4789;
void interpretMessage(TcpServerSocket *server, AdminManager *admin,
                    GameManager *gameMan, int sd, string input);
void parseMessage(vector<string> &result, string input);
void sendRegisterACK(TcpServerSocket *server, int sd, string id);
void sendRegisterNAK(TcpServerSocket *server, int sd, string id, string reason);
void sendLoginNAK(TcpServerSocket *server, int sd, string id, string reason);
void sendLoginACK(TcpServerSocket *server, int sd, string id);
void sendListACK(TcpServerSocket *server, int sd, string id, string answer);
void sendDeregisterACK(TcpServerSocket *server, int sd, string id);
void sendDeregisterNAK(TcpServerSocket *server, int sd, string id, string reason);
void sendRankReq(TcpServerSocket *server, int sd, string id, string answer);


int main() {
    // Load Server, Admin, and Game Manager
    TcpServerSocket *server = new TcpServerSocket(SERVER_PORT);
    AdminManager *admin = new AdminManager();
    GameManager *gameMan = new GameManager();

    while (true) {
        char message[50];
        int newSd = server->acceptFrom();
        cerr << "recevied connection" << endl;
        server->recvFrom(newSd, message, 50);
        string answer(message);
        cout << answer << endl;
        interpretMessage(server, admin, gameMan, newSd, answer);
        //server->sendTo(newSd, message);
        // close(newSd);
    }

    return 0;
}

void interpretMessage(TcpServerSocket *server, AdminManager *admin,
                    GameManager *gameMan, int sd, string input) {
    //parsing input message with ' '
    vector<string> message;
    parseMessage(message, input);
    if (message.size() == 0) {
        cerr << "Empty message" << endl;
        return;
    }

    string request = message[0];
    string id = message[1];
    string reason;
    if (request == "REGISTER") {
        if (admin->registerUser(id, reason)) {
            sendRegisterACK(server, sd, id);
        } else {
            sendRegisterNAK(server, sd, id, reason);
        }
    } else if (request == "LOGIN") {
        if (admin->login(id, reason)) {
            sendLoginACK(server, sd, id);
        } else {
            sendLoginNAK(server, sd, id, reason);
        }

    } else if (request == "LIST") {
        string answer = admin->getRoomList();
        sendListACK(server, sd, id, answer);
    } else if (request == "CREATE") {

    } else if (request == "JOIN") {

    } else if (request == "EXIT") {

    } else if (request == "DEREGISTER") {
        if (admin->deregisterUser(id, reason)) {
            sendDeregisterACK(server, sd, id);
        } else {
            sendDeregisterNAK(server, sd, id, reason);
        }
    } else if (request == "RANKREQ") {
        string answer = admin->getRank();
        sendRankReq(server, sd, id, answer);
    }
}

void parseMessage(vector<string> &result, string input) {
    //parsing input message with ' '
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

void sendRegisterACK(TcpServerSocket *server, int sd, string id) {
    // form ack message
    string ackForm = "REGISTER ACK " + id + " \r\n";
    server->sendTo(sd, ackForm);

    // close socket
    close(sd);
}

void sendRegisterNAK(TcpServerSocket *server, int sd, string id, string reason) {
    // form ack message
    string nakForm = "REGISTER NAK " + id + " " + reason + " \r\n";
    server->sendTo(sd, nakForm);

    // close socket
    close(sd);
}

void sendLoginACK(TcpServerSocket *server, int sd, string id) {
    string ackForm = "LOGIN ACK " + id + " \r\n";
    server->sendTo(sd, ackForm);
    close(sd);
}

void sendLoginNAK(TcpServerSocket *server, int sd, string id, string reason) {
    string nakForm = "LOGIN NAK " + id + " " + reason + " \r\n";
    server->sendTo(sd, nakForm);
    close(sd);
}

void sendListACK(TcpServerSocket *server, int sd, string id, string answer) {
    string ackForm = "LIST ACK " + id + " \n";
    ackForm += answer;
    ackForm += " \r\n";

    server->sendTo(sd, ackForm);

    close(sd);
}

void sendDeregisterACK(TcpServerSocket *server, int sd, string id) {
    string ackForm = "DEREGISTER ACK " + id + " \r\n";
    server->sendTo(sd, ackForm);
    close(sd);
}

void sendDeregisterNAK(TcpServerSocket *server, int sd, string id, string reason) {
    string nakForm = "DEREGISTER NAK " + id + " " + reason + " \r\n";
    server->sendTo(sd, nakForm);
    close(sd);
}

void sendRankReq(TcpServerSocket *server, int sd, string id, string answer) {
    string ackForm = "RANKREQ ACK " + id + "\n";
    ackForm += answer;
    ackForm += " \r\n";
    server->sendTo(sd, ackForm);
    close(sd);
}