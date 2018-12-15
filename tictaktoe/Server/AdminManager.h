#ifndef ADMIN_MANAGER_H
#define ADMIN_MANAGER_H
/*
 * Administrator class which takes care of admin information
 * to the client.
 */

#include <map>
#include <vector>
#include <iostream>
#include <string>
#include <queue>

class AdminManager {
public:
    AdminManager();
    ~AdminManager();

    bool isRegistered(std::string &id);
    bool registerUser(std::string &id, std::string &message);
    bool deregisterUser(std::string &id, std::string &message);
    std::string getRoomList();
    std::string getRank();
    bool login(std::string &id, std::string &message);


private:
    struct User {
        std::string id;
        int numWin;
        int numLoss;
    };

    struct CmpUserPtrs {
        bool operator() (const User *lhs, const User *rhs) const {
            if (lhs->numWin == rhs->numWin) {
                return lhs->numLoss <= rhs->numLoss;
            }
            return lhs->numWin > rhs->numWin;
        }
    };

    std::map<std::string, User*> users;
    std::map<std::string, int> roomList;
    std::map<std::string, bool> currentUsers;
};

#endif