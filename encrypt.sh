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
openssl aes-128-cbc -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -k $password 

openssl aes-128-cbc -salt -in assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -out assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -k $password

cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.original assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java

cp assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.original assignments/assignment1/A1-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java


# Assignment 2
openssl aes-128-cbc -salt -in assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.aes -k $password 

cp assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java.original assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/model/PalantiriManager.java

openssl aes-128-cbc -salt -in assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java.aes -k $password 

cp assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java.original assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/BeingAsyncTask.java 

openssl aes-128-cbc -salt -in  assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java -out assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.aes -k $password 

cp assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java.original assignments/assignment2/A2-Android-App/app/src/main/java/edu/vandy/presenter/PalantiriPresenter.java

