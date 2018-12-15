#include "TcpClientSocket.h"
#include <vector>
#include <string>
using namespace std;

bool registerID(string id, char *destAddr);
bool login(string id, char *destAddr);
bool listRoom(string id, char *destAddr);
bool showRank(string id, char *destAddr);
bool deregister(string id, char *destAddr);
void printRest(vector<string> list, int start);


const int SERVER_PORT = 4789;

int main(int argc, char *argv[]) {
    if (argc != 2) {
        cerr << "wrong argument format: [program] [server Ip]" << endl;
        exit(EXIT_FAILURE);
    }

    // make connection to the server
    char *destAddr = argv[1];

    string id = "Anonymous";
    bool loggedIn = false;
    int action = 0;

    cout << "Welcome to Sutda1.0!" << endl;

    while (action != 1 && action != 2) {
        // log in or register
        cout << "[1]: Register" << endl;
        cout << "[2]: Log In" << endl;
        cout << "press the number for your action: ";
        cin >> action;
    }

    if (action == 1) {
        bool registered = false;
        string tempId = "";
        while (!registered) {
            cout << "<Registration>" << endl;
            cout << "Enter ID: ";
            cin >> tempId;
            if (tempId.size() > 20) {
                cout << "Input id is too long, please enter your id less than 20 characters" << endl;
            }
            registered = registerID(tempId, destAddr);
        }

        cout << "Registered as " << tempId << endl;
        action = 2;
    }
    if (action == 2) { // action == 2
        string tempId = "";
        while (!loggedIn) {
            cout << "<Login>" << endl;
            cout << "Enter your ID: ";
            cin >> tempId;

            loggedIn = login(tempId, destAddr);
        }
        cout << "Logged in as " << tempId << endl;
        id = tempId;
    }

    // logged in, then
    // deregister id?
    // room list, create room, ranking?

    action = 0;

    while (action != 1 && action != 2 && action != 3 && action != 4) {
        // log in or register
        cout << "[1]: List rooms" << endl;
        cout << "[2]: Create a room" << endl;
        cout << "[3]: Show rankings" << endl;
        cout << "[4]: Deregister" << endl;
        cout << "press the number for your action: ";
        cin >> action;
    }

    if (action == 1) {
        listRoom(id, destAddr);
    } else if (action == 2) {

    } else if (action == 3) {
        showRank(id, destAddr);
    } else { // action == 4
        deregister(id, destAddr);
    }

    // string message = "Hello";
    // client.sendTo(message);

    // char temp[10];
    // vector<string> answer = client.recvFrom();
    // cout << answer[0] << endl;


    return 0;
}

bool registerID(string id, char *destAddr) {
    TcpClientSocket client(SERVER_PORT);
    client.connectTo(destAddr);
    string message = "REGISTER " + id;
    client.sendTo(message);
    vector<string> reply = client.recvFrom();
    if (reply[0] == "REGISTER" && reply[1] == "ACK" && reply[2] == id) {
        return true;
    }
    printRest(reply, 2);
    return false;
}

bool login(string id, char *destAddr) {
    TcpClientSocket client(SERVER_PORT);
    client.connectTo(destAddr);
    string message = "LOGIN " + id;
    client.sendTo(message);
    vector<string> reply = client.recvFrom();
    int size = reply.size();
    for (int i = 0; i < size; i++) {
        cout << reply[i] << " ";
    }
    cout << endl;
    if (reply[0] == "LOGIN" && reply[1] == "ACK" && reply[2] == id) {
        return true;
    }
    printRest(reply, 3);
    return false;
}

bool listRoom(string id, char *destAddr) {
    TcpClientSocket client(SERVER_PORT);
    client.connectTo(destAddr);
    string message = "LIST " + id;
    client.sendTo(message);
    vector<string> reply = client.recvFrom();
    cout << "Current list of room to join: " << endl;
    printRest(reply, 4);
    return true;
}

bool showRank(string id, char *destAddr) {
    TcpClientSocket client(SERVER_PORT);
    client.connectTo(destAddr);
    string message = "RANKREQ " + id;
    client.sendTo(message);
    vector<string> reply = client.recvFrom();
    if (reply.size() < 5) {
        cout << "No ranks available" << endl;
    } else {
        cout << "Ranking: " << endl;
        printRest(reply, 4);
    }
    return true;
}

bool deregister(string id, char *destAddr) {
    TcpClientSocket client(SERVER_PORT);
    client.connectTo(destAddr);
    string message = "DEREGISTER " + id;
    client.sendTo(message);
    vector<string> reply = client.recvFrom();
    if (reply[0] == "DEREGISTER" && reply[1] == "ACK" && reply[2] == id) {
        return true;
    }
    printRest(reply, 3);
    return false;
}

void printRest(vector<string> list, int start) {
    string current = list[0];
    int i = start;
    cout << "before " << endl;
    while (current != "\r\n") {
        current = list[i];
        cout << current + " ";
        i++;
    }
}