package com.example.user.presentation;

public class ReqPutUsersByIdDTOApiV1 {

    private UserDTO user;

    public ReqPutUsersByIdDTOApiV1(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }

    public static class UserDTO {

        private String name;

        public UserDTO(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
