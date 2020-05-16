# JAVA CHAT
Real time multi-user chat.

### Prerequisites
* java SDK 1.8.0_252
* MySQL

### Installation
1. Create a new user, with the following credentials, and a new database MySQL and give all the permissions to the user. 
```
	Database name: java_chat
	Username: java_chat
	Password: java_chat
```
2. Clone the repository on your device.
```
	 git clone https://github.com/sebastianosrt/java_chat
```
3. Open the cloned directory.
4. (Optional) Change the MySQL credentials in the file:
```
	java_chat/src/Server/MySQL
```
<p align="center">
<img src="https://i.ibb.co/Hn6qY6g/cdbchat.png" alt="cdbchat" border="0"> <!--http://prnt.sc/shtma1 da aggiungere alla fine-->
</p>

6. To setup the database execute the SetUpDB file:
```
	 java_chat/src/Server/MySQL/SetUpDB.java
```

Almost finished, there are a few things left!

7.  Execute the Server file:
```
	 java_chat/src/Server/Main.java
```
8. Execute the Client file:
```
	 java_chat/src/Client/Main.java
```

Finished! Now you can chat!

## Use
To use the chat you have to register.
1. Click on the "registrati" button to create an account.
2. After registering click go to login.
3. Type the credentials and sign in.

Now you can:
* Search user to message with. 
* See your contacts.
* Send messages or files
* Discover other things...

## Credits
* **Sartor Sebastiano** - [sebastianosrt](https://github.com/sebastianosrt)
* **Filiberto Abbatangelo** - [Filibertoo](https://github.com/Filibertoo)
* **Ton Federico** - [Fedeton](https://github.com/Fedeton)

##  Copyright and License
Copyright 2020-2021 Java_chat. 
