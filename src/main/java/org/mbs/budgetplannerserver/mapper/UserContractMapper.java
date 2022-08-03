package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserContractMapper {
    public List<UserContract> map(Iterable<User> users) {
        List<UserContract> userContracts = new ArrayList<>();
        users.forEach(user -> {
            userContracts.add(fromUser(user));
        });

        return userContracts;
    }

    public UserContract fromUser(User user) {
        UserContract userContract = new UserContract();
        userContract.setId(user.getId());
        userContract.setEmail(user.getEmail());
        userContract.setName(user.getName());
        userContract.setUserName(user.getUserName());
        userContract.setAdmin(user.getAdmin());
        userContract.setMunicipalityId(user.getMunicipality().getId());
        return userContract;
    }
}
