package com.example.hotel_reviewfrontend.model;

public class Model {
    public class UserModel {
        private String username;
        private String name;
        private String surname;
        private String password;
        private String email;
        private String phone;
        private String address;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            System.out.println(this.password);
            System.out.println(password);
            this.password = password;
        }

        public String getRole() {
            if (role != "worker" && role != "admin")
                role = "worker";

            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

    }
}
