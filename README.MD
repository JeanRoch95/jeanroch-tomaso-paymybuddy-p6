# PayMyBuddy 

PayMyBuddy est une application de transfer d'argent entre amis.

## Diagramme de classe 

![jj](https://i.ibb.co/XJw9LRn/uml.png)

## Diagramme MPD

![Capture d’écran 2023-08-29 à 18.02.56.png](/Users/sky2/Desktop/OpenClassRoom/Projet/Projet6/uml.png)

## Prérequis

* Java 17
* Maven
*  MySQL ou MariaDB

## Installation et configuration

1. Cloner le dépôt :  
   ``git clone https://github.com/votre_username/paymybuddy-p6.git``

2. Naviguer vers le dossier du projet  
   ``` cd paymybuddy-p6 ```

3. Configurer la base de données
  * Assurez-vous que MySQL ou MariaDB est installé et en cours d'éxécution
  * Mettez à jour 'src/main/resources/application.properties' avec vos propres informations de connexion a la base de donnée

   ``spring.datasource.url=jdbc:mysql://localhost:3307/paymybuddy``  
   ``spring.datasource.username=root``
   ``spring.datasource.password=votre_mot_de_passe``

4. Initialisation de la Base de Données

Pour initialiser la base de données avec des données par défault, un fichier ``data.sql`` est fournis dans le répertoire ``src/main/resources`

5. Compilez et packagez le projet avec Maven
   
   ```mvn clean install```

## Exécution 

1. Lancer l'application

   ```mvn spring-boot:run```  

2. Ouvrez un navigateur et naviguer vers  

   ``http://localhost:9000``

## User

Vous pouvez utiliser jusqu'à 3 user avant de vous inscrire : 

* Email : Jhon@mail.com --- Password: password
* Email : JR@mail.com --- Password: password
* Email : JP@mail.com --- Password : password
