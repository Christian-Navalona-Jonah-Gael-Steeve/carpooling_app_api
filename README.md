# Application de Covoiturage

## Présentation

Ce projet a été réalisé dans le cadre du **Projet d'Innovation (TPI)** du **Master 2 MIAGE – parcours MBDS**.
Il s'agit d'une **application mobile de covoiturage** visant à faciliter le partage de trajets entre particuliers, en mettant l'accent sur la **simplicité, la sécurité** et l'**expérience utilisateur en temps réel**.

## Fonctionnalités principales

- **Inscription utilisateur complète** : Upload de photo de profil et de justificatifs (CIN/Passeport/Permis/Carte étudiant), avec envoi de mail de confirmation pour la validation du compte
- **Connexion utilisateur** : Authentification sécurisée via Firebase Authentication
- **Covoiturage planifié et non planifié** : Recherche rapide de covoiturage adapté à votre itinéraire et à votre horaire, avec notifications des trajets à proximité
- **Messagerie instantanée P2P** : Communication décentralisée en peer-to-peer entre conducteur et passager une fois mis en relation
- **Intégration de l'API Google Maps** : Définition et visualisation des trajets sur la carte avec géolocalisation en temps réel
- **Communication en temps réel P2P** : Appels audio et vidéo directs via WebRTC

## Technologies utilisées

### Backend
- **Spring Boot** : Framework Java pour créer des applications robustes et scalables
- **PostgreSQL + PostGIS** : Base de données relationnelle avec extension géospatiale pour la gestion des données de localisation

### Frontend
- **React Native** : Framework JavaScript pour créer une application mobile multiplateforme (iOS et Android)
- **Expo** : Plateforme pour faciliter le développement et le déploiement d'applications React Native

### Services & APIs
- **Firebase Authentication** : Service d'authentification sécurisé pour la gestion des utilisateurs
- **Google Maps API** : API de cartographie pour le suivi des trajets et la navigation
- **WebRTC** : Technologie de communication pair-à-pair pour les appels audio/vidéo en temps réel

## Démarche et mise en place

### Pré-requis

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou supérieur
- [Maven](https://maven.apache.org/download.cgi) (version 3.6+ recommandée)
- [PostgreSQL](https://www.postgresql.org/download/) (version 12+ recommandée)
- [PostGIS](https://postgis.net/install/) - Extension géospatiale pour PostgreSQL

### Lancement de l'application

1. Cloner le projet backend:
   ```bash
   git clone https://github.com/Christian-Navalona-Jonah-Gael-Steeve/carpooling_app_api.git
   ```

2. Cloner, configurer et lancer le projet frontend sur [https://github.com/Christian-Navalona-Jonah-Gael-Steeve/carpooling_proto_front.git](https://github.com/Christian-Navalona-Jonah-Gael-Steeve/carpooling_proto_front.git)

3. Configurer la base de données PostgreSQL :
   - Créer une base de données nommée `carpooling`
   ```sql
   CREATE DATABASE carpooling;
   ```
   - Activer l'extension PostGIS
   ```sql
   \c carpooling
   CREATE EXTENSION postgis;
   ```

4. Configurer les variables d'environnement :
   - Modifier le fichier `src/main/resources/application.properties`
   - Configurer les paramètres de connexion à la base de données :
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/carpooling
     spring.datasource.username=votre_username
     spring.datasource.password=votre_password
     ```
   - Configurer le répertoire d'upload des fichiers :
     ```properties
     app.upload.dir=/chemin/vers/votre/repertoire/uploads
     spring.web.resources.static-locations=file:/chemin/vers/votre/repertoire/uploads/
     ```
   - Ajouter votre clé API Firebase si nécessaire

5. Installer les dépendances Maven :
   ```bash
   mvn clean install
   ```

6. Connecter tout sur le même réseau (pour les tests avec le frontend mobile)

7. Lancer l'application :
   ```bash
   mvn spring-boot:run
   ```

   L'API sera accessible sur `http://localhost:8080`

   Documentation Swagger disponible sur `http://localhost:8080/swagger-ui.html`
### Branche principale

La branche par défaut pour ce projet est `main`. Toutes les contributions doivent être basées sur cette branche.

## Documentation

Pour plus d'informations sur le développement :

- [Documentation React Native](https://reactnative.dev/docs/getting-started)
- [Documentation Expo](https://docs.expo.dev/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [WebRTC Documentation](https://webrtc.org/)

## Contribution

Ce projet est développé dans le cadre académique de l'Université Côte d'Azur (MIAGE MBDS).
