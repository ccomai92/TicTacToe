#include "AdminManager.h"

using namespace std;

// load saved data (users, ranks, records)
AdminManager::AdminManager() {

}

// save data (users, ranks, records)
AdminManager::~AdminManager() {

}

bool AdminManager::isRegistered(string &id) {
    // check whether user id is registered
    User *user = this->users[id];

    // true if registered
    return user != nullptr;
}

bool AdminManager::registerUser(string &id, string &message) {
    if (this->isRegistered(id)) {
        message = "Already registered";
        return false;
    }

    // register user and save
    User *user = new User{id, 0, 0};
    this->users[id] = user;
    return true;
}

bool AdminManager::deregisterUser(string &id, string &message) {
    if (!this->isRegistered(id)) {
        message = "Not registered";
        return false;
    }
    User *user = this->users[id];
    delete user;
    this->users[id] = nullptr;
    return true;
}

string AdminManager::getRoomList() {
    if (this->roomList.size() == 0) {
        return "No room is available.";
    }
    string result = "";

    for (auto userPair: this->roomList) {
        string id = userPair.first;
        User *user = this->users[id];

        result += id + "\t";
        result += user->numWin + "\t";
        result += user->numLoss + "\n";
    }
    return result;
}


// update rank everytime called
string AdminManager::getRank() {
    if (this->users.size() == 0) {
        return "No users in the system. ";
    }
    string result = "";
    priority_queue<User *, vector<User *>, CmpUserPtrs> pq;
    for (auto userPair: this->users) {
        User *user = userPair.second;
        pq.push(user);
    }

    int count = 0;
    while (!pq.empty() && count < 10) {
        User *temp = pq.top();
        result += temp->id + "\t";
        result += temp->numWin + "\t";
        result += temp->numLoss + "\n";
        count++;
    }
    return result;
}


bool AdminManager::login(string &id, string &message) {
    if(this->currentUsers[id]) {
        message = "Already logged in";
        return false;
    }
    if (this->users[id] == nullptr) {
        message = "Not registered";
        return false;
    }

    this->currentUsers[id] = true;
    return true;
}
