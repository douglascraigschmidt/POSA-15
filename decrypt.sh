#!/bin/sh

echo "Please enter the password";
read -s password;

#cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.original

#cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.original

openssl aes-128-cbc -d -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -k $password

openssl aes-128-cbc -d -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -k $password

