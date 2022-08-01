package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;

public class UserContractMapper {
    public UserContract map(Iterable<User> users) {
        UserContract userContract = new UserContract();
        users.forEach(user -> {
            userContract.setName(user.getName());
            userContract.setUserName(user.getUserName());
            userContract.setAdmin(user.getAdmin());
        });

        return userContract;
    }

    public UserContract getUser(User user) {
        UserContract userContract = new UserContract();
        userContract.setName(user.getName());
        userContract.setUserName(user.getUserName());
        userContract.setAdmin(user.getAdmin());
        return userContract;
    }
}
