# JavaSpring - Gestion de CIN

Application Spring Boot de gestion de citoyens, de demandes de CIN, d'authentification et de génération de QR code.

Le projet fonctionne maintenant en local par defaut avec une base H2 fichier, ce qui permet de lancer l'application sans installer PostgreSQL. Il reste aussi possible d'utiliser PostgreSQL en passant des variables d'environnement.

## 1. Technologies utilisees

- Java 17+
- Spring Boot 4
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database
- PostgreSQL en option
- Maven Wrapper (`mvnw`)

## 2. Fonctionnalites principales

- Authentification et inscription utilisateur
- Gestion des citoyens
- Gestion des demandes de CIN
- Tableau de bord administrateur
- Gestion des utilisateurs
- Verification d'un citoyen
- Generation de QR code
- Reinitialisation de mot de passe par OTP

## 3. Arborescence utile

- `src/main/java/org/insi/javaspring/controller` : controllers web et API
- `src/main/java/org/insi/javaspring/service` : logique metier
- `src/main/java/org/insi/javaspring/model` : entites JPA
- `src/main/java/org/insi/javaspring/repository` : acces base de donnees
- `src/main/resources/templates` : vues Thymeleaf
- `src/main/resources/application.properties` : configuration principale
- `.mvn/maven.config` : configuration Maven locale

## 4. Prerequis

- Java installe
- Avoir `java -version` qui fonctionne
- Aucun PostgreSQL n'est obligatoire pour un lancement local simple

## 5. Configuration actuelle

Le projet utilise par defaut H2 en local.

Configuration principale actuelle :

```properties
spring.datasource.url=${DB_URL:jdbc:h2:file:./data/javaspring_db;MODE=PostgreSQL;AUTO_SERVER=TRUE}
spring.datasource.username=${DB_USERNAME:sa}
spring.datasource.password=${DB_PASSWORD:}
```

Cela signifie :

- si `DB_URL` n'est pas defini, l'application utilise H2
- si `DB_URL` est defini, l'application utilise la base fournie

## 6. Etapes pour faire fonctionner le projet

### Etape 1 - Ouvrir le projet

Place-toi dans le dossier du projet :

```bash
cd project-CIN
```

### Etape 2 - Lancer les tests

```bash
./mvnw test
```

Resultat attendu :

- `BUILD SUCCESS`

### Etape 3 - Demarrer l'application

```bash
./mvnw spring-boot:run
```

Resultat attendu :

- Tomcat demarre sur le port `8080`
- la base H2 est creee automatiquement dans `./data`
- un compte administrateur par defaut est cree si aucun admin n'existe

### Etape 4 - Ouvrir l'application

Dans le navigateur :

- `http://localhost:8080`

## 7. Compte administrateur par defaut

Au premier lancement, le projet cree automatiquement un admin via `src/main/java/org/insi/javaspring/config/DataInitializer.java`.

Identifiants par defaut :

- Email : `admin@cin.local`
- Mot de passe : `admin123`

## 8. Pages et routes principales

### Pages publiques

- `/`
- `/auth/login`
- `/auth/register`
- `/auth/forgot-password`
- `/verify/**`
- `/qr/**`

### Pages protegees

- `/home`
- `/citoyens`
- `/demandes`

### Pages admin

- `/dashboard`
- `/admin/users`

### API REST

- `/api/citoyen/add`
- `/api/citoyen/all`
- `/api/citoyen/{id}`

## 9. Base de donnees H2

Le projet peut tourner entierement avec H2.

### Console H2

- URL : `http://localhost:8080/h2-console`
- JDBC URL : `jdbc:h2:file:./data/javaspring_db`
- User Name : `sa`
- Password : vide

### Fichier de base

La base H2 locale est stockee dans :

- `data/`

## 10. Utiliser PostgreSQL a la place de H2

Si tu veux utiliser PostgreSQL, definis les variables d'environnement avant le lancement.

Exemple :

```bash
export DB_URL=jdbc:postgresql://localhost:5432/javaspring_db
export DB_USERNAME=postgres
export DB_PASSWORD=motdepasse
./mvnw spring-boot:run
```

Dans ce cas :

- l'application n'utilisera plus H2
- elle se connectera a PostgreSQL

## 11. Configuration mail

Le projet contient une configuration SMTP Gmail dans `src/main/resources/application.properties`.

Si tu ne veux pas utiliser l'envoi d'email, tu peux remplacer ces valeurs par tes propres identifiants ou les externaliser plus tard.

## 12. Securite

Le projet utilise Spring Security avec :

- login formulaire
- roles utilisateurs
- routes publiques et routes protegees
- JWT filter present dans la configuration

Regles principales dans `src/main/java/org/insi/javaspring/config/SecurityConfig.java` :

- `/auth/**` accessible publiquement
- `/dashboard/**` reserve a `ADMIN`
- `/admin/**` reserve a `ADMIN`
- `/citoyens/**` authentification obligatoire
- `/demandes/**` authentification obligatoire

## 13. Templates disponibles

Le projet contient notamment les vues suivantes :

- accueil
- login
- register
- forgot password
- verify otp
- reset password
- liste des citoyens
- formulaire citoyen
- verification citoyen
- dashboard admin
- gestion des demandes
- gestion des utilisateurs

## 14. Depannage

### Problemes Maven

Si Maven echoue dans un environnement restreint, le projet utilise deja un depot Maven local via :

- `.mvn/maven.config`

### Probleme de port 8080 deja utilise

Lance l'application sur un autre port :

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Probleme PostgreSQL

Si PostgreSQL ne demarre pas ou refuse la connexion :

- retire les variables `DB_*` pour revenir a H2
- ou corrige l'utilisateur, le mot de passe et la base PostgreSQL

### Reinitialiser les donnees locales H2

Arrete l'application puis supprime les fichiers dans :

- `data/`

Au prochain lancement, la base sera recreee.

## 15. Commandes utiles

Lancer les tests :

```bash
./mvnw test
```

Demarrer l'application :

```bash
./mvnw spring-boot:run
```

Construire le projet :

```bash
./mvnw clean package
```

## 16. Etat actuel du projet

Etat verifie localement :

- les tests passent
- l'application demarre sur `http://localhost:8080`
- le projet fonctionne sans PostgreSQL grace a H2

## 17. Ameliorations conseillees

- externaliser les secrets mail
- ajouter un vrai `application-prod.properties`
- ajouter plus de tests metier et controller
- desactiver les logs SQL en production
- desactiver `spring.jpa.open-in-view` si non necessaire
# springboot
