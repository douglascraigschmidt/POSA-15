#!/bin/sh

echo "Please enter the password";
read -s password;

openssl aes-128-cbc -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -k $password 

openssl aes-128-cbc -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -k $password

cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.original assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java

cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.original assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java
