package com.diluv.api.graphql;

import com.diluv.api.graphql.data.RegistrationCodes;
import com.diluv.api.graphql.data.User;
import graphql.kickstart.tools.GraphQLResolver;

public class RegistrationCodesResolver implements GraphQLResolver<RegistrationCodes> {

    public User user (RegistrationCodes registrationCodes) {

        return new User(registrationCodes.getEntity().getUser());
    }
}
