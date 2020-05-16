# JAVA CHAT
Chat multi-utente, permette lo scambio di messaggi scritti che appaiono in tempo reale sul monitor di ciascun partecipante.
## Installazione 
### Pre-requisiti
* java SDK versione 1.8.0_252
* IDE java
* MySQL
### Fase 2

1. Creare un nuovo utente e un nuovo database su MySQL in modo che l'utente creato abbia tutti i privilegi per modificare il database appena creato. In questa guida per esempio utilizzeremo queste credenziali: 
```
	Nome database: esempio_dbChat
	Nome utente: user_dbChat
	Password utente: password_dbChat
```
2. Clonare la repository sul proprio computer in locale
```
	 git clone sebastianosrt/java_chat
```
3.  Aprire la repo appena scaricata con il proprio IDE in java
4. Spostarsi (attraverso il proprio IDE) nella cartella di MySQL
```
	java_chat/src/Server/MySQL
```
5. Aprire il file `MySQL.java` e modificare gli attributi privati utilizzati per la connessione al database con quelli registrati in precendenza nel punto 1

<img src="https://i.ibb.co/Hn6qY6g/cdbchat.png" alt="cdbchat" border="0"> <!--http://prnt.sc/shtma1 da aggiungere alla fine-->

6. Dopo essersi assicurati che MySQL sia in esecuzione, eseguire tramite il proprio IDE il file `SetUpDB.java` che si occuperà di creare le tabelle necessarie nel database per il corretto funzionamento della chat
```
	 java_chat/src/Server/MySQL/SetUpDB.java
```

### Fase 3
Quasi finito, mancano pochi passaggi!

7.  Azionare il server tramite il proprio IDE in java
```
	 java_chat/src/Server/Main.java
```
8. Aprire il client 
```
	 java_chat/src/Client/Main.java
```

Finito! Ora è possibile utilizzare la chat!

## Utilizzo
Per poter usufruire della chat è necessario registrarsi ad essa.
1. Premere sul pulsante apposito "registrati" per creare un account
2. Dopo aver creato l'account tornare sul menù di login tramite il pulsante apposito
3. Effettuare l'accesso con le credenziali usate in fase di registrazione tramite l'interfaccia grafica

Una volta effettuato l'accesso tramite la UI sarà possibile: 
* Aggiungere contatti tramite l'apposito pulsante di ricerca
* Visualizzare la lista contatti
* Inviare messaggi testuali o file nelle chat
* molto altro ancora da scoprire...

## Crediti 
Sebastiano
Federico
Filiberto

##  Copyright and License
Copyright 2020-2021 Java_chat. 