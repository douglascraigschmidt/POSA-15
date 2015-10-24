#!/bin/sh

while true
do
	read -s -p "Password: " password
	echo
	read -s -p "Password (again): " password2
	echo
	[ "$password" = "$password2" ] && break
	echo "Please try again"
done

# Assignment 1
openssl aes-128-cbc -d -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -k $password

openssl aes-128-cbc -d -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -k $password

# Assignment 2
openssl aes-128-cbc -d -salt -in assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -k $password

openssl aes-128-cbc -d -salt -in assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java.aes -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java -k $password

openssl aes-128-cbc -d -salt -in assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -k $password

